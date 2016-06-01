package com.tionsoft.mas.tas.filter.background;

import javax.annotation.Resource;

import com.tionsoft.mas.tas.repository.MessageStopWatchRepository;
import com.tionsoft.mas.tas.taslet.TasMessage;
import com.tionsoft.mas.tas.taslet.TasMessage.MessageDirection;
import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.platform.exception.PlatformException;
import com.tionsoft.platform.filter.Filter;
import com.tionsoft.platform.filter.FilterChain;

/**
 * License
 * @author Administrator
 *
 */
public final class TasBackgroundMonitoringFilter implements Filter {
	
	@Resource
	private MessageStopWatchRepository messageRepository;
	
	@Override
	public void doFilter(Object message, FilterChain chain) throws PlatformException {
		
		TasMessage tMessage = (TasMessage) message;
		
		TasRequest req = tMessage.getTasRequest();
		
		MessageDirection di =  tMessage.getMessageDirection();
		String taskId="";
		StringBuffer task = new StringBuffer();
		String messageId = req.getPlatformHeader("MESSAGE_ID",String.class);
		String applicationId = req.getPlatformHeader("APPLICATION_ID",String.class);
		Long sessionId = req.getPlatformHeader("SESSION_ID",Long.class);
		task.append(applicationId).append("-").append(messageId).append("[").append(sessionId).append("]");
		taskId = task.toString();
		
	
		if(di.equals(MessageDirection.RECEIVED)){
			
			messageRepository.requestMessageInfo(taskId);
			
		}else if(di.equals(MessageDirection.SENT))
		{
			messageRepository.removeRequestMessageInfo(taskId);
		}
		
		chain.doFilter(tMessage);
		
	}
	
	
	

}
