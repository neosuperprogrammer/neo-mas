package com.tionsoft.tmg.command.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.tionsoft.lgd.UDock;
import com.tionsoft.tdv.client.TdvClientLite;
import com.tionsoft.tmg.command.Command;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.util.CalendarHelper;
import com.tionsoft.tmg.util.FileIo;
import com.tionsoft.tmg.util.StringEncoder;

/**
 * 파일뷰어 
 * @author 서버개발실 이주용
 */
public class FILE00001 extends Command {
	private TdvClientLite tdvClient;
	private UDock udockClient;

	protected final Logger logger = LoggerFactory.getLogger("tas"); // test

	public TdvClientLite getTdvClient() {
		return tdvClient;
	}

	public void setTdvClient(TdvClientLite tdvClient) {
		this.tdvClient = tdvClient;
	}
	
	public UDock getUdockCleint() {
		return udockClient;
	}
	
	public void setUdockClient(UDock udockClient) {
		this.udockClient = udockClient;
	}
	
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader, jsonBody, jsonRespHeader, jsonRespBody;
		Map<String, Object> headerMap, bodyMap, result;
		String accountId, groupCode, status, errorMessage = "", fileUrl;
		byte [] image = null;
		
		try {
			jsonHeader	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			jsonBody	= JSONObject.fromObject(request.getBody("In_JSON", String.class));
		
			accountId	= jsonHeader.getString("AccountId");
			groupCode	= jsonHeader.getString("GroupCode");

			// 2013-01-06 민동원 : 아이디가 대문자로 들어올 경우 udock 링크 찾지 못하는 문제 수정
			try {
				String fileLocation = jsonBody.getString("FileLocation");
				if (fileLocation.endsWith(accountId.toUpperCase())) {
					fileLocation = fileLocation.substring(0, fileLocation.length() - accountId.length()) + accountId.toLowerCase();
					jsonBody.put("FileLocation", fileLocation);
				}
			} catch (Exception e) {
			}
			// 2013-01-06 민동원
			
			headerMap	= createHeader(request, jsonHeader);
			bodyMap		= createBody(groupCode, accountId, jsonBody);
				
			result	= tdvClient.send(headerMap, bodyMap);
			status	= result.get("STATUS").toString();
			fileUrl = result.get("URL").toString();
			
			jsonRespHeader	= JSONObject.fromObject(result.get("HEADER"));
			jsonRespBody	= JSONObject.fromObject(result.get("BODY"));
			
			if (Integer.parseInt(status) < 1) {
				errorMessage = jsonRespHeader.getString("ErrorMsg");
				throw new TmgException(Short.valueOf(status), errorMessage);
			}
			
			responseBody.put("ContentsType",	jsonRespBody.get("ContentsType"));
			responseBody.put("TotalPage",		jsonRespBody.get("TotalPage"));
			responseBody.put("RequestPage",		jsonRespBody.get("RequestPage"));
			responseBody.put("ResponseData",	jsonRespBody.get("ResponseData"));
			responseBody.put("IsPassword",		jsonRespBody.get("IsPassword"));
			
			if (configuration.getString("configuration.tdv.pdf.domain").equals(jsonBody.getString("Domain"))) {
				String fileName = jsonBody.getString("FileName");
				
				if (fileName.toLowerCase().endsWith(".zip")) {
					fileUrl = fileUrl.replace("/zip/", "/");					
					responseBody.put("ContentsType", "1");
				} 
				
				FileIo.urlDownload(fileUrl, getToPath(jsonBody, accountId));
				responseBody.put("ResponseData", getDownloadUrl(jsonBody, accountId));
				response.setBody("Out_BYTE", new byte[] {(byte) 0x00});
				
			} else if (	configuration.getString("configuration.tdv.excel.domain").equals(jsonBody.getString("Domain"))) {
				System.out.println("excel domain");
				
				FileIo.urlDownload(fileUrl, getToExcelPath(jsonBody, accountId));
				
				responseBody.put("ResponseData", getExcelDownloadUrl(jsonBody, accountId));
				response.setBody("Out_BYTE", new byte[] {(byte) 0x00});				
			} else {
				response.setBody("Out_BYTE", image = FileIo.urlToByte(fileUrl, false));
			}
		} catch (TmgException e) {
			throw e;
		} catch (JSONException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		} finally {
			if (image == null) {
				response.setBody("Out_BYTE", new byte[] {(byte) 0x00});
			}
		}
	}

	/**
	 * 다운로드 URL 생성
	 * @param jsonBody 바디 
	 * @param accountId 사용자 계정 
	 * @return
	 */
	private String getDownloadUrl(JSONObject jsonBody, String accountId) {		
		// 2013-12-05 민동원 : pdf 다운로드 시 원본 파일명을 유지하도록 수정하는 코드
		String fileName = createFilename(jsonBody);		
		boolean isImage = isImage(fileName);
		
		try {
			fileName = URLEncoder.encode(fileName, "utf-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return String.format("%s/%s/%s/%s." + resolveExtension(fileName, isImage), configuration.getString("configuration.tdv.pdf.baseUrl"), 
				 																  accountId, 
				 																  CalendarHelper.getFormatedDateString(CalendarHelper.getCurrentTime(), "yyyyMMdd"),
				 																  fileName);
	}

	/**
	 * 다운로드 경로 생성 
	 * @param jsonBody 바디 
	 * @param accountId 사용자 계정 
	 * @return
	 */
	private String getToPath(JSONObject jsonBody, String accountId) {		
		String fileName = createFilename(jsonBody);
		boolean isImage = isImage(fileName);
		
		return String.format("%s/%s/%s/%s." + resolveExtension(fileName, isImage), configuration.getString("configuration.tdv.pdf.downloadPath"), 
				 																  accountId,
				 																  CalendarHelper.getFormatedDateString(CalendarHelper.getCurrentTime(), "yyyyMMdd"),
				 																  fileName);
	}

	/**
	 * 엑셀 다운로드 URL 생성 
	 * @param jsonBody 바디 
	 * @param accountId 사용자 계정 
	 * @return
	 */
	private String getExcelDownloadUrl(JSONObject jsonBody, String accountId) {
		String fileName = createFilename(jsonBody);
		
		try {
			fileName = URLEncoder.encode(fileName, "utf-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return String.format("%s/%s/%s/%s." + fileName.substring(fileName.lastIndexOf(".") + 1), configuration.getString("configuration.tdv.excel.baseUrl"), 
				 																				 accountId,
				 																				 CalendarHelper.getFormatedDateString(CalendarHelper.getCurrentTime(), "yyyyMMdd"),
				 																				 fileName);
	}

	/**
	 * 엑셀 다운로드 경로 생성 
	 * @param jsonBody 바디 
	 * @param accountId 사용자 계
	 * @return
	 */
	private String getToExcelPath(JSONObject jsonBody, String accountId) {
		String fileName = createFilename(jsonBody);
		
		return String.format("%s/%s/%s/%s." + fileName.substring(fileName.lastIndexOf(".") + 1), configuration.getString("configuration.tdv.excel.downloadPath"), 
				 																				 accountId,
				 																				 CalendarHelper.getFormatedDateString(CalendarHelper.getCurrentTime(), "yyyyMMdd"),
				 																				 fileName);
	}
	
	/**
	 * 이미지 파일 여부 판단 
	 * @param fileName 파일명 
	 * @return
	 */
	private boolean isImage(String fileName) {
		return fileName.endsWith(".bmp") || fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".zip") || fileName.endsWith(".txt");
	}

	/**
	 * 파일명 생성 
	 * @param jsonBody 바디 
	 * @return
	 */
	private String createFilename(JSONObject jsonBody) {
		String prefix = configuration.getString("configuration.udock.prefix");
		String postfix = configuration.getString("configuration.udock.postfix");
		String fileName = jsonBody.get("FileName").toString();
		
		if (fileName.endsWith(postfix))
			fileName = fileName.substring(0, fileName.length() - postfix.length());
		
		if (fileName.startsWith(prefix))
			fileName = fileName.substring(prefix.length());
		
		return fileName;
	}

	/**
	 * 파일 확장자 결정 
	 * @param fileName 파일명 
	 * @param isImage 이미지 여부 
	 * @return
	 */
	private String resolveExtension(String fileName, boolean isImage) {
		return isImage ? fileName.substring(fileName.lastIndexOf(".") + 1) : "pdf";
	}

	/**
	 * 요청 헤더 생성
	 * @param request
	 * @param jsonHeader
	 * @return
	 */
	private Map<String, Object> createHeader(TasRequest request, JSONObject jsonHeader) {
		Map<String, Object> headerMap = new HashMap<String, Object>();
		
		headerMap.put("LegacyId",	jsonHeader.get("LegacyId"));
		headerMap.put("Min",		request.getPlatformHeader("MSISDN").toString().trim());
		headerMap.put("Wifi",		request.getPlatformHeader("WIFI_MAC").toString().trim());
		headerMap.put("Version",	jsonHeader.get("Version"));
		headerMap.put("Status",		"0");
		
		return headerMap;
	}
	
	/**
	 * 요청 바디 생성
	 * @param groupCode
	 * @param accountId
	 * @param jsonBody
	 * @param fileUrl
	 * @param fileSize
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> createBody(String groupCode, String accountId, JSONObject jsonBody) throws Exception {
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		Map<String, Object> userInfo = commandService.getUserInfo(groupCode, accountId);
		Map<String, Object> flieInfo = checkDocument(accountId, jsonBody);
		
		bodyMap.put("Domain",					jsonBody.get("Domain"));
		bodyMap.put("FileUsage",				jsonBody.get("FileUsage"));
		bodyMap.put("AccountId",				jsonBody.get("AccountId"));
		bodyMap.put("FileId",					jsonBody.get("FileId"));
		bodyMap.put("FileName",					flieInfo.get("FileName"));
		bodyMap.put("FileLocationType",			jsonBody.get("FileLocationType") != null ? jsonBody.get("FileLocationType").toString() : "1"); 
		bodyMap.put("FileLocation",				flieInfo.get("FileLocation").toString());
		bodyMap.put("FileLocationParameter",	jsonBody.get("FileLocationParameter"));
		bodyMap.put("FileSize",					flieInfo.get("FileSize"));
		bodyMap.put("RequestPage",				jsonBody.get("RequestPage").toString());
		bodyMap.put("RequestUnit",				jsonBody.get("RequestUnit"));
		bodyMap.put("PageSetup",				jsonBody.get("PageSetup").toString());
		bodyMap.put("Resolution",				jsonBody.get("Resolution").toString());
		bodyMap.put("DocumentPassword",			jsonBody.get("DocumentPassword") != null ? jsonBody.get("DocumentPassword") : "");				
		bodyMap.put("WatermarkText",			String.format("%s(%s)/%s/%s", userInfo.get("EMPLOY_NAME"), accountId, userInfo.get("DEPT_NAME"), userInfo.get("POSITION")));					
		bodyMap.put("AffiliateCode",			"");
		bodyMap.put("Language",					"0");
		
		return bodyMap;
	}

	/**
	 * U-DOCK 파일 여부 검사
	 * @param accountId 사용자 계정 
	 * @param jsonBody 요청 바디 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	private Map<String, Object> checkDocument(String accountId, JSONObject jsonBody) throws UnsupportedEncodingException, TmgException, Exception {
		String fileLocation			= jsonBody.get("FileLocation").toString();
		String fileName 			= jsonBody.get("FileName").toString();
		String prefix				= configuration.getString("configuration.udock.prefix");
		String postfix				= configuration.getString("configuration.udock.postfix");
		Map<String, Object> result	= new HashMap<String, Object>();
		String downloadPath			= configuration.getString("configuration.edms.downloadPath");
		String downloadUrl			= configuration.getString("configuration.edms.downloadUrl");
		
		String fileLocationType = jsonBody.get("FileLocationType") != null ? jsonBody.get("FileLocationType").toString() : "1";
		
		if (fileName.startsWith(prefix) && fileName.endsWith(postfix)) {
			UDock.FileInfo fileInfo;
			
			if (fileLocation.startsWith(configuration.getString("configuration.edms.url"))) {
				FileIo.urlDownload(fileLocation, String.format("%s/%s/%s", downloadPath, accountId, StringEncoder.digest(fileName)));
				fileInfo = udockClient.getFileInfo(accountId, String.format("%s/%s/%s", downloadUrl, accountId, StringEncoder.digest(fileName)));
				
			} else {
				fileLocation = encodeFileName(fileLocation);
				fileInfo = udockClient.getFileInfo(accountId, fileLocation);
			}

			// 문서 조회 권한 검사
			if (!udockClient.getDownloadPermission(accountId, fileInfo.fileId)) throw new TmgException(TmgExceptionType.DOCUMENT_PERMISSION_DENIED);

			result.put("FileName",		fileInfo.fileName);
			result.put("FileLocation",	fileInfo.fileUrl);
			result.put("FileSize",		String.valueOf(fileInfo.fileLength));
		} else {
			result.put("FileName",		jsonBody.get("FileName"));
			result.put("FileLocation",	jsonBody.getString("FileLocation"));
			result.put("FileSize",		jsonBody.getString("FileSize"));
		}

		if (Integer.parseInt(result.get("FileSize") != null ? result.get("FileSize").toString() : "0") > 31457280) {
			throw new TmgException(TmgExceptionType.DOCUMENT_OVER_THAN_30M);
		}
		
		if (fileLocationType.equals("5")) {
			result.put("FileLocation", jsonBody.getString("FileLocation"));
		}
		
		return result;
	}

	/**
	 * 파일 경로를 URL 인코딩한다.
	 * @param fileLocation 파일 경로 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String encodeFileName(String fileLocation) throws UnsupportedEncodingException {
		String [] u = fileLocation.split("/");
		StringBuffer sb = new StringBuffer();
		u[u.length - 1] = URLEncoder.encode(u[u.length - 1], "utf-8");
		for (String s : u) sb.append(s).append("/");
		
		return sb.toString().substring(0, sb.length() - 1);
	}
}