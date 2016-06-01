package com.tionsoft.mas.tas.share;

public interface TasShare {
	
	
	/**
	 * store a key and object in the shared object without considering it to to be existed in the shared object
	 * @param key
	 * @param o
	 */
	public void put(String key, Object o);
	
	
	/**
	 * store a key and object in the shared object with considering it to to be existed in the shared object
	 * @param key
	 * @param o
	 */
	public void putIfEmpty(String key, Object o);
	
	/**
	 * if returned object mapped with key is empty, it will return null
	 * @param key
	 */
	public Object get(String key);

}
