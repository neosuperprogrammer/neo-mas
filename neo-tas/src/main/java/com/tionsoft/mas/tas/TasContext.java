package com.tionsoft.mas.tas;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Map;

import javax.management.MBeanServer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.io.FileUtils;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tionsoft.mas.tas.exception.TasException;
import com.tionsoft.mas.tas.pushlet.PushService;
import com.tionsoft.mas.tas.session.UselessSession;
import com.tionsoft.mas.tas.share.TasAppShare;
import com.tionsoft.mas.tas.share.TasShare;
import com.tionsoft.mas.tas.taslet.TasResponse;
import com.tionsoft.platform.utils.StringUtils;

public class TasContext {
	
	private final Logger LOGGER = LoggerFactory.getLogger("tas");

	private TcpAppConfig tcpAppConfig;
	private EmbeddedCacheManager cacheManager;
	private ClassLoader tasClassLoader;
	private ClassLoader tcpAppClassLoader;
	private XMLConfiguration protocolConfig;
	private ClassPathXmlApplicationContext applicationContext;
	private NioSocketAcceptor acceptor;
	private UselessSession uselessSessionMapObject;
	private MBeanServer mBeanServer = null;//jmx
	public TasShare sharedObject;
	
	
	public TasContext(TcpAppConfig tcpAppConfig, ClassLoader tasClassLoader) {
		this.tcpAppConfig = tcpAppConfig;
		this.tasClassLoader = tasClassLoader;
		this.acceptor = new NioSocketAcceptor();
		System.out.println("================================================================================================");
		System.out.println(">>>>> Loading TcpApp (" + tcpAppConfig.getName() + ") Libraries ...");
		System.out.println("================================================================================================");
		this.tcpAppClassLoader = createTcpAppClassLoader();
		this.sharedObject= null;
		init();
	}
	
	public TasContext(TcpAppConfig tcpAppConfig, ClassLoader tasClassLoader,TasShare sharedObject) {
		this.tcpAppConfig = tcpAppConfig;
		this.tasClassLoader = tasClassLoader;
		this.sharedObject = sharedObject;
		this.acceptor = new NioSocketAcceptor();
		System.out.println("================================================================================================");
		System.out.println(">>>>> Loading TcpApp (" + tcpAppConfig.getName() + ") Libraries ...");
		System.out.println("================================================================================================");
		this.tcpAppClassLoader = createTcpAppClassLoader();
		
		init();
	}
	
	public TasContext(TcpAppConfig tcpAppConfig, ClassLoader tasClassLoader, MBeanServer mBeanServer) {
		this.tcpAppConfig = tcpAppConfig;
		this.tasClassLoader = tasClassLoader;
		this.acceptor = new NioSocketAcceptor();
		System.out.println("================================================================================================");
		System.out.println(">>>>> Loading TcpApp (" + tcpAppConfig.getName() + ") Libraries ...");
		System.out.println("================================================================================================");
		this.tcpAppClassLoader = createTcpAppClassLoader();
		this.mBeanServer = mBeanServer;
		this.sharedObject= null;
		init();
	}
	
	public TasContext(TcpAppConfig tcpAppConfig, ClassLoader tasClassLoader, MBeanServer mBeanServer,TasShare sharedObject) {
		this.tcpAppConfig = tcpAppConfig;
		this.tasClassLoader = tasClassLoader;
		this.acceptor = new NioSocketAcceptor();
		System.out.println("================================================================================================");
		System.out.println(">>>>> Loading TcpApp (" + tcpAppConfig.getName() + ") Libraries ...");
		System.out.println("================================================================================================");
		this.tcpAppClassLoader = createTcpAppClassLoader();
		this.mBeanServer = mBeanServer;
		this.sharedObject = sharedObject;
		init();
	}

	public TasContext(TcpAppConfig tcpAppConfig, URLClassLoader tasClassLoader, ClassPathXmlApplicationContext applicationContext, XMLConfiguration protocolConfig, NioSocketAcceptor ioAcceptor) {

		this.tcpAppConfig = tcpAppConfig;
		this.tasClassLoader = tasClassLoader;
		this.applicationContext = applicationContext;
		this.protocolConfig = protocolConfig;
		this.setAcceptor(ioAcceptor);
		URL urls[] = new URL[0];
		this.tcpAppClassLoader = new URLClassLoader(urls);
		initDataGrid();
	}

	private void init() {
		ClassLoader ccl = Thread.currentThread().getContextClassLoader(); 
		Thread.currentThread().setContextClassLoader(tcpAppClassLoader);
		loadProcotolConfig();
		loadTcpApplicationContext();
		initDataGrid();
		startTcpApp();
		Thread.currentThread().setContextClassLoader(ccl);
	}
	
	
	public TasShare getTasSharedObject()
	{
		return this.sharedObject;
				
	}
	
	
	public void setUselessSession(UselessSession uselessSessionMapObject)
	{
		this.uselessSessionMapObject = uselessSessionMapObject;
	}
	
	
	public UselessSession getUselessSessionStore()
	{
		return this.uselessSessionMapObject;
	}
	
	/**
	 * <transport
     *	clusterName = "MyCluster"
     * machineId = "LinuxServer01"
     * rackId = "Rack01"
     * siteId = "US-WestCoast" />
	 * machineId - this is probably the most useful, to disambiguate between multiple JVM instances on the same node, or even multiple virtual hosts on the same physical host.
	 * rackId - in larger clusters with nodes occupying more than a single rack, this setting would help prevent backups being stored on the same rack.
	 * siteId - to differentiate between nodes in different data centres replicating to each other. 
	 */
	private void initDataGrid() {
		if(tcpAppConfig.getClusterConfig().isEnable()) {

			cacheManager = new DefaultCacheManager(new GlobalConfigurationBuilder().clusteredDefault().transport()
								.clusterName(tcpAppConfig.getClusterConfig().getName())
								.machineId(tcpAppConfig.getClusterConfig().getMachineId())
								.rackId(tcpAppConfig.getClusterConfig().getRackId())
								.siteId(tcpAppConfig.getClusterConfig().getSiteId())
							.build());
			
			Configuration sessionCacheConfig = new ConfigurationBuilder().clustering().cacheMode(CacheMode.DIST_ASYNC)
												.async().l1()
												.hash().numOwners(tcpAppConfig.getClusterConfig().getNumOwners())
												.eviction().maxEntries(-1).strategy(EvictionStrategy.LIRS).expiration()
												.maxIdle(tcpAppConfig.getSessionConfig().getTimeout())
												.wakeUpInterval(1000L)
												.build();

			cacheManager.defineConfiguration(tcpAppConfig.getName() + ".session", sessionCacheConfig);
			cacheManager.getCache(tcpAppConfig.getName() + ".session").addListener(new SessionCacheListener());
			cacheManager.startCaches(tcpAppConfig.getName() + ".session");
		} else {

			cacheManager = new DefaultCacheManager(new GlobalConfigurationBuilder().nonClusteredDefault().globalJmxStatistics()
							.cacheManagerName(tcpAppConfig.getName() + ".session").transport()
							.build());
			Configuration sessionCacheConfig = new ConfigurationBuilder().clustering().cacheMode(CacheMode.LOCAL)
						.eviction().maxEntries(-1) // 
						.expiration()
						.wakeUpInterval(1000L) // 
						.maxIdle(tcpAppConfig.getSessionConfig().getTimeout())
						.build();
	
			cacheManager.defineConfiguration(tcpAppConfig.getName() + ".session", sessionCacheConfig);
			cacheManager.getCache(tcpAppConfig.getName() + ".session").addListener(new SessionCacheListener());
			cacheManager.startCaches(tcpAppConfig.getName() + ".session");
		}
		
	}
	
	private void loadProcotolConfig() {
		try {
			protocolConfig = new XMLConfiguration(tcpAppConfig.getBase() + "/conf/protocol.xml");
			protocolConfig.setExpressionEngine(new XPathExpressionEngine());
		} catch (ConfigurationException e) {
			throw new TasException(e);
		}
	}
	
	private void loadTcpApplicationContext() {
		String config = "file:" + tcpAppConfig.getBase() + "/conf/tcp.xml";
		applicationContext = new ClassPathXmlApplicationContext(new String[]{config}, false);
		applicationContext.setClassLoader(tcpAppClassLoader);
		applicationContext.refresh();
		
		
		String sharedObjectFlag = StringUtils.defaultIfEmpty(System.getProperty("mas.tas.shared.object"), "false");
		if(sharedObjectFlag.equals("true"))	putTasSharedObject();
		
		
	}
	
	private void putTasSharedObject()
	{
		try{
			Map<String,TasAppShare> sharedObjectMap = applicationContext.getBeansOfType(TasAppShare.class);
			
			for(String key: sharedObjectMap.keySet())
			{
				sharedObject.putIfEmpty(key, sharedObjectMap.get(key));
			}
			
		}catch(NoSuchBeanDefinitionException e)
		{
			//ignore exception
		}
		
		
	}
	
	//jmx
	public MBeanServer getmBeanServer() {
		return mBeanServer;
	}
	
	public Cache<Object, Object> getSessionCache() {
		return cacheManager.getCache(tcpAppConfig.getName()+".session");
	}
	
	public Cache<Object, Object> getPushCache() {
		return cacheManager.getCache(tcpAppConfig.getName()+".push");
	}
	
	public XMLConfiguration getProtocolConfig() {
		return protocolConfig;
	}
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	public void setAcceptor(NioSocketAcceptor acceptor) {
		this.acceptor = acceptor;
	}
	
	public NioSocketAcceptor getAcceptor() {
		return this.acceptor;
	}

	
	public TcpAppConfig getTcpAppConfig() {
		return tcpAppConfig;
	}
	
	public ClassLoader getTcpAppClassLoader() {
		return tcpAppClassLoader;
	}
	
	private URLClassLoader createTcpAppClassLoader() {
		String tcpAppLibPath = tcpAppConfig.getBase() + "/lib/";
		String tcpAppClassPath = tcpAppConfig.getBase() + "/classes/";
		String tcpAppConfPath = tcpAppConfig.getBase() + "/conf/";
		
		Collection<File> jarList = FileUtils.listFiles(new File(tcpAppLibPath), new String[] { "jar" }, false);
		
		URL urls[] = new URL[jarList.size() + 2];
		
		int i = 0;
		
		try {
			urls[i] = new File(tcpAppClassPath).toURI().toURL();
			urls[++i] = new File(tcpAppConfPath).toURI().toURL();
			i++;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		for (File file : jarList) {
			try {
				urls[i] = file.toURI().toURL();
				System.out.println(file.getCanonicalPath());
			} catch (MalformedURLException e) {
				continue;
			} catch (IOException e) {
				continue;
			} finally {
				i++;
			}
		}
		
		return new URLClassLoader(urls, tasClassLoader);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void startTcpApp() {
		try {
			Class startupCls = tcpAppClassLoader.loadClass("com.tionsoft.mas.tas.TcpApp");
			Constructor constuctor = startupCls.getConstructor(TasContext.class, NioSocketAcceptor.class); 
			Object startupObj = constuctor.newInstance(this, acceptor);
			Method method = startupCls.getMethod("bind"); 
			method.invoke(startupObj, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Listener (sync = false)
	public class SessionCacheListener {
		
		@CacheEntryModified
		public void handleEntryModified(CacheEntryModifiedEvent<Long, String> event) {
			LOGGER.debug("Session Entry modified.  Details = " + event.getKey() + " ... " + event);
		}
	}
	
	@Listener (sync = false)
	public class PushCacheListener {
		
		@CacheEntryModified
		public void handleEntryModified(CacheEntryModifiedEvent<Object, Object> event) {
			if(event.getValue() != null && !event.isOriginLocal() && !event.isPre()) {
				if(event.getKey() instanceof String) {
					String targetClientKey = (String)event.getKey();
					if(event.getValue() instanceof TasResponse) {
						@SuppressWarnings("unchecked")
						TasResponse pushItem = (TasResponse)event.getValue();
						LOGGER.debug("Push Entry modified.  Details = " + event.getValue() + " ... " + event);
						PushService pushService = new PushService(TasContext.this);
						pushService.pushItemToLocalOnly(targetClientKey, pushItem);
//						IPushService pushService = new InternalPushService(TasContext.this.getAcceptor().getManagedSessions(), targetClientKey);
//						pushService.push(pushItem);
					} else if(event.getValue() instanceof String) {
						String clientKey = (String)event.getValue();
						LOGGER.debug("Push Entry modified.  Details = " + event.getValue() + " ... " + event);
						removeLocalPushSession(clientKey);
					}
				}
			}
		}
		
		private void removeLocalPushSession(String targetClientKey) {
			Collection<IoSession> ioSessions =  TasContext.this.getAcceptor().getManagedSessions().values();
			
			for(IoSession ioSession : ioSessions) {
				if(ioSession.getAttribute(PushService.CLIENT_KEY) != null) {
					String clientKey = (String)ioSession.getAttribute(PushService.CLIENT_KEY);
					if(clientKey.equalsIgnoreCase(targetClientKey)) {
						ioSession.close(true);
						LOGGER.debug("Duplicated Clustered Push Session is closed(" + targetClientKey +")");
					}
				}
			}
			
		}
	}
	
}