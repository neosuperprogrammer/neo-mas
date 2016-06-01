package com.tionsoft.tmg.command.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import org.springframework.dao.EmptyResultDataAccessException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.tionsoft.tmg.command.Command;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.service.RelayService;
import com.tionsoft.tmg.util.LgdCommon;

/**
 * 초기데이터 - 메인화면 
 * @author 서버개발실 이주용
 */
public class COM000001 extends Command {
	private RelayService relayService;

	public RelayService getRelayService() {
		return relayService;
	}

	public void setRelayService(RelayService relayService) {
		this.relayService = relayService;
	}
	
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader;
		String accountId, groupCode, wifi, appId, osType;
		
		try {
			jsonHeader	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			accountId	= jsonHeader.getString("AccountId");
			groupCode	= jsonHeader.getString("GroupCode");
			wifi		= request.getPlatformHeader("WIFI_MAC", String.class).trim();
			appId		= jsonHeader.getString("AppId");
			osType		= request.getPlatformHeader(PlatformHeader.OS_TYPE, String.class);
			
			String boardId = configuration.getString("configuration.board.default");
			Date rightNow = Calendar.getInstance().getTime();
			 
			// TODO : 권한 획득 SP 수정 필요
			Map<String, Object> permissions = commandService.getPermission(groupCode, accountId, wifi);
			Map<String, Object> badges = commandService.getBadgeCount(groupCode, accountId, boardId, rightNow);
			
			getMailCount(jsonHeader, badges);
			setDefaultPushConfig(groupCode, accountId, wifi, osType, appId);
			
			responseBody.putAll(permissions);
			responseBody.putAll(badges);
		} catch (JSONException e) {
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}

	/**
	 * 메일 전체 개수 카운트 조회 
	 * @param jsonHeader 헤더 
	 * @param badges 배지 맵 
	 * @throws TmgException
	 */
	private void getMailCount(JSONObject jsonHeader, Map<String, Object> badges) throws TmgException {
//		jsonHeader.put("LegacyId", "MAIL00000");
//		Map<String, Object> result = relayService.sendRequest(jsonHeader.toString(), "{}");
//		
//		badges.put("Mail_Count", JSONObject.fromObject(result.get("BODY")).getString("Mail_Count"));
		badges.put("Mail_Count", "0");
	}
	
	/**
	 * 푸시 기본 설정 셋팅 
	 * @param groupCode 그룹 코드 
	 * @param accountId 사용자 계정 
	 * @param wifi WI-FI 주소 
	 * @param osType OS 구
	 * @param appId 앱아이디 
	 * @throws TmgException
	 */
	private void setDefaultPushConfig(String groupCode, String accountId, String wifi, String osType, String appId) throws TmgException {
		try {
			commandService.getPushConfiguration(groupCode, accountId, wifi);
		} catch (EmptyResultDataAccessException e) {
			LgdCommon.setDefaultPushConfig(commandService, configuration, groupCode, accountId, wifi, osType, appId);
		}
	}
}
