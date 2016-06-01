package com.tionsoft.mas.tas.taslet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tionsoft.mas.tas.interceptor.InterceptorBeforeHandler;
import com.tionsoft.mas.tas.license.AuthenticationInfo;
import com.tionsoft.mas.tas.license.LicenseInfo;
import com.tionsoft.mas.tas.license.SessionInfo;
import com.tionsoft.mas.tas.queue.TasBackgroundQueue;
import com.tionsoft.mas.tas.taslet.TasMessage.MessageDirection;
import com.tionsoft.platform.error.ErrorCode;
import com.tionsoft.platform.exception.PlatformException;
import com.tionsoft.platform.queue.RequestQueue;
import com.tionsoft.platform.utils.StringUtils;


@Component
public class TasletIntercepterBeforeHandler implements InterceptorBeforeHandler {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	private RequestQueue queue = TasBackgroundQueue.getInstance(10000000);
	
	
	@Autowired(required=false)
	private LicenseInfo licenseInfo;
	
	@Autowired(required=false)
	private AuthenticationInfo authInfo;
	
	@Autowired(required=false)
	private SessionInfo sessionInfo;
	
	
	public TasletIntercepterBeforeHandler()
	{
		System.out.println(">>>>>>>>>>>>>>>>TasletIntercepterBeforeHandler  init>>>>>>>>>>>>>>>>>>>>>");
	}
	
	@Override
	public void handle(TasRequest tReq, TasResponse tRes) throws PlatformException
	{
		if(logger.isDebugEnabled()) logger.debug("TasletIntercepter' tasletBeforeHandler is  started");
		
		try{
			
			TasMessage message = new TasMessage(tReq, tRes,MessageDirection.RECEIVED);
			
			queue.putQueueItemRequest(message);
			synchronized (queue.getQueueItemRequestQueue()) {
				queue.notifyService();
			}
			
			
			try{
				String licenseModule = "true";
				licenseModule = StringUtils.defaultIfEmpty(System.getProperty("tmas.license.handling"), "true") ;
				
				if(licenseModule.equalsIgnoreCase("true") && (licenseInfo==null || !licenseInfo.isValid())){
					throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.LICNESE_PASS_EXPIRE_DATE, "License is not valid");
				}
				
				String authenticationModule = "true";
				authenticationModule = StringUtils.defaultIfEmpty(System.getProperty("tmas.authentication.handling"), "true") ;
				if(authenticationModule.equalsIgnoreCase("true"))
				{
					String wifi;
					int serviceId;
					
					try{
						wifi = tReq.getPlatformHeader("WIFI_MAC", String.class);
						serviceId = tReq.getPlatformHeader("SERVICE_ID", Integer.class);
						
					}catch(Exception e)
					{
						throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.AUTH, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.AUTH_NO_HEAD,e);
					}
					
					if(!authInfo.isAuth(String.valueOf(serviceId),wifi.trim()))
					{
						throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.AUTH, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.NO_AUTH, "service id : "+serviceId+" wifi : "+wifi+" does not have an authentication");
					}
				}
				
				String sessionModule = "true";
				sessionModule = StringUtils.defaultIfEmpty(System.getProperty("tmas.concurrent.session.handling"), "true") ;
				
				if(sessionModule.equalsIgnoreCase("true")){
					
					if(sessionInfo == null || sessionInfo.isViolated())
					{
						throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.LICNESE_LARGE_CONCURRENT_USER_CNT, "License's concurrent user cnt must be greater than concurrent use cnt in TMAS");
					}
					
				}
				
				
			}catch(PlatformException e)
			{
				throw e;
				
			}catch(Exception e)
			{
				throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.LICNESE_PASS_EXPIRE_DATE, e);
				
			}
			
		}catch(Exception e)
		{
			throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.LICENSE, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.QUEUE_INTERCEPTER, e);
			
		}
		
		if(logger.isDebugEnabled()) logger.debug("TasletIntercepter' tasletBeforeHandler is  finished");
		
	}

}
