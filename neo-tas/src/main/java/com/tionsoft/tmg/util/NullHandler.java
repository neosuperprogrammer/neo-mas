package com.tionsoft.tmg.util;

/**
 * Null로 할당된 변수의 값을 지정된 대체값으로 변경
 * @author 이주
 */
public class NullHandler {
	/**
	 * 
	 * @param value
	 * @param alternateValue
	 * @return
	 */
	public static int ifNullTo(Object value, int alternateValue) {
		return value == null ? alternateValue : Integer.parseInt(value.toString());
	}
}
