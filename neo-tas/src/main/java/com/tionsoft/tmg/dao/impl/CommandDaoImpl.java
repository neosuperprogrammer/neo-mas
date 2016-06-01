package com.tionsoft.tmg.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tionsoft.tmg.dao.CommandDao;
import com.tionsoft.tmg.util.CalendarHelper;
import com.tionsoft.tmg.util.NullHandler;

/**
 * 
 * @author 서버개발실 이주용
 */
public class CommandDaoImpl extends SqlMapClientDaoSupport implements CommandDao {
	/**
	 * 권한 조회
	 */
	@Override
	public Map<String, Object> getPermission(String groupCode, String accountId, String wifi) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("GROUP_CODE", groupCode);
		params.put("ACCOUNT_ID", accountId);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("command.getPermission", params);
		
		if (result == null || result.size() < 1) throw new EmptyResultDataAccessException(1);
		
		return result;
	}

	/**
	 * 일정 개수 조회
	 */
	@Override
	public int getScheduleCount(String groupCode, String accountId, Date date) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("GROUP_CODE", groupCode);
		params.put("ACCOUNT_ID", accountId);
		params.put("REQ_DATE", CalendarHelper.getFormatedDateString(date, "yyyyMMdd"));
		
		return (Integer) getSqlMapClientTemplate().queryForObject("command.getTodayScheduleCount", params);
	}

	/**
	 * 타시스템 카운트 조회 ( 20140919 추가 (정도경영 카운트 취득위해) )
	 */
	@Override
	public int getCount(String id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ID", id);
		return (Integer) getSqlMapClientTemplate().queryForObject("command.getCount", params);
	}

	/**
	 * 지정 일자에 게시된 게시물 개수 조회
	 */
	@Override
	public int getBoardItemCount(String groupCode, String boardId, Date date) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("GROUP_CODE", groupCode);
		params.put("BOARD_ID", boardId);
		params.put("REQ_DATE", date);
		
		return (Integer) getSqlMapClientTemplate().queryForObject("command.getTodayNewBoardItemCount", params);
	}

	/**
	 * 서비스 데스크 정보 조회
	 */
	@Override
	public Map<String, Object> getServiceDesk(String groupCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("GROUP_CODE", groupCode);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("command.getServiceDesk", params);
		
		if (result == null || result.size() < 1) throw new EmptyResultDataAccessException(1);
		
		return result;
	}

	/**
	 * 사용자 설정 조회
	 */
	@Override
	public Map<String, Object> getUserConfiguration(String groupCode, String accountId, String wifi) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("GROUP_CODE",	groupCode);
		params.put("ACCOUNT_ID",	accountId);
		params.put("WIFI",			wifi);

		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) getSqlMapClientTemplate().queryForObject("command.getUserConfiguration", params);
		
		if (result == null) throw new EmptyResultDataAccessException(1);
		
		return result;
	}

	/**
	 * 사용자 설정 추가
	 */
	@Override
	public void insertUserConfiguration(String groupCode, String accountId, String wifi, Object... objects) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("GROUP_CODE",	groupCode);
		params.put("ACCOUNT_ID",	accountId);
		params.put("WIFI",			wifi);
		
		if (objects.length > 0)		params.put("SIGNATURE",			objects[0]);
		if (objects.length > 1) 	params.put("USE_PUSH",			objects[1]);
		if (objects.length > 2) 	params.put("PUSH_START_TIME",	objects[2]);
		if (objects.length > 3) 	params.put("PUSH_END_TIME",		objects[3]);
		if (objects.length > 4) 	params.put("TOKEN_TYPE",		objects[4]);
		if (objects.length > 5) 	params.put("DEVICE_TOKEN",		objects[5]);
		if (objects.length > 6) 	params.put("USE_PUSH_0",		objects[6]);
		if (objects.length > 7) 	params.put("USE_PUSH_1",		objects[7]);
		if (objects.length > 8) 	params.put("USE_PUSH_2",		objects[8]);
		if (objects.length > 9) 	params.put("USE_PUSH_3",		objects[9]);
		if (objects.length > 10) 	params.put("USE_PUSH_4",		objects[10]);
		if (objects.length > 11) 	params.put("USE_PUSH_5",		objects[11]);
		if (objects.length > 12) 	params.put("USE_PUSH_6",		objects[12]);

		getSqlMapClientTemplate().insert("command.insertUserConfiguration", params);
	}
	
	/**
	 * 사용자 설정 업데이트
	 */
	@Override
	public void updateUserConfiguration(String groupCode, String accountId, String wifi, Object...objects) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("GROUP_CODE",	groupCode);
		params.put("ACCOUNT_ID",	accountId);
		params.put("WIFI",			wifi);
		
		if (objects.length > 0)		params.put("SIGNATURE",			objects[0]);
		if (objects.length > 1) 	params.put("USE_PUSH",			objects[1]);
		if (objects.length > 2) 	params.put("PUSH_START_TIME",	objects[2]);
		if (objects.length > 3) 	params.put("PUSH_END_TIME",		objects[3]);
		if (objects.length > 4) 	params.put("TOKEN_TYPE",		objects[4]);
		if (objects.length > 5) 	params.put("DEVICE_TOKEN",		objects[5]);
		if (objects.length > 6) 	params.put("USE_PUSH_0",		objects[6]);
		if (objects.length > 7) 	params.put("USE_PUSH_1",		objects[7]);
		if (objects.length > 8) 	params.put("USE_PUSH_2",		objects[8]);
		if (objects.length > 9) 	params.put("USE_PUSH_3",		objects[9]);
		if (objects.length > 10) 	params.put("USE_PUSH_4",		objects[10]);
		if (objects.length > 11) 	params.put("USE_PUSH_5",		objects[11]);
		if (objects.length > 12) 	params.put("USE_PUSH_6",		objects[12]);
		
		getSqlMapClientTemplate().insert("command.updateUserConfiguration", params);
	}

	/**
	 * 주소록 목록 조회
	 */
	@Override
	public List<Map<String, Object>> getAddressList(String accountId, String locale, String groupCode, String addressGroupId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("LOCALE",		locale);
		params.put("GROUP_CODE",	groupCode);
		params.put("GROUP_ID",		addressGroupId);
		params.put("ACCOUNT_ID",	accountId);
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>)getSqlMapClientTemplate().queryForList("command.getAddressList", params);

		return result;
	}

	/**
	 * 주소록 검색 목록 조회
	 */
	@Override
	public List<Map<String, Object>> getAddressSearchList(String accountId, String locale, String groupCode, String keyword) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("LOCALE",		locale);
		params.put("GROUP_CODE",	groupCode);
		params.put("KEYWORD",		keyword);
		params.put("ACCOUNT_ID",	accountId);
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>)getSqlMapClientTemplate().queryForList("command.getAddressSearchList", params);

		return result;
	}

	/**
	 * 주소록 상세 정보 조회
	 */
	@Override
	public Map<String, Object> getAddress(String accountId, String locale, String groupCode, String addressId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("LOCALE",		locale);
		params.put("GROUP_CODE",	groupCode);
		params.put("ACCOUNT_ID2",	accountId);
		
		try {
			params.put("ADDRESS_ID",	Integer.parseInt(addressId));
		} catch (Exception e) {
//			params.put("ACCOUNT_ID",	addressId.split("@")[0]);
			params.put("ACCOUNT_ID",	addressId);
		}
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("command.getAddress", params);
		
		return result;
	}

	/**
	 * 단순 일정 목록 조회
	 */
	@Override
	public List<Map<String, Object>> getSimpleScheduleList(String accountId, String startDate, String endDate) {
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("ACCOUNT_ID",	accountId);
		params.put("START_DATE",	startDate);
		params.put("END_DATE",		endDate);
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>)getSqlMapClientTemplate().queryForList("command.getSimpleSchedule", params);
		
		return result;
	}

	/**
	 * 일정 목록 조회
	 */
	@Override
	public List<Map<String, Object>> getScheduleList(String accountId, String startDate, String endDate) {
		Map<String, Object> params = new HashMap<String, Object>();
		String statementName;
		
		startDate	= startDate.substring(0, 8);
		endDate		= endDate.substring(0, 8);
		
		params.put("ACCOUNT_ID",	accountId);
		params.put("START_DATE",	startDate);
		params.put("END_DATE",		endDate);
		
		statementName = startDate.equals(endDate) ? "command.getScheduleListByDay" : "command.getScheduleList";
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>)getSqlMapClientTemplate().queryForList(statementName, params);
		
		return result;
	}
	
	/**
	 * 일정 목록 조회
	 */
	@Override
	public List<Map<String, Object>> getOffLineScheduleList(String accountId, String startDate, String endDate ,String syncLastTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		String statementName = "";
		
		startDate	= startDate.substring(0, 8);
		endDate		= endDate.substring(0, 8);
		
		if (syncLastTime.isEmpty()) {
			statementName = "command.getOffLineScheduleList";
		} else {
			statementName = "command.getOffLineScheduleUpdateData";
		}
		
		params.put("ACCOUNT_ID",	accountId);
		params.put("START_DATE",	startDate);
		params.put("END_DATE",		endDate);
		params.put("SYNC_DATE",		syncLastTime);
		
		/**
		 * 서비스 개발팀(김용수) 
		 * 2014.03.19
		 * PMG.LETS_SCHEDULE : 일정 정보가 있는 테이블
		 * BW_LETS_SCHEDULE_S_I : 모바일에서 등록한 일정을 공유 하기 위한 인터페이스 테이블
		 * BW_LETS_SCHEDULE_R_I : 웹에서 등록한 일정을 공유 받기 위한 인터페이스 테이블
		 * 
		 * 특이사항 : 모바일에서 일정 등록시 LGD서버와 공유를 하기 위해 BW_LETS_SCHEDULE_S_I에 등록이 되고 LGD에서 모바일에서 등록된 일정을 반영 후 다시
		 * BW_LETS_SCHEDULE_R_I에 값이 넘어 오기 때문에 BW_LETS_SCHEDULE_R_I만 참고 하면 된다
		 * BW_LETS_SCHEDULE_R_I에 등록된 값이 PMG.LETS_SCHEDULE에 반영이 되지만 삭제시 PMG.LETS_SCHEDULE에 값이 없기 때문에 BW_LETS_SCHEDULE_R_I테이블을 참조한다.
		 * 
		 * 1.Sync_LastTime값이 없다면 PMG.LETS_SCHEDULE 테이블에서 아이디에 해당하는 날짜에 대해 조회를 한다.
		 * 2.Sync_LastTime이 있다면 BW_LETS_SCHEDULE_S_I(모바일에서 변경한 일정),BW_LETS_SCHEDULE_R_I(웹에서 변경한 일정) 테이블에서 변경 및 삭제 데이터를 조회한다.
		 *   단말에서 변경한 정보가 있는 BW_LETS_SCHEDULE_S_I 테이블을 조회 하는 이유는 한 아이디를 2개 이상 사용하는 경우 A단말에서 수정한 내용을 B단말에서 반영 될 수 있도록 하기 위함
		 */											       
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>)getSqlMapClientTemplate().queryForList(statementName, params);
		
		return result;
	}	

	/**
	 * 일정 목록 조회
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getScheduleList(String accountId, String startDate, int direction) {
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("ACCOUNT_ID", accountId);
		params.put("START_DATE", startDate);
		
		List<Map<String, Object>> result = null;
		
		switch (direction) {
		case 0:
			result = (List<Map<String, Object>>)getSqlMapClientTemplate().queryForList("command.getScheduleListBeforeToday", params);
			break;
		case 1:
			result = (List<Map<String, Object>>)getSqlMapClientTemplate().queryForList("command.getScheduleListAfterToday", params);
			break;
		default:
		}
		
		return result;
	}
	
	/**
	 * 일정 목록 조회
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int getScheduleCount(String accountId, String startDate, int direction) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> result = null;
		int resultCount = 0;
		
		params.put("ACCOUNT_ID", accountId);
		params.put("START_DATE", startDate);
		
//		switch (direction) {
//		case 0:
//			result = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("command.getScheduleCountBeforeToday", params);
//			break;
//		case 1:
//			result = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("command.getScheduleCountAfterToday", params);
//		}
		
		if (direction == 0) {
			result = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("command.getScheduleCountBeforeToday", params);
		} else {
			result = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("command.getScheduleCountAfterToday", params);
		}
		
		resultCount = NullHandler.ifNullTo(result.get("TotalCount"), 0);
		
		return resultCount;
	}
	

	/**
	 * 일정 상세 조회
	 */
	@Override
	public Map<String, Object> getSchedule(String accountId, String scheduleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SCHEDULE_ID", scheduleId);
		params.put("ACCOUNT_ID", accountId);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("command.getSchedule", params);

		return result;
	}

	/**
	 * 일정 공유자 조회
	 */
	@Override
	public List<Map<String, Object>> getScheduleSharedUsers(String scheduleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SCHEDULE_ID", scheduleId);
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>)getSqlMapClientTemplate().queryForList("command.getScheduleSharedUsers", params);
		
		return result;
	}

	/**
	 * 일정 검색 목록 조회
	 */
	@Override
	public List<Map<String, Object>> getScheduleList(String accountId, String startDate, String endDate, String titile, String place, String body) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID",	accountId);
		params.put("START_DATE",	startDate);
		params.put("END_DATE",		endDate);
		params.put("TITLE",			titile);
		params.put("PLACE", 		place);
		params.put("BODY", 			body);
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>)getSqlMapClientTemplate().queryForList("command.getScheduleSearchList", params);
		
		return result;
	}

	/**
	 * 일정 삭제
	 */
	@Override
	public int deleteSchedule(String scheduleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SCHEDULE_ID",	scheduleId);
		
		return getSqlMapClientTemplate().delete("command.deleteSchedule", params);
	}
	
	/**
	 * 일정 삭제
	 */
	@Override
	public int deleteSchedule(String[] scheduleIDs) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SCHEDULE_IDS",	scheduleIDs);
		
		return getSqlMapClientTemplate().delete("command.deleteSchedules", params);
	}
	
	/*
	 * 오프라인 일정에서 사용하는 함수
	 * 마지막 싱크 시간 얻는 함수
	 */
	public String serverTime() {
		return (String)  getSqlMapClientTemplate().queryForObject("command.GetServerTime");
	}
	
	public String serverTime(String faketime, int interval) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("FAKE_TIME",	faketime);
		params.put("MIN",	interval);
		
		return (String)  getSqlMapClientTemplate().queryForObject("command.GetServerFakeTime", params);
	}
	
	public int deleteSchedule(String scheduleId, String [] attendees) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SCHEDULE_ID",	scheduleId);
		params.put("ATTENDEES_IDS",	attendees);
		
		return getSqlMapClientTemplate().delete("command.deleteSharedSchedule", params);
	}
	
	public int deleteSharedSchedule(String scheduleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SCHEDULE_ID",	scheduleId);
		
		return getSqlMapClientTemplate().delete("command.deleteSharedSchedule", params);
	}

	/**
	 * 일정 등록
	 */
	@Override
	public String insertSchedule(String accountId, String scheduleId, String isShare, String isAllDayEvent,
								String startDate, String startTime, String endDate, String endTime, String title, String place, String body) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID",		accountId);
		params.put("SCHEDULE_ID",		scheduleId);
		params.put("IS_SHARE",			isShare);
		params.put("IS_ALL_DAY_EVENT",	isAllDayEvent);
		params.put("START_DATE",		startDate);
		params.put("START_TIME",		startTime);
		params.put("END_DATE",			endDate);
		params.put("END_TIME",			endTime);
		params.put("TITLE",				title.replace("'", "`"));
		params.put("PLACE",				place.replace("'", "`"));
		params.put("BODY",				body.replace("'", "`"));
		
		return (String) getSqlMapClientTemplate().insert("command.insertSchedule", params);
	}
	
	/**
	 * 일정 수정
	 */
	@Override
	public void updateSchedule(String accountId, String scheduleId, String isShare, String isAllDayEvent,
								String startDate, String startTime, String endDate, String endTime, String title, String place, String body) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID",		accountId);
		params.put("SCHEDULE_ID",		scheduleId);
		params.put("IS_SHARE",			isShare);
		params.put("IS_ALL_DAY_EVENT",	isAllDayEvent);
		params.put("START_DATE",		startDate);
		params.put("START_TIME",		startTime);
		params.put("END_DATE",			endDate);
		params.put("END_TIME",			endTime);
		params.put("TITLE",				title.replace("'", "`"));
		params.put("PLACE",				place.replace("'", "`"));
		params.put("BODY",				body.replace("'", "`"));
		
		getSqlMapClientTemplate().update("command.updateSchedule", params);
		
	}

	/**
	 * 공유 일정 추가 
	 */
	@Override
	public void insertSharedSchedule(String accountId, String attendeesId, String scheduleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID",		accountId);
		params.put("ATTENDEES_ID",		attendeesId);
		params.put("SCHEDULE_ID",		scheduleId);
		
		getSqlMapClientTemplate().insert("command.insertSharedSchedule", params);
	}

	/**
	 * 푸시키 등록
	 */
	@Override
	public void insertPushKey(String wifi, String appId, String deviceToken) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("WIFI",			wifi);
		params.put("APP_ID",		appId);
		params.put("DEVICE_TOKEN",	deviceToken);
		
		getSqlMapClientTemplate().insert("command.insertPushkey", params);
	}

	/**
	 * 푸시키 확인
	 */
	@Override
	public Map<String, Object> getPushKey(String wifi, String appId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("WIFI",			wifi);
		params.put("APP_ID",		appId);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("command.getPushKey", params);
		if (result == null) throw new EmptyResultDataAccessException(1);
		return result;
	}
	
	/**
	 * 푸시키 갱신
	 */
	@Override
	public void updatePushKey(String wifi, String appId, String deviceToken) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("WIFI",			wifi);
		params.put("APP_ID",		appId);
		params.put("DEVICE_TOKEN",	deviceToken);
		
		System.out.println("-------------------------4-1 " + deviceToken);
		
		getSqlMapClientTemplate().update("command.updatePushkey", params);
	}
	
	/**
	 * 리퀘스트 로그 삽입
	 */
	@Override
	public String insertConnectRequestLog(String accountId, String packageName, String wifi) {
		  Map<String, String> params = new HashMap<String, String>();
		  params.put("PACKAGE_NAME", packageName);
		  params.put("ACCOUNT_ID", accountId);
		  params.put("WIFI", wifi);
		  
		  @SuppressWarnings("unchecked")
		  Map<String, Object> ret = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("command.insertConnectRequestLog", params);
		  if (ret == null) throw new EmptyResultDataAccessException(1);
		  return ret.get("CONNECT_ID").toString();
	}
	
	/**
	 * 리퀘스트에 대한 리스폰스 업데이트
	 */
	@Override
	public void updateConnectResponseLog(String connectId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("CONNECT_ID", connectId);
		getSqlMapClientTemplate().update("command.updateConnectResponseLog", params);
	}
	
	/**
	 * 타인일정 사용자 카운트
	 * @param accountId
	 * @return
	 */
	@Override
	public int getOtherUserTotalCount(String accountId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("ACCOUNT_ID", accountId);
		return (Integer) getSqlMapClientTemplate().queryForObject("command.getOtherUserTotalCount", params);
	}

	/**
	 * 타인일정 사용자 리스트 조회
	 * @param accountId 사용자 계정
	 * @param requestPage 요청 페이지
	 * @param countPerPage 페이지 당 아이템 수
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getOtherUserList(String accountId, int beginNum, int endNum) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("ACCOUNT_ID", accountId);
		params.put("BEGIN_NUM", Integer.toString(beginNum));
		params.put("END_NUM", Integer.toString(endNum));
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> ret = (List<Map<String, Object>>) getSqlMapClientTemplate().queryForList("command.getOtherUserList", params);
		if (ret == null) ret = new ArrayList<Map<String, Object>>();
		return ret;
	}
	
	@Override
	public void insertOtherUser(String accountId, String otherUserId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("ACCOUNT_ID", accountId);
		params.put("OTHER_ACCOUNT_ID", otherUserId);
		
		int isExist = (Integer) getSqlMapClientTemplate().queryForObject("command.isOtherUserExist", params);
		if (isExist == 0)
			getSqlMapClientTemplate().update("command.insertOtherUser", params);
	}
	
	@Override
	public void deleteOtherUser(String accountId, String otherUserId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("ACCOUNT_ID", accountId);
		params.put("OTHER_ACCOUNT_ID", otherUserId);
		
		getSqlMapClientTemplate().delete("command.deleteOtherUser", params);
	}
}
