package com.tionsoft.mas.tas.license.management;

import java.text.ParseException;

import com.tionsoft.mas.tas.license.auth.Authentication;
import com.tionsoft.platform.exception.PlatformException;

public interface AuthenticationManagement {

	
	/**
	 * get an authentication according to service id and wifi
	 * @param serviceId
	 * @param wifi
	 * @return
	 * @throws PlatformException
	 */
	public Authentication getAuthentication(String serviceId, String wifi)throws PlatformException;
	
	
	/**
	 * TION_MEAP_APPLICATION_SYSTEM_ERROR
	 * @param serviceId
	 * @param wifi
	 * @return
	 * @throws PlatformException
	 */
	public void checkAuthentication(String serviceId, String wifi) throws PlatformException; 
	
	/**
	 * TION_MEAP_APPLICATION_SYSTEM_ERROR
	 * @param serviceId
	 * @param wifi
	 * @return
	 * @throws PlatformException
	 */
	public boolean isAuthenticated(String serviceId, String wifi) throws PlatformException; 
	
	
	/**
	 * redundant authentication information is cleansed
	 * @param serviceId
	 * @param wifi
	 * @return
	 * @throws PlatformException
	 */
	public void clean()throws ParseException; 
}
