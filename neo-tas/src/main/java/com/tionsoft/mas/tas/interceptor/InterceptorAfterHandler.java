package com.tionsoft.mas.tas.interceptor;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import com.tionsoft.mas.tas.taslet.TasMessage.MessageDirection;

public interface InterceptorAfterHandler {
	
	public MessageDirection direction =  MessageDirection.SENT;
	
	public void handle(TasRequest tReq, TasResponse tRes);

}
