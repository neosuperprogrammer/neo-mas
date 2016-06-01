package com.tionsoft.mas.tas.interceptor;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import com.tionsoft.mas.tas.taslet.TasMessage.MessageDirection;
import com.tionsoft.platform.exception.PlatformException;

public interface InterceptorBeforeHandler {
	
	public MessageDirection direction =  MessageDirection.RECEIVED;
	
	public void handle(TasRequest tReq, TasResponse tRes) throws PlatformException;

}
