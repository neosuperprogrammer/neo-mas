package com.tionsoft.mas.tas.license.db.domain;




/**
 * get an authentication of user according to service id
 * @author Administrator
 *
 */
public class AuthenticationDomain {
	
	private String serviceId;
	private String userId;
	private boolean isUse=false;
	
	
	public String getServiceId() {
		return serviceId;
	}
	
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isUse() {
		return isUse;
	}

	public void setUse(boolean isUse) {
		this.isUse = isUse;
	}
	

}
