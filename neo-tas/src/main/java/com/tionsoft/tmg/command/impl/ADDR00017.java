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
 * 주소 검색 
 * @author 서버개발실 이주용
 */
public class ADDR00017 extends Command {
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader, jsonBody;
		String accountId, groupCode, locale, keyword, keywordType;
		int requestPage, countPerPage;
		
		try {
			jsonHeader = JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			jsonBody = JSONObject.fromObject(request.getBody("In_JSON", String.class));
			
			groupCode		= jsonHeader.getString("GroupCode");
			locale			= jsonHeader.getString("Language");
			accountId		= jsonHeader.getString("AccountId");
			
			// 2013-12-30 민동원 : 특정 단말 로케일이 "KO_" 로 들어오는 경우 있어 땜빵 
			locale 			= LgdCommon.correctLocale(locale);
			
			keyword			= jsonBody.getString("Search_Keyword");
			keywordType		= jsonBody.getString("Search_Keyword_Type");
			requestPage		= jsonBody.getInt("Req_Page");
			countPerPage	= jsonBody.getInt("AddressPerPage");
			
			// TODO : keywordType 별 검색 구분 수정
			GeneralList addressList = commandService.getAddressSearchList(accountId, locale, groupCode, keywordType, keyword, requestPage, countPerPage);
			responseBody.put("Cur_Page",		requestPage);
			responseBody.put("Total_Page",		addressList.getTotalPage());
			responseBody.put("Total_Count",		addressList.getTotalCount());
			responseBody.put("Address_Count",	addressList.getList().size());
			responseBody.put("List",			addressList.getList());
		} catch (JSONException e) {
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}
}
