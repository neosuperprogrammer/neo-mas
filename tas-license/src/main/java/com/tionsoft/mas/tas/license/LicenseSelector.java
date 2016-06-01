package com.tionsoft.mas.tas.license;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tionsoft.mas.tas.license.db.domain.LicenseDomain;
import com.tionsoft.mas.tas.license.db.license.LicenseDAO;
import com.tionsoft.platform.encrypt.AESPassword;
import com.tionsoft.platform.error.ErrorCode;
import com.tionsoft.platform.exception.PlatformException;
import com.tionsoft.platform.license.encode.License;
import com.tionsoft.platform.license.encode.LicenseEncrypter;
import com.tionsoft.platform.utils.DateUtil;


/**
 * Get License Domain by a day
 * @author Administrator
 *
 */

@Component
public class LicenseSelector {
	
	private static Logger logger  = LoggerFactory.getLogger(LicenseSelector.class);
	
	@Autowired(required=false)
	private LicenseDAO licenseDao;
	private LicenseEncrypter encrypt;
	
	
	public void init() throws PlatformException
	{
		try {
			encrypt = new LicenseEncrypter(AESPassword.getPassword());
			
		} catch (Exception e) {
			throw PlatformException.createException(ErrorCode.TION_MANAGEMENT_SYSTEM_ERROR.LICENSE, ErrorCode.TION_MANAGEMENT_SYSTEM_CODE.LICENSE_DECRYPT, e);
		}
	}
	
	
	/**
	 * set License
	 * @param license
	 */
	public void setLicenseDao(LicenseDAO licenseDao)
	{
		this.licenseDao = licenseDao;
	}
	
	
	
	/**
	 * get License instance from DB
	 * @return
	 * @throws PlatformException 
	 */
	public License getLicense() throws PlatformException
	{
		List<LicenseDomain> licenseDomains = licenseDao.select();
		
		
		if(licenseDomains==null || licenseDomains.isEmpty())
		{
			throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.NO_LICENSE, "There is no license information");
		}
		
		LicenseDomain licenseDomain = chooseLicense(licenseDomains);
		
		licenseDomain.setLicenseEncrypt(encrypt);
		
		License license = new License(licenseDomain.getUserCnt(),licenseDomain.getDeviceCnt(), licenseDomain.getConcurrentUseCnt(),licenseDomain.getExpiredDate(),licenseDomain.getApplicationUserCnt(),"");
		try {
			logger.info("======================================================================================================");
			logger.info("===============================License Information is started=========================================");
			logger.info("===============================ueser cnt : "+license.getUserCnt()+"=========================================");
			logger.info("===============================device cnt : "+license.getDeviceCnt()+"=========================================");
			logger.info("===============================concurrent device cnt : "+license.getConcurrentUseCnt()+"=========================================");
			logger.info("===============================application use cnt : "+license.getApplicationUserCnt()+"=========================================");
			logger.info("===============================expired date : "+DateUtil.format("yyyyMMdd","yyyy-MM-dd",license.getExpiredDate())+"=========================================");
			logger.info("===============================License Information is ended===========================================");
		} catch (ParseException e) {
			throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.LICENSE_PARSE_EXPIRE_DATE, e);
		}
		return license;
	}
	
	
	private LicenseDomain chooseLicense(List<LicenseDomain> licenseDomains) throws PlatformException
	{
		LicenseDomain lastLicenseDomain = null;
		Date sourceDate = null;
		LicenseDomain licenseDomain = null;
		if(licenseDomains.size()==1) {
			
			licenseDomain =  licenseDomains.get(0);
			licenseDomain.setLicenseEncrypt(encrypt);
			lastLicenseDomain = licenseDomain;
			
		}else if(licenseDomains.size() >1)
		{
			for(LicenseDomain temp:licenseDomains)
			{
				temp.setLicenseEncrypt(encrypt);
				
				try{
					if(sourceDate==null){
						sourceDate = DateUtil.parseDate(temp.getExpiredDate(), "yyyyMMdd");
						lastLicenseDomain = temp;
					}
					else {
						Date tempDate = DateUtil.parseDate(temp.getExpiredDate(), "yyyyMMdd");
						
						if(sourceDate.before(tempDate)){
							sourceDate = tempDate;
							lastLicenseDomain = temp;
						}
						
					}
				}catch(ParseException e)
				{
					throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.LICENSE_PARSE_EXPIRE_DATE, e);
				}
				
				
			}
		}
		
		return lastLicenseDomain;
		
	}
	
	
	
		

}
