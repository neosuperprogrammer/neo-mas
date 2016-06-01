package com.tionsoft.tmg.command.impl;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.tionsoft.tmg.command.Command;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;

/**
 * 게시판 문서 댓글 삭제 
 * @author 서버개발실 이주용
 */
public class BBS000009  extends Command {
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonBody;
		String articleId, commentId;
		
		try {
			jsonBody	= JSONObject.fromObject(request.getBody("In_JSON", String.class));
			
			articleId	= jsonBody.getString("Doc_ID");
			commentId	= jsonBody.getString("Comment_ID");
			
			boardService.deleteComment(articleId, commentId);
		} catch (TmgException e) {
			throw e;
		} catch (JSONException e) {
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}
}
