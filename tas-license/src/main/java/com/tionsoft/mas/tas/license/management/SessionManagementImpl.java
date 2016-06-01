package com.tionsoft.mas.tas.license.management;

import org.apache.mina.core.service.IoAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tionsoft.mas.tas.license.LicenseInfo;
import com.tionsoft.mas.tas.license.SessionInfo;
import com.tionsoft.mas.tas.session.SessionManagement;


@Component
public class SessionManagementImpl implements SessionManagement {

	private Logger logger = LoggerFactory.getLogger(SessionManagementImpl.class); 
	private int sessionCount = 0;
	private IoAcceptor acceptor = null;
	private boolean isViolated = false;
	
	@Autowired(required=false)
	private LicenseInfo licenseInfo = null;
	
	@Autowired(required=false)
	private SessionInfo sessionInfo = null;
	
	public void setSessionInfo(SessionInfo sessionInfo)
	{
		this.sessionInfo = sessionInfo;
	}
	
	public void setLicenseInfo(LicenseInfo licenseInfo)
	{
		this.licenseInfo = licenseInfo;
	}
	
	@Override
	public void setIoAcceptor(IoAcceptor acceptor) {
		
		this.acceptor = acceptor;
		
	}

	@Override
	public int getCurrentSessionCnt() {
		return sessionCount;
	}
	
	private void checkSessionCnt()
	{
		if(acceptor !=null)	sessionCount = acceptor.getManagedSessionCount();
		else {
			if(logger.isDebugEnabled())
			{
				logger.debug("IoAcceptor is null");
			}
			
		}
	}

	public boolean isViolated() {
		checkConcurrentUser();
		return isViolated;
	}
	
	private void checkConcurrentUser()
	{
		checkSessionCnt();
		int concurrentUserCnt =  Integer.parseInt(licenseInfo.getLicense().getConcurrentUseCnt());
		
		sessionInfo.setSessionCount(concurrentUserCnt);
		if(sessionCount <= concurrentUserCnt)
		{
			isViolated=false;
		}else {
			isViolated =true;
		}
		
		sessionInfo.setViolated(isViolated);
	}

	

}
