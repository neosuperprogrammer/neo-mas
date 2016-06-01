package com.tionsoft.tmg.command.impl;

import java.util.Map;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.tionsoft.tmg.command.Command;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;

/**
 * 헬프데스크 
 * @author 서버개발실 이주용
 */
public class COM000002 extends Command {
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader;
		String groupCode;
		
		try {
			jsonHeader = JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			groupCode = jsonHeader.getString("GroupCode");
			
			Map<String, Object> serviceDesk = commandService.getServiceDesk(groupCode);
			responseBody.put("Telephone", serviceDesk.get("TELEPHONE"));
			responseBody.put("Email", serviceDesk.get("EMAIL"));
		} catch (JSONException e) {
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}
}
