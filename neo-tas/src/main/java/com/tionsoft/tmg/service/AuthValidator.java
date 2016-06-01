package com.tionsoft.tmg.service;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.tmg.exception.TmgException;

/**
 * 인증 정보 검증 서비스
 * @author 서버개발실 이주용
 */
public interface AuthValidator {
	/**
	 * 인증 정보를 검증한다.
	 * @param authInfo
	 * @throws TmgException
	 */
	public void validate(TasRequest request) throws TmgException;
}
