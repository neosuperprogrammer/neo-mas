package com.tionsoft.mas.tas.license.management;

/**
 * making an authentication key which is used in authentication management
 * @author Administrator
 *
 */
public class AuthenticationKey {

	public static String makeKey(String serviceId, String wifi)
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(serviceId).append("_").append(wifi);
		
		return sb.toString();
	}
	
	
	public static String[] splitKey(String key)
	{
		return key.split("_");
	}
}
