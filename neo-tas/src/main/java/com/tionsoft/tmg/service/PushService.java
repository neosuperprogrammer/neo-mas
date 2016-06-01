package com.tionsoft.tmg.service;

/**
 * Push Service
 * @author 서버개발실 이주용
 */
public interface PushService {
	/**
	 * 푸시 전송
	 * @param accountId 사용자 계정
	 * @param message 메시지
	 * @param badge 배지 카운트
	 */
	public void sendPush(String accountId, String message, int badge);
}
