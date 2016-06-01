package com.tionsoft.mas.tas.filter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.MDC;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.taslet.TasMessage;

public class TasDBLoggingFilter extends IoFilterAdapter {
	private Logger LOGGER  = LoggerFactory.getLogger("tas_db");
	
	@Override
	public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
//		System.out.println("#############################TasDBLoggingFilter exceptionCaught##################");
		session.setAttribute("LOGGING_EXCEPTION_FLAG", true);	

		nextFilter.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
//		System.out.println("#############################TasDBLoggingFilter messageReceived##################");
		
		String deviceId = "";
		TasMessage tasMessage = (TasMessage)message;
		
		deviceId = tasMessage.getTasRequest().getBody("DEVICE_ID", String.class);
		
		session.setAttributeIfAbsent("DEVICE_ID", deviceId);
		printPlatformHeader(tasMessage.getTasRequest().getPlatformHeader(),"SS",deviceId);
		
		nextFilter.messageReceived(session, message);
	}

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		
		String deviceId = "";
		
		deviceId = (String) session.getAttribute("DEVICE_ID");
//		System.out.println("XX : " + writeRequest.getMessage());
		// exceptionCaught 아니면 로그 처리 (phv101 처리후 phv100 처리하면 (writeRequest.getMessage() instanceof TasMessage )에서 true 가 리턴함
		if(writeRequest.getMessage() instanceof TasMessage) {
//			System.out.println(":#############################TasDBLoggingFilter messageSent 1##################:");
			TasMessage tasMessage = (TasMessage)writeRequest.getMessage();
			
			String applicationId = tasMessage.getTasResponse().getPlatformHeader().getValue(PlatformHeader.APPLICATION_ID, String.class);
			String messageId = tasMessage.getTasResponse().getPlatformHeader().getValue(PlatformHeader.MESSAGE_ID, String.class);

			short statusCode = tasMessage.getTasResponse().getPlatformHeader().getValue(PlatformHeader.STATUS_CODE, Short.class);
			if(statusCode > 0){ //상태 코드가 에러 이면 로그을 안찍는다. 
				return;
			}
			
//			System.out.println(":#############################TasDBLoggingFilter messageSent 3##################:");
			
			if(!applicationId.equals(PlatformHeader.PLATFORM_APPLICATION_ID) && !messageId.equals(PlatformHeader.KEEPALIVE_MESSAGE_ID)) {
				printPlatformHeader(tasMessage.getTasResponse().getPlatformHeader(),"RR",deviceId);
				
			}
		} else {//exception 일때 처리을 위해서
//			System.out.println(":#############################TasDBLoggingFilter messageSent 2##################:");
			boolean loggingExceptionFlage = false;
			try {
				loggingExceptionFlage = (Boolean)session.getAttribute("LOGGING_EXCEPTION_FLAG");	
	        } catch(Exception e) {
	        	loggingExceptionFlage = false;
	        }
			
//			System.out.println(":#############################TasDBLoggingFilter messageSent 2##################:"+loggingExceptionFlage);
			
			if(loggingExceptionFlage) {
				TasMessage tasMessage = (TasMessage)session.getAttribute("TAS_MESSAGE");
				printPlatformHeader(tasMessage.getTasResponse().getPlatformHeader(),"RR",deviceId);
				session.removeAttribute("LOGGING_EXCEPTION_FLAG");	
			}

		}

		nextFilter.messageSent(session, writeRequest);
	}
	
	private void printPlatformHeader(PlatformHeader platformHeader,String inoutType,String deviceId) {
		String phv = platformHeader.getValue(PlatformHeader.VERSION, String.class);
		String appId = platformHeader.getValue(PlatformHeader.APPLICATION_ID, String.class);
		String msId = platformHeader.getValue(PlatformHeader.MESSAGE_ID, String.class);
		long  sessionId =  platformHeader.getValue(PlatformHeader.SESSION_ID, Long.class);
		long tranId = 0;
		int serviceId = 0;
		String imei = "";
		String msisdn = "";
		String wifiMac = "";
		String modelNo = ""; 
		
		String ispName = "";
		String osType = "";
		String osVer = "";
		String uuid = "";
		short bodyType = platformHeader.getValue(PlatformHeader.BODY_TYPE, Short.class);
		short statusCode = platformHeader.getValue(PlatformHeader.STATUS_CODE, Short.class);
		int headerLength = platformHeader.getValue(PlatformHeader.HEADER_LENGTH, Integer.class);
		int bodyLength = platformHeader.getValue(PlatformHeader.BODY_LENGTH, Integer.class);

		if(phv.equals("PHV101")) {
			tranId =  platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class);
			serviceId = platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class);
			imei = platformHeader.getValue(PlatformHeader.IMEI, String.class);
			msisdn = platformHeader.getValue(PlatformHeader.MSISDN, String.class);
			//PHV101
			log(phv,  appId ,msId, sessionId,  tranId, serviceId, imei, msisdn, bodyType, statusCode, headerLength, bodyLength, inoutType,deviceId);
			
		}else if(phv.equals("PHV102") || phv.equals("PHV103") || phv.equals("PHV105")) {
			tranId =  platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class);
			serviceId = platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class);
			imei = platformHeader.getValue(PlatformHeader.IMEI, String.class);
			wifiMac = platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class);
			msisdn = platformHeader.getValue(PlatformHeader.MSISDN, String.class);
			modelNo = platformHeader.getValue(PlatformHeader.MODEL_NO, String.class);
			ispName = platformHeader.getValue(PlatformHeader.ISP_NAME, String.class);
			osType = platformHeader.getValue(PlatformHeader.OS_TYPE, String.class);
			osVer = platformHeader.getValue(PlatformHeader.OS_VERSION, String.class);
			uuid = platformHeader.getValue(PlatformHeader.UUID, String.class);
			//PHV102
			log(phv,  appId ,msId, sessionId,  tranId, serviceId, imei, msisdn, wifiMac, modelNo, ispName, osType, osVer, uuid, bodyType,statusCode, headerLength, bodyLength, inoutType,deviceId);

		}else{////PHV100
			log(phv,  appId ,msId, sessionId,  bodyType, statusCode, headerLength, bodyLength, inoutType,deviceId);
		}	
		

	}
	
	//PHV100
    public void log(String phv,  String appId ,String msId, long sessionId, short bodyType, short statusCode, int headerLength, int bodyLength, String inoutType,String deviceId){
    	log(phv, appId, msId, sessionId, 0, 0, "", "", bodyType, statusCode, headerLength, bodyLength, inoutType,deviceId);
    }

    //PHV101
    public void log(String phv,  String appId ,String msId, long sessionId,  long tranId, 
    		int serviceId, String imei, String msisdn, short bodyType, short statusCode, int headerLength, int bodyLength, String inoutType,String deviceId){
    	log(phv, appId, msId, sessionId, tranId, serviceId, imei, msisdn,"","","","","","", bodyType, statusCode, headerLength, bodyLength, inoutType,deviceId);
    }
	
    //PHV102, 
    public void log(String phv,  String appId ,String msId, long sessionId,  long tranId, 
    		int serviceId, String imei, String msisdn, String wifiMac, String modelNo, 
    		String ispName, String osType, String osVer, String uuid, short bodyType,
    		short statusCode, int headerLength, int bodyLength, String inoutType,String deviceId)
    {
		
    	Calendar calendar = Calendar.getInstance();
    	SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    	String regDate = dateformat.format(calendar.getTime());
    	
//     	System.out.println("TasDBLoggingFilter INOUTTYPE:"+inoutType);
//     	System.out.println("TasDBLoggingFilter REGDATE:"+regDate);
//		System.out.println("TasDBLoggingFilter PHV:"+phv);
//		System.out.println("TasDBLoggingFilter APPLICATION_ID:"+ appId );
//		System.out.println("TasDBLoggingFilter MESSAGE_ID:"+ msId );
//		System.out.println("TasDBLoggingFilter SESSION_ID:"+ sessionId );
//		System.out.println("TasDBLoggingFilter TRANSACTION_ID:"+ tranId );
//		System.out.println("TasDBLoggingFilter SERVICE_ID:"+ serviceId );
//		System.out.println("TasDBLoggingFilter IMEI:"+ imei );
//		System.out.println("TasDBLoggingFilter MSISDN:"+ msisdn );
//		System.out.println("TasDBLoggingFilter WIFI_MAC:"+ wifiMac );
//		System.out.println("TasDBLoggingFilter MODEL_NO:"+ modelNo );
//		System.out.println("TasDBLoggingFilter ISP_NAME:"+ ispName );
//		System.out.println("TasDBLoggingFilter OS_TYPE:"+ osType );
//		System.out.println("TasDBLoggingFilter OS_VERSION:"+ osVer );
//		System.out.println("TasDBLoggingFilter UUID:"+ uuid );
//		System.out.println("TasDBLoggingFilter BODY_TYPE:"+ bodyType );
//		System.out.println("TasDBLoggingFilter STATUS_CODE:"+ statusCode );
//		System.out.println("TasDBLoggingFilter HEADER_LENGTH:"+ headerLength );
//		System.out.println("TasDBLoggingFilter BODY_LENGTH:"+ bodyLength );
    	
    	MDC.put("INOUTTYPE",inoutType);
    	MDC.put("REGDATE",regDate);
		MDC.put("PHV",phv);
		MDC.put("APPLICATION_ID", appId );
		MDC.put("MESSAGE_ID", msId );
		MDC.put("SESSION_ID", sessionId );
		MDC.put("TRANSACTION_ID", tranId );
		MDC.put("SERVICE_ID", serviceId );
		MDC.put("IMEI", imei );
		MDC.put("MSISDN", msisdn );
		MDC.put("WIFI_MAC", wifiMac );
		MDC.put("MODEL_NO", modelNo );
		MDC.put("ISP_NAME", ispName );
		MDC.put("OS_TYPE", osType );
		MDC.put("OS_VERSION", osVer );
		MDC.put("UUID", uuid );
		MDC.put("BODY_TYPE", bodyType );
		MDC.put("STATUS_CODE", statusCode );
		MDC.put("HEADER_LENGTH", headerLength );
		MDC.put("BODY_LENGTH", bodyLength );
		MDC.put("DEVICE_ID", deviceId );
		
		LOGGER.info("");
		MDC.remove("REGDATE");
		MDC.remove("PHV");
		MDC.remove("APPLICATION_ID");
		MDC.remove("MESSAGE_ID");
		MDC.remove("SESSION_ID");
		MDC.remove("TRANSACTION_ID");
		MDC.remove("SERVICE_ID");
		MDC.remove("IMEI");
		MDC.remove("MSISDN");
		MDC.remove("WIFI_MAC");
		MDC.remove("MODEL_NO");
		MDC.remove("ISP_NAME");
		MDC.remove("OS_TYPE");
		MDC.remove("OS_VERSION");
		MDC.remove("UUID");
		MDC.remove("BODY_TYPE");
		MDC.remove("STATUS_CODE");
		MDC.remove("HEADER_LENGTH");
		MDC.remove("BODY_LENGTH");
		MDC.remove("DEVICE_ID");

    }


	
}
