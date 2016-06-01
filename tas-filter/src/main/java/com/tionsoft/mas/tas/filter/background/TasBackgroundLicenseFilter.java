package com.tionsoft.mas.tas.filter.background;

import com.tionsoft.mas.tas.license.LicenseInfo;
import com.tionsoft.mas.tas.license.management.LicenseManagement;
import com.tionsoft.mas.tas.taslet.TasMessage;
import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import com.tionsoft.mas.tas.taslet.TasMessage.MessageDirection;
import com.tionsoft.platform.exception.PlatformException;
import com.tionsoft.platform.filter.Filter;
import com.tionsoft.platform.filter.FilterChain;
import com.tionsoft.platform.utils.DateUtil;

/**
 * License
 * @author Administrator
 *
 */
public final class TasBackgroundLicenseFilter implements Filter {
	
	private LicenseManagement licenseManagement=null;
	private LicenseInfo licenseInfo;
	
	private String inspectionDt;
	
	
	public void setLicenseManagement(LicenseManagement licenseManagement)
	{
		this.licenseManagement = licenseManagement;
	}
	
	public void setLicenseInfo(LicenseInfo licenseInfo)
	{
		this.licenseInfo = licenseInfo;
	}
	
	
	@Override
	public void doFilter(Object message, FilterChain chain) throws PlatformException {
		
		TasMessage tMessage = (TasMessage) message;
		
		TasRequest tasRequest = tMessage.getTasRequest();
		TasResponse tasResonse =  tMessage.getTasResponse();
		
		MessageDirection di =  tMessage.getMessageDirection();
		if(di.equals(MessageDirection.SENT))license(tasRequest,tasResonse);
		
		chain.doFilter(tMessage);
		
	}
	
	private void license(TasRequest req, TasResponse res) 
	{
		try{
			if(inspectionDt==null){
				inspectionDt = DateUtil.getCurrentDate();
			}
			
			if(!inspectionDt.equals(DateUtil.getCurrentDate())){
				licenseManagement.checkLicense();
				licenseInfo.setLicense(licenseManagement.getLicense());
			}
			
		}catch(Exception e)
		{
			licenseInfo.setIsValid(false);
		}
		
	}
	

}
