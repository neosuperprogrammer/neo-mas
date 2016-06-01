package com.tionsoft.mas.tas.exception;

public class TasDumpPrintException extends RuntimeException {

	private static final long serialVersionUID = -3486123363953056816L;
	private final ErrorType errorType;

	public TasDumpPrintException() {
		super();
		errorType = null;
	}

	public TasDumpPrintException(String message) {
		super(message);
		errorType = null;
	}
	
	public TasDumpPrintException(ErrorType errorType) {
		super(errorType.getMessage());
		this.errorType = errorType;
	}

	public TasDumpPrintException(String message, Throwable cause) {
		super(message, cause);
		if(cause instanceof TasDumpPrintException) {
			TasDumpPrintException tasDumpException = (TasDumpPrintException)cause;
			errorType = tasDumpException.getErrorType();
		} else {
			errorType = null;
		}
	}
	
	public TasDumpPrintException(ErrorType errorType, Throwable cause) {
		super(errorType.getMessage(), cause);
		this.errorType = errorType;;
	}

	public TasDumpPrintException(Throwable cause) {
		super(cause);
		if(cause instanceof TasDumpPrintException) {
			TasDumpPrintException tasDumpException = (TasDumpPrintException)cause;
			errorType = tasDumpException.getErrorType();
		} else {
			errorType = null;
		}
	}
	
	public ErrorType getErrorType() {
		return errorType;
	}
}