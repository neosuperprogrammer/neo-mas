package com.tionsoft.tmg.command;

import javax.annotation.Resource;

import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import com.tionsoft.mas.tas.taslet.Taslet;
import com.tionsoft.mas.tas.taslet.TasletException;
import net.sf.json.JSONObject;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;


import com.tionsoft.tmg.domain.Header;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.service.AuthValidator;
import com.tionsoft.tmg.service.BoardService;
import com.tionsoft.tmg.service.CommandService;

/**
 * Command : 각 커맨드의 부모 클래스, 공통 처리 작업 담당 (처리 결과에 따라 HEADER_JSON 의 STATUS 값 쓰기)
 * @author 이주용
 * @since 2013.07.02
 */
@Controller
public abstract class Command implements Taslet {
	/**
	 * configuration.xml 파일
	 */
	@Resource
	protected XMLConfiguration configuration;

	/**
	 * 인증 정보 검사 서비스
	 */
	@Resource
	protected AuthValidator authValidator;

	/**
	 * 커맨드의 비즈니스 로직 실행 서비스
	 */
	protected CommandService commandService;
	
	/**
	 * 게시판의 비즈니스 로직 실행 서비스
	 */
	protected BoardService boardService;

	/**
	 * TMG 커맨드 로깅 객체
	 */
	protected final Logger logger = LoggerFactory.getLogger("tmg");
	
	/**
	 * TMG 에러 로깅
	 */
	protected final Logger errorLogger = LoggerFactory.getLogger("tmg-error");
	
	/**
	 * execute : Taslet.excute() 구현
	 * @param request 요청
	 * @param response 응답
	 */
	public void execute(TasRequest request, TasResponse response) throws TasletException {
		JSONObject responseHeader = null;
		JSONObject responseBody = JSONObject.fromObject("{}");

		short status = TmgExceptionType.SUCCESS;
		String errorMessage = "";
//		String locale = "default";
		Header header = null;
		String logId = "";

		try {
			// 1. 헤더 조회 및 유효성 검사
			header = getHeader(request);
			
			// 2. Locale 획득
//			locale = header.getLanguage();

			// 3. 인증 유효성 검사
			if (!header.getLegacyId().equals("COM000001")) {
				authValidator.validate(request);
			}
			
			// 4. 커맨드 실행
			// 131105 민동원 : 접속 로그 추가
			String commandId = request.getPlatformHeader(PlatformHeader.MESSAGE_ID, String.class).trim();
			if (commandId.equals("MAIL00002") || commandId.equals("CAL000022") || commandId.equals("CAL000009") || commandId.equals("ADDR00016")) {
				String osType = request.getPlatformHeader(PlatformHeader.OS_TYPE, String.class);
				String wifi = request.getPlatformHeader(PlatformHeader.WIFI_MAC, String.class).toUpperCase().trim();
				String accountId = header.getAccountId();
				
				logId = commandService.setConnectRequestLog(accountId, osType.equals("A") ? "com.tionsoft.groupware.lgd" : "com.lgdisplay.tionsoft.tmg", wifi);
			}
			writeLog(request, String.format("%s.doExecute.begin", header.getLegacyId()));
			doExecute(request, response, responseBody);
			writeLog(request, String.format("%s.doExecute.end", header.getLegacyId()));
		} catch (TmgException e) {
			status = e.getErrorCode();			
			errorMessage = e.getErrorMessage();				

			errorLogger.error(errorMessage + "(" + status + ")");
			if (header != null) writeLog(request, String.format("%s.doExecute.exception(%d) : %s", header.getLegacyId(), status, errorMessage));
		} catch (Exception e) {
			status = TmgExceptionType.UNDEFINED_EXCEPTION;
			errorMessage = "";
			
			errorLogger.error(e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
			if (header != null) writeLog(request, String.format("%s.doExecute.exception(%d) : %s", header.getLegacyId(), status, errorMessage));
		} finally {
			responseHeader = JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			responseHeader.put("STATUS", status);
			responseHeader.put("ERROR_MSG", errorMessage);
			
			response.setHeader("Header_JSON", responseHeader.toString());										// 응답 헤더에 작성된 헤더 데이터를 삽입
			
			// 도미노 I/F에서 넘어온 값을 가공없이 넘기기 위해 Body에 Out_JSON이 추가되어 있지 않을 경우에만 responseBody 넣어주도록 처리 
			try {
				response.getBody("Out_JSON");
			} catch (Exception e) {
				response.setBody("Out_JSON", responseBody.toString());											// 응답 보디에 작성된 보디 데이터를 삽입
			}
			
			String legacyId = "";			// Findbug 분석 결과 보완
			boolean includeOutByte = false;
			
			if (header != null) legacyId = header.getLegacyId();
			
			// Out_Byte가 포함되어야 하나 예외로 인해 응답 데이터 없을 경우 처리
			String [] includeOutbyteMessages = configuration.getStringArray("configuration.default.exception.includeOutByteMessages");
			for (String s : includeOutbyteMessages) {
				if (s.equals(legacyId)) includeOutByte = true;
			}
			
			if (includeOutByte) {
				try {
					response.getBody("Out_BYTE");
				} catch (Exception e) {
					response.setBody("Out_BYTE", new byte[] {0});
				}
			}
			
			// 20131105 민동원 : 접속 로그 추가
			if (logId != null && logId.isEmpty() == false)
				commandService.setConnectResponseLog(logId);
			
			response.setPlatformHeader(PlatformHeader.STATUS_CODE, status);										// 플랫폼 헤더에 STATUS 추가
		}
	}

	/**
	 * 로그 기록
	 * @param request
	 * @param header
	 * @param log
	 */
	protected void writeLog(TasRequest request, String log) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("Session : %s, ", request.getPlatformHeader("SESSION_ID").toString()));
		sb.append(String.format("TransoutAction : %s, ", request.getPlatformHeader("SESSION_ID").toString()));
		sb.append(log);
		getLogger().info(sb.toString());
	}
	
	/**
	 * 요청 헤더 조회
	 * @param request
	 * @return Header 객체
	 * @throws TmgException
	 */
	protected Header getHeader(TasRequest request) throws TmgException {
		JSONObject requestHeader = JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
		return new Header(requestHeader);
	}
	
	/**
	 * 요청 본문 조회
	 * @param request
	 * @return
	 */
	protected JSONObject getBody(TasRequest request) {
		JSONObject jsonBody;
		
		try {
			jsonBody = JSONObject.fromObject(request.getBody("In_JSON", String.class));
		} catch (Exception e) {
			jsonBody = JSONObject.fromObject("{}");
		}
		
		return jsonBody;
	}
	
	/**
	 * 각 커맨드 별 처리가 이루어지는 가상 메서드
	 * @param 요청 Body (IN_JSON)
	 * @return 응답 Body (OUT_JSON)
	 * @brief 각 커맨드의 공통적인 선/후처리가 있기 때문에 template-method idiom 으로 변경함
	 */
	protected abstract void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws Exception;

	/**
	 * configuration.xml getter
	 * @return 설정 정보
	 */
	public XMLConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * configuration.xml setter
	 * @param configuration 설정 정보
	 */
	public void setConfiguration(XMLConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * 인증 정보 검증 서비스 getter
	 * @return 인증 정보 검증 서비스 instance
	 */
	public AuthValidator getAuthValidator() {
		return authValidator;
	}

	/**
	 * 인증 정보 검증 서비스 getter
	 * @param authValidator 인증 정보 검증 서비스 instance
	 */
	public void setAuthValidator(AuthValidator authValidator) {
		this.authValidator = authValidator;
	}

	/**
	 * @return the commandService
	 */
	public CommandService getCommandService() {
		return commandService;
	}

	/**
	 * @param commandService the commandService to set
	 */
	public void setCommandService(CommandService commandService) {
		this.commandService = commandService;
	}
	
	/**
	 * TMG Logger getter
	 * @return TMG 관련 로깅 객체
	 */
	protected Logger getLogger() {
		return logger;
	}
	
	/**
	 * TMG Error Logger getter
	 * @return TMG 관련 에러 로깅 객체
	 */
	protected Logger getErrorLogger() {
		return errorLogger;
	}

	public BoardService getBoardService() {
		return boardService;
	}

	public void setBoardService(BoardService boardService) {
		this.boardService = boardService;
	}
}
