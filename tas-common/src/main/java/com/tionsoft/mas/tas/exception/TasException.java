package com.tionsoft.mas.tas.exception;

public class TasException extends RuntimeException {

	private static final long serialVersionUID = -3486123363953056816L;
	private final ErrorType errorType;

	public TasException() {
		super();
		errorType = null;
	}

	public TasException(String message) {
		super(message);
		errorType = null;
	}
	
	public TasException(ErrorType errorType) {
		super(errorType.getMessage());
		this.errorType = errorType;
	}

	public TasException(String message, Throwable cause) {
		super(message, cause);
		if(cause instanceof TasException) {
			TasException tasException = (TasException)cause;
			errorType = tasException.getErrorType();
		} else {
			errorType = null;
		}
	}
	
	public TasException(ErrorType errorType, Throwable cause) {
		super(errorType.getMessage(), cause);
		this.errorType = errorType;;
	}

	public TasException(Throwable cause) {
		super(cause);
		if(cause instanceof TasException) {
			TasException tasException = (TasException)cause;
			errorType = tasException.getErrorType();
		} else {
			errorType = null;
		}
	}
	
	public ErrorType getErrorType() {
		return errorType;
	}
}