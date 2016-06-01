package com.tionsoft.mas.tas.license;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tionsoft.mas.tas.license.db.deviceregister.DeviceRegisterDAO;
import com.tionsoft.mas.tas.license.db.userregister.UserRegisterDAO;


@Component
public class LicenseValidator {
	
	@Autowired(required=false)
	private DeviceRegisterDAO device;
	
	@Autowired(required=false)
	private UserRegisterDAO user;
	
	
	public void setDeviceRegisterDao(DeviceRegisterDAO device){
		this.device = device;
	}
	
	
	public  void setUserRegisterDao(UserRegisterDAO user)
	{
		this.user=user;
	}

	
	public boolean isDeviceRegisterValid(String deviceCnt)
	{
		return this.device.isValid(deviceCnt);
	}
	
	
	public boolean isUserRegisterValid(String userCnt)
	{
		return this.user.isValid(userCnt);
	}
	

}
