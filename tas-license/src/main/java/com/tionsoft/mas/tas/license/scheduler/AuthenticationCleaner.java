package com.tionsoft.mas.tas.license.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tionsoft.mas.tas.license.management.AuthenticationManagement;
import com.tionsoft.platform.error.ErrorCode;
import com.tionsoft.platform.exception.PlatformException;

public class AuthenticationCleaner {
	private Logger logger = LoggerFactory.getLogger(AuthenticationCleaner.class); 
	private AuthenticationManagement auth;
	
	public void setAuthenticationManagement(AuthenticationManagement auth)
	{
		this.auth = auth;
	}

	public void clean() {
		
		if(logger.isDebugEnabled()){
			logger.debug("AuthenticationCleaner is started");
		}
		try{
			
			this.auth.clean();
			
		}catch(Exception e)
		{
			PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.AUTHSCHEDULER, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.SERVICE_USER_CLEAN, e);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("AuthenticationCleaner is ended");
		}
		
	}

}
