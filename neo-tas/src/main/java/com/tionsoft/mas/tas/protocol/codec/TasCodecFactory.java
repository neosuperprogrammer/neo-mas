package com.tionsoft.mas.tas.protocol.codec;

import org.apache.mina.filter.codec.ProtocolCodecFactory;

import com.tionsoft.mas.tas.TasContext;

public abstract class TasCodecFactory implements ProtocolCodecFactory {
	
	//에러시 사용할 platformheader 정보
	public static final String PLATFORMHEADER_ERROR_MESSAGE = "PLATFORMHEADER_ERROR_MESSAGE";
	//protocol.xml 처리한 필드 정보 (protocol 처리시 에러메세지 정보 제공
	public static final String PARSING_PROTOCOL_NAME = "PARSING_PROTOCOL_NAME";
	
	public static final String TAS_MESSAGE = "TAS_MESSAGE";
	public static final String TOTAL_MESSAGE_LENGTH_TO_RECEIVE = "TOTAL_MESSAGE_SIZE";
	public static final String RECEIVED_MESSAGE_LENGTH = "SAVED_MESSAGE_SIZE";
	public static final String TEMP_FILE_PATH = "TEMP_FILE_PATH";
	public static final String TEMP_FILE_CHANNEL = "TEMP_FILE_CHANNEL";
	public static final String MODE = "MODE";
	
	abstract public void setTasContext(TasContext tasContext);
	
}
