package com.tionsoft.tmg.command.impl;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.tionsoft.tmg.command.Command;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.service.domain.GeneralList;
import com.tionsoft.tmg.util.LgdCommon;

/**
 * 그룹내 주소 목록 
 * @author 서버개발실 이주용
 */
public class ADDR00016 extends Command {
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader, jsonBody;
		String accountId, groupCode, locale, addressGroupId;
		int requestPage, countPerPage;
		
		try {
			jsonHeader	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			jsonBody	= JSONObject.fromObject(request.getBody("In_JSON", String.class));
			
			locale			= jsonHeader.getString("Language");
			
			// 2013-12-30 민동원 : 특정 단말 로케일이 "KO_" 로 들어오는 경우 있어 땜빵 
			locale = LgdCommon.correctLocale(locale);
			
			accountId		= jsonHeader.getString("AccountId");
			addressGroupId	= jsonBody.getString("Group_ID");
			groupCode		= jsonBody.getString("Member_Code");
			requestPage		= jsonBody.getInt("Req_Page");
			countPerPage	= jsonBody.getInt("AddressPerPage");
			
			GeneralList addressList = commandService.getAddressList(accountId, locale, groupCode, addressGroupId, requestPage, countPerPage);
			responseBody.put("Cur_Page",		requestPage);
			responseBody.put("Total_Page",		addressList.getTotalPage());
			responseBody.put("Total_Count", 	addressList.getTotalCount());
			responseBody.put("Address_Count", 	addressList.getList().size());
			responseBody.put("List",			addressList.getList());
		} catch (JSONException e) {
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}
}
