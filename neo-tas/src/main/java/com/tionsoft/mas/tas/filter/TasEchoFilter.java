package com.tionsoft.mas.tas.filter;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tionsoft.mas.tas.taslet.TasMessage;
import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;

public class TasEchoFilter extends IoFilterAdapter {
	
	private final Logger LOGGER = LoggerFactory.getLogger("tas");
	
	@Override
	public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
		super.exceptionCaught(nextFilter, session, cause);
	}

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		TasMessage tasMessage = (TasMessage)message;
		TasRequest request = tasMessage.getTasRequest();
		TasResponse response = tasMessage.getTasResponse();
		response.setPlatformHeader(request.getPlatformHeader());
		response.setHeader(request.getHeader());
		response.setBody(request.getBody());
		session.write(tasMessage);
	}

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		super.messageSent(nextFilter, session, writeRequest);
	}

}
