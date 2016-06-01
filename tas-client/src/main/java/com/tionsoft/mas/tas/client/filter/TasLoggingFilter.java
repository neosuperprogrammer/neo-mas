package com.tionsoft.mas.tas.client.filter;

import java.util.Collection;
import java.util.Set;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.client.message.TasMessage;

public class TasLoggingFilter extends IoFilterAdapter {
	
	@Override
	public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
		nextFilter.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		TasMessage tasMessage = (TasMessage)message;
		
		System.out.println("===================================================================================================");
		System.out.println("##### Server --> Client #####");
		System.out.println("===================================================================================================");

		printPlatformHeader(tasMessage.getTasResponse().getPlatformHeader());
		
		if(tasMessage.getTasResponse().getHeader()!= null && tasMessage.getTasResponse().getHeader().getParams().size() > 0) {
			printHeader(tasMessage.getTasResponse().getHeader());
		}
		
		if(tasMessage.getTasResponse().getBody()!= null && tasMessage.getTasResponse().getBody().getParams().size() > 0) {
			printBody(tasMessage.getTasResponse().getBody());
		}
		
		nextFilter.messageReceived(session, message);
	}

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		if(writeRequest.getMessage() instanceof TasMessage) {
			TasMessage tasMessage = (TasMessage)writeRequest.getMessage();
			System.out.println("===================================================================================================");
			System.out.println("##### Client --> Server #####");
			System.out.println("===================================================================================================");
			
			printPlatformHeader(tasMessage.getTasRequest().getPlatformHeader());
				
			if(tasMessage.getTasRequest().getHeader()!= null && tasMessage.getTasRequest().getHeader().getParams().size() > 0) {
				printHeader(tasMessage.getTasRequest().getHeader());
			}
				
			if(tasMessage.getTasRequest().getBody()!= null && tasMessage.getTasRequest().getBody().getParams().size() > 0) {
				printBody(tasMessage.getTasRequest().getBody());
			}
		}

		nextFilter.messageSent(session, writeRequest);
	}
	
	private void printPlatformHeader(PlatformHeader platformHeader) {
		String version = platformHeader.getValue(PlatformHeader.VERSION, String.class);
		
		System.out.println("-----------------------------------------------------------------------------------------------------");
		System.out.println("[Platform Header]");
		System.out.println("-----------------------------------------------------------------------------------------------------");
		
		System.out.println("VERSION        : [" +version + "]");
		
		System.out.println("APPLICATION_ID : [" + platformHeader.getValue(PlatformHeader.APPLICATION_ID, String.class) + "]");
		System.out.println("MESSAGE_ID     : [" + platformHeader.getValue(PlatformHeader.MESSAGE_ID, String.class) + "]");
		System.out.println("SESSION_ID     : [" + platformHeader.getValue(PlatformHeader.SESSION_ID, Long.class) + "]");
		if(version.equals("PHV101")) {
			System.out.println("TRANSACTION_ID : [" + platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class) + "]");
			System.out.println("SERVICE_ID     : [" + platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class) + "]");
			System.out.println("IMEI           : [" + platformHeader.getValue(PlatformHeader.IMEI, String.class) + "]");
			System.out.println("MSISDN         : [" + platformHeader.getValue(PlatformHeader.MSISDN, String.class) + "]");
		}
		
		if(version.equals("PHV102")) {
			System.out.println("TRANSACTION_ID : [" + platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class) + "]");
			System.out.println("SERVICE_ID     : [" + platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class) + "]");
			System.out.println("IMEI           : [" + platformHeader.getValue(PlatformHeader.IMEI, String.class) + "]");
			System.out.println("WIFI_MAC       : [" + platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class) + "]");
			System.out.println("MSISDN         : [" + platformHeader.getValue(PlatformHeader.MSISDN, String.class) + "]");
			System.out.println("MODEL_NO       : [" + platformHeader.getValue(PlatformHeader.MODEL_NO, String.class) + "]");
			System.out.println("ISP_NAME       : [" + platformHeader.getValue(PlatformHeader.ISP_NAME, String.class) + "]");
			System.out.println("OS_TYPE        : [" + platformHeader.getValue(PlatformHeader.OS_TYPE, String.class) + "]");
			System.out.println("OS_VERSION     : [" + platformHeader.getValue(PlatformHeader.OS_VERSION, String.class) + "]");
			System.out.println("UUID           : [" + platformHeader.getValue(PlatformHeader.UUID, String.class) + "]");
		}
		
		if(version.equals("PHV103")) {
			System.out.println("TRANSACTION_ID : [" + platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class) + "]");
			System.out.println("SERVICE_ID     : [" + platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class) + "]");
			System.out.println("IMEI           : [" + platformHeader.getValue(PlatformHeader.IMEI, String.class) + "]");
			System.out.println("WIFI_MAC       : [" + platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class) + "]");
			System.out.println("MSISDN         : [" + platformHeader.getValue(PlatformHeader.MSISDN, String.class) + "]");
			System.out.println("MODEL_NO       : [" + platformHeader.getValue(PlatformHeader.MODEL_NO, String.class) + "]");
			System.out.println("ISP_NAME       : [" + platformHeader.getValue(PlatformHeader.ISP_NAME, String.class) + "]");
			System.out.println("OS_TYPE        : [" + platformHeader.getValue(PlatformHeader.OS_TYPE, String.class) + "]");
			System.out.println("OS_VERSION     : [" + platformHeader.getValue(PlatformHeader.OS_VERSION, String.class) + "]");
			System.out.println("UUID           : [" + platformHeader.getValue(PlatformHeader.UUID, String.class) + "]");
		}
		
		if(version.equals("PHV105")) {
			System.out.println("TRANSACTION_ID : [" + platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class) + "]");
			System.out.println("SERVICE_ID     : [" + platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class) + "]");
			System.out.println("IMEI           : [" + platformHeader.getValue(PlatformHeader.IMEI, String.class) + "]");
			System.out.println("WIFI_MAC       : [" + platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class) + "]");
			System.out.println("MSISDN         : [" + platformHeader.getValue(PlatformHeader.MSISDN, String.class) + "]");
			System.out.println("MODEL_NO       : [" + platformHeader.getValue(PlatformHeader.MODEL_NO, String.class) + "]");
			System.out.println("ISP_NAME       : [" + platformHeader.getValue(PlatformHeader.ISP_NAME, String.class) + "]");
			System.out.println("OS_TYPE        : [" + platformHeader.getValue(PlatformHeader.OS_TYPE, String.class) + "]");
			System.out.println("OS_VERSION     : [" + platformHeader.getValue(PlatformHeader.OS_VERSION, String.class) + "]");
			System.out.println("UUID           : [" + platformHeader.getValue(PlatformHeader.UUID, String.class) + "]");
		}
		
		System.out.println("BODY_TYPE      : [" + platformHeader.getValue(PlatformHeader.BODY_TYPE, Short.class) + "]");
		System.out.println("STATUS_CODE    : [" + platformHeader.getValue(PlatformHeader.STATUS_CODE, Short.class) + "]");
		System.out.println("HEADER_LENGTH  : [" + platformHeader.getValue(PlatformHeader.HEADER_LENGTH, Integer.class) + "]");
		System.out.println("BODY_LENGTH    : [" + platformHeader.getValue(PlatformHeader.BODY_LENGTH, Integer.class) + "]");
	}
	
	private void printHeader(TasBean header) {
		if(header.getParamNames().size() > 0) {
			System.out.println("-----------------------------------------------------------------------------------------------------");
			System.out.println("[Header]");
			System.out.println("-----------------------------------------------------------------------------------------------------");
			printTasBean(header);
		}
	}
	
	private void printBody(TasBean body) {
		if(body.getParamNames().size() > 0) {
			System.out.println("-----------------------------------------------------------------------------------------------------");
			System.out.println("[Body]");
			System.out.println("-----------------------------------------------------------------------------------------------------");
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
			}else if (obj instanceof Collection) {
				Collection<TasBean> beans =  printTasBean.getValue(key, Collection.class); 
				int index=0;
				for(TasBean bean : beans) {
					printSubTasBean(bean,key + "[" + index + "]");
					index++;
				}
			}else{
				System.out.println("\t[" + key + "] : [" + printTasBean.getValue(key) + "]");
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
				printSubTasBean(subBean,key+"."+name);
			} else {
				System.out.println("\t[" + key + "." + name + "] : [" + bean.getValue(name) + "]");

			}
		}
	}
	
}
