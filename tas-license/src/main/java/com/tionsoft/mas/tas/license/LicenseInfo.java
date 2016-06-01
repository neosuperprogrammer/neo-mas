package com.tionsoft.mas.tas.license;

import org.springframework.stereotype.Component;

import com.tionsoft.platform.license.encode.License;

@Component
public class LicenseInfo {
	
	private License license;
	private boolean isValid=false;
	private String inspectionDt=null; 
	
	public void setLicense(License license)
	{
		this.license = license;
	}
	
	
	public void setIsValid(boolean isValid)
	{
		this.isValid=isValid;
	}
	
	public boolean isValid()
	{
		return isValid;
	}
	
	
	public License getLicense()
	{
		return license;
	}


	public String getInspectionDt() {
		return inspectionDt;
	}


	public void setInspectionDt(String inspectionDt) {
		this.inspectionDt = inspectionDt;
	}
	
	
	

}
