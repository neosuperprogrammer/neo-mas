package com.tionsoft.mas.tas;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.integration.jmx.IoServiceMBean;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import com.tionsoft.mas.tas.exception.TasException;
import com.tionsoft.mas.tas.filter.TasFilterChain;
import com.tionsoft.mas.tas.handler.TasDefaultHandler;
import com.tionsoft.mas.tas.handler.TasHandler;
import com.tionsoft.mas.tas.handler.TasHandlerChain;
import com.tionsoft.mas.tas.session.SessionManagement;
import com.tionsoft.mas.tas.session.UselessSession;
import com.tionsoft.mas.tas.support.TcpAppStartupListener;
import com.tionsoft.mas.tas.support.TcpAppStartupListenerFactory;
import com.tionsoft.platform.listener.TasSessionClosedListener;
import com.tionsoft.platform.listener.TasSessionIdleListener;


public class TcpApp {

	private static Logger logger = LoggerFactory.getLogger(TcpApp.class);
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


	private final TasContext tasContext;
	private final NioSocketAcceptor ioAcceptor;

	public TcpApp(TasContext tasContext, NioSocketAcceptor ioAcceptor) {
		this.tasContext = tasContext;
		Thread.currentThread().setContextClassLoader(tasContext.getApplicationContext().getClassLoader());
		this.ioAcceptor =  ioAcceptor;

		initTcpApp();
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

			//jmx 등록
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

		TasHandlerChain handlerChain = tasContext.getApplicationContext().getBean(HANDLER_CHAIN_BEAN_NAME, TasHandlerChain.class);
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
			if(logger.isDebugEnabled())
			{
				logger.debug(TAS_APP_SESSION_IDLE_BEAN_NAME + " is not defined");
			}
		}

		try{

			closedListener = tasContext.getApplicationContext().getBean(TAS_APP_SESSION_CLOSED_BEAN_NAME, TasSessionClosedListener.class);
			handler.setTasSessionClosedListener(closedListener);

		}catch (NoSuchBeanDefinitionException e) {
			if(logger.isDebugEnabled())
			{
				logger.debug(TAS_APP_SESSION_CLOSED_BEAN_NAME + " is not defined");
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

}
