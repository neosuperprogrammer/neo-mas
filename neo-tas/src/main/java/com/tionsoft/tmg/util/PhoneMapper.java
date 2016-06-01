package com.tionsoft.tmg.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.configuration.XMLConfiguration;

/**
 * 사내번호와 사외번호 맵핑
 * @author 서버개발실 이주용
 */
public class PhoneMapper {
	private XMLConfiguration configuration;
	private Properties phoneMap;

	public XMLConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(XMLConfiguration configuration) {
		this.configuration = configuration;
		
		phoneMap = new Properties();
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		
		try {
			fis = new FileInputStream(configuration.getString("configuration.address.phoneMap.path"));
			bis = new BufferedInputStream(fis);
			phoneMap.load(bis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
	
	public String getMapedNumber(String phoneNo) {
		String result = phoneNo;
		
		try {
			for (Object s : phoneMap.keySet()) {
				if (phoneNo.startsWith(s.toString())) {
					result = phoneMap.getProperty(s.toString()) + phoneNo.substring(s.toString().length());
					break;
				}
			}
		} catch (NullPointerException e) {
			result = phoneNo;
		}
		
		return result;
	}
}
