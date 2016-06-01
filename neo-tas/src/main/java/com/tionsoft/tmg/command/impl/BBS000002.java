package com.tionsoft.tmg.command.impl;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.tionsoft.tmg.command.Command;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.service.domain.GeneralList;

/**
 * 게시판 문서 & 응답문서 목록 
 * @author 서버개발실 이주용
 */
public class BBS000002  extends Command {
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader, jsonBody;
		String accountId, boardId;
		int requestPage, countPerPage;
		
		try {
			jsonHeader	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			jsonBody	= JSONObject.fromObject(request.getBody("In_JSON", String.class));
			
			accountId		= jsonHeader.getString("AccountId");
			boardId			= jsonBody.getString("Bbs_ID");
			requestPage		= jsonBody.getInt("Req_Page");
			countPerPage	= jsonBody.getInt("ItemPerPage");
			
			GeneralList articleList = boardService.getArticleList(accountId, boardId, requestPage, countPerPage);
			
			responseBody.put("Cur_Page",	requestPage);
			responseBody.put("Total_Page",	articleList.getTotalPage());
			responseBody.put("Item_Count",	articleList.getList().size());
			responseBody.put("List",		articleList.getList());
		} catch (JSONException e) {
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}
}
