package com.tionsoft.tmg.dao;

import java.util.Map;

/**
 * 사용자 정보 조회 DAO
 * @author 서버개발실 이주용
 */
public interface UserDao {
	/**
	 * 사용자 정보 조회
	 * @param groupCode 그룹코드
	 * @param accountId 사용자아이디
	 * @return
	 */
	public Map<String, Object> getUserInfo(String groupCode, String accountId);

	/**
	 * 사용자 정보 조회
	 * @param groupCode 그룹코드
	 * @param addressId 사용자아이디
	 * @return
	 */
	public Map<String, Object> getUserInfo(String groupCode, int addressId);
}
