package com.tionsoft.mas.tas.client.codec;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.tionsoft.mas.tas.client.handler.TasProgressListener;

public class TasClientCodecFactory implements ProtocolCodecFactory {

	public static final String TAS_MESSAGE = "TAS_MESSAGE";
	public static final String TOTAL_MESSAGE_LENGTH_TO_RECEIVE = "TOTAL_MESSAGE_SIZE";
	public static final String RECEIVED_MESSAGE_LENGTH = "SAVED_MESSAGE_SIZE";
	public static final String TEMP_FILE_PATH = "TEMP_FILE_PATH";
	public static final String TEMP_FILE_CHANNEL = "TEMP_FILE_CHANNEL";
	public static final String MODE = "MODE";
	
	private TasClientEncoder encoder;
	private TastClientDecoder decoder;
	
	public TasClientCodecFactory(XMLConfiguration protocolConfig) {
		decoder = new TastClientDecoder(protocolConfig);
		encoder = new TasClientEncoder(protocolConfig);
	}
	
	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}
	
	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}
	
	public void setProgressListener(TasProgressListener progressListener) {
		decoder.setProgressListener(progressListener);
	}
}
