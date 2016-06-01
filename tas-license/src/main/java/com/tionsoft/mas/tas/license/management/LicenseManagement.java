package com.tionsoft.mas.tas.license.management;

import com.tionsoft.platform.exception.PlatformException;
import com.tionsoft.platform.license.encode.License;

public interface LicenseManagement extends Management {
	
	/**
	 * get a license
	 * @return
	 */
	public License getLicense();
	
	/**
	 * check a license
	 * @throws PlatformException
	 */
	public void checkLicense() throws PlatformException; 
}
