package com.tionsoft.mas.tas.license.management;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.tionsoft.mas.tas.license.auth.Authentication;

@Component
public class AuthenticationMap {

	private Map<String,Authentication> authMap = new ConcurrentHashMap<String, Authentication>();
	
	public void setAuth(String key , Authentication auth)
	{
		authMap.put(key, auth);
	}
	
	public void put(String key , Authentication auth)
	{
		authMap.put(key, auth);
	}
	
	public Authentication getAuth(String key)
	{
		return authMap.get(key);
	}
	
	public Authentication get(String key)
	{
		return authMap.get(key);
	}
	
	public boolean containsKey(String key)
	{
		return authMap.containsKey(key);
	}
	
	
	public List<String> getKeys()
	{
		List<String> keys = new ArrayList<String>();
		Iterator<String> itr = authMap.keySet().iterator();
		
		String key ="";
		while(itr.hasNext())
		{
			key = itr.next();
			keys.add(key);
		}
		
		return keys;
	}
	
	/**
	 * authentication checking logic is a kind of background process
	 * When a first request about an authentication related to service id and wifi, it is not ready
	 * so a first request can get a true
	 * @param key
	 * @return
	 */
	public boolean isAuth(String key)
	{
		if(authMap.containsKey(key)){
			Authentication auth =authMap.get(key);
			
			return auth.isAuth();
			
		}else {
			return true;
		}
	}
	
	public void remove(String key)
	{
		authMap.remove(key);
	}
	
}
