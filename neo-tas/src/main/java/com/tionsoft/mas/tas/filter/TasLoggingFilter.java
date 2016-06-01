package com.tionsoft.mas.tas.filter;

import java.util.Collection;
import java.util.Set;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.taslet.TasMessage;

public class TasLoggingFilter extends IoFilterAdapter {
	
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public void setLogger(String name) {
		LOGGER = LoggerFactory.getLogger(name);
	}
	
	@Override
	public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
//		cause.printStackTrace();
		
		nextFilter.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		TasMessage tasMessage = (TasMessage)message;
		
		LOGGER.info("===================================================================================================");
		LOGGER.info("##### Client --> Server #####");
		LOGGER.info("===================================================================================================");

		printPlatformHeader(tasMessage.getTasRequest().getPlatformHeader());
		
		if(tasMessage.getTasRequest().getHeader()!= null && tasMessage.getTasRequest().getHeader().getParams().size() > 0) {
			printHeader(tasMessage.getTasRequest().getHeader());
		}
		
		if(tasMessage.getTasRequest().getBody()!= null && tasMessage.getTasRequest().getBody().getParams().size() > 0) {
			printBody(tasMessage.getTasRequest().getBody());
		}
		
		nextFilter.messageReceived(session, message);
	}

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		if(writeRequest.getMessage() instanceof TasMessage) {
			TasMessage tasMessage = (TasMessage)writeRequest.getMessage();
			LOGGER.info("===================================================================================================");
			LOGGER.info("##### Server --> Client #####");
			LOGGER.info("===================================================================================================");
			
			String applicationId = tasMessage.getTasResponse().getPlatformHeader().getValue(PlatformHeader.APPLICATION_ID, String.class);
			String messageId = tasMessage.getTasResponse().getPlatformHeader().getValue(PlatformHeader.MESSAGE_ID, String.class);
			
			if(!applicationId.equals(PlatformHeader.PLATFORM_APPLICATION_ID) && !messageId.equals(PlatformHeader.KEEPALIVE_MESSAGE_ID)) {
				printPlatformHeader(tasMessage.getTasResponse().getPlatformHeader());
				
				if(tasMessage.getTasResponse().getHeader()!= null && tasMessage.getTasResponse().getHeader().getParams().size() > 0) {
					printHeader(tasMessage.getTasResponse().getHeader());
				}
				
				if(tasMessage.getTasResponse().getBody()!= null && tasMessage.getTasResponse().getBody().getParams().size() > 0) {
					printBody(tasMessage.getTasResponse().getBody());
				}
			}
		}

		nextFilter.messageSent(session, writeRequest);
	}
	
	private void printPlatformHeader(PlatformHeader platformHeader) {
		String version = platformHeader.getValue(PlatformHeader.VERSION, String.class);
		
		LOGGER.info("-----------------------------------------------------------------------------------------------------");
		LOGGER.info("[Platform Header]");
		LOGGER.info("-----------------------------------------------------------------------------------------------------");
		
		LOGGER.info("VERSION : [" +version + "]");
		
		LOGGER.info("APPLICATION_ID : [" + platformHeader.getValue(PlatformHeader.APPLICATION_ID, String.class) + "]");
		LOGGER.info("MESSAGE_ID     : [" + platformHeader.getValue(PlatformHeader.MESSAGE_ID, String.class) + "]");
		LOGGER.info("SESSION_ID     : [" + platformHeader.getValue(PlatformHeader.SESSION_ID, Long.class) + "]");
		if(version.equals("PHV101")) {
			LOGGER.info("TRANSACTION_ID : [" + platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class) + "]");
			LOGGER.info("SERVICE_ID     : [" + platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class) + "]");
			LOGGER.info("IMEI           : [" + platformHeader.getValue(PlatformHeader.IMEI, String.class) + "]");
			LOGGER.info("MSISDN         : [" + platformHeader.getValue(PlatformHeader.MSISDN, String.class) + "]");
		}
		
		if(version.equals("PHV102")) {
			LOGGER.info("TRANSACTION_ID : [" + platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class) + "]");
			LOGGER.info("SERVICE_ID     : [" + platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class) + "]");
			LOGGER.info("IMEI           : [" + platformHeader.getValue(PlatformHeader.IMEI, String.class) + "]");
			LOGGER.info("WIFI_MAC       : [" + platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class) + "]");
			LOGGER.info("MSISDN         : [" + platformHeader.getValue(PlatformHeader.MSISDN, String.class) + "]");
			LOGGER.info("MODEL_NO       : [" + platformHeader.getValue(PlatformHeader.MODEL_NO, String.class) + "]");
			LOGGER.info("ISP_NAME       : [" + platformHeader.getValue(PlatformHeader.ISP_NAME, String.class) + "]");
			LOGGER.info("OS_TYPE        : [" + platformHeader.getValue(PlatformHeader.OS_TYPE, String.class) + "]");
			LOGGER.info("OS_VERSION     : [" + platformHeader.getValue(PlatformHeader.OS_VERSION, String.class) + "]");
			LOGGER.info("UUID           : [" + platformHeader.getValue(PlatformHeader.UUID, String.class) + "]");
		}

		if(version.equals("PHV103")) {
			LOGGER.info("TRANSACTION_ID : [" + platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class) + "]");
			LOGGER.info("SERVICE_ID     : [" + platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class) + "]");
			LOGGER.info("IMEI           : [" + platformHeader.getValue(PlatformHeader.IMEI, String.class) + "]");
			LOGGER.info("WIFI_MAC       : [" + platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class) + "]");
			LOGGER.info("MSISDN         : [" + platformHeader.getValue(PlatformHeader.MSISDN, String.class) + "]");
			LOGGER.info("MODEL_NO       : [" + platformHeader.getValue(PlatformHeader.MODEL_NO, String.class) + "]");
			LOGGER.info("ISP_NAME       : [" + platformHeader.getValue(PlatformHeader.ISP_NAME, String.class) + "]");
			LOGGER.info("OS_TYPE        : [" + platformHeader.getValue(PlatformHeader.OS_TYPE, String.class) + "]");
			LOGGER.info("OS_VERSION     : [" + platformHeader.getValue(PlatformHeader.OS_VERSION, String.class) + "]");
			LOGGER.info("UUID           : [" + platformHeader.getValue(PlatformHeader.UUID, String.class) + "]");
		}
		
		if(version.equals("PHV105")) {
			LOGGER.info("TRANSACTION_ID : [" + platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class) + "]");
			LOGGER.info("SERVICE_ID     : [" + platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class) + "]");
			LOGGER.info("IMEI           : [" + platformHeader.getValue(PlatformHeader.IMEI, String.class) + "]");
			LOGGER.info("WIFI_MAC       : [" + platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class) + "]");
			LOGGER.info("MSISDN         : [" + platformHeader.getValue(PlatformHeader.MSISDN, String.class) + "]");
			LOGGER.info("MODEL_NO       : [" + platformHeader.getValue(PlatformHeader.MODEL_NO, String.class) + "]");
			LOGGER.info("ISP_NAME       : [" + platformHeader.getValue(PlatformHeader.ISP_NAME, String.class) + "]");
			LOGGER.info("OS_TYPE        : [" + platformHeader.getValue(PlatformHeader.OS_TYPE, String.class) + "]");
			LOGGER.info("OS_VERSION     : [" + platformHeader.getValue(PlatformHeader.OS_VERSION, String.class) + "]");
			LOGGER.info("UUID           : [" + platformHeader.getValue(PlatformHeader.UUID, String.class) + "]");
		}
		
		LOGGER.info("BODY_TYPE      : [" + platformHeader.getValue(PlatformHeader.BODY_TYPE, Short.class) + "]");
		LOGGER.info("STATUS_CODE    : [" + platformHeader.getValue(PlatformHeader.STATUS_CODE, Short.class) + "]");
		LOGGER.info("HEADER_LENGTH  : [" + platformHeader.getValue(PlatformHeader.HEADER_LENGTH, Integer.class) + "]");
		LOGGER.info("BODY_LENGTH    : [" + platformHeader.getValue(PlatformHeader.BODY_LENGTH, Integer.class) + "]");
	}
	
	private void printHeader(TasBean header) {
		if(header.getParamNames().size() > 0) {
			LOGGER.info("-----------------------------------------------------------------------------------------------------");
			LOGGER.info("[Header]");
			LOGGER.info("-----------------------------------------------------------------------------------------------------");
			printTasBean(header);
		}
	}
	
	private void printBody(TasBean body) {
		if(body.getParamNames().size() > 0) {
			LOGGER.info("-----------------------------------------------------------------------------------------------------");
			LOGGER.info("[Body]");
			LOGGER.info("-----------------------------------------------------------------------------------------------------");
			printTasBean(body);
		}
	}
	
	private void printTasBean(TasBean printTasBean) {
		Set<String> keys = printTasBean.getParamNames();
		for(String key : keys){
			Object obj = printTasBean.getValue(key);
			if(obj instanceof TasBean) {
				TasBean tasBean = printTasBean.getValue(key, TasBean.class); 
				printSubTasBean(tasBean,key);
			} else if (obj instanceof Collection) {
				Collection<TasBean> beans =  printTasBean.getValue(key, Collection.class); 
				int index=0;
				for(TasBean bean : beans) {
					printSubTasBean(bean,key + "[" + index + "]");
					index++;
				}
			} else {
				LOGGER.info("\t[" + key + "] : [" + printTasBean.getValue(key) + "]");
			}
		}
	}
	
	private void printSubTasBean(TasBean bean,String key) {
		Set<String> paramNames = bean.getParamNames();
		for(String name : paramNames) {
			Object obj = bean.getValue(name);
			if(obj instanceof Collection) {
				Collection<TasBean> subBeans = bean.getValue(name, Collection.class);
				int index=0;
				for(TasBean subBean : subBeans) {
					printSubTasBean(subBean,key + "." + name + "[" + index + "]");
					index++;
				}
			} else if (obj instanceof TasBean) {
				TasBean subBean = bean.getValue(name, TasBean.class);
				printSubTasBean(subBean,key + "." + name);
			} else {
				LOGGER.info("\t[" + key + "." + name + "] : [" + bean.getValue(name) + "]");

			}
		}
	}
	
}
