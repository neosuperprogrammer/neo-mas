package com.tionsoft.mas.tas.pushlet;

import java.lang.reflect.Constructor;

public class InterestProcessorFactory {
	
	public static InterestProcessor getProcessor() throws RuntimeException {
		String interestProcessor = System.getProperty("mas.push.interest.processor");
		try {
			Class<?> interestProcessorCls = InterestProcessorFactory.class.getClassLoader().loadClass(interestProcessor);
			Constructor<?> constuctor = interestProcessorCls.getConstructor(); 
			Object interestProcessorObj = constuctor.newInstance();
			
			return (InterestProcessor)interestProcessorObj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}