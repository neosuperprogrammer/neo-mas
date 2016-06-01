package com.tionsoft.mas.tas.taslet;

import com.tionsoft.mas.tas.exception.ErrorType;
import com.tionsoft.mas.tas.exception.TasException;

/**
 * TAS Exception 발생시 발생하는Exception.  
 * @version : 1.0.0
 * 
 */
public class TasletException extends TasException {

	private static final long serialVersionUID = -6449098791226129006L;
	private final ErrorType errorType;

	/**
	  * 생성자.
	  */
	public TasletException() {
		super();
		errorType = null;
	}

	/**
	  * 생성자.
	  * @param message - Exception 발생시 메세지.  
	  */
	public TasletException(String message) {
		super(message);
		errorType = null;
	}
	
	/**
	  * 생성자.
	  * @param errorType - Exception 발생시 Type.  
	  */
	public TasletException(ErrorType errorType) {
		super(errorType.getMessage());
		this.errorType = errorType;
	}

	/**
	  * 생성자.
	  * @param message - Exception 발생시 메세지.  
	  * @param cause - Exception .  
	  */
	public TasletException(String message, Throwable cause) {
		super(message, cause);
		if(cause instanceof TasException) {
			TasException tasException = (TasException)cause;
			errorType = tasException.getErrorType();
		} else {
			errorType = null;
		}
		
	}
	
	/**
	  * 생성자.
	  * @param errorType - Exception 발생시 Type.  
	  * @param cause - Exception .  
	  */
	public TasletException(ErrorType errorType, Throwable cause) {
		super(errorType.getMessage(), cause);
		this.errorType = errorType;
	}

	/**
	  * 생성자.
	  * @param cause - Exception .  
	  */
	public TasletException(Throwable cause) {
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
	
//	@Override
//	public String toString() {
//	    String s = getClass().getName();
//	    return (message != null) ? (s + ": " + message) : s;
//	}
}