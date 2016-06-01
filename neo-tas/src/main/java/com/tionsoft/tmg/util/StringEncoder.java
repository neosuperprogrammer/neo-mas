package com.tionsoft.tmg.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringEncoder {
	public static final String DEFAULT_ENCODING_TYPE = "UTF-8";

	public static String encodeUrl(String s, String encodingType) throws UnsupportedEncodingException {
		return URLEncoder.encode(s, encodingType);
	}

	public static String encodeUrl(String s) throws UnsupportedEncodingException {
		return URLEncoder.encode(s, DEFAULT_ENCODING_TYPE);
	}
	
	public static String decodeUrl(String s, String encodingType) throws UnsupportedEncodingException {
		return URLDecoder.decode(s, encodingType);
	}

	public static String decodeUrl(String s) throws UnsupportedEncodingException {
		return URLDecoder.decode(s, DEFAULT_ENCODING_TYPE);
	}
	
	/**
	 * String UnEscape 처리
	 * @param src
	 * @return
	 */
	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		int lastPos = 0, pos = 0;
		char ch;

		tmp.ensureCapacity(src.length());
		
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
					if (ch != 9)
						tmp.append(ch);
					
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		
		return tmp.toString();
	}
	
	/**
	 * String Escape 처리
	 * @param src
	 * @return
	 */
	public static String escape(String src) {
		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length() * 6);
		for (i = 0; i < src.length(); i++) {
			j = src.charAt(i);
			if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j))
				tmp.append(j);
			else if (j < 256) {
				tmp.append("%");
				if (j < 16) tmp.append("0");
				tmp.append(Integer.toString(j, 16));
			} else {
				tmp.append("%u");
				tmp.append(Integer.toString(j, 16));
			}
		}
		
		System.out.println("ESCAPE : " + tmp.toString());
		
		return tmp.toString();
	}
	
	public static String digest(String s) {
		MessageDigest md;
		StringBuffer result = new StringBuffer();
		
		try {
			md = MessageDigest.getInstance("SHA-256");
	        try {
				md.update(s.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	        
	        byte data[] = md.digest();
	        
	        
	        for (int i = 0; i < data.length; i++) {
	        	result.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
	        }
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return result.toString();
	}
	
}
