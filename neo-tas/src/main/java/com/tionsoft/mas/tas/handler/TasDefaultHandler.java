package com.tionsoft.mas.tas.handler;

import com.tionsoft.mas.tas.TasContext;
import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.exception.ErrorType;
import com.tionsoft.mas.tas.exception.TasException;
import com.tionsoft.mas.tas.protocol.codec.TasCodecFactory;
import com.tionsoft.mas.tas.protocol.codec.encoder.ByteBufferEncoder;
import com.tionsoft.mas.tas.pushlet.InterestProcessor;
import com.tionsoft.mas.tas.pushlet.InterestProcessorFactory;
import com.tionsoft.mas.tas.pushlet.PushService;
import com.tionsoft.mas.tas.taslet.TasMessage;
import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import com.tionsoft.platform.listener.TasSessionClosedListener;
import com.tionsoft.platform.listener.TasSessionIdleListener;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.DefaultFileRegion;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderException;
import org.apache.mina.handler.chain.IoHandlerChain;
import org.apache.mina.integration.jmx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.net.ssl.SSLHandshakeException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class TasDefaultHandler extends TasHandler {
	
	private Logger LOGGER;
	private TasContext tasContext;
	private IoHandlerChain handlerChain;
	private TasSessionIdleListener idleListener;
	private TasSessionClosedListener closedListener;

	//jmx
	private MBeanServer mBeanServer;

	
	
	public void setTasSessionIdleListener(TasSessionIdleListener idleListener)
	{
		this.idleListener = idleListener;
	}
	
	public void setTasSessionClosedListener(TasSessionClosedListener closedListener)
	{
		this.closedListener = closedListener;
	}
	
	@Override
	public void setTasContext(TasContext tasContext) {
		this.LOGGER = LoggerFactory.getLogger(tasContext.getTcpAppConfig().getName());
		this.tasContext = tasContext;
		this.mBeanServer = tasContext.getmBeanServer();
	}
	
	@Override
	public void setHandlerChain(IoHandlerChain handlerChain) {
		this.handlerChain = handlerChain;
	}
	
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		if(mBeanServer != null) {  //jmx
	    	LOGGER.debug("TasDefaultHandler jmx sessionCreated registerMBean start");
	    	
	        // create a session MBean in order to load into the MBeanServer and allow
	        // this session to be managed by the JMX subsystem.
	        IoSessionMBean sessionMBean = new IoSessionMBean( session );
	        
	        // create a JMX ObjectName.  This has to be in a specific format.  
	        ObjectName sessionName = new ObjectName( session.getClass().getPackage().getName() + 
	            ":type=session,name=" + tasContext.getTcpAppConfig().getName() + "-" + session.getId());
	        
//	        LOGGER.debug("TasDefaultHandler sessionCreated  session.getClass().getPackage().getName():"+session.getClass().getPackage().getName());
//	        LOGGER.debug("TasDefaultHandler sessionCreated  session.getClass().getSimpleName():"+session.getClass().getSimpleName());
//	        LOGGER.debug("TasDefaultHandler sessionCreated  session.getId():"+session.getId());
	        
	        // register the bean on the MBeanServer.  Without this line, no JMX will happen for
	        // this session
	        mBeanServer.registerMBean( sessionMBean, sessionName );
	        LOGGER.debug("TasDefaultHandler jmx sessionCreated registerMBean end");
		}
	}

	@Override
	public void sessionOpened(IoSession session) {

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
//		LOGGER.debug("***************************sessionClosed***************************");
		closeTemporaryFileIfRequired(session);
		
		if(mBeanServer != null) {  //jmx
			LOGGER.debug("TasDefaultHandler jmx sessionClosed unregisterMBean start");
		       
	        // create a JMX ObjectName.  This has to be in a specific format.  
	        ObjectName sessionName = new ObjectName( session.getClass().getPackage().getName() + 
	            ":type=session,name=" + tasContext.getTcpAppConfig().getName() + "-" + session.getId());
			
	        mBeanServer.unregisterMBean( sessionName );
	        LOGGER.debug("TasDefaultHandler jmx sessionClosed unregisterMBean end");
		}
		
		if(this.closedListener != null)
		{
			if(LOGGER.isDebugEnabled())
			{
				LOGGER.debug("TasDefaultHandler sessionClosed's closedlistener is called");
			}
			closedListener.execute(session);
			
		}

	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		LOGGER.debug("***************************sessionIdle***************************");
		
		if(this.idleListener != null)
		{
			if(LOGGER.isDebugEnabled())
			{
				LOGGER.debug("TasDefaultHandler sessionIdle's idlelistener is called");
			}
			idleListener.execute(session);
			
		}
		
		session.close(true);
		if(mBeanServer != null) {  //jmx
			LOGGER.debug("TasDefaultHandler jmx sessionIdle unregisterMBean start");
		       
	        // create a JMX ObjectName.  This has to be in a specific format.  
	        ObjectName sessionName = new ObjectName( session.getClass().getPackage().getName() + 
	            ":type=session,name=" + tasContext.getTcpAppConfig().getName() + "-" + session.getId());
			
	        mBeanServer.unregisterMBean( sessionName );
	        LOGGER.debug("TasDefaultHandler jmx sessionIdle unregisterMBean start");
		}
		
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		TasMessage tasMessage = (TasMessage)message;
		TasRequest tasRequest = tasMessage.getTasRequest();
		String version = tasRequest.getPlatformHeader().getValue(PlatformHeader.VERSION, String.class);
		String appId = tasRequest.getPlatformHeader().getValue(PlatformHeader.APPLICATION_ID, String.class);
		String msgId = tasRequest.getPlatformHeader().getValue(PlatformHeader.MESSAGE_ID, String.class);
		
		// PS,PC 하위 버전 호환을 위한 패치[시작]
		session.setAttribute(PlatformHeader.VERSION, version);
		if(appId.equals("0000") && msgId.equals("000000000")) {
			appId = PlatformHeader.PLATFORM_APPLICATION_ID;
			msgId = PlatformHeader.KEEPALIVE_MESSAGE_ID;
		} else if (appId.equals("PC02") && msgId.equals("M00000007")) { // push_ack
			appId = PlatformHeader.PLATFORM_APPLICATION_ID;
			msgId = PlatformHeader.PUSH_ACK_MESSAGE_ID; 
		}
		// PS,PC 하위 버전 호환을 위한 패치[끝]
		
		if((PlatformHeader.PLATFORM_APPLICATION_ID.equals(appId) && (PlatformHeader.KEEPALIVE_MESSAGE_ID.equals(msgId)))) {
			session.write(message);
		} else if((appId.equals(PlatformHeader.PLATFORM_APPLICATION_ID) && (msgId.equals(PlatformHeader.PUSH_ACK_MESSAGE_ID)))) {
			try {
				InterestProcessor processor = InterestProcessorFactory.getProcessor();
				processor.removeInterest(tasMessage, session);
			} catch (Exception e) {
				
			}
			handlerChain.execute(null, session, tasMessage);
		} else {
			handlerChain.execute(null, session, tasMessage);
			session.write(message);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		int sentMessageCount = 0;
		if(session.getAttribute(ByteBufferEncoder.SENT_MESSAGE_COUNT) != null) {
			sentMessageCount = (Integer)session.getAttribute(ByteBufferEncoder.SENT_MESSAGE_COUNT);
		}

		if(message instanceof IoBuffer) {
			IoBuffer buffer = (IoBuffer)message;
			session.setAttribute(ByteBufferEncoder.SENT_MESSAGE_COUNT, sentMessageCount + buffer.limit());
		} else if (message instanceof DefaultFileRegion) {
			DefaultFileRegion fileRegion = (DefaultFileRegion)message;
			
			FileChannel fc = fileRegion.getFileChannel();
			if(fc.isOpen()){
				fc.close();
			}
			
			session.setAttribute(ByteBufferEncoder.SENT_MESSAGE_COUNT, sentMessageCount + (int)fileRegion.getWrittenBytes());
			
			//File 파일 전송 완료후 전송 파일 삭제
			if(session.getAttribute(ByteBufferEncoder.DEL_FILEPATH) != null) {
				Object delType= session.getAttribute(ByteBufferEncoder.DEL_FILEPATH);
				
				if(delType instanceof String){
					String strDelFilePath = (String)session.getAttribute(ByteBufferEncoder.DEL_FILEPATH);
					deleteSentFile(strDelFilePath,fileRegion.getFilename());
				}else if(delType instanceof ArrayList<?>){
					ArrayList<String> delFilePathList = (ArrayList<String>)session.getAttribute(ByteBufferEncoder.DEL_FILEPATH);
					for(String delFilePath : delFilePathList){
						deleteSentFile(delFilePath,fileRegion.getFilename());
					}
				}
			}
			
		//mina 에서	TasMessage을 줌
		} else if (message instanceof TasMessage) {
			TasMessage tasMessage = (TasMessage)message;
			TasResponse tasResponse = tasMessage.getTasResponse();
			
			try {
				String appId = tasResponse.getPlatformHeader().getValue(PlatformHeader.APPLICATION_ID, String.class);
				String msgId = tasResponse.getPlatformHeader().getValue(PlatformHeader.MESSAGE_ID, String.class);
				
				// PS,PC 하위 버전 호환을 위한 패치[시작]
				if (appId.equals("PC02") && msgId.equals("M00000002")) { // push
					appId = PlatformHeader.PLATFORM_APPLICATION_ID;
					msgId = PlatformHeader.PUSH_MESSAGE_ID;
				}
				// PS,PC 하위 버전 호환을 위한 패치[끝]
				
//				if( appId.equals(PlatformHeader.PLATFORM_APPLICATION_ID) && msgId.equals(PlatformHeader.PUSH_MESSAGE_ID) ) {
				//push 전문 추가("M00000003"; // Push 을 위한값)로 수정 김민수 차석
				if( appId.equals(PlatformHeader.PLATFORM_APPLICATION_ID) && (msgId.equals(PlatformHeader.PUSH_MESSAGE_ID) || msgId.equals(PlatformHeader.PUSH_MESSAGE_ID_FOR_PI))) {
					try {
						InterestProcessor processor = InterestProcessorFactory.getProcessor();
						processor.addInterest(tasMessage, session);
					} catch (Exception e) {
						
					}
					LOGGER.debug("Message is pushed [" + session.getAttribute(PushService.CLIENT_KEY) + "]");
				}
			
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
		
		sentMessageCount = (Integer)session.getAttribute(ByteBufferEncoder.SENT_MESSAGE_COUNT);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		LOGGER.debug("***************************TasDefaultHandler exceptionCaught cause:"+cause);
		closeTemporaryFileIfRequired(session);
		
		if(!(cause instanceof IOException)) {
			TasMessage errorMessage = null;
			String tas_error_msg = ""; //PHV103 에서 추가 (에러 메세지 저장)
			if(session.getAttribute(TasCodecFactory.PLATFORMHEADER_ERROR_MESSAGE) != null) { // Decoding 까지 성공했을 경우의 Error 처리, 이경우 TasMessage  가 만들어져 있으므로 처리가 쉽다.
				PlatformHeader platformHeader = (PlatformHeader)session.getAttribute(TasCodecFactory.PLATFORMHEADER_ERROR_MESSAGE);
				String version = platformHeader.getValue(PlatformHeader.VERSION, String.class);
				
				if( version != null && version.equals("PHV103")) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					PrintStream pinrtStream = new PrintStream(out);  
					cause.printStackTrace(pinrtStream);
					tas_error_msg = out.toString();
				}

				short errorCode = ErrorType.ERROR_CODE_DEFAULT;
				if (cause instanceof TasException) {
					cause.printStackTrace();
					
					TasException tasException = (TasException)cause;
					ErrorType errorType = tasException.getErrorType();
					if(errorType != null){
						errorCode = errorType.getCode();
						if(errorType.getPlatformHeader() != null){
							platformHeader = errorType.getPlatformHeader();
						}
					}
					
				} else if (cause instanceof ProtocolEncoderException) { // TasEncoder 의 encode 메소드에서 TasException 으로 throw 한 TasException 처리
					cause.printStackTrace();
					
					ProtocolEncoderException exception = (ProtocolEncoderException)cause;
					ErrorType errorType = ((TasException)exception.getCause()).getErrorType();
					if(errorType != null){
						errorCode = errorType.getCode();
						if(errorType.getPlatformHeader() != null){
							platformHeader = errorType.getPlatformHeader();
						}
					}
					
				} else if (cause instanceof Exception) {
					cause.printStackTrace();
					
					Exception exception = (Exception)cause;
					if(exception.getCause() instanceof TasException) {
						ErrorType errorType = ((TasException)exception.getCause()).getErrorType();
						if(errorType != null){
							errorCode = errorType.getCode();
							if(errorType.getPlatformHeader() != null){
								platformHeader = errorType.getPlatformHeader();
							}
						}
					} else {
						LOGGER.debug("Skip Response Exception");
						cause.printStackTrace();
					}
				} else {
				}
				
				if( version != null && version.equals("PHV103")) {
					//error message 생성(platformheader 만 생성함)
					TasBean errorHeader = new TasBean();
					errorHeader.setValue("TAS_ERROR_MSG", tas_error_msg);
					
					TasResponse tasResponse = new TasResponse(platformHeader, errorHeader, null);
					TasMessage tasMessage = new TasMessage(null, tasResponse);
					tasMessage.getTasResponse().getPlatformHeader().setValue(PlatformHeader.STATUS_CODE, errorCode);
					errorMessage = tasMessage;
				}else{
					//error message 생성(platformheader 만 생성함)
					TasResponse tasResponse = new TasResponse(platformHeader, null, null);
					TasMessage tasMessage = new TasMessage(null, tasResponse);
					tasMessage.getTasResponse().getPlatformHeader().setValue(PlatformHeader.STATUS_CODE, errorCode);
					errorMessage = tasMessage;
				}
				
			} else { // Decoding 전 or Decoding 중에 에러가 발생했을 경우의 Exception 처리
				// Decoding된 TasMessage 메시지가 존재하지 않으므로 TasRequest 정보는 알 수 없고 오류 전송용 TasResponse 를 생성하여
				// 응답한다. 단, 여러 가지 형태의 PlatformHeader 가 존재하므로 어떤 PlatformHeader 로 응답할지가 문제다.
				

			}
			
			//IOException Type 의 경우  MINA 가 Connection 을 종료해 버리므로 에러코드를 전송할 수 없다.
			if(!(cause instanceof IOException)) {
				session.write(errorMessage);
			}
		}else if(cause instanceof SSLHandshakeException) {// sslFilter 처리시 SSLHandshakeException 는 IOException임 따라서 session.close(true) 처리

			LOGGER.error("SSLHandshakeException session.close()");
			session.close(true);
		}
	}
	
	private final void closeTemporaryFileIfRequired(IoSession session) {
		if(session.getAttribute(TasCodecFactory.TEMP_FILE_CHANNEL) != null) {
			FileChannel fc = (FileChannel)session.getAttribute(TasCodecFactory.TEMP_FILE_CHANNEL);
			try {
				fc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		//exception 시 File 타입 파일으로 설정되어있는 파일 삭제 수정 
		if(session.getAttribute(ByteBufferEncoder.DEL_FILEPATH) != null) {
			Object delType= session.getAttribute(ByteBufferEncoder.DEL_FILEPATH);
			
			if(delType instanceof String){
				String strDelFilePath = (String)session.getAttribute(ByteBufferEncoder.DEL_FILEPATH);
				deleteSentFile( strDelFilePath);
			}else if(delType instanceof ArrayList<?>){
				ArrayList<String> delFilePathList = (ArrayList<String>)session.getAttribute(ByteBufferEncoder.DEL_FILEPATH);
				File reqFileName =null;
				for(String delFilePath : delFilePathList){
					deleteSentFile( delFilePath);
				}

			}


		}
	}
	
	
	//File 파일 전송 완료후 전송 파일 삭제
	private void deleteSentFile(String reqFilePathName, String delFilePathName) {
		//reqFileName :삭제요청한 filename명(iosession 으로 요청한내용)
		//delFileName :실제 삭제해야할 filepath + filename(fileRegion.getFilename())
//		LOGGER.debug("***************************deleteSentFile start***********************************");
//		LOGGER.debug("reqFilePathName :"+reqFilePathName);
//		LOGGER.debug("delFilePathName :"+delFilePathName);
		File reqFileName = new File(reqFilePathName);
		File delFileName = new File(delFilePathName);
//		LOGGER.debug("request FileName:"+reqFileName.getName()+", delete FileName:"+delFileName.getName());
		if(reqFileName.getName().equals(delFileName.getName())){
			boolean ck = delFileName.delete();
			LOGGER.debug("delFileName.delete():" +ck);
		}
	}
	
	//File 파일 전송 완료후 전송 파일 삭제
	private void deleteSentFile( String delFilePathName) {
		//delFileName :실제 삭제해야할 filepath + filename(fileRegion.getFilename())
//		LOGGER.debug("***************************deleteSentFile start***********************************");
//		LOGGER.debug("delFilePathName :"+delFilePathName);
		File delFileName = new File(delFilePathName);
		boolean ck = delFileName.delete();
		LOGGER.debug("delFileName.delete():" +ck);

	}

}