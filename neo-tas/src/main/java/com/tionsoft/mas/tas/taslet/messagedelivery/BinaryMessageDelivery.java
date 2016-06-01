package com.tionsoft.mas.tas.taslet.messagedelivery;

import org.apache.commons.configuration.*;

import com.tionsoft.mas.tas.bean.*;
import com.tionsoft.mas.tas.bean.platform.*;
import com.tionsoft.mas.tas.taslet.*;

public class BinaryMessageDelivery extends MessageDelivery implements Taslet {
	
	/* 
	 * Binary 타입 protocol을 pbi쪽 연결 (gateway역활)
	 */
	
	//PMAS 연동을 위한 Configuration
	public void setConfiguration(XMLConfiguration configuration) {
		setMDConfiguration(configuration);
	}
	
	
	@Override
	public void execute(TasRequest tasRequest, TasResponse tasResponse) throws TasletException {
		try {
			
			
			//PBI 로 메세지전송 처리
			com.tionsoft.mas.tas.client.message.TasResponse tasClientResponse = requestToTCPServer(tasRequest,tasResponse);
			
			createResponseMessage(tasClientResponse,tasResponse);
		} catch (Exception e) {
			throw new TasletException(e);
		}
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
		TasBean body = tasRequest.getBody();
	
		
		
		com.tionsoft.mas.tas.client.message.TasRequest psRequest = new com.tionsoft.mas.tas.client.message.TasRequest(platformHeader, header, body);
		return psRequest;
	}
	
}
