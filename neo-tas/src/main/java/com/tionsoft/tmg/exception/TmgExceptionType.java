package com.tionsoft.tmg.exception;

public class TmgExceptionType {
	/**
	 * Legacy 시스템 내부 오류
	 */
	public static final short SUCCESS = 0;

	/**
	 * Legacy 시스템 내부 오류
	 */
	public static final short UNDEFINED_EXCEPTION = 14220;
	
	/**
	 * Legacy 시스템 내부 오류
	 */
	public static final short OUTJSON_ALREADTY_FILLED = 14000;
	
	/**
	 * Legacy Interface 서버 연결 실패
	 */
	public static final short LEGACY_CONNECTION_FAILED = 14010;
	
	/**ww3rww
	 * 서버 규격 필수 항목 누락
	 */
	public static final short MISSING_REQUIRED_ITEMS = 14030;
	
	/**
	 * 정의되지 않은 Message 요청
	 */
	public static final short NOT_EXISTS_REQUEST_MESSAGE_ID = 14040;
	
	/**
	 * 정의되지 않은 Message 요청
	 */
	public static final short NOT_EXISTS_RESPONSE_DATA = 14050;

	/**
	 * 인증 실패
	 */
	public static final short AUTHENTICATION_FAILED = 14060;
	
	/**
	 * Request Data 없음
	 */
	public static final int NOT_EXISTS_REQUEST_DATA = 14070;
	
	/**
	 * 데이터 파싱 오류
	 */
	public static final short PARSING_FAILED = 14080;
	
	/**
	 * 연락처 없음
	 */
	public static final short NOT_EXISTS_ADDRESS = 14110;

	/**
	 * 받는 사람(TO, CC, BCC) 이메일 정보가 없습니다.
	 */
	public static final short NOT_EXISTS_SENDER_INFO = 14210;

	/**
	 * 시작날짜 또는 종료날짜가 잘못되었습니다.
	 */
	public static final short INVALID_REQUEST_DATE = 14210;
	
	/**
	 * Domino I/F 서비스 오류
	 */
	public static final short DOMINO_IF_SERVICE_FAIL = 14500;
	/**
	 * Domino I/F 서비스 오류
	 */
	public static final short DOMINO_IF_SERVICE_FAIL1 = 14591;

	/**
	 * 인증키 불일치
	 */
	public static final short NOT_MATCHED_AUTHKEY = 14610;
	
	/**
	 * 미등록 단말
	 */
	public static final short NOT_LINKED_ACCOUNT_DEVICE = 14620;
	
	/**
	 * 검색 조건의 데이터 없음
	 */
	public static final short NOT_EXISTS_DATA = 14110;
	
	/**
	 * 문서 조회 권한 없음
	 */
	public static final short DOCUMENT_PERMISSION_DENIED = 14420;
	
	/**
	 * 30메가 이상 모바일 조회 불가
	 */
	public static final short DOCUMENT_OVER_THAN_30M = 14430;

	/**
	 * 현재 에러 코드
	 */
	private short code = SUCCESS;
	
	/**
	 * 생성자
	 * @param errorCode 에러코드
	 */
	public TmgExceptionType(short errorCode) {
		this.code = errorCode;
	}
	
	/**
	 * errorCode getter
	 * @return 에러 코드 번호
	 */
	public short getCode() {
		return code;
	}
	
	/**
	 * 로케일에 따른 에러 메시지 텍스트를 반환
	 * @param locale TmcErrorMessage 싱글턴 (에러 메시지 파일 데이터는 OnLoadListener에서 최초 한번 로딩됨)
	 * @return 에러 메시지
	 */
	public String getMessage(String locale) {
		try {
			return TmgExceptionMessage.getInstance().getMessage(getCode(), locale);
		} catch (Exception ex) {
			return getMessage();
		}
	}
	
	/**
	 * 기본 로케일 설정에 따른 에러 메시지 텍스트 반환
	 * @return 에러 메시지
	 */
	public String getMessage() {
		return getMessage("default");
	}
}
