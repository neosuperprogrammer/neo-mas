package com.tionsoft.tmg.command.impl;

import java.util.Map;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.tionsoft.tmg.command.Command;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.service.RelayService;
import com.tionsoft.tmg.util.LgdCommon;

/**
 * 메일 상세
 * @author 서버개발실 이주용
 */
public class MAIL00003 extends Command {
	private RelayService relayService;

	public RelayService getRelayService() {
		return relayService;
	}

	public void setRelayService(RelayService relayService) {
		this.relayService = relayService;
	}

	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader, jsonBody, dominoHeader, dominoBody;
		String baseUrl, downloadPath, internalDomains[];
		String mailId, bodyUrl, mailBody, groupCode, accountId;
		Map<String, Object> result = null, userInfo;

		try {
			jsonHeader	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			jsonBody	= JSONObject.fromObject(request.getBody("In_JSON", String.class));
			
			result		= relayService.sendRequest(jsonHeader.toString(), jsonBody.toString());
			groupCode	= jsonHeader.getString("GroupCode");
			accountId	= jsonHeader.getString("AccountId");
			userInfo	= commandService.getUserInfo(groupCode, accountId);

			dominoHeader	= JSONObject.fromObject(result.get("HEADER"));
			dominoBody		= JSONObject.fromObject(result.get("BODY"));
			
			if (dominoHeader.getInt("Status") != 0) {
				throw new TmgException((short) dominoHeader.getInt("Status"), dominoHeader.getString("ErrorMsg"));
			}
			
			mailId	= jsonBody.getString("Mail_ID");
			bodyUrl = dominoBody.getString("Mail_Body");

			baseUrl			= configuration.getString("configuration.image.mail.baseUrl");
			downloadPath	= configuration.getString("configuration.image.mail.downloadPath");
			internalDomains = configuration.getStringArray("configuration.image.internalDomain");
			
			mailBody = LgdCommon.getMailBody(bodyUrl, mailId, internalDomains, downloadPath, baseUrl);
			dominoBody.put("Mail_Body", mailBody);
			
			// 임원일 경우 PDF 사용 가능
			responseBody.put("Use_Pdf_Download", userInfo.get("IS_EXCLUSIVE").toString().equals("O") ? 1 : 0);
			responseBody.putAll(dominoBody);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.PARSING_FAILED);
		} 
	}
}
