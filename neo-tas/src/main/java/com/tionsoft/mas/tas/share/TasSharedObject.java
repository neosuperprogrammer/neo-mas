package com.tionsoft.mas.tas.share;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 
 * @author Administrator
 *
 */
public class TasSharedObject implements TasShare {
	
	private Map<String,Object> sharedObject= new ConcurrentHashMap<String, Object>();

	private static TasSharedObject object = new TasSharedObject();
	
	private TasSharedObject()
	{
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.tionsoft.mas.tas.share.TasShare#put(java.lang.String, java.lang.Object)
	 */
	@Override
	public void put(String key, Object o) {
		sharedObject.put(key, o);
	}

	/*
	 * (non-Javadoc)
	 * @see com.tionsoft.mas.tas.share.TasShare#putIfEmpty(java.lang.String, java.lang.Object)
	 */
	@Override
	public void putIfEmpty(String key, Object o) {
		
		
		if(!sharedObject.containsKey(key))
		{
			sharedObject.put(key, o);
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.tionsoft.mas.tas.share.TasShare#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		return sharedObject.get(key);
	}


	public static TasSharedObject getInstance(){
		return object;
	}

	

}
