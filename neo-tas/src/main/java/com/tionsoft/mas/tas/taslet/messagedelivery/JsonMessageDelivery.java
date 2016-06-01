package com.tionsoft.mas.tas.taslet.messagedelivery;

import java.util.*;

import org.apache.commons.configuration.*;

import com.tionsoft.mas.tas.bean.*;
import com.tionsoft.mas.tas.bean.platform.*;
import com.tionsoft.mas.tas.taslet.*;

public class JsonMessageDelivery  extends MessageDelivery implements Taslet{

	/* 
	 * json 타입 protocol을 pbi쪽 연결 (gateway역활)
	 */
	
	
	//PMAS 연동을 위한 Configuration
	public void setConfiguration(XMLConfiguration configuration) {
		setMDConfiguration(configuration);
	}
	
	@Override
	public void execute(TasRequest tasRequest, TasResponse tasResponse) throws TasletException {
//		printJson(tasRequest);
		
		
		//PBI 로 메세지전송 처리
		com.tionsoft.mas.tas.client.message.TasResponse tasClientResponse = requestToTCPServer(tasRequest,tasResponse);
		
		createResponseMessage(tasClientResponse,tasResponse);
	}
	



	/**
	 * TCP 전송용 메세지(TasRequest) 생성
	 * 
	 * @param	TasRequest - Client로 부터 받은 Request
	 * @return	TasRequest - PBI 서버쪽에 전달할 Request
	 */
	@Override
	protected com.tionsoft.mas.tas.client.message.TasRequest createPBIRequestMessage(TasRequest tasRequest) {
		PlatformHeader platformHeader = tasRequest.getPlatformHeader();
		TasBean header = tasRequest.getHeader();
//		TasBean body = tasRequest.getBody();
	
		
		//Body 생성
		TasBean body = new TasBean();
		Set<String> keys = tasRequest.getBody().getParamNames();
		for(String key : keys){
			Object obj = tasRequest.getBody().getValue(key);
			
			if(obj instanceof TasBean) {
				body.setValue(key, getJsonStringData(tasRequest.getBody(),key));
			} else if (obj instanceof Collection) {
				body.setValue(key, getJsonStringData(tasRequest.getBody(),key));
			}
		}
		
		com.tionsoft.mas.tas.client.message.TasRequest psRequest = new com.tionsoft.mas.tas.client.message.TasRequest(platformHeader, header, body);
		return psRequest;
	}

	
	
	private void printJson(TasRequest request) {
		printTasBean(request.getBody());
		
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
				printSubTasBean(subBean,key + "." + name);
			} else {
				System.out.println("\t[" + key + "." + name + "] : [" + bean.getValue(name) + "]");

			}
		}
	}

}
