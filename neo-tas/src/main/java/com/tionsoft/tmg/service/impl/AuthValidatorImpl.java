package com.tionsoft.tmg.service.impl;

import java.io.IOException;

import com.tionsoft.mas.tas.taslet.TasRequest;
import org.apache.commons.httpclient.HttpException;

import net.sf.json.JSONObject;

import com.tionsoft.tmc.api.TmcApi;
import com.tionsoft.tmc.exception.TmcException;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.service.AuthValidator;

/**
 * 인증 유효성 검증 - LGD向 
 * @author 서버개발실 이주용
 */
public class AuthValidatorImpl implements AuthValidator {
	private TmcApi tmcApi;


	/**
	 * @return the tmcApi
	 */
	public TmcApi getTmcApi() {
		return tmcApi;
	}

	/**
	 * @param tmcApi the tmcApi to set
	 */
	public void setTmcApi(TmcApi tmcApi) {
		this.tmcApi = tmcApi;
	}
	
	/**
	 *  TMC API 호출하여 인증 정보 검증
	 */
	public void validate(TasRequest request) throws TmgException {
		
		// TODO : 예외 코드 정리 필요함
		try {
			JSONObject header	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			String wifi			= request.getPlatformHeader("WIFI_MAC", String.class).trim();
			String appId		= header.getString("AppId");
			String accountId	= header.getString("AccountId");
			String authKey		= header.getString("AuthKey").replace("\\u003d", "=").replace("\\u0026", "&");
			
			try {
				tmcApi.validateAuthKey2(appId, accountId, authKey, wifi);
			} catch (TmcException e0) {
				throw new TmgException(e0.getErrorCode());		
			}
		} catch (HttpException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.AUTHENTICATION_FAILED);
		} catch (IOException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.AUTHENTICATION_FAILED);
		} catch (TmgException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}

}
