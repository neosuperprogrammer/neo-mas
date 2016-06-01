package com.tionsoft.tmg.command.impl;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.tionsoft.tmg.command.Command;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;

/**
 * 메일 서명 변경 
 * @author 서버개발실 이주용
 */
public class AUTH00003 extends Command {
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader, jsonBody;
		String accountId, groupCode, signature, wifi;
		
		try {
			jsonHeader	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			jsonBody	= JSONObject.fromObject(request.getBody("In_JSON", String.class));
			accountId	= jsonHeader.getString("AccountId");
			groupCode	= jsonHeader.getString("GroupCode");
			signature	= jsonBody.getString("Signature");
			wifi		= request.getPlatformHeader("WIFI_MAC", String.class).trim();
			
			commandService.setSignature(groupCode, accountId, wifi, signature);
		} catch (JSONException e) {
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}
}
