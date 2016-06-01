package com.tionsoft.tmg.dao;

/**
 * Push DAO 
 * @author 서버개발실 이주용
 */
public interface PushDao {
	/**
	 * 푸시 등록
	 * @param accountId 사용자 계정
	 * @param applicationName 리시버
	 * @param packageName 패키지명
	 * @param payload 페이로드
	 */
	public void insertPush(String accountId, String applicationName, String applicationPackageName, String broadcastAction, String payload);
}
