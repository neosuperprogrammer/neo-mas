package com.tionsoft.mas.tas.exception;

import com.tionsoft.mas.tas.bean.platform.*;


public final class ErrorType {
	
	public static final short ERROR_CODE_DEFAULT = (short)2999;  //MAS_NO_DEFINITION_ERROR
	public static final short ERROR_CODE_BUFFER_UNDERFLOW = (short)1002; //MAS_REQUEST_ INSIDE_ BUFFER_UNDERFLOW
	public static final short ERROR_CODE_NOT_FOUND_REQUEST_BODY_DEFINITION = (short)1003; //MAS_REQUEST_ INSIDE _ NOT_FOUND_ BODY_DEFINITION
	public static final short ERROR_CODE_NOT_FOUND_RESPONSE_BODY_DEFINITION = (short)1004; //MAS_ RESPONSE_ INSIDE _ NOT_FOUND_ BODY_DEFINITION
	
	public static final short ERROR_CODE_NOT_FOUND_TASLET = (short)1006; //MAS _ INSIDE _ NOT_FOUND_TASLET
	public static final short ERROR_CODE_NULL_FIELD = (short)1007; //MAS_REQUEST_ INSIDE _ NULL_FIELD
	public static final short ERROR_CODE_NOT_FOUND_REQUEST_STRUCTURE_DEFINITION = (short)1008; //MAS_REQUEST_ INSIDE _NOT_FOUND_ STRUCTURE_DEFINITION
	public static final short ERROR_CODE_NOT_FOUND_RESPONSE_STRUCTURE_DEFINITION = (short)1009; //MAS_ RESPONSE _ INSIDE _ NOT_FOUND_STRUCTURE_DEFINITION
	
	public static final short ERROR_CODE_PROTOCOL_PARSING = (short)1010; //MAS_REQUEST_INSIDE_ PROTOCOL_PARSING
	public static final short ERROR_CODE_FILETYPE_PROTOCOL_PARSING = (short)1011; //MAS_REQUEST_INSIDE _ FILETYPE_PROTOCOL_PARSING
	public static final short ERROR_CODE_REQUEST_JSON_BODYTYPE_NOT_STRING = (short)1012; //MAS_REQUEST_INSIDE_JSON_BODYTYPE_NOT_STRING
	public static final short ERROR_CODE_RESPONSE_JSON_BODYTYPE_NOT_STRING = (short)1013; //MAS_RESPONSE_INSIDE _JSON_BODYTYPE_NOT_STRING
	public static final short ERROR_CODE_JSON_NOT_SUPPORTED_VALUES = (short)1014; //MAS_ REQUEST _INSIDE _ JSON_NOT_SUPPORTED_VALUES
	public static final short ERROR_CODE_JSON_PARSING = (short)1015; //MAS_ REQUEST _INSIDE _ JSON_PARSING
	
	public static final short ERROR_CODE_BUFFER_OUTOFMEMORY = (short)1016;//MAS_ REQUEST _INSIDE _ BUFFER_OUTOFMEMORY
	
	
	public static final short ERROR_CODE_ClassCastException = (short)2002; //MAS_TASLET_CLASS_CAST_EXCEPTON
	public static final short ERROR_CODE_CannotCreateTransactionException = (short)2003; //MAS_TASLET_ CANNOT_CREATE_TRANSACTION_EXCEPTION
	public static final short ERROR_CODE_BadSqlGrammarException = (short)2004; //MAS_TASLET_ BAD_SQL_GRAMMAR_EXCEPTION
	

	public static final short ERROR_CODE_NONE	= (short)0;
	
	/* Platform Header 에 설정된 Header + Body 의 합보다 메시지가 더 긴 경우 */ 
	//MAS_REQUEST_INSIDE_BUFFER_OVERFLOW
	public static final short ERROR_CODE_BUFFER_OVERFLOW = (short)1001;
	
	public static final short ERROR_CODE_TASLET	= (short)2000; //MAS_TASLET_TASLET_EXCEPTION
//	public static final short ERROR_CODE_TASLET = (short)2000;   //PHV103에서 사용함 //MAS_TASLET_TASLET_EXCEPTION
	
	public static final short ERROR_CODE_PUSHLET	= (short)2001; //MAS_TASLET_PUSHLET_ERROR
	public static final short ERROR_CODE_UNKNOWN =  (short)2999; //MAS_NO_DEFINITION_ERROR
	
	public static final short ERROR_CODE_TAS_CLIENT	= (short)2005; //MAS_TASLET_ TASCLIENT_EXCEPTION
	public static final short ERROR_CODE_TAS_CLIENT_CONNECT	= (short)2006; //MAS_TASLET_ TASCLIENT_CONNECTION_EXCEPTION 
	public static final short ERROR_CODE_TAS_CLIENT_RESPONSE_TIMEOUT = (short)2007; //MAS_TASLET_ TASCLIENT_RESPONSE_TIMEOUT_EXCEPTION

	
    private final String msg;
    private final short code;
	//에러시 사용할 platformheader 정보
    private final PlatformHeader platformHeaderErrMsg;
	
    public ErrorType(PlatformHeader platformHeaderErrMsg, short code, String msg) {
        this.code = code;
        this.msg = msg;
        this.platformHeaderErrMsg = platformHeaderErrMsg;
    }
    
    public short getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }
    
    public PlatformHeader getPlatformHeader() {
        return platformHeaderErrMsg;
    }

    public String toString() {
        return "[" + code + "]" + msg;
    }
}