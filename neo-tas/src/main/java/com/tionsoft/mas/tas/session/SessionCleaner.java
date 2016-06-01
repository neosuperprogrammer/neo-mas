package com.tionsoft.mas.tas.session;

import java.util.Iterator;
import java.util.Queue;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tionsoft.platform.error.ErrorCode;
import com.tionsoft.platform.exception.PlatformException;


public class SessionCleaner {
	
	private Logger logger  = LoggerFactory.getLogger(SessionCleaner.class);
	UselessSession uselessSessionMapObject = null;
	
	
	public void setUselessSession(UselessSession uselessSessionMapObject)
	{
		this.uselessSessionMapObject = uselessSessionMapObject;
	}
	
	
	public void clean() {
		
		if(logger.isDebugEnabled()){
			logger.debug("SessionCleaner is started");
		}
		
		try{
			Queue<IoSession> sessions =  this.uselessSessionMapObject.getUselessSessions();
			
			Iterator<IoSession> itr =  sessions.iterator();
			
			IoSession useLessSession = null;
			
			while(itr.hasNext())
			{
				useLessSession = itr.next();
				
				logger.debug("Session ["+useLessSession.getId()+"] is cleansed");
				
				useLessSession.close(true);
			}
			
		}catch(Exception e)
		{
			PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE,ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.QUEUE_LICENSE_SCHEDULER , e);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("SessionCleaner is ended");
		}
		
		
	} 

}
