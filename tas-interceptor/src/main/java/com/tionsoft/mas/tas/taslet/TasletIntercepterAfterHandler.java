package com.tionsoft.mas.tas.taslet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tionsoft.mas.tas.interceptor.InterceptorAfterHandler;
import com.tionsoft.mas.tas.queue.TasBackgroundQueue;
import com.tionsoft.mas.tas.taslet.TasMessage.MessageDirection;
import com.tionsoft.platform.queue.RequestQueue;


@Component
public class TasletIntercepterAfterHandler implements InterceptorAfterHandler {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	private RequestQueue queue = TasBackgroundQueue.getInstance(10000000);
	
	public TasletIntercepterAfterHandler()
	{
		System.out.println(">>>>>>>>>>>>>>>>TasletIntercepterAfterHandler  init>>>>>>>>>>>>>>>>>>>>>");
	}
	
	@Override
	public void handle(TasRequest tReq, TasResponse tRes)
	{
		
		if(logger.isDebugEnabled()) logger.debug("TasletIntercepter' tasletAfterHandler is  started");
		
		try{
			
			TasMessage message = new TasMessage(tReq, tRes,MessageDirection.SENT);
			
			queue.putQueueItemRequest(message);
			synchronized (queue.getQueueItemRequestQueue()) {
				queue.notifyService();
			}
			
		}catch(Exception e)
		{
			logger.error("TasletIntercepterAfterHandler", e);
		}
		
		if(logger.isDebugEnabled()) logger.debug("TasletIntercepter' tasletAfterHandler is  finished");
		
	}
	

}
