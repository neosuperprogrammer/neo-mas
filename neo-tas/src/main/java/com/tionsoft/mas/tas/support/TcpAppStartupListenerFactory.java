package com.tionsoft.mas.tas.support;

import java.lang.reflect.Constructor;

public class TcpAppStartupListenerFactory {
	
	public static TcpAppStartupListener getListener(ClassLoader loader, String listenerClassName) throws RuntimeException {
		try {
			Class<?> listenerCls = loader.loadClass(listenerClassName);
			Constructor<?> constuctor = listenerCls.getConstructor(); 
			Object listenerObj = constuctor.newInstance();
			
			return (TcpAppStartupListener)listenerObj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}