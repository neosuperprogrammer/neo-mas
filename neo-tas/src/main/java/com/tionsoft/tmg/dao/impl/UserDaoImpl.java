package com.tionsoft.tmg.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tionsoft.tmg.dao.UserDao;

/**
 * 사용자 정보 조회 DAO 구현
 * @author 서버개발실 이주용
 */
public class UserDaoImpl extends SqlMapClientDaoSupport implements UserDao {

	/**
	 * 사용자 정보 조회<br/>
	 * @return Map 반환
	 * <ul>
	 * <li>사원번호</li>
	 * <li>직급코드</li>
	 * </ul>
	 */
	public Map<String, Object> getUserInfo(String groupCode, String accountId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID", accountId);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> ret = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("user.getUserInfo", params);
		
		if (ret == null || ret.size() < 1) throw new EmptyResultDataAccessException(1);
		
		return ret;
	}

	@Override
	public Map<String, Object> getUserInfo(String groupCode, int addressId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ADDRESS_ID", addressId);
			
		@SuppressWarnings("unchecked")
		Map<String, Object> ret = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("user.getUserInfo", params);
		
		if (ret == null || ret.size() < 1) throw new EmptyResultDataAccessException(1);
		
		return ret;
	}
}
