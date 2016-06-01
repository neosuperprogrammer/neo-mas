package com.tionsoft.mas.tas.license.db.domain;

import com.tionsoft.platform.exception.PlatformException;
import com.tionsoft.platform.license.encode.LicenseEncrypter;

public class LicenseDomain {
	
	private String userCnt;
	private String deviceCnt;
	private String concurrentUseCnt;
	private String expiredDate;
	private String applicationUserCnt="0";
	private LicenseEncrypter encrypt=  null;
	
	public LicenseEncrypter getLicenseEncrypt() {
		return encrypt;
	}


	public void setLicenseEncrypt(LicenseEncrypter encrypt) {
		this.encrypt = encrypt;
	}

	public void setUserCnt(String userCnt) {
		this.userCnt = userCnt;
	}
	
	public String getUserCnt() throws PlatformException {
		return encrypt.decrypt(userCnt);
	}
	
	public String getDeviceCnt() throws PlatformException {
		return encrypt.decrypt(deviceCnt);
	}
	public String getConcurrentUseCnt() throws PlatformException {
		return encrypt.decrypt(concurrentUseCnt);
	}
	public String getExpiredDate() throws PlatformException {
		return encrypt.decrypt(expiredDate);
	}
	
	public void setDeviceCnt(String deviceCnt) {
		this.deviceCnt = deviceCnt;
	}
	
	public void setConcurrentUseCnt(String concurrentUseCnt) {
		this.concurrentUseCnt = concurrentUseCnt;
	}
	
	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}
	
	
	public String getApplicationUserCnt() throws PlatformException {
		return encrypt.decrypt(applicationUserCnt);
	}


	public void setApplicationUserCnt(String applicationUserCnt) {
		this.applicationUserCnt = applicationUserCnt;
	}

}
