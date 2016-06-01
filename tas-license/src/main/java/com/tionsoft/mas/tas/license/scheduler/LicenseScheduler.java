package com.tionsoft.mas.tas.license.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tionsoft.mas.tas.license.management.LicenseManagement;
import com.tionsoft.platform.error.ErrorCode;
import com.tionsoft.platform.exception.PlatformException;

public class LicenseScheduler {
	private Logger logger = LoggerFactory.getLogger(LicenseScheduler.class); 
	
	private LicenseManagement licenseMgmt; 
	
	public void setLicenseManagement(LicenseManagement licenseMgmt)
	{
		this.licenseMgmt = licenseMgmt;
	}

	public void licenseCheck() {
		
		if(logger.isDebugEnabled()){
			logger.debug("LicenseScheduler is started");
		}
		
		try{
			this.licenseMgmt.checkLicense();
			
		}catch(Exception e)
		{
			PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE,ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.QUEUE_LICENSE_SCHEDULER , e);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("LicenseScheduler is ended");
		}
		
		
	}

}
