package com.tionsoft.mas.tas.bean.platform;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlatformHeaderFactory {
	
	private static Map<String,PlatformHeader> platformheaderMap = new ConcurrentHashMap<String, PlatformHeader>();
	
	public static PlatformHeader getPlatformHeader(String version) throws RuntimeException {
		String platformHeaderClass = "com.tionsoft.mas.tas.bean.platform.PH" + version.substring(3);
		try {
			
			PlatformHeader platformHeaderCache = null;
			if(platformheaderMap.containsKey(platformHeaderClass)){
				platformHeaderCache = platformheaderMap.get(platformHeaderClass);
				
				return platformHeaderCache.structureCopy();
			}
			
			Class<?> platformHeaderCls = PlatformHeaderFactory.class.getClassLoader().loadClass(platformHeaderClass);
			Constructor<?> constuctor = platformHeaderCls.getConstructor(); 
			Object platformHeaderObj = constuctor.newInstance();
			
			platformheaderMap.put(platformHeaderClass, (PlatformHeader)platformHeaderObj);
			
			return (PlatformHeader)platformHeaderObj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}