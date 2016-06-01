package com.tionsoft.tmg.exception;

@SuppressWarnings("serial")
public class TmgException extends Exception {

	private TmgExceptionType tmcErrorType = null;
	private String legacyErrorMessage;
	private short legacyErrorCode = 0;
		
	public TmgException(short tmgErrorCode) {
		this.tmcErrorType = new TmgExceptionType(tmgErrorCode);
	}

	public TmgException(short legacyErrorCode, String legacyErrorMessage) {
		 this.legacyErrorCode = legacyErrorCode;
		 this.legacyErrorMessage = legacyErrorMessage;
	}

	public TmgExceptionType getErrorType() {
		return tmcErrorType;
	}
	
	public short getErrorCode() {
		return legacyErrorCode == 0 ? getErrorType().getCode() : legacyErrorCode;
	}
	
	public String getErrorMessage() {
		return legacyErrorCode == 0 ? getErrorType().getMessage() : legacyErrorMessage;
	}
	
	public String getErrorMessage(String locale) {
		String message = "";
		
		if (getErrorType() == null)
			message = legacyErrorMessage;
		else
			message = getErrorType().getMessage(locale);
		
		return message;
	}

}
