package com.tionsoft.mas.tas.protocol.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.tionsoft.mas.tas.TasContext;

public class TasDefaultCodecFactory extends TasCodecFactory {

	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;
	
	@Override
	public void setTasContext(TasContext tasContext) {
		decoder = new TasDefaultDecoder(tasContext);
		encoder = new TasDefaultEncoder(tasContext);
	}
	
	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}
	
	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}
}
