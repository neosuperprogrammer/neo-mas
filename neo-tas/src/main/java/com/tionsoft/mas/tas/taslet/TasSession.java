package com.tionsoft.mas.tas.taslet;

import org.infinispan.Cache;

import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.*;

/**
 * Session 관리 .  
 * @version : 1.0.0
 * 
 */
public class TasSession {

	private final Cache<Object, Object> cache;
	private final long sessionId;

	/**
	  * TasSession 생성자.
	  * @param cache - Clustering session 정보.  
	  * @param sessionId -  session ID 정보.  
	  */
	public TasSession(Cache<Object, Object> cache, long sessionId) {
		this.cache = cache;
		if(cache.get(sessionId) != null) {
			this.sessionId = sessionId;
		} else {
			//PMAS가 gateway 서버역활을 할때 client에서 요청한 sessinoid을 PBI 서버쪽으로
			//bypass 하기위해서 
			String bypass = System.getProperty("mas.tas.sessionid.bypass");
			if(bypass != null && bypass.equalsIgnoreCase("true")) {
				this.sessionId = sessionId;
			}else{
				this.sessionId = System.nanoTime();
			}
			
		}
	}
	
	/**
	  * SessionId 반환.
	  */
	public long getSessionId() {
		return sessionId;
	}

	
	/**
	  * SessionId에 해당하는 session 정보를 반환.
	  * @param key - key.  
	  * @param required - value 을 required 타입으로 Casing .
	  */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key, Class<T> required) {
		if(cache.get(sessionId) != null) {
			TasBean sessionBean = (TasBean) cache.get(sessionId);
			cache.put(sessionId, sessionBean);
			return (T) sessionBean.getValue(key);
		} else {
			return (T) null;
		}
	}

	public void setAttribute(String key, Object value) {
		TasBean sessionBean;
		if(cache.get(sessionId) != null) {
			sessionBean = (TasBean) cache.get(sessionId);
		} else {
			sessionBean = new TasBean();
		}
		sessionBean.setValue(key, value);
		cache.put(sessionId, sessionBean);
	}

	public void removeAttribute(String key) {
		if(cache.get(sessionId) != null) {
			TasBean sessionBean = (TasBean) cache.get(sessionId);
			sessionBean.remove(key);
			cache.put(sessionId, sessionBean);
		} else {
			return;
		}
	}
}