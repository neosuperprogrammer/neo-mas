package com.tionsoft.tmg.command.impl;

import java.util.Map;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import net.sf.json.JSONObject;


import com.tionsoft.tmg.service.RelayService;
import com.tionsoft.tmg.command.Command;
import com.tionsoft.tmg.exception.TmgException;

/**
 * 기본 릴레이 커맨드 
 * @author 서버개발실 이주용
 */
public class Relay extends Command {
	private RelayService relayService;

	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		String header = getHeader(request).toString();
		String body = getBody(request).toString();
		
		Map<String, Object> results = relayService.sendRequest(header, body);
		response.setBody("Out_JSON", results.get("BODY"));
	}

	/**
	 * @return the relayService
	 */
	public RelayService getRelayService() {
		return relayService;
	}

	/**
	 * @param relayService the relayService to set
	 */
	public void setRelayService(RelayService relayService) {
		this.relayService = relayService;
	}
}
