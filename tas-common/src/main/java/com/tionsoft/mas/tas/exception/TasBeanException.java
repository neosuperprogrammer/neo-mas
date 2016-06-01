package com.tionsoft.mas.tas.exception;

public class TasBeanException extends RuntimeException {

	private static final long serialVersionUID = -3486123363953056816L;
	private final ErrorType errorType;

	public TasBeanException() {
		super();
		errorType = null;
	}

	public TasBeanException(String message) {
		super(message);
		errorType = null;
	}
	
	public TasBeanException(ErrorType errorType) {
		super(errorType.getMessage());
		this.errorType = errorType;
	}

	public TasBeanException(String message, Throwable cause) {
		super(message, cause);
		if(cause instanceof TasBeanException) {
			TasBeanException tasBeanException = (TasBeanException)cause;
			errorType = tasBeanException.getErrorType();
		} else {
			errorType = null;
		}
	}
	
	public TasBeanException(ErrorType errorType, Throwable cause) {
		super(errorType.getMessage(), cause);
		this.errorType = errorType;;
	}

	public TasBeanException(Throwable cause) {
		super(cause);
		if(cause instanceof TasBeanException) {
			TasBeanException tasBeanException = (TasBeanException)cause;
			errorType = tasBeanException.getErrorType();
		} else {
			errorType = null;
		}
	}
	
	public ErrorType getErrorType() {
		return errorType;
	}
}