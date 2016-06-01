package com.tionsoft.tmg.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.service.domain.GeneralList;
import com.tionsoft.tmg.service.domain.ParticularArrayList;

/**
 * 커맨드 서비스
 * @author 서버개발실 이주용
 */
public interface CommandService {
	/**
	 * 사용권한 조회
	 * @param groupCode 그룹 코드
	 * @param accountId 사용자 아이디
	 * @return
	 */
	public Map<String, Object> getPermission(String groupCode, String accountId, String wifi) throws TmgException;
	
	/**
	 * 배지 카운트 조회
	 * @param groupCode 그룹 코드
	 * @param accountId 사용자 아이디
	 * @param objects 기타 파라미터 - 구현에서 적절히 정의
	 * @return
	 */
	public Map<String, Object> getBadgeCount(String groupCode, String accountId, Object... objects);
	
	/**
	 * 서비스데스크 조회
	 * @param groupCode 그룹 코드
	 * @return
	 */
	public Map<String, Object> getServiceDesk(String groupCode);
	
	/**
	 * 메일 서명 조회
	 * @param groupCode 그룹코드
	 * @param accountId 사용자 계정
	 * @return
	 */
	public String getSignature(String groupCode, String accountId, String wifi);
	
	/**
	 * 메일 서명 저장
	 * @param groupCode 그룹 코드
	 * @param accountId 사용자 계정
	 * @param signature 메일 서명
	 */
	public void setSignature(String groupCode, String accountId, String wifi, String signature);
	
	/**
	 * 주소록 목록 조회
	 * @param groupCode 그룹 코드
	 * @param addressGroupId 주소 그룹 아이디
	 */
	public GeneralList getAddressList(String accountId, String locale, String groupCode, String addressGroupId, int requestPage, int countPerPage);
	
	/**
	 * 주소록 검색 목록 조회
	 * @param groupCode 그룹 코드
	 * @param keywordType 검색어 유형
	 * @param keyword 검색어
	 */
	public GeneralList getAddressSearchList(String accountId, String locale, String groupCode, String keyworType, String keyword, int requestPage, int countPerPage);
	
	/**
	 * 주소록 상세 정보 조회
	 * @param groupCode 그룹 코드
	 * @param addressId 주소 아이디
	 * @return
	 */
	public Map<String, Object> getAddress(String accountId, String locale, String groupCode, String addressId) throws TmgException;
	
	/**
	 * 일정 월별 조회
	 * @param accountId	 사용자 계정
	 * @param targetId 타인 일정 조회 대상 계정
	 * @param yyyyMM 일정 조회 년월 - yyyyMM
	 * @return
	 * @throws TmgException
	 */
	public List<Map<String, Object>> getSimpleMonthlySchedules(String groupCode, String accountId, int targetId, String yyyyMM) throws TmgException;
	
	/**
	 * 일정 주별 주회
	 * @param accountId 사용자 계정
	 * @param targetId 타인 일정 조회 대상 계정
	 * @param yyyyMMdd 일정 조회 년월일 - yyyyMMdd
	 * @return
	 * @throws TmgException
	 */
	public List<Map<String, Object>> getSimpleWeeklySchedule(String groupCode, String accountId, int targetId, String yyyyMMdd) throws TmgException;
	
	
	/**
	 * 일정 목록 조회
	 * @param accountId 사용자 계정
	 * @param targetId 타인 일정 조회 대상 계정
	 * @param startDate 일정 조회 시작일 (yyyyMMdd)
	 * @param endDate 일정 조회 종료일 (yyyyMMdd)
	 * @return
	 * @throws TmgException
	 */
	public List<Map<String, Object>> getScheduleList(String locale, String groupCode, String accountId, int targetId, String startDate, String endDate) throws TmgException;
	
	
	/**
	 * 오프라인을 위한 가간별 일정
	 * @param accountId 사용자 계정
	 * @param targetId 타인 일정 조회 대상 계정
	 * @param startDate 일정 조회 시작일 (yyyyMMdd)
	 * @param endDate 일정 조회 종료일 (yyyyMMdd)
	 * @param Sync_LastTime 마지막싱크 시간(yyyy-MM-dd HH:mm:ss)
	 * @return
	 * @throws TmgException
	 */
	public ParticularArrayList<String, Object, String> getOffLineScheduleList(String locale, String groupCode, String accountId, int targetId, String startDate, String endDate,String Sync_LastTime) throws TmgException, NoSuchAlgorithmException;

	/**
	 * 마지막 싱크 시간 조회 (오프라인 일정에서 사용)
	 * @return
	 * @throws TmgException
	 */
	public String serverTime() throws TmgException;
	
	/**
	 * 정해진 시간 간격으로 가짜 싱크 시간 조회 (오프라인 일정 테스트에서 사용)
	 * @param faketime
	 * @param interval
	 * @return
	 * @throws TmgException
	 */
	public String serverTime(String faketime, int interval) throws TmgException;
		
	/**
	 * 일정 목록 조회
	 * @param accountId 사용자 계정
	 * @param targetId 타인 일정 조회 대상 계정
	 * @param startDate 일정 조회일 (yyyyMMdd)
	 * @return
	 * @throws TmgException
	 */
	public List<Map<String, Object>> getScheduleList(String locale, String groupCode, String accountId, int targetId, String startDate) throws TmgException;

	/**
	 * 일정 목록 조회
	 * @param accountId 사용자 계정
	 * @param targetId 타인 일정 조회 대상 계정
	 * @param startDate 일정 조회 시작일 (yyyyMMdd)
	 * @param direction 조회 방향<br/>
	 * <ul>
	 * <li>0 : 오늘 포함하여 이전 일정 조회</li>
	 * <li>1 : 오늘 포함하여 이후 일정 조회</li>
	 * </ul>
	 * @param requestPage 조회 요청 페이지
	 * @param countPerPage 페이지 당 아이템 수
	 * @return
	 * @throws TmgException
	 */
	public GeneralList getScheduleList(String locale, String groupCode, String accountId, int targetId, String startDate, int direction, int requestPage, int countPerPage) throws TmgException;
	
	/**
	 * 일정 상세 조회
	 * @param accountId 사용자 계정
	 * @param targetId	타인 일정 조회 대상 계정
	 * @param scheduleId 일정 아이디
	 * @return
	 */
	public Map<String, Object> getSchedule(String locale, String groupCode, String accountId, int targetId, String scheduleId) throws TmgException;
	
	/**
	 * 일정 검색 목록 조회
	 * @param accountId 사용자 계정
	 * @param targetId 타인 일정 조회 대상 계정
	 * @param searchType 검색 방법
	 * <ul>
	 * <li>1 : 제목 검색</li>
	 * <li>2 : 장소 검색</li>
	 * <li>3 : 본문 검색</li>
	 * </ul>
	 * @param searchTerm 검색 기간
	 * <ul>
	 * <li>1 : 1주</li>
	 * <li>2 : 1개월</li>
	 * <li>3 : 2개월</li>
	 * <li>4 : 3개월</li>
	 * <li>5 : 5개월</li>
	 * <li>6 : 전체</li>
	 * </ul>
	 * @param keyword 검색어
	 * @param requestPage 조회 요청 페이지
	 * @param countPerPage 페이지당 아이템 수
	 * @return
	 * @throws TmgException
	 */
	public GeneralList getScheduleSearchList(String locale, String groupCode, String accountId, int targetId, String searchType, String searchTerm,
												String keyword, int requestPage, int countPerPage) throws TmgException;
	
	/**
	 * 일정 등록
	 * @param accountId 사용자 계정
	 * @param attendees 일정 참여자 계정
	 * @param isShare 공개 여부
	 * @param isAllDayEvent 전일 일정 여부
	 * @param startDateTime 일정 시작 일시
	 * @param endDateTime 일정 종료 일시
	 * @param title 제목
	 * @param place 장송
	 * @param body 내용
	 * @return scheduleId 스케줄러 아이디
	 * @throws TmgException
	 */
	public String addSchedule(String accountId, String scheduleId, String [] attendees, String isShare, String isAllDayEvent,
								String startDateTime, String endDateTime, String title, String place, String body) throws TmgException ;
	
	/**
	 * 일정 업데이트 
	 * @param accountId 사용자 계정
	 * @param scheduleId 일정 아이디
	 * @param attendees 일정 참여자 계정
	 * @param isShare 공개 여부
	 * @param isAllDayEvent 전일 일정 여부
	 * @param startDateTime 일정 시작 일시
	 * @param endDateTime 일정 종료 일시
	 * @param title 제목
	 * @param place 장송
	 * @param body 내용
	 * @throws TmgException
	 */
	public void updateSchedule(String accountId, String scheduleId, String [] attendees, String isShare, String isAllDayEvent,
								String startDateTime, String endDateTime, String title, String place, String body) throws TmgException;
	
	/**
	 * 일정 삭제
	 * @param scheduleId 일정 아이디
	 * @throws TmgException
	 */
	public void deleteSchedule(String scheduleId) throws TmgException;
	
	/**
	 * 일정 삭제
	 * @param scheduleIDs 일정 아이디
	 * @throws TmgException
	 */
	public void deleteSchedule(String [] scheduleIDs) throws TmgException;

	/**
	 * 푸시 설정 조회
	 * @param groupCode 그룹 코드
	 * @param accountId 사용자 계정
	 * @param wifi MAC 주소
	 */
	public Map<String, Object> getPushConfiguration(String groupCode, String accountId, String wifi);
	
	/**
	 * 푸시 설정 저장
	 * @param groupCode 그룹 코드
	 * @param accountId 사용자 계정
	 * @param wifi MAC 주소
	 * @param usePush 푸시 사용 여부
	 * @param pushTime 푸시 허용 시간
	 * @paran tokenType 토큰 타입 (0 : APNS, 1 : GCM) 
	 * @param deviceToken 디바이스 토큰
	 * @param weekDays 요일별 푸시 허용 여부 설정(일, 월, ... , 금, 토 순서)
	 */
	public void setPushConfiguration(String groupCode, String accountId, String wifi, boolean usePush, String pushTime, String tokenType, String deviceToken, String osType, String appId, boolean...weekDays) throws TmgException;
	
	/**
	 * 디바이스 토큰 등록
	 * @param groupCode 그룹 코드
	 * @param accountId 사용자 계정
	 * @param wifi 주소
	 * @paran tokenType 토큰 타입 (0 : APNS, 1 : GCM) 
	 * @param deviceToken 디바이스 토큰
	 */
	public void setDeviceToken(String groupCode, String accountId, String wifi, String tokenType, String osType, String deviceToken, String appId);
	
	/**
	 * 사용자 정보 조회
	 * @param groupCode 그룹 코드
	 * @param accountId 사용자 계정
	 * @return
	 */
	public Map<String, Object> getUserInfo(String groupCode, String accountId);
	
	/**
	 * 사용자 정보 조회
	 * @param groupCode 그룹 코드
	 * @param addressId 주소 아이디
	 * @return
	 */
	public Map<String, Object> getUserInfo(String groupCode, int addressId);
	
	/**
	 * 접속 로그 등록
	 * @param accountId 사용자 계정
	 * @param packageName 앱 패키지 명
	 * @param wifi 단말 WIFI
	 * @return 접속 로그 아이디
	 */
	public String setConnectRequestLog(String accountId, String packageName, String wifi);
	
	/**
	 * 접속 로그 응답 업데이트
	 * @param connectId 접속 로그 아이디
	 */
	public void setConnectResponseLog(String connectId);

	/**
	 * 타인일정 사용자 리스트 조회
	 * @param accountId 사용자 계정
	 * @param requestPage 요청 페이지
	 * @param countPerPage 페이지당 아이템 수
	 * @return
	 * @throws Exception 
	 */
	public GeneralList getOtherUserList(String accountId, int requestPage, int countPerPage) throws Exception;

	/**
	 * 타인일정 사용자 추가
	 * @param accountId 사용자 계정
	 * @param otherUserId 요청 페이지
	 * @param processType 페이지당 아이템 수
	 * @return
	 */
	public void setOtherUser(String accountId, String otherUserId, String processType) throws Exception;
}
