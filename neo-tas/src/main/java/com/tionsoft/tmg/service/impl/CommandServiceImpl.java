package com.tionsoft.tmg.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.XMLConfiguration;
import org.springframework.dao.EmptyResultDataAccessException;

import com.tionsoft.tmg.dao.CommandDao;
import com.tionsoft.tmg.dao.UserDao;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.service.CommandService;
import com.tionsoft.tmg.service.domain.GeneralList;
import com.tionsoft.tmg.service.domain.ParticularArrayList;
import com.tionsoft.tmg.util.CalendarHelper;
import com.tionsoft.tmg.util.LgdCommon;
import com.tionsoft.tmg.util.PhoneMapper;

 /* CommandService 구현
 * @author 서버개발실 이주용
 */
public class CommandServiceImpl implements CommandService {
	private XMLConfiguration configuration;
	private CommandDao commandDao;
	private UserDao userDao;
	private PhoneMapper phoneMapper;

	public CommandDao getCommandDao() {
		return commandDao;
	}

	public void setCommandDao(CommandDao commandDao) {
		this.commandDao = commandDao;
	}

	public XMLConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(XMLConfiguration configuration) {
		this.configuration = configuration;
	}
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public PhoneMapper getPhoneMapper() {
		return this.phoneMapper;
	}
	
	public void setPhoneMapper(PhoneMapper phoneMapper) {
		this.phoneMapper = phoneMapper;
	}

	/**
	 * 사용권한 조회
	 * @throws TmgException 
	 */
	@Override
	public Map<String, Object> getPermission(String groupCode, String accountId, String wifi) throws TmgException {
		Map<String, Object> permissions = null;
		String approvalPermission = "n";
		
		try {
			permissions = commandDao.getPermission(groupCode, accountId, wifi);
			approvalPermission = (String) permissions.get("USE_APPRV");
			
			// 결재 권한이 있으면 모바일 결재서비스 URL 전달
			if (approvalPermission.equalsIgnoreCase("y")) {
				permissions.put("APPRV_URL", getConfiguration().getString("configuration.approveService.url"));
			} else {
				permissions.put("APPRV_URL", "");
			}
		} catch (EmptyResultDataAccessException e) {
			permissions = new HashMap<String, Object>();
			permissions.put("USE_MAIL",		"Y");
			permissions.put("USE_CAL",		"Y");
			permissions.put("USE_ADDR",		"Y");
			permissions.put("USE_BOARD",	"Y");
			permissions.put("USE_APPRV",	"Y");
			permissions.put("APPRV_URL",	"");
			permissions.put("USE_OFLN",		"Z");
			
			// TODO : 원클릭 작업 될때까지 그냥 통과
			//throw new TmgException(TmgExceptionType.NOT_LINKED_ACCOUNT_DEVICE);
		}
		
		return permissions;
	}

	/**
	 * 배지 카운트 조회<br/>
	 * <p>
	 * 파라미터 순서
	 * <ul>
	 * <li>게시판아이디 - java.lang.String</li>
	 * <li>조회일자 - java.util.Date</li>
	 * </ul>
	 * </p>
	 */
	@Override
	public Map<String, Object> getBadgeCount(String groupCode, String accountId, Object... objects) {
		String boardId	= (String) objects[0];
		Date reqDate	= (Date) objects[1];
		Map<String, Object> badges = new HashMap<String, Object>();
		
		// TDODO : 메일카운트 추가 필요 
		badges.put("Mail_Count",		"0");
		badges.put("Bbs_Today_Count",	String.format("%d", commandDao.getBoardItemCount(groupCode, boardId, reqDate)));
		badges.put("Schedule_Count",	String.format("%d", commandDao.getScheduleCount(groupCode, accountId, reqDate)));
		// 20140919 : 정도경영 카운트 추가
		badges.put("Ams_Count",	String.format("%d", commandDao.getCount("AMS")));
		
		return badges;
	}

	/**
	 * 서비스데스크 조회
	 */
	@Override
	public Map<String, Object> getServiceDesk(String groupCode) {
		return commandDao.getServiceDesk(groupCode);
	}

	/**
	 * 메일 서명 조회
	 */
	@Override
	public String getSignature(String groupCode, String accountId, String wifi) {
		String result = "";
		
		try {
			result = commandDao.getUserConfiguration(groupCode, accountId, wifi).get("SIGNATURE").toString();
		} catch (EmptyResultDataAccessException e) {
		} catch (NullPointerException e) {
		}
		
		return result;
	}

	/**
	 * 메일 서명 저장
	 */
	@Override
	public void setSignature(String groupCode, String accountId, String wifi, String signature) {
		try {
			commandDao.getUserConfiguration(groupCode, accountId, wifi);
			commandDao.updateUserConfiguration(groupCode, accountId, wifi, signature);
		} catch (EmptyResultDataAccessException e) {
			commandDao.insertUserConfiguration(groupCode, accountId, wifi, signature);
		}
	}
	
	/**
	 * 주소록 목록 조회
	 */
	@Override
	public GeneralList getAddressList(String accountId, String locale, String groupCode, String addressGroupId, int requestPage, int countPerPage) {
		List<Map<String, Object>> daoResults;
		List<Map<String, Object>> respResults = new ArrayList<Map<String, Object>>();
		Map<String, Object> daoRow, respRow;
		GeneralList addressList = new GeneralList();
		String name, position, department, company;
		
		daoResults = commandDao.getAddressList(accountId, locale, groupCode, addressGroupId);
		
		for (int i = (requestPage - 1) * countPerPage; i < requestPage * countPerPage; i++) {
			try {
				daoRow = daoResults.get(i);
				respRow = new HashMap<String, Object>();
				
				name		= daoRow.get("NAME")			!= null ? daoRow.get("NAME").toString()			: "";
				position	= daoRow.get("POS_NAME")		!= null ? daoRow.get("POS_NAME").toString()		: "";
				department	= daoRow.get("DEPT_NAME")		!= null ? daoRow.get("DEPT_NAME").toString()	: "";
				company		= daoRow.get("COMPANY_NAME") 	!= null ? daoRow.get("COMPANY_NAME").toString()	: "";
				
				respRow.put("Address_ID",		daoRow.get("ADDRESS_ID"));
				respRow.put("Address_Type",		daoRow.get("ADDRESSTYPE"));
				respRow.put("Name",				String.format("%s|%s|%s|%s", name, position, department, company));
				respRow.put("Member_Code",		daoRow.get("MEMBER_CODE"));
				respRow.put("Email_Address",	daoRow.get("EMAIL"));
				respRow.put("Mobilephone",		daoRow.get("MOBILE"));
				// 2013-12-30 민동원 : 리스트에서도 폰 번호 변경 처리
				respRow.put("Telephone",		phoneMapper.getMapedNumber(daoRow.get("TELEPHONE").toString()));
				//respRow.put("Telephone",		daoRow.get("TELEPHONE"));
				respRow.put("Homephone",		"");
				respRow.put("Is_Tbt_Installed",	daoRow.get("IS_TBT_INSTALLED"));
				respRow.put("Is_Favorite", daoRow.get("IS_FAVORITE"));
//				respRow.put("Is_Executive", daoRow.get("IS_EXECLUSIVE")); // QQQ 20140725 조사 : 주소록 임원/팀장여부 표시위해.
				
				respResults.add(respRow);
			} catch (IndexOutOfBoundsException e) {
				break;
			}
		}
		
		addressList.setTotalCount(daoResults.size());
		addressList.setTotalPage(addressList.getTotalCount() > 0 ? (daoResults.size() - 1) / countPerPage + 1 : 0);
		addressList.setList(respResults);
		
		return addressList;
	}

	/**
	 * 주소록 검색 목록 조회
	 */
	@Override
	public GeneralList getAddressSearchList(String accountId, String locale, String groupCode, String keywordType, String keyword, int requestPage, int countPerPage) {
		List<Map<String, Object>> daoResults = commandDao.getAddressSearchList(accountId, locale, groupCode, keyword);
		List<Map<String, Object>> respResults = new ArrayList<Map<String, Object>>();
		Map<String, Object> daoRow, respRow;
		GeneralList addressList = new GeneralList();
		String name, position, department, company;
		
		for (int i = (requestPage - 1) * countPerPage; i < requestPage * countPerPage; i++) {
			try {
				daoRow = daoResults.get(i);
				respRow = new HashMap<String, Object>();
				
				name		= daoRow.get("NAME")			!= null ? daoRow.get("NAME").toString()			: "";
				position	= daoRow.get("POS_NAME")		!= null ? daoRow.get("POS_NAME").toString()		: "";
				department	= daoRow.get("DEPT_NAME")		!= null ? daoRow.get("DEPT_NAME").toString()	: "";
				company		= daoRow.get("COMPANY_NAME") 	!= null ? daoRow.get("COMPANY_NAME").toString()	: "";

				respRow.put("Address_ID",		daoRow.get("ADDRESS_ID"));
				respRow.put("Address_Type",		"3");
				respRow.put("Name",				String.format("%s|%s|%s|%s", name, position, department, company));
				respRow.put("Member_Code",		daoRow.get("MEMBER_CODE"));
				respRow.put("Email_Address",	daoRow.get("EMAIL"));
				respRow.put("Mobilephone",		daoRow.get("MOBILE"));
				// 2013-12-30 민동원 : 리스트에서도 폰 번호 변경 처리
				respRow.put("Telephone",		phoneMapper.getMapedNumber(daoRow.get("TELEPHONE").toString()));
				//respRow.put("Telephone",		daoRow.get("TELEPHONE"));
				respRow.put("Homephone",		"");
				respRow.put("Is_Tbt_Installed",	daoRow.get("IS_TBT_INSTALLED"));
				respRow.put("Is_Favorite", daoRow.get("IS_FAVORITE"));
				
				respResults.add(respRow);
			} catch (IndexOutOfBoundsException e) {
				break;
			}
		}
		
		addressList.setTotalCount(daoResults.size());
		addressList.setTotalPage(addressList.getTotalCount() > 0 ? (daoResults.size() - 1) / countPerPage + 1 : 0);
		addressList.setList(respResults);
		
		return addressList;
	}

	/**
	 * 주소록 상세 정보 조회
	 * @throws TmgException 
	 */
	@Override
	public Map<String, Object> getAddress(String accountId, String locale, String groupCode, String addressId) throws TmgException {
		Map<String, Object> result = commandDao.getAddress(accountId, locale, groupCode, addressId);
		Map<String, Object> respResult = new HashMap<String, Object>();
		
		if (result == null) throw new TmgException((short) TmgExceptionType.NOT_EXISTS_ADDRESS);
		
		respResult.put("Name",			String.format("%s|%s|%s|%s|%s", result.get("NAME")			!= null ? result.get("NAME") : "", 
																	 	result.get("POS_NAME")		!= null ? result.get("POS_NAME") : "",
																	 	result.get("DEPT_NAME")	!= null ? result.get("DEPT_NAME") : "",
																	 	result.get("COMPANY_NAME")	!= null ? result.get("COMPANY_NAME") : "",
																	 	result.get("USER_ID")	!= null ? result.get("USER_ID") : ""));
		respResult.put("Email",				result.get("EMAIL"));
		respResult.put("MobilePhone",		result.get("MOBILE"));
		respResult.put("TelePhone",			phoneMapper.getMapedNumber(result.get("TELEPHONE").toString()));
		respResult.put("HomePhone",			"");
		respResult.put("Picture",			result.get("BLOG_PHOTO"));
		respResult.put("EmployNo",			result.get("EMPLOY_NO"));
		respResult.put("Is_Tbt_Installed",	result.get("IS_TBT_INSTALLED"));		
		respResult.put("Is_Favorite", 		result.get("IS_FAVORITE"));
		
		return respResult;
	}

	/**
	 * 일정 월별 조회
	 */
	@Override
	public List<Map<String, Object>> getSimpleMonthlySchedules(String groupCode, String accountId, int targetId, String yyyyMM) throws TmgException {
		String startDate, endDate;
		String dateFormat = configuration.getString("configuration.default.dateFormat");
		Map<String, Integer> countedSchedules = new HashMap<String, Integer>();
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

		try {
			startDate = String.format("%s01", yyyyMM);
			endDate = CalendarHelper.getLastDateOfMonth(startDate, "yyyyMMdd");
			accountId = getTargetAccountId(groupCode, accountId, targetId);
			
			List<Map<String, Object>> sourceSchedules = this.commandDao.getSimpleScheduleList(accountId, startDate, endDate);
			
			// 날짜별 일정 일수 계산
			int scheduleCount = 0;
			for (Map<String, Object> ss : sourceSchedules) {
				String sd	= (String) ss.get("ITEM_START_DATE");
				String ed	= (String) ss.get("ITEM_END_DATE");
				int diff	= (int) CalendarHelper.getDifferenceOfDate(sd, ed, dateFormat);
				Calendar c 	= CalendarHelper.getCalendar(sd, dateFormat);
				
				for (int i = 0; i <= diff; i++) {
					String keyDate = CalendarHelper.getFormatedDateString(c.getTime(), dateFormat);
					if (countedSchedules.containsKey(keyDate)) {
						countedSchedules.put(keyDate, countedSchedules.get(keyDate) + 1);
					} else {
						countedSchedules.put(keyDate, 1);
					}
					scheduleCount += 1;
					
					c.add(Calendar.DATE, 1);
				}
			}
			
			// 총 일정 건수 처리
			Map<String, Object> value = new HashMap<String, Object>();
			value.put("SCHEDULE_COUNT", scheduleCount);
			results.add(value);
			
			// 일별 리스트 처리
			for (Entry<String, Integer> s : countedSchedules.entrySet()) {
				value = new HashMap<String, Object>();
				value.put("Day", s.getKey());
				value.put("Day_Doc_Count", s.getValue());
				results.add(value);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.PARSING_FAILED);
		}
			
		return results;
	}

	/**
	 * 일정 주별 주회 
	 */
	@Override
	public List<Map<String, Object>> getSimpleWeeklySchedule(String groupCode, String accountId, int targetId, String yyyyMMdd) throws TmgException {
		String startDate, endDate;
		String dateFormat = configuration.getString("configuration.default.dateFormat");
		
		try {
			startDate = yyyyMMdd;
			endDate = CalendarHelper.addDate(startDate, dateFormat, Calendar.DATE, 6);
			accountId = getTargetAccountId(groupCode, accountId, targetId);
			
			List<Map<String, Object>> sourceSchedules = this.commandDao.getSimpleScheduleList(accountId, startDate, endDate);
			List<String> schedules = new ArrayList<String>();
			
			for (Map<String, Object> hm : sourceSchedules) {
				schedules.add(String.format("%s%s", hm.get("ITEM_START_DATE"), hm.get("ITEM_START_TIME")));
				schedules.add(String.format("%s%s", hm.get("ITEM_END_DATE"), hm.get("ITEM_END_TIME")));
			}
		} catch (ParseException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.PARSING_FAILED);
		} catch (EmptyResultDataAccessException e) {
			throw new TmgException(TmgExceptionType.NOT_EXISTS_ADDRESS);
		}
		
		return null;
	}

	/**
	 * 일정 목록 조회
	 */
	@Override
	public List<Map<String, Object>> getScheduleList(String locale, String groupCode, String accountId, int targetId, String startDate, String endDate) throws TmgException {
		List<Map<String, Object>> schedules = new ArrayList<Map<String, Object>>();

		try {
			accountId = getTargetAccountId(groupCode, accountId, targetId);
			List<Map<String, Object>> sourceSchedules = this.commandDao.getScheduleList(accountId, startDate, endDate);
			
			Map<String, Object> s, s1;
			for (Map<String, Object> ss : sourceSchedules) {
				s = new HashMap<String, Object>();
				
				s.put("Doc_ID",			ss.get("ITEM_ID"));
				s.put("Doc_Start",		String.format("%s%s", ss.get("ITEM_START_DATE"), ss.get("ITEM_START_TIME")));
				s.put("Doc_End",		String.format("%s%s", ss.get("ITEM_END_DATE"), ss.get("ITEM_END_TIME")));
				s.put("All_Day_Event",	ss.get("IS_WHOLE_DAY"));
				s.put("Doc_Subject",	LgdCommon.checkPrivateSchedule(configuration, locale, targetId, ss.get("ITEM_CLOSE"), ss.get("ITEM_TITLE")));
				s.put("Doc_Room",		LgdCommon.checkPrivateSchedule(configuration, locale, targetId, ss.get("ITEM_CLOSE"), ss.get("ITEM_PLACE")));
				
				// 타인 일정은 삭제 권한 없음
				if (targetId == 0)
					s.put("Permission", ss.get("OWN_USER_ID").equals(accountId) ? "1" : "0");
				else
					s.put("Permission", "0");
				
				schedules.add(s);
				
				// 여러날 일정 처리
				if (!ss.get("ITEM_START_DATE").toString().equals(ss.get("ITEM_END_DATE").toString())) {
					long endTime0	= Long.parseLong(s.get("Doc_End").toString());
					long dateDiff	= CalendarHelper.getDifferenceOfDate(ss.get("ITEM_START_DATE").toString(), ss.get("ITEM_END_DATE").toString(), "yyyyMMdd");
					Date date		= CalendarHelper.getCalendar(ss.get("ITEM_START_DATE").toString(), "yyyyMMdd").getTime();
					long endTime, startTime;
					
					s.put("Doc_Cur_Start",	s.get("Doc_Start"));
					s.put("Doc_Cur_End",	String.format("%s2359", ss.get("ITEM_START_DATE")));

					for (int i = 1; i <= dateDiff; i++) {
						startTime = Long.parseLong(CalendarHelper.getFormatedDateString(CalendarHelper.addDate(date, Calendar.DATE, i), "yyyyMMdd") + "0000");
						endTime = Long.parseLong(CalendarHelper.getFormatedDateString(CalendarHelper.addDate(date, Calendar.DATE, i), "yyyyMMdd") + "2359");
						
						s1 = new HashMap<String, Object>();
						s1.putAll(s);
						
						s1.put("Doc_Cur_Start", String.valueOf(startTime));
						s1.put("Doc_Cur_End",	String.valueOf(endTime0 > endTime ? endTime : endTime0));
						
						schedules.add(s1);
					}
				} else {
					s.put("Doc_Cur_Start",	s.get("Doc_Start"));
					s.put("Doc_Cur_End",	s.get("Doc_End"));
				}
			}
			
			// 범위 내의 일정이 아니면 제거
			ArrayList<Map<String, Object>>  rangeOutSchedules = new ArrayList<Map<String, Object>>(); 
			for (Map<String, Object> schedule : schedules) {
				if (Long.parseLong(schedule.get("Doc_Cur_Start").toString()) / 10000 < Long.parseLong(startDate)) rangeOutSchedules.add(schedule);
				if (Long.parseLong(schedule.get("Doc_Cur_End").toString()) / 10000 > Long.parseLong(endDate)) rangeOutSchedules.add(schedule);
			}
			schedules.removeAll(rangeOutSchedules);
		} catch (ParseException e) {
			//e.printStackTrace();
			throw new TmgException(TmgExceptionType.PARSING_FAILED);
		}
		
		return schedules;
	}
	
	/**
	 * 오프라인 일정 목록 조회
	 */
	@Override
	public ParticularArrayList<String, Object, String> getOffLineScheduleList(String locale, String groupCode, String accountId, int targetId, String startDate, String endDate, String syncLastTime) throws TmgException {
		ParticularArrayList<String, Object, String> schedules = new ParticularArrayList<String, Object, String>();
		String lastSyncTime = "";
		
		try {
			accountId = getTargetAccountId(groupCode, accountId, targetId);
			List<Map<String, Object>> sourceSchedules = this.commandDao.getOffLineScheduleList(accountId, startDate, endDate, syncLastTime);
			
			Map<String, Object> s, s1;
			for (Map<String, Object> ss : sourceSchedules) {
				s = new HashMap<String, Object>();
				
				if (ss.get("Status").equals("D")) {
					s.put("Doc_ID",			ss.get("ITEM_ID"));
					s.put("Doc_Start",		" ");
					s.put("Doc_End",		" ");
					s.put("All_Day_Event",	"0");
					s.put("Doc_Subject",	" ");
					s.put("Doc_Room",		" ");
					s.put("Doc_Status",		ss.get("Status"));
					s.put("Doc_Cur_Start",	" ");
					s.put("Doc_Cur_End",	" ");

					schedules.add(s);				
					lastSyncTime = ss.get("APPLICATION_TRANSFER_DATE").toString();
				} else {
					s.put("Doc_ID",			ss.get("ITEM_ID"));
					s.put("Doc_Start",		String.format("%s%s", ss.get("ITEM_START_DATE"), ss.get("ITEM_START_TIME")));
					s.put("Doc_End",		String.format("%s%s", ss.get("ITEM_END_DATE"), ss.get("ITEM_END_TIME")));
					s.put("All_Day_Event",	ss.get("IS_WHOLE_DAY"));
					s.put("Doc_Subject",	LgdCommon.checkPrivateSchedule(configuration, locale, targetId, ss.get("ITEM_CLOSE"), ss.get("ITEM_TITLE")));
					s.put("Doc_Room",		LgdCommon.checkPrivateSchedule(configuration, locale, targetId, ss.get("ITEM_CLOSE"), ss.get("ITEM_PLACE")));
					s.put("Doc_Status",		ss.get("Status"));
					
					schedules.add(s);				
					lastSyncTime = ss.get("APPLICATION_TRANSFER_DATE").toString();
					
					// 여러날 일정 처리
					if (!ss.get("ITEM_START_DATE").toString().equals(ss.get("ITEM_END_DATE").toString())) {
						long endTime0	= Long.parseLong(s.get("Doc_End").toString());
						long dateDiff	= CalendarHelper.getDifferenceOfDate(ss.get("ITEM_START_DATE").toString(), ss.get("ITEM_END_DATE").toString(), "yyyyMMdd");
						Date date		= CalendarHelper.getCalendar(ss.get("ITEM_START_DATE").toString(), "yyyyMMdd").getTime();
						long endTime, startTime;
						
						s.put("Doc_Cur_Start",	s.get("Doc_Start"));
						s.put("Doc_Cur_End",	String.format("%s2359", ss.get("ITEM_START_DATE")));

						for (int i = 1; i <= dateDiff; i++) {
							startTime = Long.parseLong(CalendarHelper.getFormatedDateString(CalendarHelper.addDate(date, Calendar.DATE, i), "yyyyMMdd") + "0000");
							endTime = Long.parseLong(CalendarHelper.getFormatedDateString(CalendarHelper.addDate(date, Calendar.DATE, i), "yyyyMMdd") + "2359");
							
							s1 = new HashMap<String, Object>();
							s1.putAll(s);
							
							s1.put("Doc_Cur_Start", String.valueOf(startTime));
							s1.put("Doc_Cur_End",	String.valueOf(endTime0 > endTime ? endTime : endTime0));
							
							schedules.add(s1);
						}
					} else {
						s.put("Doc_Cur_Start",	s.get("Doc_Start"));
						s.put("Doc_Cur_End",	s.get("Doc_End"));
					}
					
				}

				if (!ss.get("Status").equals("D")) {
					// 범위 내의 일정이 아니면 제거
					ArrayList<Map<String, Object>> rangeOutSchedules = new ArrayList<Map<String, Object>>(); 
					for (Map<String, Object> schedule : schedules) {
						try {
							if (Long.parseLong(schedule.get("Doc_Cur_Start").toString()) / 10000 < Long.parseLong(startDate)) rangeOutSchedules.add(schedule);
							if (Long.parseLong(schedule.get("Doc_Cur_End").toString()) / 10000 > Long.parseLong(endDate)) rangeOutSchedules.add(schedule);
						} catch (NumberFormatException ex) {
						}
					}
					
					schedules.removeAll(rangeOutSchedules);
				}
			}
			
			schedules.setParticularData(lastSyncTime);
		} catch (ParseException e) {
			//e.printStackTrace();
			throw new TmgException(TmgExceptionType.PARSING_FAILED);
		}
		
		return schedules;
	}
	
	public String serverTime() {
		String result = null;
		result = this.commandDao.serverTime();
		return result;
	}
	
	public String serverTime(String faketime, int interval) {
		String result = null;
		result = this.commandDao.serverTime(faketime, interval);
		
		return result;
	}

	/**
	 * 일정 목록 조회
	 */
	@Override
	public List<Map<String, Object>> getScheduleList(String locale, String groupCode, String accountId, int targetId, String startDate) throws TmgException {
		List<Map<String, Object>> schedules = new ArrayList<Map<String, Object>>();

		try {
			accountId = getTargetAccountId(groupCode, accountId, targetId);
			List<Map<String, Object>> sourceSchedules = this.commandDao.getScheduleList(accountId, startDate, startDate);
			
			Map<String, Object> s;
			for (Map<String, Object> ss : sourceSchedules) {
				s = new HashMap<String, Object>();
				
				s.put("Doc_ID",			ss.get("ITEM_ID"));
				s.put("Doc_Subject",	LgdCommon.checkPrivateSchedule(configuration, locale, targetId, ss.get("ITEM_CLOSE"), ss.get("ITEM_TITLE")));
				s.put("Doc_Room",		LgdCommon.checkPrivateSchedule(configuration, locale, targetId, ss.get("ITEM_CLOSE"), ss.get("ITEM_PLACE")));
				s.put("Doc_Start",		String.format("%s%s", ss.get("ITEM_START_DATE"), ss.get("ITEM_START_TIME")));
				s.put("Doc_End",		String.format("%s%s", ss.get("ITEM_END_DATE"), ss.get("ITEM_END_TIME")));
				s.put("All_Day_Event",	ss.get("IS_WHOLE_DAY"));
				
				// 타인 일정은 삭제 권한 없음
				if (targetId == 0)
					s.put("Permission", ss.get("OWN_USER_ID").equals(accountId) ? "1" : "0");
				else
					s.put("Permission", "0");
				
				schedules.add(s);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
		
		return schedules;
	}

	/**
	 * 일정 목록 조회
	 */
	@Override
	public GeneralList getScheduleList(String locale, String groupCode, String accountId, int targetId, String startDate, int direction, int requestPage, int countPerPage) throws TmgException {
		GeneralList scheduleList = new GeneralList();
		
		try {
			accountId = getTargetAccountId(groupCode, accountId, targetId);
			
			List<Map<String, Object>> sourceSchedules = commandDao.getScheduleList(accountId, startDate, direction);
			
			int prevTotalCount = commandDao.getScheduleCount(accountId, startDate, 0);
			int nextTotalCount = commandDao.getScheduleCount(accountId, startDate, 1);
			List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
			
			Map<String, Object> otherData = new HashMap<String, Object>();
			otherData.put("Prev_Total_Count", prevTotalCount);
			otherData.put("Prev_Total_Page", prevTotalCount > 0 ? (prevTotalCount - 1) / countPerPage + 1 : 0);
			otherData.put("Next_Total_Count", nextTotalCount);
			otherData.put("Next_Total_Page", nextTotalCount > 0 ? (nextTotalCount - 1) / countPerPage + 1 : 0);
			
			Map<String, Object> s, r;
			for (int i = (requestPage - 1) * countPerPage; i < requestPage * countPerPage; i++) {
				try {
					s = sourceSchedules.get(i);
					r = new HashMap<String, Object>();
					
					r.put("Doc_ID", 		s.get("ITEM_ID"));
					r.put("Doc_Subject",	LgdCommon.checkPrivateSchedule(configuration, locale, targetId, s.get("ITEM_CLOSE"), s.get("ITEM_TITLE")));
					r.put("Doc_Room",		LgdCommon.checkPrivateSchedule(configuration, locale, targetId, s.get("ITEM_CLOSE"), s.get("ITEM_PLACE")));
					r.put("Doc_Start", 		String.format("%s%s", s.get("ITEM_START_DATE"), s.get("ITEM_START_TIME")));
					r.put("Doc_End", 		String.format("%s%s", s.get("ITEM_END_DATE"), s.get("ITEM_END_TIME")));
					r.put("All_Day_Event",	s.get("IS_WHOLE_DAY"));
					
					// 타인 일정은 삭제 권한 없음
					try {
						if (targetId != 0)
							r.put("Permission", s.get("OWN_USER_ID").equals(accountId) ? "1" : "0");
						else
							r.put("Permission", "0");
					} catch (Exception e) {
						r.put("Permission", "0");
					}
					
					results.add(r);
				} catch (IndexOutOfBoundsException e) {
					break;
				}
			}
			
			scheduleList.setTotalCount(sourceSchedules.size());
			scheduleList.setTotalPage(sourceSchedules.size() > 0 ? (sourceSchedules.size() - 1) / countPerPage + 1 : 0);
			scheduleList.setList(results);
			scheduleList.setOtherData(otherData);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
		
		return scheduleList;
	}

	/**
	 * 일정 상세 조회
	 * @throws TmgException 
	 */
	@Override
	public Map<String, Object> getSchedule(String locale, String groupCode, String accountId, int targetId, String scheduleId) throws TmgException {
		Map<String, Object> schedule,  result = new HashMap<String, Object>();
		List<Map<String, Object>> sharedUsers;
		String body;
		
		try {
			accountId = getTargetAccountId(groupCode, accountId, targetId);

			schedule	= commandDao.getSchedule(accountId, scheduleId);
			sharedUsers = commandDao.getScheduleSharedUsers(scheduleId);
			
			if (schedule == null || schedule.size() == 0) throw new TmgException(TmgExceptionType.NOT_EXISTS_DATA);
			
			body = schedule == null ? "" : LgdCommon.checkPrivateSchedule(configuration, locale, targetId, schedule.get("ITEM_CLOSE"), schedule.get("ITEM_CONTENT"));
			
			// 2013-12-18 민동원 : IOS 내용없음 일괄되게 처리
			if (body != null && body.equals("<P style=\"LINE-HEIGHT: 120%; MARGIN-TOP: 0px; MARGIN-BOTTOM: 0px\">&nbsp;</P>")) {
				body = "";
			} else {
				body = LgdCommon.htmlToPlainText(body);				
			}
			
			// IOS7이 맨 끝에 오는 개행문자를 처리 못해서 \n\n 삭제 
			if (body.endsWith("\n\n")) {
				body = body.substring(0, body.length() - 2);
			}
			
			result.put("Start_Day",		String.format("%s%s", schedule.get("ITEM_START_DATE"), schedule.get("ITEM_START_TIME")));
			result.put("End_Day", 		String.format("%s%s", schedule.get("ITEM_END_DATE"), schedule.get("ITEM_END_TIME")));
			result.put("Doc_Title",		LgdCommon.checkPrivateSchedule(configuration, locale, targetId, schedule.get("ITEM_CLOSE"), schedule.get("ITEM_TITLE")));
			result.put("Doc_Room",		LgdCommon.checkPrivateSchedule(configuration, locale, targetId, schedule.get("ITEM_CLOSE"), schedule.get("ITEM_PLACE")));
			result.put("Doc_Body",		body);
			result.put("Permission",	schedule.get("OWN_USER_ID").equals(accountId) ? "1" : "0");
			result.put("All_Day_Event",	schedule.get("IS_WHOLE_DAY"));
						
			// 공개설정 : "0"은 공개, "2"는 비공개, 그 외에는 비공개
			if (schedule.get("ITEM_CLOSE").toString().equals("0"))
				result.put("IsShare", "1");
			else if (schedule.get("ITEM_CLOSE").toString().equals("2"))
				result.put("IsShare", "0");
			
			// 타인 일정은 삭제 권한 없음
			if (targetId == 0)
				result.put("Permission", schedule.get("OWN_USER_ID").equals(accountId) ? "1" : "0");
			else {
				result.put("Permission", "0");
			}

			// 타인일정이고 비공개이면 참석자, 초대자 표시 안함
			if (targetId > 0 && schedule.get("ITEM_CLOSE").toString().equals("2")) {
				result.put("Attendees", 	"");
				result.put("Invite_From", 	"");
			} else {
				StringBuffer sb = new StringBuffer();
				
				for (Map<String, Object> s : sharedUsers) {
					sb.append(String.format("%s|%s|%s|%s|%s|%s::", s.get("SHARE_USER_ID"), s.get("USER_NAME"), s.get("DEPT_NAME"), s.get("EMAIL"), s.get("POSITION"), s.get("USER_ID")));
				}

				result.put("Attendees",		sb.length() == 0 ? "" : sb.toString().substring(0, sb.length() - 2));
				result.put("Invite_From", 	String.format("%s|%s|%s|%s|%s|%s", schedule.get("OWN_USER_ID"), schedule.get("USER_NAME"), schedule.get("DEPT_NAME"), schedule.get("EMAIL"), schedule.get("POSITION"), schedule.get("USER_ID")));
			}
		} catch (TmgException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
		
		return result;
	}

	/**
	 * 일정 등록 
	 */
	@Override
	public String addSchedule(String accountId, String scheduleId, String[] attendees, String isShare, String isAllDayEvent, String startDateTime,
								String endDateTime, String title, String place, String body) throws TmgException {
		try {
			String startDate	= startDateTime.substring(0, 8);
			String startTime	= LgdCommon.roundupMinute(isAllDayEvent.equals("0") ? startDateTime.substring(8) : "0000");
			String endDate		= endDateTime.substring(0, 8);
			String endTime		= LgdCommon.roundupMinute(isAllDayEvent.equals("0") ? endDateTime.substring(8) : "2359");
			String itemClose	= "0";
			
			if (isShare.equals("1")) itemClose = "0";
			if (isShare.equals("0")) itemClose = "2";
			
			body = LgdCommon.plainTextToHtml(body, configuration.getString("configuration.default.scheduleBody.front"), configuration.getString("configuration.default.scheduleBody.rear"));
			scheduleId = commandDao.insertSchedule(accountId, scheduleId, itemClose, isAllDayEvent, startDate, startTime, endDate, endTime, title, place, body);
			
			if (attendees != null) {
				String attendeesId;
				for (String a : attendees) {
					// TODO : 테스트를 위해 일단 방어 코드로 처리
					attendeesId = a.split("@")[0];
					commandDao.insertSharedSchedule(accountId, attendeesId, scheduleId);
				}
			}
			return scheduleId;
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}

	/**
	 * 일정 업데이트
	 */
	@Override
	public void updateSchedule(String accountId, String scheduleId, String[] attendees, String isShare, String isAllDayEvent, String startDateTime,
								String endDateTime, String title, String place, String body) throws TmgException {
		try {
			String startDate	= startDateTime.substring(0, 8);
			String startTime	= isAllDayEvent.equals("0") ? startDateTime.substring(8) : "0000";
			String endDate		= endDateTime.substring(0, 8);
			String endTime		= isAllDayEvent.equals("0") ? endDateTime.substring(8) : "2359";
			String itemClose = "0";
			
			if (isShare.equals("1")) itemClose = "0";
			if (isShare.equals("0")) itemClose = "2";
			
			body = LgdCommon.plainTextToHtml(body, configuration.getString("configuration.default.scheduleBody.front"), configuration.getString("configuration.default.scheduleBody.rear"));
			commandDao.updateSchedule(accountId, scheduleId, itemClose, isAllDayEvent, startDate, startTime, endDate, endTime, title, place, body);
			commandDao.deleteSchedule(scheduleId, null);
			
			if (attendees != null) {
				String attendeesId;
				for (String a : attendees) {
					if (a.isEmpty()) continue;
					attendeesId = a.split("@")[0];
					commandDao.insertSharedSchedule(accountId, attendeesId, scheduleId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}

	/**
	 * 일정 검색 목록 조회
	 */
	@Override
	public GeneralList getScheduleSearchList(String locale, String groupCode, String accountId, int targetId, String searchType, String searchTerm, String keyword, int requestPage, int countPerPage) throws TmgException {
		List<Map<String, Object>> schedules, results;
		String title = "", place = "", body = "";
		String startDate, endDate;
		int st;
		GeneralList scheduleList = new GeneralList();
		
		try {
			accountId = getTargetAccountId(groupCode, accountId, targetId);
			
			// 검색 방법 설정
			st = Integer.parseInt(searchType);
			switch (st) {
			case 1:		// 제목 검색
				title = keyword;
				break;
			case 2:		// 장소 검색
				place = keyword;
				break;
			case 3:		// 본문 검색
				body = keyword;
				break;
			default:
				body = "";
			}
			
			// 검색 기간
			Date today = CalendarHelper.getCurrentTime();
			st = Integer.parseInt(searchTerm);
			switch (st) {
			case 1:		// 1주
				startDate	= CalendarHelper.getFormatedDateString(CalendarHelper.addDate(today, Calendar.DATE, -7), "yyyyMMdd");
				endDate		= CalendarHelper.getFormatedDateString(CalendarHelper.addDate(today, Calendar.DATE, 7), "yyyyMMdd");
				break;
			case 2:		// 1개월
				startDate	= CalendarHelper.getFormatedDateString(CalendarHelper.addDate(today, Calendar.MONTH, -1), "yyyyMMdd");
				endDate		= CalendarHelper.getFormatedDateString(CalendarHelper.addDate(today, Calendar.MONTH, 1), "yyyyMMdd");
				break;
			case 3:		// 2개월
				startDate	= CalendarHelper.getFormatedDateString(CalendarHelper.addDate(today, Calendar.MONTH, -2), "yyyyMMdd");
				endDate		= CalendarHelper.getFormatedDateString(CalendarHelper.addDate(today, Calendar.MONTH, 2), "yyyyMMdd");
				break;
			case 4:		// 3개월
				startDate	= CalendarHelper.getFormatedDateString(CalendarHelper.addDate(today, Calendar.MONTH, -3), "yyyyMMdd");
				endDate		= CalendarHelper.getFormatedDateString(CalendarHelper.addDate(today, Calendar.MONTH, 3), "yyyyMMdd");
				break;
			case 5:		// 4개월
				startDate	= CalendarHelper.getFormatedDateString(CalendarHelper.addDate(today, Calendar.MONTH, -6), "yyyyMMdd");
				endDate		= CalendarHelper.getFormatedDateString(CalendarHelper.addDate(today, Calendar.MONTH, 6), "yyyyMMdd");
				break;
			default:	// 전체
				startDate	= "";
				endDate		= "";
				break;
			}
			
			schedules = commandDao.getScheduleList(accountId, startDate, endDate, title, place, body);
			results = new ArrayList<Map<String, Object>>();
			
			Map<String, Object> s, r;
			for (int i = (requestPage - 1) * countPerPage; i < requestPage * countPerPage; i++) {
				try {
					s = schedules.get(i);
					r = new HashMap<String, Object>();
					
					r.put("Doc_ID", 		s.get("ITEM_ID"));
					r.put("Doc_Subject",	LgdCommon.checkPrivateSchedule(configuration, locale, targetId, s.get("ITEM_CLOSE"), s.get("ITEM_TITLE")));
					r.put("Doc_Room",		LgdCommon.checkPrivateSchedule(configuration, locale, targetId, s.get("ITEM_CLOSE"), s.get("ITEM_PLACE")));
					r.put("Doc_Start", 		String.format("%s%s", s.get("ITEM_START_DATE"), s.get("ITEM_START_TIME")));
					r.put("Doc_End", 		String.format("%s%s", s.get("ITEM_END_DATE"), s.get("ITEM_END_TIME")));
					r.put("All_Day_Event",	s.get("IS_WHOLE_DAY"));
					
					results.add(r);
				} catch (IndexOutOfBoundsException e) {
					break;
				}
			}
			
			scheduleList.setTotalCount(schedules.size());
			scheduleList.setTotalPage(scheduleList.getTotalCount() > 0 ? (scheduleList.getTotalCount() - 1) / countPerPage + 1 : 0);
			scheduleList.setList(results);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.PARSING_FAILED);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}

		return scheduleList;
	}
	
	/**
	 * 일정 삭제
	 */
	@Override
	public void deleteSchedule(String scheduleId) throws TmgException {
		try {
			commandDao.deleteSchedule(scheduleId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}

	/**
	 * 일정 삭제
	 */
	@Override
	public void deleteSchedule(String[] scheduleIDs) throws TmgException {
		try {
			commandDao.deleteSchedule(scheduleIDs);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		}
	}

	/**
	 * 푸시 설정 조회
	 */
	@Override
	public Map<String, Object> getPushConfiguration(String groupCode, String accountId, String wifi) {
		Map<String, Object> daoResult, result = new HashMap<String, Object>();
		
		daoResult = commandDao.getUserConfiguration(groupCode, accountId, wifi);
		
		if (daoResult.get("USE_PUSH") == null) throw new EmptyResultDataAccessException(1);
		
		result.put("SetPush", 		daoResult.get("USE_PUSH"));
		result.put("SetSunday", 	daoResult.get("USE_PUSH_0"));
		result.put("SetMonday", 	daoResult.get("USE_PUSH_1"));
		result.put("SetTuesday", 	daoResult.get("USE_PUSH_2"));
		result.put("SetWednesday", 	daoResult.get("USE_PUSH_3"));
		result.put("SetThursday", 	daoResult.get("USE_PUSH_4"));
		result.put("SetFriday", 	daoResult.get("USE_PUSH_5"));
		result.put("SetSaturday", 	daoResult.get("USE_PUSH_6"));
		result.put("SetTime", 		String.format("%s-%s", daoResult.get("PUSH_START_TIME"), daoResult.get("PUSH_END_TIME")));
		
		return result;
	}

	/**
	 * 푸시 설정 저장
	 */
	@Override
	public void setPushConfiguration(String groupCode, String accountId, String wifi, boolean usePush, String pushTime,
										String tokenType, String deviceToken, String osType, String appId, boolean...weekDays) throws TmgException {
		String [] pushTimes = null;
		
		try {
			pushTimes = pushTime.split("-");
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.PARSING_FAILED);
		}
		
		try {
			commandDao.getUserConfiguration(groupCode, accountId, wifi);
			commandDao.updateUserConfiguration(groupCode, accountId, wifi, null, usePush, pushTimes[0], pushTimes[1], tokenType, deviceToken,
												weekDays[0], weekDays[1], weekDays[2], weekDays[3], weekDays[4], weekDays[5], weekDays[6]);
			
			if (osType.equals("I")) commandDao.updatePushKey(wifi, appId, deviceToken);
		} catch (EmptyResultDataAccessException e) {
			commandDao.insertUserConfiguration(groupCode, accountId, wifi, "", usePush, pushTimes[0], pushTimes[1], tokenType, deviceToken,
												weekDays[0], weekDays[1], weekDays[2], weekDays[3], weekDays[4], weekDays[5], weekDays[6]);
			if (osType.equals("I")) commandDao.insertPushKey(wifi, appId, deviceToken);
		}
	}
	
	/**
	 * 디바이스 토큰 등록
	 */
	@Override
	public void setDeviceToken(String groupCode, String accountId, String wifi, String tokenType, String osType, String deviceToken, String appId) {
		try {
			commandDao.getUserConfiguration(groupCode, accountId, wifi);
			commandDao.updateUserConfiguration(groupCode, accountId, wifi, null, null, null, null, tokenType, deviceToken);
		} catch (EmptyResultDataAccessException e) {
			commandDao.insertUserConfiguration(groupCode, accountId, wifi, "", null, null, null, tokenType, deviceToken);
		}

		// 민동원 푸시키 삽입 / 업데이트는 푸시 설정과 분리
		if (osType.equals("I")) {
			try {
				commandDao.getPushKey(wifi, appId);
				commandDao.updatePushKey(wifi, appId, deviceToken);
				
			} catch (EmptyResultDataAccessException e) {
				commandDao.insertPushKey(wifi, appId, deviceToken);
			}
		}
	}
	
	/**
	 * 사용자 정보 조회
	 */
	@Override
	public Map<String, Object> getUserInfo(String groupCode, String accountId) {
		return userDao.getUserInfo(groupCode, accountId);
	}

	/**
	 * 사용자 정보 조회
	 */
	@Override
	public Map<String, Object> getUserInfo(String groupCode, int addressId) {
		return userDao.getUserInfo(groupCode, addressId);
	}

	/**
	 * 접속 로그 등록
	 */
	@Override
	public String setConnectRequestLog(String accountId, String packageName, String wifi) {
		String ret = "";
		try {
			ret = commandDao.insertConnectRequestLog(accountId, packageName, wifi);
		} catch (Exception e) {
		}
		return ret;
	}
	
	/**
	 * 접속 로그 응답 업데이트
	 */
	@Override
	public void setConnectResponseLog(String connectId) {
		try {
			if (connectId != null && connectId.isEmpty() == false)
			commandDao.updateConnectResponseLog(connectId);   
		} catch (Exception e) {
		}
	}
	
	/**
	 * 타인일정 사용자 리스트 조회
	 */
	@Override
	public GeneralList getOtherUserList(String accountId, int requestPage, int countPerPage) throws Exception {
		GeneralList ret = new GeneralList();
		
		try {
			int totalCount = commandDao.getOtherUserTotalCount(accountId);
			int beginNum = (requestPage * countPerPage) - (countPerPage - 1);
			int endNum = beginNum + countPerPage - 1;
			
			List<Map<String, Object>> otherUserList = commandDao.getOtherUserList(accountId, beginNum, endNum);
			
			for (Map<String, Object> otherUser : otherUserList) {
				otherUser.remove("DISP_ORDER");
			}

			ret.setList(otherUserList);
			ret.setTotalCount(totalCount);
			ret.setTotalPage(totalCount > 0 ? (totalCount - 1) / countPerPage + 1 : 0);
			
		} catch (Exception e) {
			throw e;
		}
		
		return ret;
	}
	
	/**
	 * 타인일정 사용자 추가
	 */
	@Override
	public void setOtherUser(String accountId, String otherUserId, String processType) throws Exception {
		try {
			if (processType.equals("1")) {
				commandDao.insertOtherUser(accountId, otherUserId);   		
			} else if (processType.equals("3")) {
				commandDao.deleteOtherUser(accountId, otherUserId);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 타인일정 대상 조회
	 * @param groupCode 그룹 코드
	 * @param accountId 사용자 계정
	 * @param targetId 타인일정 조회 대상자 계정
	 * @return
	 */
	private String getTargetAccountId(String groupCode, String accountId, int targetId) {
		// 조회 대상 계정이 있으면 타인 일정을 조회
		if (targetId != 0) {
			Map<String, Object> u = userDao.getUserInfo(groupCode, targetId);
			accountId = u.get("ACCOUNT_ID").toString();
		}
		
		return accountId;
	}
	

}
