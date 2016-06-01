package com.tionsoft.tmg.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * CalendarHelper 클래스 
 * @author 솔루션개발1팀 이주용
 */
public class CalendarHelper {
	/**
	 * 포맷형식의 날짜에서 그 달의 마지막 날짜를 같은 형식의 포맷 문자열로 반환한다.
	 * @param date 날짜 문자열
	 * @param format 포맷 문자열
	 * @return 포맷문자열로 변환된 날짜
	 * @throws ParseException
	 */
	public static String getLastDateOfMonth(String date, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		
		c.setTime(sdf.parse(date));
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DATE, 1);
		c.add(Calendar.DATE, -1);

		return sdf.format(c.getTime());
	}
	
	/**
	 * 포맷형식의 날짜에 CalenarField 형식 만큼 날짜를 더하여 같은 형식의 포맷 문자열로 반환한다.
	 * @param date 날짜 문자열
	 * @param format 포맷 문자열
	 * @param calendarField 캘린더 필드
	 * @param amount 더해줄 날짜 값
	 * @return
	 * @throws ParseException
	 */
	public static String addDate(String date, String format, int calendarField, int amount) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		
		c.setTime(sdf.parse(date));
		c.add(calendarField, amount);
		
		return sdf.format(c.getTime());
	}
	
	/**
	 * Calendar를 이용한 날짜 연산 수행 
	 * @param date 대상 날짜 
	 * @param calendarField 연산 수행할 날짜 필드 
	 * @param amount 변경되는 값 
	 * @return
	 */
	public static Date addDate(Date date, int calendarField, int amount) {
		Calendar c = Calendar.getInstance();
		
		c.setTime(date);
		c.add(calendarField, amount);
		
		return c.getTime();
	}
	
	/**
	 * 포맷형식의 시작날짜와 종료날짜 사이의 간격을 반환한다.
	 * @param startDate 시작 날짜 문자열
	 * @param endDate 종료 날짜 문자열
	 * @param format 포맷 문자열
	 * @return 날짜 간격
	 * @throws ParseException
	 */
	public static long getDifferenceOfDate(String startDate, String endDate, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		
		Date sd = sdf.parse(startDate);
	    Date ed = sdf.parse(endDate);
	    
		return (ed.getTime() - sd.getTime()) / (24 * 60 * 60 * 1000) ; 
	}

	/**
	 * 포맷형식의 날짜 Calendar instance 반환한다.
	 * @param date 날짜
	 * @param format 포맷 문자열
	 * @return Calendar instance
	 * @throws ParseException
	 */
	public static Calendar getCalendar(String date, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		
		c.setTime(sdf.parse(date));
		
		return c;
	}
	
	/**
	 * 날짜를 포맷 형식의 문자열로 변환하여 반환한다.
	 * @param date 날짜
	 * @param format 포맷 문자열
	 * @return 
	 */
	public static String getFormatedDateString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		
		return sdf.format(date);
	}
	
	/**
	 * 현재 시간을 반환한다.
	 * @return
	 */
	public static Date getCurrentTime() {
		return Calendar.getInstance().getTime();
	}
	
	/**
	 * GMT기준으로 한국 시간을 변환한다.
	 * @return
	 */
	public static String gmtConvert(String gmt,String date)
	{		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
		
		Calendar cal = Calendar.getInstance();
		formatter.setTimeZone( TimeZone.getTimeZone(gmt) );
		try {
			cal.setTime(formatter.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return sdf.format( cal.getTime() );
	}
	
	/**
	 * 입력된 시간을 지정한 시간대로 변경한다.  
	 * @param dateTime 입력시간 
	 * @param sourceTimezone 현재 시간대 
	 * @param destTimezone 변경할 시간대 
	 * @return 변환된 시간
	 * @throws ParseException
	 */
	public static String convertTimezone(String dateTime, String sourceTimezone, String destTimezone) throws ParseException {
		SimpleDateFormat oldTimezoneFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat newTimezoneFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		dateTime = dateTime + sourceTimezone.substring(3);
		oldTimezoneFormat.setTimeZone(TimeZone.getTimeZone(sourceTimezone));
		newTimezoneFormat.setTimeZone(TimeZone.getTimeZone(destTimezone));
		
		Date newDateTime = oldTimezoneFormat.parse(dateTime);
		
		return newTimezoneFormat.format(newDateTime);
	}
	
}
