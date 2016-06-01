package com.tionsoft.mas.tas.taslet;

import java.io.Serializable;

import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.PlatformHeader;

/**
 * 응답 메세지 관리 .  
 * @version : 1.0.0
 * 
 */
public class TasResponse implements Serializable,Cloneable {
	
	private static final long serialVersionUID = 5405855802637972148L;
	
	private TasBean platformHeader;
	private TasBean header;
	private TasBean body;
	
	/**
	  * TasResponse 생성자.
	  * @param platformHeader - platformHeader 정보.  
	  * @param header - protocol.xml header 정보.  
	  * @param body - protocol.xml body 정보.  
	  */
	public TasResponse(TasBean platformHeader, TasBean header, TasBean body) {
		this.platformHeader = platformHeader;
		this.header = header;
		this.body = body;
	}
	
	/**
	  * PlatformHeader 메세지 반환.
	  */
	public PlatformHeader getPlatformHeader() {
		return (PlatformHeader)platformHeader;
	}

	/**
	  * PlatformHeader 메세지 반환.
	  * @param name - PlatformHeader 중 파라미터 name value 요청.
	  */
	public Object getPlatformHeader(String name) {
		return platformHeader.getValue(name);
	}
	
	/**
	  * PlatformHeader 메세지 반환.
	  * @param name - PlatformHeader 중 파라미터 name value 요청.
	  * @param required - name value 을 required 타입으로 Casing .
	  */
	public <T> T getPlatformHeader(String name, Class<T> required) {
		return platformHeader.getValue(name, required);
	}
	
	/**
	  * Header 메세지 반환.
	  */
	public TasBean getHeader() {
		return header;
	}

	/**
	  * Header 메세지 반환.
	  * @param name - Header 중 파라미터 name value 요청.
	  */
	public Object getHeader(String name) {
		return header.getValue(name);
	}
	
	/**
	  * Header 메세지 반환.
	  * @param name - Header 중 파라미터 name value 요청.
	  * @param required - name value 을 required 타입으로 Casing .
	  */
	public <T> T getHeader(String name, Class<T> required) {
		return header.getValue(name, required);
	}
	
	/**
	  * Body 메세지 반환.
	  */
	public TasBean getBody() {
		return body;
	}

	/**
	  * Body 메세지 반환.
	  * @param name - Body 중 파라미터 name value 요청.
	  */
	public Object getBody(String name) {
		return body.getValue(name);
	}

	/**
	  * Body 메세지 반환.
	  * @param name - Body 중 파라미터 name value 요청.
	  * @param required - name value 을 required 타입으로 Casing .
	  */
	public <T> T getBody(String name, Class<T> required) {
		return body.getValue(name, required);
	}

	/**
	  * PlatformHeader 설정.
	  * @param params - PlatformHeader .
	  */
	public void setPlatformHeader(TasBean params) {
		this.platformHeader = params;
	}
	
	/**
	  * PlatformHeader 설정.
	  * @param params - PlatformHeader .
	  */
	public void setPlatformHeader(String name, Object value) {
		platformHeader.setValue(name, value);
	}
	
	/**
	  * Header 설정.
	  * @param params - Header .
	  */
	public void setHeader(TasBean params) {
		this.header = params;
	}

	/**
	  * Header 설정.
	  * @param name - key .
	  * @param value - value .
	  */
	public void setHeader(String name, Object value) {
		header.setValue(name, value);
	}
	
	/**
	  * Body 설정.
	  * @param params - Body .
	  */
	public void setBody(TasBean params) {
		this.body = params;
	}

	/**
	  * Body 설정.
	  * @param name - key .
	  * @param value - value .
	  */
	public void setBody(String name, Object value) {
		body.setValue(name, value);
	}
	
	/**
	 * copy a tas response 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public TasResponse copy() throws CloneNotSupportedException
	{
		return (TasResponse) super.clone();
	}
	
	
	/**
	 * copy a tas response deeply
	 * @return
	 */
	public TasResponse deepCopy()
	{
		TasResponse res = new TasResponse(this.getPlatformHeader().deepCopy(), this.getHeader().deepCopy(), this.getBody().deepCopy());
		
		return res;
		
	}
}