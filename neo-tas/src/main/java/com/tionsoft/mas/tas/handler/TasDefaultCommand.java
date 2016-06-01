package com.tionsoft.mas.tas.handler;

import java.io.*;

import org.apache.mina.core.session.*;
import org.springframework.beans.factory.*;
import org.springframework.context.*;
import org.springframework.jdbc.*;
import org.springframework.transaction.*;

import com.tionsoft.mas.tas.*;
import com.tionsoft.mas.tas.bean.platform.*;
import com.tionsoft.mas.tas.exception.*;
import com.tionsoft.mas.tas.pushlet.*;
import com.tionsoft.mas.tas.taslet.*;
import com.tionsoft.platform.exception.PlatformException;

public class TasDefaultCommand extends TasCommand {
	
	private TasContext tasContext;
	
	public void setTasContext(TasContext tasContext) {
		this.tasContext = tasContext;
	}
	
    @Override
	public void execute(NextCommand next, IoSession session, Object message) throws Exception {
    	
    	TasMessage tasMessage = (TasMessage)message;
    	String version = tasMessage.getTasRequest().getPlatformHeader().getValue(PlatformHeader.VERSION, String.class);
    	
    	try {
    		ApplicationContext ac = tasContext.getApplicationContext();
    		Object obj = ac.getBean(getTasletId(tasMessage));
    		
    		if(obj instanceof TasContextAware) {
    			TasContextAware aware = ac.getBean(getTasletId(tasMessage), TasContextAware.class);
    			aware.setTasContext(tasContext);
    		}
    		
    		if(obj instanceof Taslet) {
    			Taslet taslet = ac.getBean(getTasletId(tasMessage), Taslet.class);
        		taslet.execute(tasMessage.getTasRequest(), tasMessage.getTasResponse());
    		} else if (obj instanceof Pushlet) {
    			Pushlet pushlet = ac.getBean(getTasletId(tasMessage), Pushlet.class);
    			TasPushResponse pushResponse = pushlet.execute(tasMessage.getTasRequest(), tasMessage.getTasResponse());
    			tasMessage.setTasPushResponse(pushResponse);
    		}
    		
    		next.execute(session, tasMessage);
    	} catch (NoSuchBeanDefinitionException e) {
    		ErrorType errorType = new ErrorType(tasMessage.getTasRequest().getPlatformHeader(),ErrorType.ERROR_CODE_NOT_FOUND_TASLET, "Taslet is Not Found : " + getTasletId(tasMessage) + " in [" + tasContext.getTcpAppConfig().getName() + "]");
    		throw new TasException(errorType);
    		
    		
    	} catch (Throwable e) {
    		
    		ErrorType errortype = null;
		    if (e instanceof TasletException) {

    			e.printStackTrace();
    			
    		    TasletException tasletException = (TasletException)e;
    		    ErrorType subErrorType = tasletException.getErrorType();
    		    if(subErrorType != null){
    		    	errortype = subErrorType;
    		    }
    		    
		    }

		    checkException(e,tasMessage.getTasRequest().getPlatformHeader());
		    
		    if( version != null && version.equals("PHV103")) {
			    if(errortype == null){
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					PrintStream pinrtStream = new PrintStream(out);  
					e.printStackTrace(pinrtStream);
					String error_msg = out.toString();
					
			    	ErrorType subErrorType = new ErrorType(tasMessage.getTasRequest().getPlatformHeader(),ErrorType.ERROR_CODE_TASLET, error_msg);
			    	
			    	throw new TasException(subErrorType);
			    }else{
			    	throw new TasException(errortype);
			    }
		    }else{
		    	if(errortype.getPlatformHeader() == null){
					ErrorType subErrorType = new ErrorType(tasMessage.getTasRequest().getPlatformHeader(),errortype.getCode(), errortype.getMessage());
					throw new TasException(subErrorType);
		    	}else{
		    		throw new TasException(errortype);
		    	}
		    	
		    }

    	}
    	
    }
    
	private String getTasletId(TasMessage tcpMessage) {
		String appId = tcpMessage.getTasRequest().getPlatformHeader(PlatformHeader.APPLICATION_ID, String.class);
		String msgId = tcpMessage.getTasRequest().getPlatformHeader(PlatformHeader.MESSAGE_ID, String.class);
		return appId + "." + msgId;
	}
	
	private void checkException(Throwable e,PlatformHeader platformHeader) {
		while(e != null){
			if (e instanceof CannotCreateTransactionException) {
	    		ErrorType errorType = new ErrorType(platformHeader,ErrorType.ERROR_CODE_CannotCreateTransactionException, "org.springframework.transaction.CannotCreateTransactionException");
	    		throw new TasException(errorType);
		    }
			
		    if (e instanceof ClassCastException) {
		    	ClassCastException exception = (ClassCastException)e;
		    	ErrorType errorType = new ErrorType(platformHeader,ErrorType.ERROR_CODE_ClassCastException, "java.lang.ClassCastException "+exception.getMessage());
	    		throw new TasException(errorType);
		    }
    		
		    if (e instanceof BadSqlGrammarException) {
		    	ErrorType errorType = new ErrorType(platformHeader,ErrorType.ERROR_CODE_BadSqlGrammarException, "org.springframework.jdbc.BadSqlGrammarException");
	    		throw new TasException(errorType);
		    }
		    
		    if (e instanceof TasBeanException) {
		    	TasBeanException tasBeanException = (TasBeanException)e;
		    	ErrorType subErrorType = tasBeanException.getErrorType();
		    	
		    	if(subErrorType.getPlatformHeader() == null){
					ErrorType subSubErrorType = new ErrorType(platformHeader,subErrorType.getCode(), subErrorType.getMessage());
					throw new TasException(subSubErrorType);
		    	}else{
		    		throw new TasException(subErrorType);
		    	}
		    }
		    
		    if (e instanceof TasException) {
        		// TasletException 으로 던지지만 TasHandler exceptionCaught 에서는 Exception Type 으로 받는다.
        		throw new TasException(e);
		    }
		    
		    if (e instanceof PlatformException) {
        		// TasletException 으로 던지지만 TasHandler exceptionCaught 에서는 Exception Type 으로 받는다.
		    	short code =  Short.valueOf(((PlatformException)e).getErrorNumber());
		    	String msg = ((PlatformException)e).getErrorMessage();
		    	ErrorType error = new ErrorType(platformHeader, code, msg);
        		throw new TasException(error,e);
		    }
		    
		    if (e instanceof TasClientException) {
		    	TasClientException tasclientException = (TasClientException)e;
		    	ErrorType subErrorType = tasclientException.getErrorType();
    		    if(subErrorType != null){
    		    	
    		    	if(subErrorType.getPlatformHeader() == null){
    					ErrorType subSubErrorType = new ErrorType(platformHeader,subErrorType.getCode(), subErrorType.getMessage());
    					throw new TasException(subSubErrorType);
    		    	}else{
    		    		throw new TasException(subErrorType);
    		    	}
    		    }else{
    		    	
    		    	ErrorType subSubErrorType = new ErrorType(platformHeader,ErrorType.ERROR_CODE_TAS_CLIENT, "TasClient Exception");
    		    	throw new TasException(subSubErrorType);
    		    }
	    		
		    }
		    
		    e =  e.getCause();
		}

	}
	
}