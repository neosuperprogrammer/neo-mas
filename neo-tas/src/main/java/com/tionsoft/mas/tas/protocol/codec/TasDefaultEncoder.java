package com.tionsoft.mas.tas.protocol.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tionsoft.mas.tas.TasContext;
import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.exception.TasException;
import com.tionsoft.mas.tas.interceptor.InterceptorAfterHandler;
import com.tionsoft.mas.tas.protocol.codec.encoder.ByteBufferEncoder;
import com.tionsoft.mas.tas.protocol.codec.encoder.JsonBodyEncoder;
import com.tionsoft.mas.tas.protocol.definition.DefinitionFinder;
import com.tionsoft.mas.tas.pushlet.PushService;
import com.tionsoft.mas.tas.taslet.TasMessage;

class TasDefaultEncoder implements ProtocolEncoder {
	
	protected final TasContext tasContext;
	private Logger LOGGER = null;
	private InterceptorAfterHandler afterHandler;
	
	public TasDefaultEncoder(TasContext tasContext) {
		this.tasContext = tasContext;
		LOGGER = LoggerFactory.getLogger(tasContext.getTcpAppConfig().getName());
		
		if(tasContext.getApplicationContext().containsBean("tasletIntercepterAfterHandler"))
		{
			afterHandler = tasContext.getApplicationContext().getBean("tasletIntercepterAfterHandler", InterceptorAfterHandler.class);
			System.out.println("===================tasletIntercepterAfterHandler is injected to tas==============================");
		}
	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
    	try {
	    	final TasMessage tasMessage = (TasMessage)message;
       		encode(session, tasMessage);
            if(tasMessage.getTasPushResponse() != null) {
            	Thread worker = new Thread(new Runnable() {
            		public void run() {
		            	PushService pushService = new PushService(tasContext, tasMessage.getTasPushResponse());
		            	pushService.pushMultiItems();
            		}
            	});
            	worker.start();
            }
    	} catch (Exception e) {
    		// TasException 으로 throw 하지만 TasHandler 의 exceptionCaught 에서는 ProtocolEncoderException 으로 받는다.
    		e.printStackTrace();
    		throw new TasException(e);
    	}
    }
	
	@Override
	public void dispose(IoSession session) throws Exception {
		
	}
	
	/**
	 * synchronized block 추가
	 * synchronized block's range is shorter to make tas encode efficiency
	 */
	private int encode(IoSession session, TasMessage tasMessage) {
		
		//synchronized(session) {	
		short bodyType = tasMessage.getTasResponse().getPlatformHeader().getValue(PlatformHeader.BODY_TYPE, Short.class);
		PlatformHeader platformHeader = tasMessage.getTasResponse().getPlatformHeader();
		TasBean header = tasMessage.getTasResponse().getHeader();
		TasBean body = tasMessage.getTasResponse().getBody();
	
		ByteBufferEncoder encoder = new ByteBufferEncoder(session, platformHeader, header, body, new DefinitionFinder(tasContext.getProtocolConfig(), platformHeader), true);
		switch(bodyType) {
		case PlatformHeader.BODY_TYPE_JSON :
			encoder.setBodyEncoder(new JsonBodyEncoder());
			break;
		}
		
		int messageLength = 0;
		
		synchronized(session) {
			messageLength =  encoder.encode();
		}
		
		
		if(afterHandler != null) afterHandler.handle(tasMessage.getTasRequest(), tasMessage.getTasResponse());
		
		return messageLength;
	}
	
	
}