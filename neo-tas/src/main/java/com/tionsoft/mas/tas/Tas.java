package com.tionsoft.mas.tas;

import java.io.*;
import java.lang.management.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.util.*;

import javax.management.*;
import javax.management.remote.*;

import org.apache.commons.configuration.*;
import org.apache.commons.configuration.tree.xpath.*;
import org.slf4j.*;

import com.tionsoft.mas.tas.exception.*;
import com.tionsoft.mas.tas.share.TasShare;
import com.tionsoft.mas.tas.share.TasSharedObject;
import com.tionsoft.platform.utils.StringUtils;

public class Tas {
	
	private int SEND_BUFFER_SIZE = 10240;
	private int RECV_BUFFER_SIZE = 10240;
	private int backLog = 1000;
	private boolean reuseAddress=false;
	private boolean tcpNoDelay=true;
	private final ClassLoader tasClassLoader;
	private XMLConfiguration serverConfiguration;
	private final String meapHome;
	private final Logger LOGGER = LoggerFactory.getLogger("tas");
	String jmxip = null;
	public Tas(ClassLoader tasClassLoader) {
		this.tasClassLoader = tasClassLoader;
		meapHome = System.getProperty("MAS_HOME");
	}
	
	public void start() {
		loadServerConfiguration();
		initTcpApps();
	}
	
	private void loadServerConfiguration() {
		System.out.println("================================================================================================");
		System.out.println(">>>>> Loading TAS Configuration ...");
		String serverConfigPath = meapHome + "/tas/conf/tas.xml";
		try {
			serverConfiguration = new XMLConfiguration(serverConfigPath);
			serverConfiguration.setExpressionEngine(new XPathExpressionEngine());
		} catch (ConfigurationException e) {
			throw new TasException(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
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

				String base = meapHome + "/" + serverConfiguration.getString(queryPrefix + "/@base");
				File baseFile = new File(base);
				if(!baseFile.isDirectory()){
					base = serverConfiguration.getString(queryPrefix + "/@base");
				}
				
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
				
				
				jmxip = ip;
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
				
				if(jmxFlage != null && jmxFlage.equalsIgnoreCase("true")) {
			        // create a JMX MBean Server server instance
			        mBeanServer = ManagementFactory.getPlatformMBeanServer();
			        System.out.println("mBeanServer.toString() " + mBeanServer.toString());
			        
			        if(sharedObjectFlag.equalsIgnoreCase("true")){
			        	startTcpApp(tcpAppConfig,mBeanServer,sharedObject);
			        }else {
			        	startTcpApp(tcpAppConfig,mBeanServer);
			        }
						
				}else{
					if(sharedObjectFlag.equalsIgnoreCase("true")){
						startTcpApp(tcpAppConfig,sharedObject);
					}else {
						startTcpApp(tcpAppConfig);
					}
					
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new TasException(e);
			}

		}
		
		//jmx
		if(jmxFlage != null && jmxFlage.equalsIgnoreCase("true")) {
	        // create a JMX MBean Server server instance
			getMBeanServer(mBeanServer);
		}

		
	}
	
	private void startTcpApp(TcpAppConfig tcpAppConfig) {
		new TasContext(tcpAppConfig, tasClassLoader);
	}
	
	private void startTcpApp(TcpAppConfig tcpAppConfig,TasShare sharedObject) {
		new TasContext(tcpAppConfig, tasClassLoader,sharedObject);
	}
	
	private void startTcpApp(TcpAppConfig tcpAppConfig,MBeanServer mBeanServer) {
		new TasContext(tcpAppConfig, tasClassLoader, mBeanServer);
	}
	
	private void startTcpApp(TcpAppConfig tcpAppConfig,MBeanServer mBeanServer,TasShare sharedObject) {
		new TasContext(tcpAppConfig, tasClassLoader, mBeanServer, sharedObject);
	}
	
	public void getMBeanServer(MBeanServer mBeanServer){
		System.out.println("Tas getMBeanServer start");
		final String rmiRegistryPort= System.getProperty("tas.rmi.registry.port","8902");
		final String rmiServerPort= System.getProperty("tas.rmi.server.port","8901");
		
		System.out.println("Tas getMBeanServer tas.rmi.registry.port:"+rmiRegistryPort);
		System.out.println("Tas getMBeanServer tas.rmi.server.port:"+rmiServerPort);
		
		preMBeanServer(rmiRegistryPort);
			
		HashMap<String,Object> env = new HashMap<String,Object>();
		  
//		String hostAddress = null;
//		try {
//			hostAddress = InetAddress.getLocalHost().getHostAddress();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		String hostname = null;
//		try {
//			hostname = InetAddress.getLocalHost().getHostName();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	
		JMXServiceURL url = null;
		String serviceUrl = "service:jmx:rmi://"+jmxip+":"+rmiServerPort+"/jndi/rmi://"+jmxip+":"+rmiRegistryPort+"/jmxrmi";
		try {
			url = new JMXServiceURL(serviceUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JMXConnectorServer cs = null;
		try {
			cs = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mBeanServer);
			cs.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		System.out.println("Start the RMI connector serviceUrl:"+serviceUrl);
	}
	
	
	public void preMBeanServer(String rmiRegistryPort){
		System.setProperty("java.rmi.server.randomIDs", "true");
		int port = Integer.parseInt(rmiRegistryPort);
        System.out.println("Create RMI registry on port "+port);
       
        try {
			LocateRegistry.createRegistry(port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new TasException(e);
		}
	}

}