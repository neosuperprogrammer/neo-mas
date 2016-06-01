package com.tionsoft.mas.tas.filter.background;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.taslet.TasMessage;
import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasMessage.MessageDirection;
import com.tionsoft.platform.exception.PlatformException;
import com.tionsoft.platform.filter.Filter;
import com.tionsoft.platform.filter.FilterChain;

/**
 * print a request and a response stream data
 * @author Administrator
 *
 */
public class TasBackgroundLoggingFilter implements Filter {
	
	private static Logger logger = LoggerFactory.getLogger(TasBackgroundLoggingFilter.class);
	@Override
	public void doFilter(Object message, FilterChain chain) throws PlatformException {
		
		StringBuffer buf = new StringBuffer();
		TasMessage tMessage = (TasMessage) message;
		TasRequest tasRequest = tMessage.getTasRequest();
		
		if(tMessage.getMessageDirection().equals(MessageDirection.RECEIVED)){
			
			
			buf.append("\n\t===================================================================================================");
			buf.append("\n\t##### Client --> Server #####");
			buf.append("\n\t===================================================================================================");
			
			printPlatformHeader(tasRequest.getPlatformHeader(),buf);
			
			if(tMessage.getTasRequest().getHeader()!= null && tMessage.getTasRequest().getHeader().getParams().size() > 0) {
				printHeader(tMessage.getTasRequest().getHeader(),buf);
			}
			
			if(tMessage.getTasRequest().getBody()!= null && tMessage.getTasRequest().getBody().getParams().size() > 0) {
				printBody(tMessage.getTasRequest().getBody(),buf);
			}
			
		}else if(tMessage.getMessageDirection().equals(MessageDirection.SENT))
		{
			buf.append("\n\t===================================================================================================\n");
			buf.append("\n\t##### Server --> Client #####");
			buf.append("\n\t===================================================================================================\n");
			
			printPlatformHeader(tasRequest.getPlatformHeader(),buf);
			
			if(tMessage.getTasRequest().getHeader()!= null && tMessage.getTasRequest().getHeader().getParams().size() > 0) {
				printHeader(tMessage.getTasResponse().getHeader(),buf);
			}
			
			if(tMessage.getTasRequest().getBody()!= null && tMessage.getTasRequest().getBody().getParams().size() > 0) {
				printBody(tMessage.getTasResponse().getBody(),buf);
			}
		}
		
		logger.info(buf.toString());
			
		
		
		chain.doFilter(tMessage);
		
	}

	private void printPlatformHeader(PlatformHeader platformHeader,StringBuffer buf) {
		String version = platformHeader.getValue(PlatformHeader.VERSION, String.class);
		
		buf.append("\n\t-----------------------------------------------------------------------------------------------------");
		buf.append("\n\t[Platform Header]");
		buf.append("\n\t-----------------------------------------------------------------------------------------------------");
		
		buf.append("\n\tVERSION : [" +version + "]");
		
		buf.append("\n\tAPPLICATION_ID : [" + platformHeader.getValue(PlatformHeader.APPLICATION_ID, String.class) + "]");
		buf.append("\n\tMESSAGE_ID     : [" + platformHeader.getValue(PlatformHeader.MESSAGE_ID, String.class) + "]");
		buf.append("\n\tSESSION_ID     : [" + platformHeader.getValue(PlatformHeader.SESSION_ID, Long.class) + "]");
		if(version.equals("PHV101")) {
			buf.append("\n\tTRANSACTION_ID : [" + platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class) + "]");
			buf.append("\n\tSERVICE_ID     : [" + platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class) + "]");
			buf.append("\n\tIMEI           : [" + platformHeader.getValue(PlatformHeader.IMEI, String.class) + "]");
			buf.append("\n\tMSISDN         : [" + platformHeader.getValue(PlatformHeader.MSISDN, String.class) + "]");
		}
		
		if(version.equals("PHV102")) {
			buf.append("\n\tTRANSACTION_ID : [" + platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class) + "]");
			buf.append("\n\tSERVICE_ID     : [" + platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class) + "]");
			buf.append("\n\tIMEI           : [" + platformHeader.getValue(PlatformHeader.IMEI, String.class) + "]");
			buf.append("\n\tWIFI_MAC       : [" + platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class) + "]");
			buf.append("\n\tMSISDN         : [" + platformHeader.getValue(PlatformHeader.MSISDN, String.class) + "]");
			buf.append("\n\tMODEL_NO       : [" + platformHeader.getValue(PlatformHeader.MODEL_NO, String.class) + "]");
			buf.append("\n\tISP_NAME       : [" + platformHeader.getValue(PlatformHeader.ISP_NAME, String.class) + "]");
			buf.append("\n\tOS_TYPE        : [" + platformHeader.getValue(PlatformHeader.OS_TYPE, String.class) + "]");
			buf.append("\n\tOS_VERSION     : [" + platformHeader.getValue(PlatformHeader.OS_VERSION, String.class) + "]");
			buf.append("\n\tUUID           : [" + platformHeader.getValue(PlatformHeader.UUID, String.class) + "]");
		}

		if(version.equals("PHV103")) {
			buf.append("\n\tTRANSACTION_ID : [" + platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class) + "]");
			buf.append("\n\tSERVICE_ID     : [" + platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class) + "]");
			buf.append("\n\tIMEI           : [" + platformHeader.getValue(PlatformHeader.IMEI, String.class) + "]");
			buf.append("\n\tWIFI_MAC       : [" + platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class) + "]");
			buf.append("\n\tMSISDN         : [" + platformHeader.getValue(PlatformHeader.MSISDN, String.class) + "]");
			buf.append("\n\tMODEL_NO       : [" + platformHeader.getValue(PlatformHeader.MODEL_NO, String.class) + "]");
			buf.append("\n\tISP_NAME       : [" + platformHeader.getValue(PlatformHeader.ISP_NAME, String.class) + "]");
			buf.append("\n\tOS_TYPE        : [" + platformHeader.getValue(PlatformHeader.OS_TYPE, String.class) + "]");
			buf.append("\n\tOS_VERSION     : [" + platformHeader.getValue(PlatformHeader.OS_VERSION, String.class) + "]");
			buf.append("\n\tUUID           : [" + platformHeader.getValue(PlatformHeader.UUID, String.class) + "]");
		}
		
		if(version.equals("PHV105")) {
			buf.append("\n\tTRANSACTION_ID : [" + platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class) + "]");
			buf.append("\n\tSERVICE_ID     : [" + platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class) + "]");
			buf.append("\n\tIMEI           : [" + platformHeader.getValue(PlatformHeader.IMEI, String.class) + "]");
			buf.append("\n\tWIFI_MAC       : [" + platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class) + "]");
			buf.append("\n\tMSISDN         : [" + platformHeader.getValue(PlatformHeader.MSISDN, String.class) + "]");
			buf.append("\n\tMODEL_NO       : [" + platformHeader.getValue(PlatformHeader.MODEL_NO, String.class) + "]");
			buf.append("\n\tISP_NAME       : [" + platformHeader.getValue(PlatformHeader.ISP_NAME, String.class) + "]");
			buf.append("\n\tOS_TYPE        : [" + platformHeader.getValue(PlatformHeader.OS_TYPE, String.class) + "]");
			buf.append("\n\tOS_VERSION     : [" + platformHeader.getValue(PlatformHeader.OS_VERSION, String.class) + "]");
			buf.append("\n\tUUID           : [" + platformHeader.getValue(PlatformHeader.UUID, String.class) + "]");
		}
		
		buf.append("\n\tBODY_TYPE      : [" + platformHeader.getValue(PlatformHeader.BODY_TYPE, Short.class) + "]");
		buf.append("\n\tSTATUS_CODE    : [" + platformHeader.getValue(PlatformHeader.STATUS_CODE, Short.class) + "]");
		buf.append("\n\tHEADER_LENGTH  : [" + platformHeader.getValue(PlatformHeader.HEADER_LENGTH, Integer.class) + "]");
		buf.append("\n\tBODY_LENGTH    : [" + platformHeader.getValue(PlatformHeader.BODY_LENGTH, Integer.class) + "]");
	}
	
	private void printHeader(TasBean header,StringBuffer buf) {
		if(header.getParamNames().size() > 0) {
			buf.append("\n\t-----------------------------------------------------------------------------------------------------");
			buf.append("\n\t[Header]");
			buf.append("\n\t-----------------------------------------------------------------------------------------------------\n\t");
			printTasBean(header,buf);
		}
	}
	
	private void printBody(TasBean body,StringBuffer buf) {
		if(body.getParamNames().size() > 0) {
			buf.append("\n\t-----------------------------------------------------------------------------------------------------");
			buf.append("\n\t[Body]");
			buf.append("\n\t-----------------------------------------------------------------------------------------------------\n\t");
			printTasBean(body,buf);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void printTasBean(TasBean printTasBean,StringBuffer buf) {
		Set<String> keys = printTasBean.getParamNames();
		for(String key : keys){
			Object obj = printTasBean.getValue(key);
			if(obj instanceof TasBean) {
				TasBean tasBean = printTasBean.getValue(key, TasBean.class); 
				printSubTasBean(tasBean,key,buf);
			} else if (obj instanceof Collection) {
				Collection<TasBean> beans =  printTasBean.getValue(key, Collection.class); 
				int index=0;
				for(TasBean bean : beans) {
					printSubTasBean(bean,key + "[" + index + "]",buf);
					index++;
				}
			} else {
				buf.append("\t[" + key + "] : [" + printTasBean.getValue(key) + "]");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void printSubTasBean(TasBean bean,String key,StringBuffer buf) {
		Set<String> paramNames = bean.getParamNames();
		for(String name : paramNames) {
			Object obj = bean.getValue(name);
			if(obj instanceof Collection) {
				Collection<TasBean> subBeans = bean.getValue(name, Collection.class);
				int index=0;
				for(TasBean subBean : subBeans) {
					printSubTasBean(subBean,key + "." + name + "[" + index + "]",buf);
					index++;
				}
			} else if (obj instanceof TasBean) {
				TasBean subBean = bean.getValue(name, TasBean.class);
				printSubTasBean(subBean,key + "." + name,buf);
			} else {
				buf.append("\t[" + key + "." + name + "] : [" + bean.getValue(name) + "]");

			}
		}
	}

}
