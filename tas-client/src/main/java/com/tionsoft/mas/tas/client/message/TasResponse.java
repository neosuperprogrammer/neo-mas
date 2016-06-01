package com.tionsoft.mas.tas.client.message;

import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.PlatformHeader;

public class TasResponse {
	
	private TasBean platformHeader;
	private TasBean header;
	private TasBean body;
	
	public TasResponse(TasBean platformHeader, TasBean header, TasBean body) {
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

	public void setPlatformHeader(TasBean params) {
		this.platformHeader = params;
	}
	
	public void setPlatformHeader(String name, Object value) {
		platformHeader.setValue(name, value);
	}
	
	public void setHeader(TasBean params) {
		this.header = params;
	}

	public void setHeader(String name, Object value) {
		header.setValue(name, value);
	}
	
	public void setBody(TasBean params) {
		this.body = params;
	}

	public void setBody(String name, Object value) {
		body.setValue(name, value);
	}
}