package com.tionsoft.mas.tas.license.scheduler;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tionsoft.mas.tas.license.management.AuthenticationKey;
import com.tionsoft.mas.tas.license.management.AuthenticationManagement;
import com.tionsoft.mas.tas.license.management.AuthenticationMap;
import com.tionsoft.platform.error.ErrorCode;
import com.tionsoft.platform.exception.PlatformException;

public class AuthenticationScheduler {
	private Logger logger = LoggerFactory.getLogger(AuthenticationScheduler.class); 
	private AuthenticationManagement auth;
	private AuthenticationMap authMap;
	
	
	public void setAuthenticationManagement(AuthenticationManagement auth)
	{
		this.auth = auth;
	}
	
	public void setAuthenticationMap(AuthenticationMap authMap)
	{
		this.authMap = authMap;
	}

	public void checkAuth() {
		
		if(logger.isDebugEnabled()){
			logger.debug("AuthenticationScheduler is started");
		}
		try{
			
			List<String> keys = authMap.getKeys();
			
			Iterator<String> itr  =  keys.iterator();
			
			String key ="";
			String [] splitKeys=null;
			while(itr.hasNext())
			{
				key = itr.next();
				splitKeys = AuthenticationKey.splitKey(key);
				this.auth.checkAuthentication(splitKeys[0],splitKeys[1]);
				
			}
			
		}catch(Exception e)
		{
			PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.AUTHSCHEDULER, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.SERVICE_USER_AUTH, e);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("AuthenticationScheduler is ended");
		}
		
	}

}
