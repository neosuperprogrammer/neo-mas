package com.tionsoft.mas.tas.filter.background;

import com.tionsoft.mas.tas.license.management.AuthenticationManagement;
import com.tionsoft.mas.tas.taslet.TasMessage;
import com.tionsoft.mas.tas.taslet.TasMessage.MessageDirection;
import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import com.tionsoft.platform.error.ErrorCode;
import com.tionsoft.platform.exception.PlatformException;
import com.tionsoft.platform.filter.Filter;
import com.tionsoft.platform.filter.FilterChain;
import com.tionsoft.platform.utils.StringUtils;

/**
 * This class is a background class which is executed in the background.
 * It is responsible for checking authentication according to service id and wifi.
 * As as result of executing this class, authentication information is created or renewed.
 * @author Administrator
 *
 */
public final class TasBackgroundAuthenticationFilter implements Filter {
	
	private AuthenticationManagement authManagement=null;
	
	public void setAuthManagement(AuthenticationManagement authManagement) {
		this.authManagement = authManagement;
	}


	@Override
	public void doFilter(Object message, FilterChain chain) throws PlatformException {
		
		TasMessage tMessage = (TasMessage) message;
		
		TasRequest tasRequest = tMessage.getTasRequest();
		TasResponse tasResonse =  tMessage.getTasResponse();
		
		MessageDirection di =  tMessage.getMessageDirection();
		if(di.equals(MessageDirection.SENT))authentication(tasRequest,tasResonse);
		
		chain.doFilter(tMessage);
		
	}
	
	
	private void authentication(TasRequest req, TasResponse res) throws PlatformException
	{
		try{
			
			String authenticationModule = "Y";
			
			try{
				authenticationModule = StringUtils.defaultIfEmpty(System.getProperty("tmas.Authentication.handling"), "Y") ;
				
			}catch(Exception e)
			{
				authenticationModule = "Y";
			}
			
			if(authenticationModule.equalsIgnoreCase("Y"))
			{
				String wifi;
				int serviceId;
				
				try{
					wifi = req.getPlatformHeader("WIFI_MAC", String.class);
					serviceId = req.getPlatformHeader("SERVICE_ID", Integer.class);
				}catch(Exception e)
				{
					throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE
															, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.AUTH_NO_HEAD
															,e);
				}
		
				String strServiceId = String.valueOf(serviceId);
				authManagement.checkAuthentication(strServiceId, wifi.trim());
				
			}
			
		}catch(Exception e)
		{
			throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.AUTH,
					  								ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.SERVICE_USER_AUTH,e);
			
			/*String wifi;
			int serviceId;
			
			try{
				wifi = req.getPlatformHeader("WIFI_MAC", String.class);
				serviceId = req.getPlatformHeader("SERVICE_ID", Integer.class);
			}catch(Exception e1)
			{
				throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE, "36006",e1);
			}
			String strServiceId = String.valueOf(serviceId);
			authInfo.setAuth(strServiceId, wifi,new Authentication());*/
		}
		
	}

}
