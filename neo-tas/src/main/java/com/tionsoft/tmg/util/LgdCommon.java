package com.tionsoft.tmg.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.XMLConfiguration;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.service.CommandService;

/**
 * LG Display 공통 유틸리티
 * @author 서버개발실 이주용
 */
public class LgdCommon {
	/**
	 * 일정 아이디 생성
	 * @param accountId 사용자 계정
	 * @return
	 */
	public static String genrateScheduleId(String accountId, String wifi) {
		String currentTime = CalendarHelper.getFormatedDateString(CalendarHelper.getCurrentTime(), "yyMMddHHmmss");
		return String.format("MO_%s%s", currentTime, wifi);
	}
	
	/**
	 * 비공개 일정 처리
	 * @param configuration
	 * @param locale
	 * @param tragetId
	 * @param closeType
	 * @param source
	 * @return
	 */
	public static String checkPrivateSchedule(XMLConfiguration configuration, String locale, int tragetId, Object closeType, Object source) {
		String result = "";
		
		if (tragetId > 0 && closeType.toString().equals("2")) {
			try {
				result = configuration.getString(String.format("configuration.schedule.private.%s", locale.toUpperCase()));
				if (result == null || "".equals(result)) {
					result = configuration.getString("configuration.schedule.private.default");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			result = source == null ? "" : source.toString();
		}
		
		return result;
	}
	
	/**
	 * 메일 본문 획득 및 내부 이미지 처리
	 * @param sourceUrl 메일 본문 원문 URL
	 * @param mailId 메일 아이디
	 * @param internalDomains 내부 도메인 - 내부 이미지 처리 대상
	 * @param downloadPath 내부 이미지 다운로드 경로
	 * @param baseUrl 내부 이미지 URL
	 * @return
	 * @throws IOException
	 */
	public static String getMailBody(String sourceUrl, String mailId, String[] internalDomains, String downloadPath, String baseUrl) throws TmgException {
		String html = "";
		
		try {
			Document doc	= Jsoup.connect(sourceUrl).get();
			html = parseHtmlDocument(mailId, internalDomains, downloadPath, baseUrl, doc);
		} catch (InterruptedException e) {
			throw new TmgException(TmgExceptionType.LEGACY_CONNECTION_FAILED);
		} catch (IOException e) {
			try {
				html = Jsoup.connect(sourceUrl).get().html();
			} catch (HttpStatusException e1) {
				throw new TmgException(TmgExceptionType.LEGACY_CONNECTION_FAILED);
			} catch (IOException e1) {
				throw new TmgException(TmgExceptionType.LEGACY_CONNECTION_FAILED);
			}
		}
		
		return html;
	}

	/**
	 * 게시물 본문의 내부 이미지 처리 및 불필요한 태그 제거
	 * @param html 게시판 본문
	 * @param articleId 게시물 아이디
	 * @param internalDomains 내부 도메인 - 내부 이미지 처리 대상
	 * @param downloadPath 내부 이미지 다운로드 경로
	 * @param baseUrl 내부 이미지 URL
	 * @return
	 */
	public static String getBoardContent(String html, String articleId, String[] internalDomains, String downloadPath, String baseUrl) {
		try {
			html = parseHtmlDocument(articleId, internalDomains, downloadPath, baseUrl, Jsoup.parse(html));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return html;
	}

	/**
	 * 게시물 본문의 내부 이미지 처리 및 불필요한 태그 제거
	 * @param contentId
	 * @param internalDomains
	 * @param downloadPath
	 * @param baseUrl
	 * @param doc
	 * @return
	 * @throws InterruptedException
	 */
	private static String parseHtmlDocument(String contentId, String[] internalDomains, String downloadPath, String baseUrl, Document doc) throws InterruptedException {
		String html;
		String file;
		Map<String, Elements> imageMap = new HashMap<String, Elements>();
		int imageCount = 0;
		
		imageMap.put("abs:src", 		 doc.select("img[src]"));
		imageMap.put("abs:background",	 doc.select("[background]"));
		
		for (Entry<String, Elements> attr : imageMap.entrySet()) {
			//imageCount += imageMap.get(attr).size();
			imageCount += attr.getValue().size();
		}
		
		String path		= String.format("%s/%s/", downloadPath, contentId);
		String [] urlFile;
		FileDownload fd;
			
		if (imageCount > 0) {
			Map<String, String> imageFiles = new HashMap<String, String>();
			fd = new FileDownload();
			
			for (Entry<String, Elements> attr : imageMap.entrySet()) {
				for (Element src : attr.getValue()) {
					URL u;
					try {
//						u = new URL(src.attr(attr));
						u = new URL(src.attr(attr.getKey()));
						urlFile = u.getFile().split("/");
						file = StringEncoder.digest(urlFile[urlFile.length - 1]) + ".image";
						
						// 내무 이미지 URL 변경
						for (String d : internalDomains) {
							if (u.getHost().endsWith(d)) {
								imageFiles.put(src.attr(attr.getKey()), String.format("%s%s", path, file));
								src.attr(attr.getKey().split(":")[1], String.format("%s/%s/%s", baseUrl, contentId, file));
							}
						}
					} catch (MalformedURLException e) {
						// 문제 있는 URL은 통과
					}
				}
			}
			
			if (imageFiles.size() > 0) {
				fd.setFiles(imageFiles);
				fd.execute();
			}
		}
		
		// 불필요한 태그 삭제
		doc.select("script").remove();
		doc.select("applet").remove();
		doc.select("input").remove();
		doc.select("embed").remove();
		
		html = doc.html();
		
		return html;
	}
	
	/**
	 * HTML을 Plain Text로 변경
	 * @param source
	 * @return
	 */
	public static String htmlToPlainText(String source) {
		Document doc = Jsoup.parse(source);
		HtmlToPlainText conveter = new HtmlToPlainText();
		String plainText = conveter.getPlainText(doc);

		return plainText;
	}
	
	/**
	 * 일정 시간을 10분 단뒤로 반올림
	 * @param time
	 * @return
	 */
	public static String roundupMinute(String time) {
		int time0 = Integer.parseInt(time);
				
		time0 = ((time0 + 5) / 10) * 10;
		if (time0 % 100 == 60) time0 = time0 + 40;
		if (time0 == 2400) time0 = 2359;
		time = String.format("0000%d", time0);
		
		return  time.substring(time.length() - 4, time.length());
	}
	
	public static void setDefaultPushConfig(CommandService commandService, XMLConfiguration configuration, String groupCode, String accountId, String wifi, String osType, String appId) throws TmgException {
		boolean weekDays[] = new boolean[7];
		String allowPushTime = configuration.getString("configuration.default.allowPushTime");
		
		weekDays[0] = configuration.getString("configuration.default.allowPushDate.weekday0").equals("yes");
		weekDays[1] = configuration.getString("configuration.default.allowPushDate.weekday1").equals("yes");
		weekDays[2] = configuration.getString("configuration.default.allowPushDate.weekday2").equals("yes");
		weekDays[3] = configuration.getString("configuration.default.allowPushDate.weekday3").equals("yes");
		weekDays[4] = configuration.getString("configuration.default.allowPushDate.weekday4").equals("yes");
		weekDays[5] = configuration.getString("configuration.default.allowPushDate.weekday5").equals("yes");
		weekDays[6] = configuration.getString("configuration.default.allowPushDate.weekday6").equals("yes");
		
		// TODO : Device Token 등록 관련 확인 필요
		commandService.setPushConfiguration(groupCode, accountId, wifi, true, allowPushTime, "", "", osType, appId, weekDays);
	}
	
	/**
	 * 
	 * @param source.
	 * @param front
	 * @param rear
	 * @return
	 */
	public static String plainTextToHtml(String source, String front, String rear) {
		return String.format("%s%s%s", front, source.replace("\n", "<br>"), rear);
	}

	public static String parseMailServerName(Object mailServer) {
		if (mailServer == null || "".equals(mailServer.toString())) {
			mailServer = "unknown";
		} else {
			mailServer = mailServer.toString().split("/")[0];
		}
		
		return mailServer.toString();
	}
	
	/**
	 * 특정 단말 로케일이 "KO_" 로 들어오는 경우 보정
	 * @param locale 로케일 
	 * @return 보정된 로케일
	 */
	public static String correctLocale(String locale) {
		if (locale.toUpperCase().startsWith("KO") && !locale.toUpperCase().equals("KO_KR")) 
			locale = "KO_KR";
		return locale;
	}
	
	/**
	 * 메일 서버의 Timezone을 설정에서 검색 
	 * @param mailServer 메일서버 이름 
	 * @param configuration TMG 설정
	 * @return
	 */
	public static String findTimezone(String mailServer, XMLConfiguration configuration) {
		String [] timezones = configuration.getString("configuration.server-timezone.timezone").split(" ");
		String foundTimezone = "";

		for (String timezone : timezones) {
			String servers =  configuration.getString("configuration.server-timezone.servers." + timezone);
			
			if (servers.indexOf(mailServer) != -1) {
				foundTimezone = timezone;
				break;
			}
		}
		
		return configuration.getString("configuration.server-timezone.location." + foundTimezone);
	}
	
}