package com.tionsoft.mas.tas.filter.background;

import java.util.HashMap;
import java.util.Map;

import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.license.db.servicelog.ServiceLogDAO;
import com.tionsoft.mas.tas.taslet.TasMessage;
import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasMessage.MessageDirection;
import com.tionsoft.platform.exception.PlatformException;
import com.tionsoft.platform.filter.Filter;
import com.tionsoft.platform.filter.FilterChain;
import com.tionsoft.platform.utils.DateUtil;

public class TasBackgroundDBOneRowLoggingFilter implements Filter {
	
	private ServiceLogDAO serviceLog;
	
	/**
	 * set service log db dao instance
	 * @param serviceLog
	 */
	public void setServiceLog(ServiceLogDAO serviceLog) {
		this.serviceLog = serviceLog;
	}

	@SuppressWarnings("static-access")
	@Override
	public void doFilter(Object message, FilterChain chain) throws PlatformException {
		
		TasMessage tMessage = (TasMessage) message;
		
		TasRequest tasRequest = tMessage.getTasRequest();
		MessageDirection direction = tMessage.getMessageDirection();
		String inoutType ="RR";
		
		if(direction.equals(MessageDirection.SENT)){
			inoutType ="RR";
		}else if(direction.equals(MessageDirection.RECEIVED))
		{
			inoutType ="SS";
		}
		printPlatformHeader(tasRequest.getPlatformHeader(),inoutType,direction);
		
		chain.doFilter(tMessage);
		
	}
	
	private void printPlatformHeader(PlatformHeader platformHeader,String inoutType,MessageDirection direction) {
		
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
			log(phv,  appId ,msId, sessionId,  tranId, serviceId, imei, msisdn, bodyType, statusCode, headerLength, bodyLength, inoutType,direction);
			
		}else if(phv.equals("PHV102") || phv.equals("PHV103")) {
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
			log(phv,  appId ,msId, sessionId,  tranId, serviceId, imei, msisdn, wifiMac, modelNo, ispName, osType, osVer, uuid, bodyType,statusCode, headerLength, bodyLength, inoutType,direction);

		}else{////PHV100
			log(phv,  appId ,msId, sessionId,  bodyType, statusCode, headerLength, bodyLength, inoutType,direction);
		}	
		

	}
	
	//PHV100
    public void log(String phv,  String appId ,String msId, long sessionId, short bodyType, short statusCode, int headerLength, int bodyLength, String inoutType,MessageDirection direction){
    	log(phv, appId, msId, sessionId, 0, 0, "", "", bodyType, statusCode, headerLength, bodyLength, inoutType,direction);
    }

    //PHV101
    public void log(String phv,  String appId ,String msId, long sessionId,  long tranId, 
    		int serviceId, String imei, String msisdn, short bodyType, short statusCode, int headerLength, int bodyLength, String inoutType,MessageDirection direction){
    	log(phv, appId, msId, sessionId, tranId, serviceId, imei, msisdn,"","","","","","", bodyType, statusCode, headerLength, bodyLength, inoutType,direction);
    }
	
	
	private void log(String phv, String appId, String msId, long sessionId,
					long tranId, int serviceId, String imei, String msisdn,
					String wifiMac, String modelNo, String ispName, String osType,
					String osVer, String uuid, short bodyType, short statusCode,
					int headerLength, int bodyLength, String inoutType,MessageDirection direction) 
	{

		Map<String, Object> args = new HashMap<String, Object>();
		args.put("SVCIDNFR", serviceId);
		args.put("REGIYHS", DateUtil.getCurrentTimeMilli());
		args.put("PHV", phv);
		args.put("SESSION_ID", sessionId);
		args.put("TRANDSTCD", inoutType);
		args.put("TRANSACTIONID", tranId);
		args.put("APPIDNFR", appId);
		args.put("MSGIDNFR", msId);
		args.put("IMEI", imei);
		args.put("WIFI", wifiMac);
		args.put("MSISDN", msisdn);
		args.put("MODEL_NO", modelNo);
		args.put("ISP_NAME", ispName);
		args.put("OS_TYPE", osType);
		args.put("OS_VERSION", osVer);
		args.put("UUID", uuid);
		args.put("BODY_TYPE", bodyType);
		args.put("STATUS_CODE", statusCode);
		args.put("HEADER_LENGTH", headerLength);
		args.put("BODY_LENGTH", bodyLength);
		
		if(direction.equals(MessageDirection.SENT) ){

			serviceLog.update(args);
			
		}else if(direction.equals(MessageDirection.RECEIVED))
		{
			serviceLog.insert(args);
		}
			
		
	}
	
	

}
