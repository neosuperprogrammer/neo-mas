package com.tionsoft.mas.tas.bean;

import java.io.*;
import java.util.*;

import com.tionsoft.mas.tas.exception.*;

public class TasBean implements Serializable,Cloneable {
	private static final long serialVersionUID = -1860288767929960413L;
	private final Map<String, Object> params;
	

	public TasBean() {
		params = Collections.synchronizedMap(new LinkedHashMap<String, Object>());
//		ConcurrentHashMap<K, V> 수정 필요
	}

	public Object getValue(String name) {
		Object object = params.get(name);
		if(object == null) {
			
			ErrorType errorType = new ErrorType(null,ErrorType.ERROR_CODE_NULL_FIELD, "Field Value [" + name + "] is not set");
    		throw new TasBeanException(errorType); //exceptionCaught 에서 버전 구분 필요
		}
		return object;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(String name, Class<T> required) {
		return (T) getValue(name);
	}
	
	public void setValue(String name, Object value) {
		params.put(name, value);
	}
	
	public void remove(String name) {
		params.remove(name);
	}
	
	public Map<String, Object> getParams() {
		return params;
	}
	
	public Set<String> getParamNames() {
		return params.keySet();
	}
	
	/**
	 * copy an object so that  data store area can be shared
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public TasBean copy() throws CloneNotSupportedException
	{
		return (TasBean) super.clone();
	}
	
	/**
	 * copy an object deeply so that data store area is unique
	 * @return
	 */
	public TasBean deepCopy()
	{
		TasBean bean = new TasBean();
		
		Iterator<String> itr = this.getParamNames().iterator();
		
		String key = "";
		
		while(itr.hasNext())
		{
			key = itr.next();
			bean.setValue(key, this.getValue(key));
		}
		
		return bean;
	}
	
}
