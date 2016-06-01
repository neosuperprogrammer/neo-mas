package com.tionsoft.mas.tas.license.management;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tionsoft.mas.tas.license.LicenseInfo;
import com.tionsoft.mas.tas.license.LicenseSelector;
import com.tionsoft.mas.tas.license.LicenseValidator;
import com.tionsoft.platform.error.ErrorCode;
import com.tionsoft.platform.exception.PlatformException;
import com.tionsoft.platform.license.encode.License;
import com.tionsoft.platform.utils.DateUtil;

/**
 * Get a license information
 * 
 * @author Administrator
 *
 */

@Component
public class LicenseManagementImpl implements LicenseManagement{
	
	private static Logger logger = LoggerFactory.getLogger(LicenseManagementImpl.class);
	
	@Autowired(required=false)
	private LicenseSelector licenseSelector;
	@Autowired(required=false)
	private LicenseValidator licenseValidator;
	@Autowired(required=false)
	private LicenseInfo licenseInfo;
	
	private License license;

	
	/**
	 * set a license info
	 * @param licenseInfo
	 */
	public void setLicenseInfo(LicenseInfo licenseInfo)
	{
		this.licenseInfo = licenseInfo;
	}
	
	/**
	 * set a license selector
	 * @param licenseSelector
	 */
	public void setLicenseSelector(LicenseSelector licenseSelector)
	{
		this.licenseSelector = licenseSelector;
	}
	
	/**
	 * set a license validator
	 * @param licenseValidator
	 */
	public void setLicenseValidator(LicenseValidator licenseValidator)
	{
		this.licenseValidator = licenseValidator;
	}
	
	
	
	public void init() throws PlatformException
	{
		license =  licenseSelector.getLicense();
		licenseInfo.setLicense(license);
		boolean isValid =isValid();
		licenseInfo.setIsValid(isValid);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.btb.meap.mas.tas.management.LicenseManagement#checkLicense()
	 */
	public void checkLicense() throws PlatformException
	{
		try{
			license =  licenseSelector.getLicense();
			licenseInfo.setLicense(license);
			boolean isValid = isValid();
			if(licenseInfo.isValid()!= isValid)
			{
				licenseInfo.setIsValid(isValid);
			}
		}catch(PlatformException e)
		{
			licenseInfo.setIsValid(false);
			throw e;
		}
		
		
	}
	
	private boolean isValid() throws PlatformException
	{
		boolean isValid =true;
		
		Date expireDate = null;
		
		try {
			expireDate = DateUtil.parseDate(this.license.getExpiredDate(),"yyyyMMdd");
			
			if(logger.isDebugEnabled()) logger.debug("expired date : " + DateUtil.formatDate(expireDate, "yyyy-MM-dd"));
			
		} catch (ParseException e) {
			throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE,ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.LICENSE_PARSE_EXPIRE_DATE, e);
		}
		
		if(DateUtil.isPast(expireDate)) isValid = false;
		
		if(isValid)
		{
			if(this.licenseValidator.isUserRegisterValid(license.getUserCnt()))
			{
				if(this.licenseValidator.isDeviceRegisterValid(license.getDeviceCnt())){
					isValid = true;
				}else {
					throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.LICENSE_LARGE_DEVICE_CNT, "The number of device registered is larger than the number of user able to be registered");
				}
				
			}else {
				throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.LICENSE_LARGE_USER_CNT, "The number of user registered is larger than the number of user able to be registered");
			}
		}
		
		return isValid;
		
	}


	/*
	 * (non-Javadoc)
	 * @see com.btb.meap.mas.tas.management.LicenseManagement#getLicense()
	 */
	@Override
	public License getLicense() {
		return license;
	}
	

}
