package com.tionsoft.tmg.command.impl;

import java.util.Map;

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
 * Push 설정 조회
 * @author 서버개발실 이주용
 */
public class PUSH00002 extends Command {
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader;
		String accountId, groupCode, wifi, appId, osType;
		Map<String, Object> result;
		
		try {
			jsonHeader	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			
			accountId	= jsonHeader.getString("AccountId");
			groupCode	= jsonHeader.getString("GroupCode");
			wifi		= request.getPlatformHeader("WIFI_MAC", String.class).trim();
			appId		= jsonHeader.getString("AppId");
			osType		= request.getPlatformHeader(PlatformHeader.OS_TYPE, String.class);
			
			try {
				result = commandService.getPushConfiguration(groupCode, accountId, wifi);
			} catch (EmptyResultDataAccessException e) {
				LgdCommon.setDefaultPushConfig(commandService, configuration, groupCode, accountId, wifi, osType, appId);
				result = commandService.getPushConfiguration(groupCode, accountId, wifi);
			}
			
			responseBody.putAll(result);
		} catch (JSONException e) {
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}
}
