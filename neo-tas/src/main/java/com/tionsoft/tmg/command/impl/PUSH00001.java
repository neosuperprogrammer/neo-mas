package com.tionsoft.tmg.command.impl;

import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.tionsoft.tmg.command.Command;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;

/**
 * Push 사용 설정
 * @author 서버개발실 이주용
 */
public class PUSH00001 extends Command {
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader, jsonBody;
		String accountId, groupCode, wifi, pushTime, tokenType, deviceToken, osType, appId;
		boolean usePush, weekDays[] = new boolean[7];
		
		try {
			jsonHeader	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			jsonBody	= JSONObject.fromObject(request.getBody("In_JSON", String.class));
			
			accountId	= jsonHeader.getString("AccountId");
			groupCode	= jsonHeader.getString("GroupCode");
			wifi		= request.getPlatformHeader("WIFI_MAC", String.class).trim();
			
			usePush		= jsonBody.getInt("SetPush") == 1;
			pushTime	= jsonBody.getString("SetTime");
			tokenType	= "";
			deviceToken	= jsonBody.getString("DeviceToken");
			weekDays[0] = jsonBody.getInt("SetSunday")		== 1;
			weekDays[1] = jsonBody.getInt("SetMonday")		== 1;
			weekDays[2] = jsonBody.getInt("SetTuesday")		== 1;
			weekDays[3] = jsonBody.getInt("SetWednesday")	== 1;
			weekDays[4] = jsonBody.getInt("SetThursday")	== 1;
			weekDays[5] = jsonBody.getInt("SetFriday")		== 1;
			weekDays[6] = jsonBody.getInt("SetSaturday")	== 1;
			appId		= jsonHeader.getString("AppId");
			osType		= request.getPlatformHeader(PlatformHeader.OS_TYPE, String.class);
			
			commandService.setPushConfiguration(groupCode, accountId, wifi, usePush, pushTime, tokenType, deviceToken, osType, appId, weekDays);
		} catch (JSONException e) {
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}
}
