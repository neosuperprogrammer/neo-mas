package com.tionsoft.mas.tas.license.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tionsoft.mas.tas.session.SessionManagement;
import com.tionsoft.platform.error.ErrorCode;
import com.tionsoft.platform.exception.PlatformException;

public class SessionScheduler {
	
	private Logger logger = LoggerFactory.getLogger(SessionScheduler.class); 
	private SessionManagement session;
	
	
	public void setSessionManagement(SessionManagement session)
	{
		this.session = session;
	}
	

	public void checkSession() {
		
		if(logger.isDebugEnabled()){
			logger.debug("SessionScheduler is started");
		}
		
		try{
			
			if(session.isViolated())
			{
				PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.SESSIONSCHEDULER, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.LICNESE_LARGE_CONCURRENT_USER_CNT, "");
			}
			
			
			
		}catch(Exception e)
		{
			PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.SESSIONSCHEDULER, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.SESSION_CHECK_ERROR, e);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("SessionScheduler is ended with " + session.isViolated());
		}
		
	}

}
