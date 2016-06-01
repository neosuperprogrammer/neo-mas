package com.tionsoft.tmg.service;

import java.util.Map;

import com.tionsoft.tmg.exception.TmgException;

public interface RelayService {
	/**
	 * 
	 * @param header
	 * @param body
	 * @return
	 * @throws TmgException
	 */
	public Map<String, Object> sendRequest(String header, String body) throws TmgException;
}
