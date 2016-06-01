package com.tionsoft.tmg.exception;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.XMLConfiguration;

public class TmgExceptionMessage {
	
	private static TmgExceptionMessage instance = null;
	
	private Map<String, Properties> messages = null;
	
	private TmgExceptionMessage(XMLConfiguration configuration) {
		
		String[] locales = configuration.getString("configuration.messages.support").split("[|]");
		String messageBasePath = configuration.getString("configuration.messages.basePath");
		messages = new HashMap<String, Properties>();

		for (String locale : locales) {
			Properties message = new Properties();
			String messagePath = String.format("%s/messages.%s.properties", messageBasePath, locale);
			BufferedInputStream bis = null;
			
			try {
				message.load(bis = new BufferedInputStream(new FileInputStream(messagePath)));
				messages.put(locale, message);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (bis != null)
					try {
						bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}
	
	public static TmgExceptionMessage initInstance(XMLConfiguration configuration) throws IOException {
		if (instance == null) {
			instance = new TmgExceptionMessage(configuration);
		}
		return instance;
	}
	
	public static TmgExceptionMessage getInstance() throws Exception {
		if (instance == null)
			throw new Exception("Failed To Get ErrorMessage Instance.");
		return instance;
	}
	
	public String getMessage(int i, String locale) throws Exception {
		Properties message = getInstance().messages.get(locale);
		return new String(message.getProperty(String.valueOf(i)).getBytes("ISO-8859-1"), "UTF-8");
	}
	
}
