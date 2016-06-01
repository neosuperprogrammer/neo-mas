package com.tionsoft.mas.tas.taslet;

import java.io.Serializable;
import org.apache.mina.core.session.IoSession;
import org.infinispan.Cache;

import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.session.UselessSession;

/**
 * Client 요청 메세지 관리 .  
 * @version : 1.0.0
 * 
 */
public class TasRequest implements Serializable,Cloneable {
	
	private static final long serialVersionUID = 9190394455500765560L;
	
	private transient final TasSession tasSession;
	private transient final IoSession ioSession;
	private final TasBean platformHeader;
	private final TasBean header;
	private final TasBean body;
	private transient UselessSession uselessSessionObject;
//	private transient TasSharedMemory sharedMemory=null;
	
	/**
	  * TasRequest 생성자.
	  * @param sessionCache - Clustering session 정보.  
	  * @param ioSession - Transaction session 정보.  
	  * @param platformHeader - platformHeader 정보.  
	  * @param header - protocol.xml header 정보.  
	  * @param body - protocol.xml body 정보.  
	  * @param connectionId - connectionId 정보.  
	  */
	public TasRequest(Cache<Object, Object> sessionCache, IoSession ioSession, TasBean platformHeader, TasBean header, TasBean body, Long connectionId) {
		this.tasSession = new TasSession(sessionCache, platformHeader.getValue(PlatformHeader.SESSION_ID, Long.class));
		this.ioSession = ioSession;
		this.platformHeader = platformHeader;
		this.platformHeader.setValue(PlatformHeader.SESSION_ID, tasSession.getSessionId());
		this.header = header;
		this.body = body;
	}
	
	public TasRequest(TasSession tasSession, IoSession ioSession, TasBean platformHeader, TasBean header, TasBean body, Long connectionId) {
		this.tasSession = tasSession;
		this.ioSession = ioSession;
		this.platformHeader = platformHeader;
		this.platformHeader.setValue(PlatformHeader.SESSION_ID, tasSession.getSessionId());
		this.header = header;
		this.body = body;
	}
	
	/**
	 * an instance of session map which manages useless sessions
	 * @param uselessSessionObject
	 */
	public void setUselessSession(UselessSession uselessSessionObject)
	{
		this.uselessSessionObject = uselessSessionObject;
	}
	
	/**
	  * TasRequest 생성자.
	  * @param sessionCache - Clustering session 정보.  
	  * @param ioSession - Transaction session 정보.  
	  * @param platformHeader - platformHeader 정보.  
	  * @param header - protocol.xml header 정보.  
	  * @param body - protocol.xml body 정보.  
	  * @param connectionId - connectionId 정보.  
	  */
//	public TasRequest(Cache<Object, Object> sessionCache, IoSession ioSession, TasBean platformHeader, TasBean header, TasBean body, Long connectionId,TasSharedMemory sharedMemory) {
//		this.tasSession = new TasSession(sessionCache, platformHeader.getValue(PlatformHeader.SESSION_ID, Long.class));
//		this.ioSession = ioSession;
//		this.platformHeader = platformHeader;
//		this.platformHeader.setValue(PlatformHeader.SESSION_ID, tasSession.getSessionId());
//		this.header = header;
//		this.body = body;
//		this.sharedMemory = sharedMemory;
//	}
	
	/**
	  * Clustering Session 메세지 반환.
	  */
	public TasSession getSession() {
		return this.tasSession;
	}
	
	/**
	  * IoSession 메세지 반환.
	  */
	public IoSession getIoSession() {
		return this.ioSession;
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
	
//	public Object getValueInSharedMemory(String key)
//	{
//		return this.sharedMemory.getValue(key);
//	}
//	
//	public Object removeSharedMemory(String key)
//	{
//		return this.sharedMemory.remove(key);
//	}
//	
//	public void setValueInSharedMemory(String key, Object value)
//	{
//		this.sharedMemory.put(key, value);
//	}
//	
//	public <T> T getValueInSharedMemory(String key, Class<T> required) {
//		return sharedMemory.getValue(key, required);
//	}
	
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
	 * copy a tas request 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public TasRequest copy() throws CloneNotSupportedException
	{
		return (TasRequest) super.clone();
	}
	
	
	/**
	 * copy a tas request deeply
	 * @return
	 */
	public TasRequest deepCopy()
	{
		TasRequest req = new TasRequest(this.getSession(), this.getIoSession(), this.getPlatformHeader().deepCopy(), this.getHeader().deepCopy(), this.getBody().deepCopy(), 0L);
		
		return req;
	}
	
	public void sessionClose()
	{
		uselessSessionObject.putUselessSession(ioSession);
	}
	
}