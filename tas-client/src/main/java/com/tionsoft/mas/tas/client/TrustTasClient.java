package com.tionsoft.mas.tas.client;

import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;

import javax.net.ssl.SSLContext;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.ssl.BogusTrustManagerFactory;
import org.apache.mina.filter.ssl.EasyBogusTrustManagerFactory;
import org.apache.mina.filter.ssl.KeyStoreFactory;
import org.apache.mina.filter.ssl.SslContextFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.bean.platform.PlatformHeaderFactory;
import com.tionsoft.mas.tas.exception.*;
import com.tionsoft.mas.tas.client.codec.TasClientCodecFactory;
import com.tionsoft.mas.tas.client.filter.TasLoggingFilter;
import com.tionsoft.mas.tas.client.handler.TasClientHandler;
import com.tionsoft.mas.tas.client.handler.TasEventListener;
import com.tionsoft.mas.tas.client.handler.TasProgressListener;
import com.tionsoft.mas.tas.client.message.TasMessage;
import com.tionsoft.mas.tas.client.message.TasRequest;
import com.tionsoft.mas.tas.client.message.TasResponse;
import com.tionsoft.mas.tas.client.ssl.TasClientSslConfig;

public class TrustTasClient {
	
	private final String ip;
	private final int port;
	private final int idleTime;
	
	private final XMLConfiguration protocolConfig;
	private TasEventListener eventListener = null;
	private TasProgressListener progressListener = null;
	private IoSession session;
	private NioSocketConnector connector;
	private ConnectFuture cf;
	private long connectTimeoutMillis = 0;
	
	private TasClientSslConfig tasClientSslConfig = null;
	private static String isBKS = "JKS";
	private static Provider provider = null;
		
	public TrustTasClient(String ip, int port, int idleTime, XMLConfiguration protocolConfig) {
		this.ip = ip;
		this.port = port;
		this.idleTime = idleTime;
		this.protocolConfig = protocolConfig;
	}
	
	public TrustTasClient(String ip, int port, int idleTime, XMLConfiguration protocolConfig, TasClientSslConfig tasClientSslConfig) {
		isBKS = tasClientSslConfig.getCertType();
		this.ip = ip;
		this.port = port;
		this.idleTime = idleTime;
		this.protocolConfig = protocolConfig;
		this.tasClientSslConfig = tasClientSslConfig;
		loadBKS();
	}
	
	public void setEventListener(TasEventListener eventListener) {
		this.eventListener = eventListener;
	}
	
	public void setProgressListener(TasProgressListener progressListener) {
		this.progressListener = progressListener;
	}
	
	@SuppressWarnings("rawtypes")
	public void loadBKS() {
		if(isBKS.equals("BKS") && provider == null ){
			try {
				Class t = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
				provider = (Provider)t.newInstance();
				Security.addProvider(provider);
	//			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Async 사용시에만 사용, Sync 사용시에는 호출하지 말 것.
	public boolean connect() {
		
		try {
			connector = new NioSocketConnector();
			
			if(connectTimeoutMillis == 0){
				connector.setConnectTimeoutMillis(5*1000); //default 5초
			}else{
				connector.setConnectTimeoutMillis(connectTimeoutMillis);
			}
			
			if(idleTime != -1) {
				connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, idleTime);
			}
			
			connector.getSessionConfig().setTcpNoDelay(true);
			
			TasClientCodecFactory codecFactory = new TasClientCodecFactory(protocolConfig);
			if(progressListener != null) { // 수신 Progress 를 위한 등록
				codecFactory.setProgressListener(progressListener);
			}
			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(codecFactory));
			
			String mode = System.getProperty("mas.tas.client.logging");
			if(mode != null && mode.equals("true")) {
				connector.getFilterChain().addLast("taslogging", new TasLoggingFilter());
			}
			
			if(tasClientSslConfig != null) {
            	SslContextFactory sslContextFactory = new SslContextFactory();
            	sslContextFactory.setProtocol(tasClientSslConfig.getProtocol());
            	sslContextFactory.setKeyManagerFactoryAlgorithm(tasClientSslConfig.getKeyManagerAlgorithm());
            	
            	KeyStoreFactory keyStoreFactory = new KeyStoreFactory();
            	keyStoreFactory.setPassword(tasClientSslConfig.getPasswd());
            	keyStoreFactory.setType(tasClientSslConfig.getCertType());
            	
        		byte[] buf = IOUtils.toByteArray(tasClientSslConfig.getInCertFile());
            	keyStoreFactory.setData(buf);
            	KeyStore keyStore = keyStoreFactory.newInstance();
            	sslContextFactory.setKeyManagerFactoryKeyStore(keyStore);
            	sslContextFactory.setKeyManagerFactoryKeyStorePassword(tasClientSslConfig.getPasswd());
            	
            	EasyBogusTrustManagerFactory bogusTrustManagerFactory = new EasyBogusTrustManagerFactory(keyStore,"SunX509");
            	//BogusTrustManagerFactory bogusTrustManagerFactory = new BogusTrustManagerFactory();
            	sslContextFactory.setTrustManagerFactory(bogusTrustManagerFactory);
                SSLContext sslContext = sslContextFactory.newInstance();
                SslFilter sslFilter = new SslFilter(sslContext);
                sslFilter.setUseClientMode(true);
                connector.getFilterChain().addFirst("sslFilter", sslFilter);
            }
			
			TasClientHandler handler = new TasClientHandler();
			if(eventListener != null) {
				handler.setEventListener(eventListener);
			} 
			
			if(progressListener != null) { // 송신 Progress 를 위한 등록
				handler.setProgressListener(progressListener);
			}
			connector.setHandler(handler);
			
			cf = connector.connect(new InetSocketAddress(ip, port));
			cf.awaitUninterruptibly();
			
			if(!cf.isConnected()) {
				return false;
			}
			
			session = cf.getSession();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public synchronized void sendRequest(TasRequest tasRequest) {
		TasMessage tasMessage = new TasMessage(tasRequest, null);

		if(connector==null) connect();
		
		System.out.println("connection :" +connector+" session :" +session);
		if(session==null || !session.isConnected()){
			connect();
			session.write(tasMessage);
		}else {
			session.write(tasMessage);
		}
		
	}
	
	public TasResponse sendRequestWithReturn(TasRequest tasRequest, long responseTimeout) {
		TasMessage tasMessage = new TasMessage(tasRequest, null);
		
		if(session ==null){
			if(!connect()){
				close();
				ErrorType errorType = new ErrorType(null,ErrorType.ERROR_CODE_TAS_CLIENT_CONNECT, "TasClient Connection Exception");
				throw new TasClientException(errorType,new Throwable("TasClient Connection Exception"));
			}
		}
		session.write(tasMessage);
		session.getConfig().setUseReadOperation(true);
		
		ReadFuture rf =  session.read();
		rf.awaitUninterruptibly(responseTimeout); // if responses does not return within timeout, clients will get null value for messages
		Object obj = rf.getMessage();
		TasResponse response = null;
		
		if(obj !=null && obj instanceof TasMessage){
			TasMessage receiveMessage =(TasMessage) obj;
			response = receiveMessage.getTasResponse();
		}
		session.getConfig().setUseReadOperation(false);
		
		return response;
	}
	
	private PlatformHeader getPlatformHeaderForKeepAliveMessage() {
        //Keep-Alive Message
		PlatformHeader platformHeader = PlatformHeaderFactory.getPlatformHeader("PHV101");
        platformHeader.setValue(PlatformHeader.APPLICATION_ID, "PFAP");
        platformHeader.setValue(PlatformHeader.MESSAGE_ID, "M00000000");
        platformHeader.setValue(PlatformHeader.SESSION_ID,  (long)0L);
        platformHeader.setValue(PlatformHeader.BODY_TYPE, (short) 1);
        platformHeader.setValue(PlatformHeader.STATUS_CODE, (short) 0);
        platformHeader.setValue(PlatformHeader.TRANSACTION_ID, System.currentTimeMillis());
        platformHeader.setValue(PlatformHeader.SERVICE_ID, 0);
		platformHeader.setValue(PlatformHeader.IMEI, "01234567890123456789");
		platformHeader.setValue(PlatformHeader.MSISDN, "01047218603");	
		
		return platformHeader;
	}
	
	public void sendAsyncRequest(TasRequest tasRequest, TasEventListener listener) {
		TasMessage tasMessage = new TasMessage(tasRequest, null);
		setEventListener(listener);
		if(connector==null) connect();
		 this.session.write(tasMessage);
	}
	
	
		 
	
	public boolean sendKeepAliveMessageRequestWithReturn(long responseTimeout) {
		
		boolean isConnected = false;
		TasRequest tasRequest = new TasRequest(getPlatformHeaderForKeepAliveMessage(), new TasBean(), new TasBean());
		TasMessage tasMessage = new TasMessage(tasRequest, null);
		
		if(session ==null){
			if(!connect()){
				close();
				ErrorType errorType = new ErrorType(null,ErrorType.ERROR_CODE_TAS_CLIENT_CONNECT, "TasClient Connection Exception");
				throw new TasClientException(errorType,new Throwable("TasClient Connection Exception"));
			}
		}
		session.write(tasMessage);
		session.getConfig().setUseReadOperation(true);
		
		ReadFuture rf =  session.read();
		rf.awaitUninterruptibly(responseTimeout); // if responses does not return within timeout, clients will get null value for messages
		Object obj = rf.getMessage();
		TasResponse response = null;
		
		if(obj !=null && obj instanceof TasMessage){
			TasMessage receiveMessage =(TasMessage) obj;
			response = receiveMessage.getTasResponse();
			if(response != null) isConnected = true;
		}
		session.getConfig().setUseReadOperation(false);
		
		return isConnected;
	}
	
	public TasResponse sendSyncRequest(TasRequest tasRequest, long responseTimeout) {
		TasMessage tasMessage = new TasMessage(tasRequest, null);
		SyncEventListener listener = new SyncEventListener();
		setEventListener(listener);
		
		if(!connect()){
			close();	
			ErrorType errorType = new ErrorType(null,ErrorType.ERROR_CODE_TAS_CLIENT_CONNECT, "TasClient Connection Exception");
			throw new TasClientException(errorType,new Throwable("TasClient Connection Exception"));
		}
		
		session.write(tasMessage);
		
		TasResponse response = null;
		long startTime = System.currentTimeMillis();
		long elaspedTime;
		while(true) {
			elaspedTime = System.currentTimeMillis() - startTime;
			if(listener.getTasResponse() == null && listener.getTasException() == null) {
					if(responseTimeout != -1L && elaspedTime > responseTimeout) {
						close();
						
						ErrorType errorType = new ErrorType(null,ErrorType.ERROR_CODE_TAS_CLIENT_RESPONSE_TIMEOUT, "TasClient Response Timeout Exception");
						throw new TasClientException(errorType,new Throwable("Response Timeout"));
					} else { 
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							System.out.println("*************************InterruptedException*******************");
							e.printStackTrace();
						}
					}
			} else if (listener.getTasResponse() != null) {
				response = listener.getTasResponse(); 
				break;
			} else if (listener.getTasException() != null) {
				close();
				throw new TasClientException(listener.getTasException()); 

			}
		}
		
		return response;
	}
	
	public boolean isConnectActive() {
		return connector.isActive();
	}
	
	public void setConnectTimeoutMillis(long timeout){
		this.connectTimeoutMillis = timeout;

	}
	
	public void close() {
		if(session != null) {
			if(session.isConnected()) {
				session.close(true);
				connector.dispose(); 
			} else {
				connector.dispose(); 
			}
		}
	}
	
	private class SyncEventListener implements TasEventListener {

		private TasResponse tasResponse = null;
		private Throwable tasException = null;
		
		public void onSessionCreated(IoSession session) {
			
		}

		public void onSessionOpened(IoSession session) {
			
		}

		public void onSessionClosed(IoSession session) {
			
		}

		public void onSessionIdle(IoSession session, IdleStatus status) {
			
		}

		public void onMessageReceived(IoSession session, TasMessage message) {
			this.tasResponse = message.getTasResponse();
			session.close(true);
		}

		public void onMessageSent(IoSession session, TasMessage message) {
			
		}

		
		@Override
		public void onExceptionCaught(IoSession session, Throwable cause) {
			this.tasException = cause;
		}
		
		public TasResponse getTasResponse() {
			return this.tasResponse;
		}
		
		public Throwable getTasException() {
			return this.tasException;
		}
		
	}
}