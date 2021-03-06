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
 * 전체 게시판 목록
 * @author 서버개발실 이주용
 */
public class BBS000001 extends Command {
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader;
		String accountId;
		
		try {
			jsonHeader	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			
			accountId	= jsonHeader.getString("AccountId");
			GeneralList boardList = boardService.getBoardList(accountId);
			
			responseBody.put("Item_Count",	boardList.getTotalCount());
			responseBody.put("List", 		boardList.getList());
		} catch (JSONException e) {
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}
}
