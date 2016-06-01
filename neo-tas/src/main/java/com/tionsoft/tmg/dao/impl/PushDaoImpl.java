package com.tionsoft.tmg.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tionsoft.tmg.dao.PushDao;

/**
 * Push DAO 구현
 * @author 서버개발실 이주용
 */
public class PushDaoImpl extends SqlMapClientDaoSupport implements PushDao {
	/**
	 * 푸시 등록
	 */
	@Override
	public void insertPush(String accountId, String applicationName, String applicationPackageName, String broadcastAction, String payload) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID",	accountId);
		params.put("PUSH_RECEIVER",	applicationName);
		params.put("PACKAGE_NAME",	applicationPackageName);
		params.put("PAYLOAD",		payload);
		params.put("ACTION",		broadcastAction);
		
		getSqlMapClientTemplate().insert("push.insertPush", params);
	}
}
