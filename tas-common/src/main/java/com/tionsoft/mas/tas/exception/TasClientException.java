package com.tionsoft.mas.tas.exception;

public class TasClientException extends RuntimeException {

	private static final long serialVersionUID = 5997697929530973325L;
	private final ErrorType errorType;

	public TasClientException() {
		super();
		errorType = null;
	}

	public TasClientException(String message) {
		super(message);
		errorType = null;
	}
	
	public TasClientException(ErrorType errorType) {
		super(errorType.getMessage());
		this.errorType = errorType;
	}

	public TasClientException(String message, Throwable cause) {
		super(message, cause);
		if(cause instanceof TasClientException) {
			TasClientException tasException = (TasClientException)cause;
			errorType = tasException.getErrorType();
		} else {
			errorType = null;
		}
	}
	
	public TasClientException(ErrorType errorType, Throwable cause) {
		super(errorType.getMessage(), cause);
		this.errorType = errorType;;
	}

	public TasClientException(Throwable cause) {
		super(cause);
		if(cause instanceof TasClientException) {
			TasClientException tasException = (TasClientException)cause;
			errorType = tasException.getErrorType();
		} else {
			errorType = null;
		}
	}
	
	public ErrorType getErrorType() {
		return errorType;
	}
}