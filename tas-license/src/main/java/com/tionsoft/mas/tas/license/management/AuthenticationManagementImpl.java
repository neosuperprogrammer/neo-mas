package com.tionsoft.mas.tas.license.management;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tionsoft.mas.tas.license.auth.Authentication;
import com.tionsoft.mas.tas.license.db.authentication.AuthenticateDAO;
import com.tionsoft.mas.tas.license.db.domain.AuthenticationDomain;
import com.tionsoft.platform.error.ErrorCode;
import com.tionsoft.platform.exception.PlatformException;
import com.tionsoft.platform.utils.DateUtil;

@Component
public class AuthenticationManagementImpl implements AuthenticationManagement {
	
	@Autowired(required=false)
	private AuthenticationMap authMap;
	
	@Autowired(required=false)
	private AuthenticateDAO authDao;
	
	public void setAuthenticationMap(AuthenticationMap authMap)
	{
		this.authMap = authMap;
	}
	
	public void setAuthenticateDAO(AuthenticateDAO authDao)
	{
		this.authDao = authDao;
	}


	/*
	 * (non-Javadoc)
	 * @see com.btb.meap.mas.tas.management.AuthenticationManagement#getAuthentication(java.lang.String, java.lang.String)
	 */
	@Override
	public Authentication getAuthentication(String serviceId, String wifi) throws PlatformException {
		
		String key = AuthenticationKey.makeKey(serviceId,wifi);
		
		Authentication auth = null;
		
		if(authMap.containsKey(key)){
			auth = authMap.get(key);
			
				try {
					if(DateUtil.isPast(auth.getCheckDate())){
						checkAuthentication(serviceId,wifi);
						auth = authMap.get(key);
					}
				} catch (ParseException e) {
					throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.AUTH, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.AUTH_PARSE_CHECK_DATE, e);
					
				}
			
		}else {
			checkAuthentication(serviceId,wifi);
			auth = authMap.get(key);
		}
		
		
		return auth;
	}

	public void checkAuthentication(String serviceId, String wifi) throws PlatformException {
		
		AuthenticationDomain authDomain = null;
		authDomain = authDao.select(serviceId,wifi);
		
		Authentication auth = new Authentication();
		auth.setAuth(authDomain.isUse());
		auth.setCheckDate(DateUtil.getCurrentDate());
		auth.setServiceId(authDomain.getServiceId());
		auth.setUserId(authDomain.getUserId());
		
		authMap.put(AuthenticationKey.makeKey(serviceId,wifi), auth);
		
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see com.btb.meap.mas.tas.management.AuthenticationManagement#isAuthenticated(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isAuthenticated(String serviceId, String wifi) throws PlatformException {
		
		String key = AuthenticationKey.makeKey(serviceId,wifi);
		
		Authentication auth = null;
		boolean isAuth;
		
		if(authMap.containsKey(key)){
			auth = authMap.get(key);
			
			try {
				if(DateUtil.isPast(auth.getCheckDate())){
					checkAuthentication(serviceId,wifi);
					auth = authMap.get(key);
				}
			} catch (ParseException e) {
				throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.AUTH,ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.AUTH_PARSE_CHECK_DATE, e);
				
			}
			
			isAuth = auth.isAuth();
		}else {
			checkAuthentication(serviceId,wifi);
			auth = authMap.get(key);
			isAuth = auth.isAuth();
		}
		
		return isAuth;
	}

	@Override
	public  void clean() throws ParseException {
		
		List<String> keys = authMap.getKeys();
		
		Authentication auth=null;
		
		String key="";
		String checkDate="";
		Date d;
		
		for(int i=0; i < keys.size();i++)
		{
			key = keys.get(i);
			auth = authMap.getAuth(key);
			checkDate = auth.getCheckDate();
			d= DateUtil.parseDate(checkDate, "yyyyMMdd");
			d = DateUtil.add(d, Calendar.DATE, 2);
			
			if(DateUtil.isPast(d)){
				authMap.remove(key);
			}
		}
		
	}

}
