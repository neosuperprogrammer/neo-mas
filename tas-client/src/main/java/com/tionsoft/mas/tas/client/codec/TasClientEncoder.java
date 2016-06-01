package com.tionsoft.mas.tas.client.codec;

import org.apache.commons.configuration.*;
import org.apache.mina.core.session.*;
import org.apache.mina.filter.codec.*;

import com.tionsoft.mas.tas.bean.*;
import com.tionsoft.mas.tas.bean.platform.*;
import com.tionsoft.mas.tas.exception.*;
import com.tionsoft.mas.tas.protocol.codec.encoder.*;
import com.tionsoft.mas.tas.protocol.definition.*;
import com.tionsoft.mas.tas.client.message.*;

class TasClientEncoder implements ProtocolEncoder {
	
	protected final XMLConfiguration protocolConfig;
	
	public TasClientEncoder(XMLConfiguration protocolConfig) {
		this.protocolConfig = protocolConfig;
	}
	
	/* 
	 * Synchronized 추가
	 */
	@Override
	public synchronized void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
    	try {
	    	TasMessage tasMessage = (TasMessage)message;
       		encode(session, tasMessage);
    	} catch (Exception e) {
    		// TasClientException 으로 throw 하지만 TasHandler 의 exceptionCaught 에서는 ProtocolEncoderException 으로 받는다.
    		throw new TasClientException(e);
    	}
    }
	
	@Override
	public void dispose(IoSession session) throws Exception {
		
	}
	
	private int encode(IoSession session, TasMessage tasMessage) {
		short bodyType = tasMessage.getTasRequest().getPlatformHeader().getValue(PlatformHeader.BODY_TYPE, Short.class);
		
		PlatformHeader platformHeader = tasMessage.getTasRequest().getPlatformHeader();
		TasBean header = tasMessage.getTasRequest().getHeader();
		TasBean body = tasMessage.getTasRequest().getBody();
		ByteBufferEncoder encoder = new ByteBufferEncoder(session, platformHeader, header, body, new DefinitionFinder(protocolConfig, platformHeader), false);
		
		switch(bodyType) {
		case PlatformHeader.BODY_TYPE_JSON :
			encoder.setBodyEncoder(new JsonBodyEncoder());
			break;
		}
		
		return encoder.encode();

	}
}