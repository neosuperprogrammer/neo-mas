package com.tionsoft.mas.tas.client.message;

import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.PlatformHeader;

public class TasRequest {
	
	private final TasBean platformHeader;
	private final TasBean header;
	private final TasBean body;
	
	public TasRequest(TasBean platformHeader, TasBean header, TasBean body) {
		this.platformHeader = platformHeader;
		this.header = header;
		this.body = body;
	}
	
	public PlatformHeader getPlatformHeader() {
		return (PlatformHeader)platformHeader;
	}
	
	public Object getPlatformHeader(String name) {
		return platformHeader.getValue(name);
	}

	public <T> T getPlatformHeader(String name, Class<T> required) {
		return platformHeader.getValue(name, required);
	}

	public TasBean getHeader() {
		return header;
	}
	
	public Object getHeader(String name) {
		return header.getValue(name);
	}

	public <T> T getHeader(String name, Class<T> required) {
		return header.getValue(name, required);
	}
	
	public TasBean getBody() {
		return body;
	}

	public Object getBody(String name) {
		return body.getValue(name);
	}

	public <T> T getBody(String name, Class<T> required) {
		return body.getValue(name, required);
	}
}