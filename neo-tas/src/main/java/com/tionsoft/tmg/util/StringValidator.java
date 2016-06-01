package com.tionsoft.tmg.util;

public class StringValidator {
	public static boolean isNumeric(String s) {
		boolean result = false;
		
		try {
			Integer.valueOf(s);
			result = true; 
		} catch (NumberFormatException e) {
		}
		
		return result;
	}
}
