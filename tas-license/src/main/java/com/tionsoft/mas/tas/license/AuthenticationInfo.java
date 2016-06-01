package com.tionsoft.mas.tas.license;

import org.springframework.beans.factory.annotation.Autowired;

import com.tionsoft.mas.tas.license.auth.Authentication;
import com.tionsoft.mas.tas.license.management.AuthenticationKey;
import com.tionsoft.mas.tas.license.management.AuthenticationMap;

public class AuthenticationInfo {
	
	@Autowired
	private AuthenticationMap authMap;
	
	public void setAuthenticationMap(AuthenticationMap authMap)
	{
		this.authMap = authMap;
	}
	
	public Authentication getAuth(String serviceId,String wifi) {
		
		String key = AuthenticationKey.makeKey(serviceId, wifi);
		return authMap.get(key);
	}
	
	public void setAuth(String serviceId,String wifi,Authentication auth) {
		
		String key = AuthenticationKey.makeKey(serviceId, wifi);
		authMap.put(key,auth);
	}


	public boolean isAuth(String serviceId,String wifi) {
		return authMap.isAuth(AuthenticationKey.makeKey(serviceId, wifi));
	}
	
}
