package com.flowgrammer.neo.tas;

import com.tionsoft.mas.tas.TasContext;
import com.tionsoft.mas.tas.TasSharedBean;
import com.tionsoft.mas.tas.TcpAppConfig;
import com.tionsoft.mas.tas.exception.TasException;
import com.tionsoft.mas.tas.filter.TasFilterChain;
import com.tionsoft.mas.tas.handler.TasDefaultHandler;
import com.tionsoft.mas.tas.handler.TasHandler;
import com.tionsoft.mas.tas.handler.TasHandlerChain;
import com.tionsoft.mas.tas.session.SessionManagement;
import com.tionsoft.mas.tas.session.UselessSession;
import com.tionsoft.mas.tas.share.TasShare;
import com.tionsoft.mas.tas.share.TasSharedObject;
import com.tionsoft.mas.tas.support.TcpAppStartupListener;
import com.tionsoft.mas.tas.support.TcpAppStartupListenerFactory;
import com.tionsoft.platform.listener.TasSessionClosedListener;
import com.tionsoft.platform.listener.TasSessionIdleListener;
import com.tionsoft.platform.utils.StringUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.integration.jmx.IoServiceMBean;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by neox on 10/22/15.
 */
public class Main {


    public static final int SEND_BUFFER_SIZE = 10240;
    public static final int RECV_BUFFER_SIZE = 10240;

    public static final String TAS_SHARED_BEAN_NAME = "tasSharedBean";
    public static final String FILTER_CHAIN_BEAN_NAME = "tasFilterChain";
    public static final String HANDLER_BEAN_NAME = "tasHandler";
    public static final String HANDLER_CHAIN_BEAN_NAME = "tasHandlerChain";
    public static final String TAS_SESSION_MGMT_BEAN_NAME = "tasSessionMgmt";
    public static final String TAS_SESSION_USELESS_MGMT_BEAN_NAME = "tasSessionUselessMgmt";
    public static final String TAS_APP_SESSION_IDLE_BEAN_NAME = "tasSessionIdleListener";
    public static final String TAS_APP_SESSION_CLOSED_BEAN_NAME = "tasSessionClosedListener";
    private int backLog = 1000;
    private boolean reuseAddress=false;
    private boolean tcpNoDelay=true;
    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private TasContext tasContext;
    private ClassPathXmlApplicationContext applicationContext;

    private static final int PORT = 8080;
    private XMLConfiguration protocolConfig;
    private NioSocketAcceptor ioAcceptor;
    private XMLConfiguration serverConfiguration;


    private void startEchoServer() throws Exception {
        LOGGER.info("start");
        String config = "conf/tas-config.xml";
        applicationContext = new ClassPathXmlApplicationContext(new String[]{config}, true);

        SocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.setReuseAddress(true);
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();


        acceptor.setHandler(new EchoProtocolHandler());
        acceptor.bind(new InetSocketAddress(PORT));

        System.out.println("Listening on port " + PORT);

        for (; ; ) {
            System.out.println("R: " + acceptor.getStatistics().getReadBytesThroughput() +
                    ", W: " + acceptor.getStatistics().getWrittenBytesThroughput());
            Thread.sleep(3000);
        }
    }
    public Main() {
        try {
//            startEchoServer();
            startTasServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTasServer() throws Exception {
        ioAcceptor = new NioSocketAcceptor();
        ioAcceptor.setReuseAddress(true);
        init();
        initTcpApp();
        bind();

//        for (; ; ) {
//            System.out.println("Heart Beat");
//            Thread.sleep(3000);
//        }
    }

    private void init() {
        loadServerConfiguration();
        loadProcotolConfig();
        loadTcpApplicationContext();
        initTcpApps();
//        startTcpApp();
    }

    private void loadServerConfiguration() {
        System.out.println("================================================================================================");
        System.out.println(">>>>> Loading TAS Configuration ...");
        String serverConfigPath = "conf/tas.xml";
        try {
            serverConfiguration = new XMLConfiguration(serverConfigPath);
            serverConfiguration.setExpressionEngine(new XPathExpressionEngine());
        } catch (ConfigurationException e) {
            throw new TasException(e);
        }
    }
    private void loadProcotolConfig() {
        try {
            protocolConfig = new XMLConfiguration("conf/protocol.xml");
            protocolConfig.setExpressionEngine(new XPathExpressionEngine());
        } catch (ConfigurationException e) {
            throw new TasException(e);
        }
    }

    private void loadTcpApplicationContext() {
        String config = "conf/tas-config.xml";
        applicationContext = new ClassPathXmlApplicationContext(new String[]{config}, true);
    }

    private void initTcpApps() {
        //jmx
        String jmxFlage = null;
        String sharedObjectFlag=null;
        MBeanServer mBeanServer = null;

        TasShare sharedObject = TasSharedObject.getInstance();

        List<String> names = serverConfiguration.getList("tcpapps/tcpapp/@name");

        for(String name : names) {
            try{
                String queryPrefix = "tcpapps/tcpapp[@name='" + name + "']";

//                String base = meapHome + "/" + serverConfiguration.getString(queryPrefix + "/@base");
//                File baseFile = new File(base);
//                if(!baseFile.isDirectory()){
//                    base = serverConfiguration.getString(queryPrefix + "/@base");
//                }
                String base = serverConfiguration.getString(queryPrefix + "/@base");
                String ip = serverConfiguration.getString(queryPrefix + "/@ip");
                int port = serverConfiguration.getInt(queryPrefix + "/@port");

                int sendBufferSize = SEND_BUFFER_SIZE;
                int recvBufferSize = RECV_BUFFER_SIZE;
                int backlog = this.backLog;
                boolean reuseAddress=this.reuseAddress;
                boolean tcpNoDelay=this.tcpNoDelay;
                try{
                    sendBufferSize = serverConfiguration.getInt(queryPrefix + "/@sendBufferSize");
                    recvBufferSize = serverConfiguration.getInt(queryPrefix + "/@recvBufferSize");
                    backlog = serverConfiguration.getInt(queryPrefix + "/@backlog");
                    reuseAddress = serverConfiguration.getBoolean(queryPrefix + "/@reuseAddress");
                    tcpNoDelay = serverConfiguration.getBoolean(queryPrefix + "/@tcpNoDelay");
                }catch(Exception e)
                {
                    sendBufferSize = SEND_BUFFER_SIZE;
                    recvBufferSize = RECV_BUFFER_SIZE;
                    backlog = this.backLog;
                    reuseAddress = this.reuseAddress;
                    tcpNoDelay=this.tcpNoDelay;
                }


//                jmxip = ip;
                boolean enable = serverConfiguration.getBoolean(queryPrefix + "/cluster/enable", false);
                String clusterName = serverConfiguration.getString(queryPrefix + "/cluster/name");
                String clusterMachineId = serverConfiguration.getString(queryPrefix + "/cluster/machineId");
                String clusterRackId = serverConfiguration.getString(queryPrefix + "/cluster/rackId");
                String clusterSiteId = serverConfiguration.getString(queryPrefix + "/cluster/siteId");
                String numOwners = serverConfiguration.getString(queryPrefix + "/cluster/numOwners");
                long sessionTimeout = serverConfiguration.getLong(queryPrefix + "/session/timeout");
//				long keepAliveTime = serverConfiguration.getLong(queryPrefix + "/shareMemory/keepAliveTime");
                int connectionTimeout = serverConfiguration.getInt(queryPrefix + "/connection/timeout");
                String startupListener = serverConfiguration.getString(queryPrefix + "/listener/startup", "");

                TcpAppConfig tcpAppConfig = new TcpAppConfig();
                tcpAppConfig.setName(name);
                tcpAppConfig.setBase(base);
                tcpAppConfig.setIp(ip);
                tcpAppConfig.setPort(port);

                tcpAppConfig.setBacklog(backlog);
                tcpAppConfig.setSEND_BUFFER_SIZE(sendBufferSize);
                tcpAppConfig.setRECV_BUFFER_SIZE(recvBufferSize);
                tcpAppConfig.setReuseAddress(reuseAddress);
                tcpAppConfig.setTcpNoDelay(tcpNoDelay);

//				tcpAppConfig.setKeepAliveTime(keepAliveTime);

                tcpAppConfig.getClusterConfig().setEnable(enable);
                tcpAppConfig.getClusterConfig().setName(clusterName);
                tcpAppConfig.getClusterConfig().setMachineId(clusterMachineId);
                tcpAppConfig.getClusterConfig().setRackId(clusterRackId);
                tcpAppConfig.getClusterConfig().setSiteId(clusterSiteId);
                tcpAppConfig.getClusterConfig().setNumOwners(Integer.parseInt(numOwners));
                tcpAppConfig.getSessionConfig().setTimeout(sessionTimeout);
                tcpAppConfig.getConnectionConfig().setTimeout(connectionTimeout);

                tcpAppConfig.getListenerConfig().setStartup(startupListener);

                System.out.println("================================================================================================");
                System.out.println(">>>>> Starting TcpApp (" + name + ") ...");


                //jmx
                jmxFlage = StringUtils.defaultIfEmpty(System.getProperty("mas.tas.jmx"),"false");
                sharedObjectFlag = StringUtils.defaultIfEmpty(System.getProperty("mas.tas.shared.object"), "false") ;
                System.out.println("jmxFlage " + jmxFlage);

//                if(jmxFlage != null && jmxFlage.equalsIgnoreCase("true")) {
//                    // create a JMX MBean Server server instance
//                    mBeanServer = ManagementFactory.getPlatformMBeanServer();
//                    System.out.println("mBeanServer.toString() " + mBeanServer.toString());
//
//                    if(sharedObjectFlag.equalsIgnoreCase("true")){
//                        startTcpApp(tcpAppConfig,mBeanServer,sharedObject);
//                    }else {
//                        startTcpApp(tcpAppConfig,mBeanServer);
//                    }
//
//                }else{
//                    if(sharedObjectFlag.equalsIgnoreCase("true")){
//                        startTcpApp(tcpAppConfig,sharedObject);
//                    }else {
//                        startTcpApp(tcpAppConfig);
//                    }
//
//                }
                initTasContext(tcpAppConfig);


            } catch (Exception e) {
                e.printStackTrace();
                throw new TasException(e);
            }

        }

        //jmx
//        if(jmxFlage != null && jmxFlage.equalsIgnoreCase("true")) {
//            // create a JMX MBean Server server instance
//            getMBeanServer(mBeanServer);
//        }


    }






    public void initTasContext(TcpAppConfig tcpAppConfig) {
        Collection<File> classpathList = new ArrayList<File>();
//        Collection<File> classpathList = getClasspathList( masHome + "/tas/lib/");
//        classpathList.add(new File("."));
//        System.out.println("================================================================================================");
//        System.out.println(">>>>> Loading TAS Libraries ...");
//        System.out.println("================================================================================================");
        URLClassLoader tasClassLoader = createClassLoader(classpathList);

        tasContext = new TasContext(tcpAppConfig, tasClassLoader, applicationContext, protocolConfig, ioAcceptor);
    }


    public URLClassLoader createClassLoader(Collection<File> classpathList) {
        URL[] urls = new URL[classpathList.size()];
        int i=0;
        for(File file : classpathList) {
            try {
                urls[i] = file.toURI().toURL();
                System.out.println(file.getCanonicalPath());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            i++;
        }

        URLClassLoader loader = new URLClassLoader(urls);
        return loader;
    }

    private void initTcpApp() {
        try {
            IoHandler tasHandler = createHandler();

            IoFilterChainBuilder filterChainBuilder = createFilterChain();

            InetSocketAddress address = null;

            if(tasContext.getTcpAppConfig().getIp().equals("*")){

                address = new InetSocketAddress(tasContext.getTcpAppConfig().getPort());

            }else {

                address = new InetSocketAddress(tasContext.getTcpAppConfig().getIp(), tasContext.getTcpAppConfig().getPort());

            }

            //InetSocketAddress address = new InetSocketAddress(tasContext.getTcpAppConfig().getIp(), tasContext.getTcpAppConfig().getPort());

            ioAcceptor.setDefaultLocalAddress(address);
            ioAcceptor.setHandler(tasHandler);
            ioAcceptor.setReuseAddress(tasContext.getTcpAppConfig().isReuseAddress());
            ioAcceptor.getSessionConfig().setReuseAddress(tasContext.getTcpAppConfig().isReuseAddress());
            ioAcceptor.getSessionConfig().setReceiveBufferSize(tasContext.getTcpAppConfig().getRECV_BUFFER_SIZE());
            ioAcceptor.getSessionConfig().setSendBufferSize(tasContext.getTcpAppConfig().getSEND_BUFFER_SIZE());
            ioAcceptor.getSessionConfig().setSoLinger(-1);
            ioAcceptor.getSessionConfig().setUseReadOperation(false);
            ioAcceptor.setBacklog(tasContext.getTcpAppConfig().getBacklog());
            ioAcceptor.getSessionConfig().setTcpNoDelay(tasContext.getTcpAppConfig().isTcpNoDelay());
            ioAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, tasContext.getTcpAppConfig().getConnectionConfig().getTimeout());
            ioAcceptor.setFilterChainBuilder(filterChainBuilder);

            tasContext.setAcceptor(ioAcceptor);

            if(tasContext.getApplicationContext().containsBean(TAS_SHARED_BEAN_NAME)) {
                TasSharedBean sharedBean = tasContext.getApplicationContext().getBean(TAS_SHARED_BEAN_NAME, TasSharedBean.class);
                sharedBean.setTasContext(tasContext);
            }

            if(tasContext.getApplicationContext().containsBean(TAS_SESSION_USELESS_MGMT_BEAN_NAME)) {
                UselessSession uselessSessionMgmt = tasContext.getApplicationContext().getBean(TAS_SESSION_USELESS_MGMT_BEAN_NAME, UselessSession.class);
                tasContext.setUselessSession(uselessSessionMgmt);
            }

            if(tasContext.getApplicationContext().containsBean(TAS_SESSION_MGMT_BEAN_NAME)) {
                SessionManagement sessionMgmt = tasContext.getApplicationContext().getBean(TAS_SESSION_MGMT_BEAN_NAME, SessionManagement.class);
                sessionMgmt.setIoAcceptor(ioAcceptor);
            }

            MBeanServer mBeanServer = tasContext.getmBeanServer();
            if(mBeanServer != null) {  //jmx
                System.out.println("TcpApp mBeanServer.registerMBean ioAcceptor start");
                IoServiceMBean acceptorMBean = new IoServiceMBean(ioAcceptor);
                ObjectName acceptorName = new ObjectName(ioAcceptor.getClass().getPackage().getName() + ":type=acceptor, name=" + ioAcceptor.getClass().getSimpleName()+"_"+tasContext.getTcpAppConfig().getName());
                mBeanServer.registerMBean(acceptorMBean, acceptorName);
            }

        } catch (Exception e) {
            throw new TasException(e);
        }
    }

    private IoHandler createHandler() {
        TasHandler handler = null;
        TasSessionIdleListener idleListener;
        TasSessionClosedListener closedListener;

        TasHandlerChain handlerChain = applicationContext.getBean(HANDLER_CHAIN_BEAN_NAME, TasHandlerChain.class);
        handlerChain.build(tasContext);
        try {
            handler = tasContext.getApplicationContext().getBean(HANDLER_BEAN_NAME, TasHandler.class);
        } catch (NoSuchBeanDefinitionException e) {
            handler = new TasDefaultHandler();
        }

        handler.setTasContext(tasContext);
        handler.setHandlerChain(handlerChain);

        try{

            idleListener = tasContext.getApplicationContext().getBean(TAS_APP_SESSION_IDLE_BEAN_NAME, TasSessionIdleListener.class);
            handler.setTasSessionIdleListener(idleListener);

        }catch (NoSuchBeanDefinitionException e) {
            if(LOGGER.isDebugEnabled())
            {
                LOGGER.debug(TAS_APP_SESSION_IDLE_BEAN_NAME + " is not defined");
            }
        }

        try{

            closedListener = tasContext.getApplicationContext().getBean(TAS_APP_SESSION_CLOSED_BEAN_NAME, TasSessionClosedListener.class);
            handler.setTasSessionClosedListener(closedListener);

        }catch (NoSuchBeanDefinitionException e) {
            if(LOGGER.isDebugEnabled())
            {
                LOGGER.debug(TAS_APP_SESSION_CLOSED_BEAN_NAME + " is not defined");
            }
        }


        return handler;
    }

    private IoFilterChainBuilder createFilterChain() {
        TasFilterChain fChain = tasContext.getApplicationContext().getBean(FILTER_CHAIN_BEAN_NAME, TasFilterChain.class);
        return fChain.build(tasContext);
    }

    public void bind() {
        try {
            ioAcceptor.bind();
            System.out.println("================================================================================================");
            System.out.println(">>>>> TcpApp(" + tasContext.getTcpAppConfig().getName() + ") is started, listening on port " + tasContext.getTcpAppConfig().getPort());
            String startupListenerClassName = tasContext.getTcpAppConfig().getListenerConfig().getStartup();
            if(!startupListenerClassName.equalsIgnoreCase("")) {
                TcpAppStartupListener startupListener = TcpAppStartupListenerFactory.getListener(tasContext.getTcpAppClassLoader(), startupListenerClassName);
                startupListener.onTcpAppStarted(tasContext);
            }
        } catch (IOException e) {
            throw new TasException(e);
        } catch (Exception e) {
            throw new TasException(e);
        }
    }

    public static void main(String[] args) throws Exception {

        LOGGER.info("start");

        new Main();


    }
}
