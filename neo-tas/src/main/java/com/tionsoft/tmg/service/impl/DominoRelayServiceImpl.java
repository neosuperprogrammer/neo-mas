package com.tionsoft.tmg.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.tionsoft.tmg.dao.UserDao;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.service.RelayService;
import com.tionsoft.tmg.util.StringEncoder;

/**
 * 도미노 I/F 호출 및 응답 처리 - LGD向 
 * @author 서버개발실 이주용
 */
public class DominoRelayServiceImpl implements RelayService {
	private static final String RESPONSE_BODY_SEPARATOR = "|";
	private XMLConfiguration configuration;
	private UserDao userDao;
	
	/**
	 * 요청 전송 및 결과 수신
	 * <ul>
	 * <li>임원/일반직원사번별 서비스 URL 분기 처리</li>
	 * <ul>
	 */
	public Map<String, Object> sendRequest(String header, String body) throws TmgException {
		HttpClient httpclient = new DefaultHttpClient();
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		UrlEncodedFormEntity formEntity = null;
        Map<String, Object> returnData = null;

		try {
//			formparams.add(new BasicNameValuePair("__Click", StringEncoder.escape("0")));
//			formparams.add(new BasicNameValuePair("Header", StringEncoder.escape(header)));
			formparams.add(new BasicNameValuePair("__Click", "0"));
			formparams.add(new BasicNameValuePair("Header", header));
			
			if (!body.isEmpty()) {
				JSONObject jsonHeader = JSONObject.fromObject(header);
				JSONObject jsonBody = JSONObject.fromObject(body);
				
				if (jsonHeader.get("LegacyId").equals("MAIL00006")) {
					if (jsonBody.get("Attached_File_Count").toString().equals("0") && jsonBody.get("List") != null) {
						jsonBody.remove("List");
						body = jsonBody.toString();						
					}
				}

//				if ( "MAIL00103".equals(jsonHeader.get("LegacyId")) ) {
//					formparams.add(new BasicNameValuePair("Body", body));
//				} else {
//					formparams.add(new BasicNameValuePair("Body", StringEncoder.escape(body)));
//				}
				formparams.add(new BasicNameValuePair("Body", body));
			}
			
			JSONObject jsonHeader = JSONObject.fromObject(header);
			String accountId = jsonHeader.getString("AccountId");
			String groupCode = jsonHeader.getString("GroupCode");
			
			formEntity = new UrlEncodedFormEntity(formparams, StringEncoder.DEFAULT_ENCODING_TYPE);
			String url = identifyUrl(groupCode, accountId);
			
			HttpPost httppost = new HttpPost(getConfiguration().getString(url)); 
			httppost.setEntity(formEntity);
			
			int connectTimeout = configuration.getInt("configuration.http.connectTimeout");
			int socketTimeout = configuration.getInt("configuration.http.socketTimeout");
			
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, connectTimeout);
			HttpConnectionParams.setSoTimeout(params, socketTimeout);			
	        HttpResponse response = httpclient.execute(httppost);

	        if (response.getEntity() == null) return null;
	        
	        String resData			= EntityUtils.toString(response.getEntity());
	        String[] tempArr		= resData.split("[" + RESPONSE_BODY_SEPARATOR + "]");
	        JSONObject resultHeader	= JSONObject.fromObject(StringEncoder.unescape(tempArr[0]));
	        
	        if (!resultHeader.getString("Status").equals("0")) {
	        	throw new TmgException(Short.valueOf(resultHeader.getString("Status")));
	        }
	        
	        returnData = new HashMap<String, Object>();
	        returnData.put("HEADER", resultHeader.toString());
	        
	        int resultCount = tempArr.length;
	        String fileUrl = "";
	        
	        if (resultCount > 1) {
	        	returnData.put("BODY", StringEncoder.unescape(tempArr[1]));
	        	returnData.put("URL", fileUrl);
	        }
		} catch (JSONException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.PARSING_FAILED);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		} catch (IOException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.DOMINO_IF_SERVICE_FAIL);
		}

        return returnData;
	}
	
	/**
	 * 직급/임원별 분기 URL 식별
	 * @param groupCode 그룹코드
	 * @param accountId 포탈아이디
	 * @return
	 */
	private String identifyUrl(String groupCode, String accountId) {
		Map<String, Object> user;
		String urlName = "0", employeeNo, titleCode;

		// TODO : 아직 데이터 이관이 마무리되지 않아 예외 처리함
		try {
			user = getUserDao().getUserInfo(groupCode, accountId);
			
			employeeNo	= (String) user.get("EMPLOY_NO");
			titleCode	= (String) user.get("IS_EXCLUSIVE");
			
			if (titleCode.startsWith("O")) {
				urlName = "officers";
			} else {
				urlName = employeeNo.split("[|]")[0].substring(employeeNo.split("[|]")[0].length() - 1);
			}
		} catch (Exception e) {
			urlName = "1";
		}
		
		return String.format("configuration.legacy.urls.url_%s", urlName);
	}

	/**
	 * @return the configuration
	 */
	public XMLConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(XMLConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * @return the userDao
	 */
	public UserDao getUserDao() {
		return userDao;
	}

	/**
	 * @param userDao the userDao to set
	 */
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}
