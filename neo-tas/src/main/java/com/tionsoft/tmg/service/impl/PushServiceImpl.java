package com.tionsoft.tmg.service.impl;

import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.tionsoft.tmg.dao.PushDao;
import com.tionsoft.tmg.service.PushService;

/**
 * Push service 구현
 * @author 서버개발실 이주용
 */
public class PushServiceImpl implements PushService {
	private XMLConfiguration configuration;
	private PushDao pushDao;
	
	protected final Logger logger = LoggerFactory.getLogger("tmg-push");
//	protected final Logger logger2 = LoggerFactory.getLogger("tas"); // test 20140602
	
	public void setConfiguration(XMLConfiguration configuration) {
		this.configuration = configuration;
	}

	public void setPushDao(PushDao pushDao) {
		this.pushDao = pushDao;
	}

	/**
	 * 푸시 전송 
	 */
	@Override
	@Transactional
	public void sendPush(String accountId, String message, int badge) {
		String [] targets = configuration.getStringArray("configuration.target");
		String broadcastAction, applicationName, applicationPackageName;
		String payload = "";
		
		logger.info("----------------------------------------");
		for (String t : targets) {
			broadcastAction			= configuration.getString(String.format("configuration.%s.broadcastAction", t));
			applicationName			= configuration.getString(String.format("configuration.%s.applicationName", t));
			applicationPackageName	= configuration.getString(String.format("configuration.%s.applicationPackageName", t));
//			message = message.substring(0, message.length()-1) + "\"USE_OFLN\":\"Y\"";
			//message = message + "\"USE_OFLN\":\"Y\"";
			payload					= String.format("{\"badge\":%d,\"flag\":\"mn\",\"message\":\"%s\"}", badge, message);


//			payload = payload.substring(0, payload.length()-1) + "\"USE_OFLN\":\"Y\"" + "}";
			//payload = payload.substring(0, payload.length()-1) + ",USE_OFLN:Y}";
			
			logger.info(t);
			logger.info("--------------");
			logger.debug("broadcastAction = " + broadcastAction);
			logger.debug("applicationName = " + applicationName);
			logger.debug("applicationPackageName = " + applicationPackageName);
			logger.debug("payload@@@@ = " + payload);
//			logger2.info(t);
//			logger2.info("--------------");
//			logger2.debug("broadcastAction = " + broadcastAction);
//			logger2.debug("applicationName = " + applicationName);
//			logger2.debug("applicationPackageName = " + applicationPackageName);
//			logger2.debug("payload**** = " + payload);
			
			try {
				pushDao.insertPush(accountId, applicationName, applicationPackageName, broadcastAction, payload);
			} catch (Exception e) {
				continue;
			}
			
			logger.debug(String.format("insertPush(%s, %s, %s, %s, %s) - ok", accountId, applicationName, applicationPackageName, broadcastAction, payload));
		}
		logger.info("----------------------------------------");
	}
}
