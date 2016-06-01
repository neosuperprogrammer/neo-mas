package com.tionsoft.tmg.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Command DAO Interface 
 * @author 서버개발실 이주용
 */
public interface CommandDao {
	/**
	 * 사용 권한 조회
	 * @param groupCode 그룹코드
	 * @param accountId 사용자 계정
	 * @return
	 */
	public Map<String, Object> getPermission(String groupCode, String accountId, String wifi);
	
	/**
	 * 일정 개수 조회
	 * @param groupCode 그룹코드
	 * @param accountId	 사용자 계정
	 * @param date 조회 일자
	 * @return
	 */
	public int getScheduleCount(String groupCode, String accountId, Date date);
	
	/**
	 * 지정 일자에 게시된 게시물 개수 조회
	 * @param groupCode 그룹코드
	 * @param boardId 게시판 아이디
	 * @param date 조회 일자
	 * @return
	 */
	public int getBoardItemCount(String groupCode, String boardId, Date date);
	
	/**
	 * 서비스 데스크 정보 조회
	 * @param groupCode 그룹코드
	 * @return
	 */
	public Map<String, Object> getServiceDesk(String groupCode);
	
	/**
	 * 사용자 설정 조회
	 * @param groupCode 그룹코드
	 * @param accountId 사용자 계정
	 * @param wifi MAC 주소
	 * @return
	 */
	public Map<String, Object> getUserConfiguration(String groupCode, String accountId, String wifi);
	
	/**
	 * 사용자 설정 추가
	 * @param groupCode 그룹코드
	 * @param accountId 사용자 계정
	 * @param wifi WIFI 주소
	 * @param objects 사용자 설정<br/>
	 * 사용자 설정파라미터 순서
	 * <ol>
	 * <li>서명</li>
	 * <li>푸시 사용 여부 (1 : 사용, 0 : 사용 안함)</li>
	 * <li>푸시 수신 시작 시간 (HHmm)</li>
	 * <li>푸시 수신 종료 시간 (HHmm)</li>
	 * <li>일요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>월요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>화요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>수요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>목요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>금요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>토요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>토큰 타입 (0 : APNS, 1 : GCM)</li>
	 * <li>디바이스 토큰</li>
	 * </ol>
	 */
	public void insertUserConfiguration(String groupCode, String accountId, String wifi, Object...objects);
	
	/**
	 * 사용자 설정 업데이트
	 * @param groupCode 그룹코드
	 * @param accountId 사용자 계정
	 * @param wifi WIFI 주소
	 * @param objects 사용자 설정<br/>
	 * 사용자 설정파라미터 순서
	 * <ol>
	 * <li>서명</li>
	 * <li>푸시 사용 여부 (1 : 사용, 0 : 사용 안함)</li>
	 * <li>푸시 수신 시작 시간 (HHmm)</li>
	 * <li>푸시 수신 종료 시간 (HHmm)</li>
	 * <li>일요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>월요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>화요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>수요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>목요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>금요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>토요일 푸시 수신 (1 : 허용, 0 : 허용 안함)</li>
	 * <li>토큰 타입 (0 : APNS, 1 : GCM)</li>
	 * <li>디바이스 토큰 (iOS 전용)</li>
	 * </ol>
	 * <p><b>주의 : </b> 사용자 설정 파라미터 중 null인 항목은 업데이트하지 않는다.</p>
	 * @return
	 */
	public void updateUserConfiguration(String groupCode, String accountId, String wifi, Object...objects);
	
	/**
	 * 주소록 목록 조회
	 * @param groupCode 그룹 코드
	 * @param addressGroupId 주소 그룹 아이디
	 */
	public List<Map<String, Object>> getAddressList(String accountId, String locale, String groupCode, String addressGroupId);
	
	/**
	 * 주소록 검색 목록 조회
	 * @param groupCode 그룹 코드
	 * @param keywordType 검색어 유형
	 * @param keyword 검색어
	 */
	public List<Map<String, Object>> getAddressSearchList(String accountId, String locale, String groupCode, String keyword);
	
	/**
	 * 주소록 상세 정보 조회
	 * @param groupCode 그룹 코드
	 * @param addressId 주소 아이디
	 * @return
	 */
	public Map<String, Object> getAddress(String accountId, String locale, String groupCode, String addressId);
	
	/**
	 * 단순 일정 목록 조회 
	 * @param accountId 사용자 계정
	 * @param startDate 조회 시작 일자
	 * @param endDate 조회 종료 일자
	 * @return
	 */
	public List<Map<String, Object>> getSimpleScheduleList(String accountId, String startDate, String endDate);
	
	/**
	 * 일정 목록 조회
	 * @param accountId 사용자 계정
	 * @param startDate 조회 시작 일자
	 * @param endDate 조회 종료 일자
	 * @return
	 */
	public List<Map<String, Object>> getScheduleList(String accountId, String startDate, String endDate	);
	
	/**
	 * 오프라인 모드를 위한 싱크 데이터 조회
	 * @param accountId 사용자 계정
	 * @param startDate 조회 시작 일자
	 * @param endDate 조회 종료 일자
	 * @param Sync_LastTime 마지막싱크 시간(yyyy-MM-dd HH:mm:ss)
	 * @return
	 */
	public List<Map<String, Object>> getOffLineScheduleList(String accountId, String startDate, String endDate, String Sync_LastTime);
	
	/*
	 * 오프라인 일정에서 사용하는 함수
	 * 마지막 싱크 시간 얻는 함수
	 */
	public String serverTime();
	
	public String serverTime(String faketime, int interval);
	
	/**
	 * 일정 목록 조회
	 * @param accuntId 사용자 계정
	 * @param startDate 조회 시작 일자
	 * @param direction 조회 방향<br/>
	 * <ul>
	 * <li>0 : 오늘 포함하여 이전 일정 조회</li>
	 * <li>1 : 오늘 포함하여 이후 일정 조회</li>
	 * </ul>
	 * @return
	 */
	public List<Map<String, Object>> getScheduleList(String accountId, String startDate, int direction);
	
	/**
	 * 일정 개수 조회
	 * @param accountId 사용자 계정
	 * @param startDate 조회 시작 일자
	 * @param direction 조회 방향<br/>
	 * <ul>
	 * <li>0 : 오늘 포함하여 이전 일정 조회</li>
	 * <li>1 : 오늘 포함하여 이후 일정 조회</li>
	 * </ul>
	 * @return
	 */
	public int getScheduleCount(String accountId, String startDate, int direction);
	
	/**
	 * 타시스템 카운트 조회 ( 20140919 추가 (정도경영 카운트 취득위해) )
	 * @return
	 */
	public int getCount(String id);
	
	/**
	 * 일정 검색 목록 조회
	 * @param accountId 사용자 계정
	 * @param startDate 조회 시작 일자
	 * @param endDate 조회 종료 일자
	 * @param searchType 조회 방식 (제목, 장소, 본문)
	 * @param keyword 검색어
	 * @return
	 */
	public List<Map<String, Object>> getScheduleList(String accountId, String startDate, String endDate, String titile, String place, String body);	

	/**
	 * 일정 상세 조회
	 * @param scheduleId 일정 아이디
	 * @return
	 */
	public Map<String, Object> getSchedule(String accountId, String scheduleId);
	
	/**
	 * 일정 공유자 조회
	 * @param scheduleId 일정 아이디
	 * @return
	 */
	public List<Map<String, Object>> getScheduleSharedUsers(String scheduleId);
	
	/**
	 * 일정 삭제
	 * @param scheduleId 일정 아이디
	 * @return
	 */
	public int deleteSchedule(String scheduleId);
	
	/**
	 * 일정 삭제
	 * @param scheduleIDs 일정 아이디
	 * @return
	 */
	public int deleteSchedule(String[] scheduleIDs);
	
	/**
	 * 공유 일정 삭제
	 * @param scheduleId
	 * @return
	 */
	public int deleteSharedSchedule(String scheduleId);
	
	/**
	 * 공유 일정 삭제
	 * @param scheduleId 일정 아이디
	 * @param attendeesId 참여자 아이디
	 * @return
	 */
	public int deleteSchedule(String scheduleId, String [] attendees);
	
	/**
	 * 일정 등록
	 * @param accountId 사용자 계정
	 * @param isShare 공개 여부
	 * @param isAlldayEvent 전일 일정 여부
	 * @param startDate 시작 일자
	 * @param startTime 시작 시간
	 * @param endDate 종료 일자
	 * @param endTime 종료 시간
	 * @param title 제목
	 * @param place 장소
	 * @param body 본문
	 * @return
	 */
	public String insertSchedule(String accountId, String scheduleId, String isShare, String isAllDayEvent,
								String startDate, String startTime, String endDate, String endTime, String title, String place, String body);

	/**
	 * 공유 일정 등록
	 * @param accountId
	 * @param attendeesId
	 * @param scheduleId
	 * @return
	 */
	public void insertSharedSchedule(String accountId, String attendeesId, String scheduleId);

	/**
	 * 일정 수정
	 * @param accountId 사용자 계정
	 * @param scheduleId 일정 아이디
	 * @param isShare 공개 여부
	 * @param startDate 시작 일자
	 * @param startTime 시작 시간
	 * @param endDate 종료 일자
	 * @param endTime 종료 시간
	 * @param title 제목
	 * @param place 장소
	 * @param body 본문
	 */
	public void updateSchedule(String accountId, String scheduleId, String isShare, String isAllDayEvent,
								String startDate, String startTime, String endDate, String endTime, String title, String place, String body);
	
	/**
	 * 푸시키 등록
	 * @param accountId 사용자 계정
	 * @param wifi WIFI 주소
	 * @param appId 패키지명
	 * @param deviceToken 디바이스토큰 - 푸시키
	 */
	public void insertPushKey(String wifi, String appId, String deviceToken);

	/**
	 * 푸시키 갱신
	 * @param accountId 사용자 계정
	 * @param wifi WIFI 주소
	 * @param appId 패키지명
	 * @param deviceToken 디바이스토큰 - 푸시키
	 */
	public void updatePushKey(String wifi, String appId, String deviceToken);
	
	/**
	 * 리퀘스트 로그 삽입
	 * @param accountId 사용자 계정
	 * @param packageName 앱 패키지 명
	 * @param wifi 단말
	 * @return 로그 아이디 반환
	 */
	public String insertConnectRequestLog(String accountId, String packageName, String wifi);
	
	/**
	 * 리퀘스트에 대한 리스폰스 업데이트
	 * @param connectId 로그 아이디
	 */
	public void updateConnectResponseLog(String connectId);

	/**
	 * 푸시키 확인
	 * @param wifi
	 * @param appId
	 * @return
	 */
	public Map<String, Object> getPushKey(String wifi, String appId);
	
	/**
	 * 타인일정 사용자 카운트
	 * @param accountId
	 * @return
	 */
	public int getOtherUserTotalCount(String accountId);

	/**
	 * 타인일정 사용자 리스트 조회
	 * @param accountId 사용자 계정
	 * @param beginNum 시작 번
	 * @param endNum 끝 번호
	 * @return
	 */
	public List<Map<String, Object>> getOtherUserList(String accountId, int beginNum, int endNum);
	
	/**
	 * 타인일정 사용자 추가
	 * @param accountId
	 * @param otherUserId
	 */
	public void insertOtherUser(String accountId, String otherUserId);

	/**
	 * 타인일정 사용자 삭제
	 * @param accountId
	 * @param otherUserId
	 */
	public void deleteOtherUser(String accountId, String otherUserId);
}
