package com.tionsoft.tmg.command.impl;

import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import org.springframework.dao.EmptyResultDataAccessException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.tionsoft.tmg.command.Command;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.util.LgdCommon;

/**
 * Push 사용 설정
 * @author 서버개발실 이주용
 */
public class PUSH00003 extends Command {
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader, jsonBody;
		String accountId, groupCode, wifi, tokenType, deviceToken, appId, osType;
		
		try {
			jsonHeader	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			jsonBody	= JSONObject.fromObject(request.getBody("In_JSON", String.class));
			
			accountId	= jsonHeader.getString("AccountId");
			groupCode	= jsonHeader.getString("GroupCode");
			wifi		= request.getPlatformHeader("WIFI_MAC", String.class).trim();
			
			tokenType	= jsonBody.getString("Token_Type");
			deviceToken	= jsonBody.getString("DeviceToken");
			appId		= jsonHeader.getString("AppId");
			osType		= request.getPlatformHeader(PlatformHeader.OS_TYPE, String.class);
			
			// 기존 정보가 없을 경우에만 기본 정보를 생성하도록 수정
			try {
				commandService.getPushConfiguration(groupCode, accountId, wifi);
			} catch (EmptyResultDataAccessException e) {
				LgdCommon.setDefaultPushConfig(commandService, configuration, groupCode, accountId, wifi, osType, appId);
			}
			
			commandService.setDeviceToken(groupCode, accountId, wifi, tokenType, osType, deviceToken, appId);
		} catch (JSONException e) {
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}
}
