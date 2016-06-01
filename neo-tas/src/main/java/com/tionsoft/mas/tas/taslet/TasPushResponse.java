package com.tionsoft.mas.tas.taslet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TasPushResponse implements Serializable {
	
	private static final long serialVersionUID = -640374367244195589L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger("tas");

	private final Map<String, TasResponse> respones;
	
	public TasPushResponse() {
		this.respones = new LinkedHashMap<String, TasResponse>();
	}
	
	public void addPush(String clientKey, TasResponse tasResponse) {
		
//		 Collection<TasResponse> tasResponses = new ArrayList<TasResponse>();
//		 tasResponses.add(tasResponse);

		this.respones.put(clientKey, tasResponse);
	}
	
	public void removePush(String clientKey) {
		this.respones.remove(clientKey);
	}
	
	public Map<String, TasResponse> getResponses() {
		return this.respones;
	}

}