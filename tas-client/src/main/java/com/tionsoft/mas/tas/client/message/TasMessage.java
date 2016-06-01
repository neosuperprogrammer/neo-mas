package com.tionsoft.mas.tas.client.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.protocol.definition.BodyDefinition;

public class TasMessage {
	
	private final TasRequest tasRequest;
	private final TasResponse tasResponse;
	
	public TasMessage(TasRequest tasRequest, TasResponse tasResponse) {
		this.tasRequest = tasRequest;
		this.tasResponse = tasResponse;
	}
	
	public TasRequest getTasRequest() {
		return tasRequest;
	}

	public TasResponse getTasResponse() {
		return tasResponse;
	}
	
	
}