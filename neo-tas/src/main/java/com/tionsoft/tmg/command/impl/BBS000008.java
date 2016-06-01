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
 * 게시판 문서 댓글 목록
 * @author 서버개발실 이주용
 */
public class BBS000008 extends Command {
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader, jsonBody;
		String accountId, articleId;
		int requestPage, countPerPage;
		
		try {
			jsonHeader	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			jsonBody	= JSONObject.fromObject(request.getBody("In_JSON", String.class));
			
			accountId		= jsonHeader.getString("AccountId");
			articleId		= jsonBody.getString("Doc_ID");
			requestPage		= jsonBody.getInt("Req_Page");
			countPerPage	= jsonBody.getInt("ItemPerPage");
			
			GeneralList articleList = boardService.getCommentList(accountId, articleId, requestPage, countPerPage);
			
			responseBody.put("Cur_Page",	requestPage);
			responseBody.put("Total_Page",	articleList.getTotalPage());
			responseBody.put("Total_Count",	articleList.getTotalCount());
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
