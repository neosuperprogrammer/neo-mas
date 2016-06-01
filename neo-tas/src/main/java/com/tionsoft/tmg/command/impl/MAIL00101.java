package com.tionsoft.tmg.command.impl;

import java.text.ParseException;
import java.util.Map;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.tionsoft.tmg.command.Command;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.service.RelayService;
import com.tionsoft.tmg.util.CalendarHelper;
import com.tionsoft.tmg.util.LgdCommon;

/**
 * 메일함 싱크 요청 처리<br/>
 * <li>
 * <ul>메일함의 안읽은 메일 개수 처리를 위해 별도 구현</ul>
 * <ul>도미노 I/F 서버에서 변경할때 까지 한시적으로 사용 예정</ul>
 * </li>  
 * @author 서비스개발실 이주용
 * @date 2014-09-17
 */
public class MAIL00101 extends Command {
	private RelayService relayService;

	public RelayService getRelayService() {
		return relayService;
	}

	public void setRelayService(RelayService relayService) {
		this.relayService = relayService;
	}

	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader, jsonBody;
		String groupCode, accountId, mailServer, clientTimezone, serverTimezone;
		String syncStartDate, syncEndDate;
		Map<String, Object> result = null;
		
		try {
			jsonHeader	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			jsonBody	= JSONObject.fromObject(request.getBody("In_JSON", String.class));
			
			groupCode		= jsonHeader.getString("GroupCode");
			accountId		= jsonHeader.getString("AccountId");
			mailServer 		= LgdCommon.parseMailServerName(commandService.getUserInfo(groupCode, accountId).get("MAIL_SERVER"));
			
			clientTimezone	= "GMT" + jsonHeader.getString("GMT");
			serverTimezone	= LgdCommon.findTimezone(mailServer, configuration);
			syncStartDate	= jsonBody.getString("Sync_Start_Date");
			syncEndDate		= jsonBody.	getString("Sync_End_Date");
			 
			if (!clientTimezone.equals(serverTimezone)) {
				syncStartDate = CalendarHelper.convertTimezone(syncStartDate, clientTimezone, serverTimezone);
				syncEndDate = CalendarHelper.convertTimezone(syncEndDate, clientTimezone, serverTimezone);
				
				jsonBody.remove("Sync_Start_Date");
				jsonBody.remove("Sync_End_Date");
				jsonBody.put("Sync_Start_Date",	syncStartDate);
				jsonBody.put("Sync_End_Date",	syncEndDate);
			}
			
			result = relayService.sendRequest(jsonHeader.toString(), jsonBody.toString());
			
			JSONObject resultBody = JSONObject.fromObject(result.get("BODY"));
			resultBody.put("Inbox_unread_count", getInboxUnreadCount(jsonHeader.toString()));
			result.put("BODY", resultBody.toString());
			
			response.setBody("Out_JSON", result.get("BODY"));
		} catch (JSONException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.PARSING_FAILED);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.INVALID_REQUEST_DATE);
		} catch (TmgException e) {
			throw e;
		}
	}
	
	/**
	 * 받은편지함 안읽은 메일 개수 조회 
	 * @param header 요청원문의 JSON 헤더 
	 * @return 안읽은 메일 개수 
	 * @throws TmgException
	 */
	private int getInboxUnreadCount(String header) throws TmgException {
		JSONObject jsonHeader	= JSONObject.fromObject(header);
		JSONObject jsonBody		= new JSONObject();
		
		// LegacyId 변경 
		jsonHeader.remove("LegacyId");
		jsonHeader.put("LegacyId", "MAIL00002");

		// 요청바디 조립 - 안읽은 메일 개수만 필요하므로 최소 수량만 요청 
		jsonBody.put("MBox_ID",			"$Inbox");
		jsonBody.put("Req_Page",		"1");
		jsonBody.put("MailItemPerPage", "1");

		Map<String, Object> result = relayService.sendRequest(jsonHeader.toString(), jsonBody.toString());
		JSONObject jsonResultBody = JSONObject.fromObject(result.get("BODY"));
		
		return jsonResultBody.getInt("Unread_Cnt");
	}
}
