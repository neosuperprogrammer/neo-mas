package com.tionsoft.mas.tas.filter;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.mina.core.filterchain.*;
import org.apache.mina.core.session.*;
import org.apache.mina.core.write.*;
import org.slf4j.*;

import com.tionsoft.mas.tas.bean.*;
import com.tionsoft.mas.tas.bean.platform.*;
import com.tionsoft.mas.tas.taslet.*;

public class TasSolrLoggingFilter extends IoFilterAdapter {
	private Logger LOGGER  = LoggerFactory.getLogger("tas_solrlog");
	private String tcpappName = "TAS"; 
	
	public void setTcpappName(String name) {
		this.tcpappName = name;
	}

	@Override
	public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
//		cause.printStackTrace();
		
		nextFilter.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		TasMessage tasMessage = (TasMessage)message;
		StringBuffer sbReceived = new StringBuffer();
		sbReceived.append("<doc>");
		printPlatformHeader(sbReceived, "REQUEST", tasMessage.getTasRequest().getPlatformHeader());
		
		if(tasMessage.getTasRequest().getHeader()!= null && tasMessage.getTasRequest().getHeader().getParams().size() > 0) {
			printHeader(sbReceived, tasMessage.getTasRequest().getHeader());
		}
		
		if(tasMessage.getTasRequest().getBody()!= null && tasMessage.getTasRequest().getBody().getParams().size() > 0) {
			printBody(sbReceived, tasMessage.getTasRequest().getBody());
		}
		sbReceived.append("</doc>");
		LOGGER.info(sbReceived.toString());
		
		nextFilter.messageReceived(session, message);
	}

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		if(writeRequest.getMessage() instanceof TasMessage) {
			TasMessage tasMessage = (TasMessage)writeRequest.getMessage();
			StringBuffer sbSent = new StringBuffer();
			sbSent.append("<doc>");
			
			String applicationId = tasMessage.getTasResponse().getPlatformHeader().getValue(PlatformHeader.APPLICATION_ID, String.class);
			String messageId = tasMessage.getTasResponse().getPlatformHeader().getValue(PlatformHeader.MESSAGE_ID, String.class);
			
			if(!applicationId.equals(PlatformHeader.PLATFORM_APPLICATION_ID) && !messageId.equals(PlatformHeader.KEEPALIVE_MESSAGE_ID)) {
				printPlatformHeader(sbSent,"RESPONSE", tasMessage.getTasResponse().getPlatformHeader());
				
				if(tasMessage.getTasResponse().getHeader()!= null && tasMessage.getTasResponse().getHeader().getParams().size() > 0) {
					printHeader(sbSent, tasMessage.getTasResponse().getHeader());
				}
				
				if(tasMessage.getTasResponse().getBody()!= null && tasMessage.getTasResponse().getBody().getParams().size() > 0) {
					printBody(sbSent, tasMessage.getTasResponse().getBody());
				}
			}
			sbSent.append("</doc>");
			
			LOGGER.info(sbSent.toString());
		}

		nextFilter.messageSent(session, writeRequest);
	}
	
	private void printPlatformHeader(StringBuffer sb, String inoutType, PlatformHeader platformHeader) {
		String version = platformHeader.getValue(PlatformHeader.VERSION, String.class);
		
		
		
//		if(version.equals("PHV100") || version.equals("PHV101")) {
//			sb.append("<field name=\"UNIQUE_KEY\">" +inoutType+"-"+tcpappName+"-"+version+"-"+
//					platformHeader.getValue(PlatformHeader.APPLICATION_ID, String.class)+"-"+
//					platformHeader.getValue(PlatformHeader.MESSAGE_ID, String.class)+"-"+
//					platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class)+"-"+
//					System.nanoTime()+
//					"</field>");
//		}else{
//			sb.append("<field name=\"UNIQUE_KEY\">" +inoutType+"-"+tcpappName+"-"+version+"-"+
//					platformHeader.getValue(PlatformHeader.APPLICATION_ID, String.class)+"-"+
//					platformHeader.getValue(PlatformHeader.MESSAGE_ID, String.class)+"-"+
//					platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class)+"-"+
//					platformHeader.getValue(PlatformHeader.IMEI, String.class).trim()+"-"+
//					platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class).trim()+"-"+
//					System.nanoTime()+
//					"</field>");
//		}
		
		
		sb.append("<field name=\"UNIQUE_KEY\">" +inoutType+"-"+tcpappName+"-"+version+"-"+
		platformHeader.getValue(PlatformHeader.APPLICATION_ID, String.class)+"-"+
		platformHeader.getValue(PlatformHeader.MESSAGE_ID, String.class)+"-"+
		platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class)+"-"+
		System.nanoTime()+
		"</field>");

		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatterDay = new SimpleDateFormat("yyyy-MM-dd");
		sb.append("<field name=\"REG_DAY\">" +formatterDay.format(today) + "</field>");
		sb.append("<field name=\"REG_DATE\">" +formatter.format(today) + "</field>");
		
		sb.append("<field name=\"APPNAME\">" +tcpappName + "</field>");
		sb.append("<field name=\"VERSION\">" +version + "</field>");
		sb.append("<field name=\"APPLICATION_ID\">" +String.valueOf(platformHeader.getValue(PlatformHeader.APPLICATION_ID, String.class)).trim() + "</field>");
		sb.append("<field name=\"MESSAGE_ID\">"     +String.valueOf(platformHeader.getValue(PlatformHeader.MESSAGE_ID, String.class)).trim() + "</field>");
		sb.append("<field name=\"SESSION_ID\">"     +String.valueOf(platformHeader.getValue(PlatformHeader.SESSION_ID, Long.class)).trim() + "</field>");
		
		if(version.equals("PHV101")) {
			sb.append("<field name=\"TRANSACTION_ID\">" +String.valueOf(platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class)).trim() + "</field>");
			sb.append("<field name=\"SERVICE_ID\">"     +String.valueOf(platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class)).trim() + "</field>");
			sb.append("<field name=\"IMEI\">"           +String.valueOf(platformHeader.getValue(PlatformHeader.IMEI, String.class)).trim() + "</field>");
			sb.append("<field name=\"MSISDN\">"         +String.valueOf(platformHeader.getValue(PlatformHeader.MSISDN, String.class)).trim() + "</field>");
			
		}
		
		if(version.equals("PHV102")) {
			sb.append("<field name=\"TRANSACTION_ID\">" +String.valueOf(platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class)).trim() + "</field>");
			sb.append("<field name=\"SERVICE_ID\">"     +String.valueOf(platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class)).trim() + "</field>");
			sb.append("<field name=\"IMEI\">"           +String.valueOf(platformHeader.getValue(PlatformHeader.IMEI, String.class)).trim() + "</field>");
			sb.append("<field name=\"WIFI_MAC\">"       +String.valueOf(platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class)).trim() + "</field>");
			
			sb.append("<field name=\"MSISDN\">"         +String.valueOf(platformHeader.getValue(PlatformHeader.MSISDN, String.class)).trim() + "</field>");
			sb.append("<field name=\"MODEL_NO\">"       +String.valueOf(platformHeader.getValue(PlatformHeader.MODEL_NO, String.class)).trim() + "</field>");
			sb.append("<field name=\"ISP_NAME\">"       +String.valueOf(platformHeader.getValue(PlatformHeader.ISP_NAME, String.class)).trim() + "</field>");
			sb.append("<field name=\"OS_TYPE\">"        +String.valueOf(platformHeader.getValue(PlatformHeader.OS_TYPE, String.class)).trim() + "</field>");
			sb.append("<field name=\"OS_VERSION\">"     +String.valueOf(platformHeader.getValue(PlatformHeader.OS_VERSION, String.class)).trim() + "</field>");
			sb.append("<field name=\"UUID\">"           +String.valueOf(platformHeader.getValue(PlatformHeader.UUID, String.class)).trim() + "</field>");
		}

		if(version.equals("PHV103")) {
			sb.append("<field name=\"TRANSACTION_ID\">" +String.valueOf(platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class)).trim() + "</field>");
			sb.append("<field name=\"SERVICE_ID\">"     +String.valueOf(platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class)).trim() + "</field>");
			sb.append("<field name=\"IMEI\">"           +String.valueOf(platformHeader.getValue(PlatformHeader.IMEI, String.class)).trim() + "</field>");
			sb.append("<field name=\"WIFI_MAC\">"       +String.valueOf(platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class)).trim() + "</field>");
			
			sb.append("<field name=\"MSISDN\">"         +String.valueOf(platformHeader.getValue(PlatformHeader.MSISDN, String.class)).trim() + "</field>");
			sb.append("<field name=\"MODEL_NO\">"       +String.valueOf(platformHeader.getValue(PlatformHeader.MODEL_NO, String.class)).trim() + "</field>");
			sb.append("<field name=\"ISP_NAME\">"       +String.valueOf(platformHeader.getValue(PlatformHeader.ISP_NAME, String.class)).trim() + "</field>");
			sb.append("<field name=\"OS_TYPE\">"        +String.valueOf(platformHeader.getValue(PlatformHeader.OS_TYPE, String.class)).trim() + "</field>");
			sb.append("<field name=\"OS_VERSION\">"     +String.valueOf(platformHeader.getValue(PlatformHeader.OS_VERSION, String.class)).trim() + "</field>");
			sb.append("<field name=\"UUID\">"           +String.valueOf(platformHeader.getValue(PlatformHeader.UUID, String.class)).trim() + "</field>");
		}
		
		sb.append("<field name=\"BODY_TYPE\">"     +String.valueOf(platformHeader.getValue(PlatformHeader.BODY_TYPE, Short.class)).trim() + "</field>");
		sb.append("<field name=\"STATUS_CODE\">"   +String.valueOf(platformHeader.getValue(PlatformHeader.STATUS_CODE, Short.class)).trim() + "</field>");
		sb.append("<field name=\"HEADER_LENGTH\">" +String.valueOf(platformHeader.getValue(PlatformHeader.HEADER_LENGTH, Integer.class)).trim() + "</field>");
		sb.append("<field name=\"BODY_LENGTH\">"   +String.valueOf(platformHeader.getValue(PlatformHeader.BODY_LENGTH, Integer.class)).trim() + "</field>");
		
	}
	
	private void printHeader(StringBuffer sb,TasBean header) {
		if(header.getParamNames().size() > 0) {
			printTasBean(sb, "HEADER", header);
		}
	}
	
	private void printBody(StringBuffer sb,TasBean body) {
		if(body.getParamNames().size() > 0) {
			printTasBean(sb, "BODY", body);
		}
	}
	
	private void printTasBean(StringBuffer sb,String fieldType,TasBean printTasBean) {
		Set<String> keys = printTasBean.getParamNames();
		for(String key : keys){
			Object obj = printTasBean.getValue(key);
			if(obj instanceof TasBean) {
				TasBean tasBean = printTasBean.getValue(key, TasBean.class); 
				printSubTasBean(sb, fieldType, tasBean,key);
			} else if (obj instanceof Collection) {
				Collection<TasBean> beans =  printTasBean.getValue(key, Collection.class); 
				int index=0;
				for(TasBean bean : beans) {
					printSubTasBean(sb, fieldType, bean,key + "[" + index + "]");
					index++;
				}
			} else {
				sb.append("<field name=\""+fieldType+"\"><![CDATA[<br>"+"[" + key+ "] : [" + printTasBean.getValue(key).toString().trim() + "]" +"]]></field>");
			}
		}
	}
	
	private void printSubTasBean(StringBuffer sb,String fieldType,TasBean bean,String key) {
		Set<String> paramNames = bean.getParamNames();
		for(String name : paramNames) {
			Object obj = bean.getValue(name);
			if(obj instanceof Collection) {
				Collection<TasBean> subBeans = bean.getValue(name, Collection.class);
				int index=0;
				for(TasBean subBean : subBeans) {
					printSubTasBean(sb, fieldType, subBean,key + "." + name + "[" + index + "]");
					index++;
				}
			} else if (obj instanceof TasBean) {
				TasBean subBean = bean.getValue(name, TasBean.class);
				printSubTasBean(sb, fieldType, subBean,key + "." + name);
			} else {
				sb.append("<field name=\""+fieldType+"\"><![CDATA[<br>"+"[" + key + "." + name + "] : [" + bean.getValue(name).toString().trim() + "]" +"]]></field>");
			}
		}
	}
	
//	public String Encoding(String src){
//		String result = null;
//		byte[] byteRes = null;
//
//		try {
//			byteRes = src.getBytes("utf-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		result = new String(byteRes);	
//		return result;
//	}
	
}
