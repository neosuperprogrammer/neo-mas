package com.tionsoft.tmg.domain;

import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.exception.TmgException;

import net.sf.json.JSONObject;

/**
 * Request HEADER_JSON 정보 (읽기 전용)
 * @author 민동원
 * @since 2013.07.02
 */
public class Header {
	private final String legacyId;
	private final String accountId;
	private final String language;
	private final String appType;
	private final String authKey;
	private final String groupCode;
	private final String appId;
	
	private JSONObject source;
	
	public Header(JSONObject json) throws TmgException {
		try {
			source 		= json;
			legacyId	= json.getString("LegacyId");
			accountId	= json.getString("AccountId");
			language	= json.getString("Language");
			appType		= json.getString("AppType");
			authKey		= json.getString("AuthKey");
			groupCode	= json.getString("GroupCode");
			appId		= json.getString("AppId");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		}
	}
	
	public Header(String jsonText) throws TmgException {
		this(JSONObject.fromObject(jsonText));
	}
	
	public String getLegacyId() {
		return legacyId;
	}
	
	public String getAccountId() {
		return accountId;
	}
	
	public String getLanguage() {
		return language;
	}

	public String getAppType() {
		return appType;
	}

	public String getAuthKey() {
		return authKey;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public String getAppId() {
		return appId;
	}	
	
	public String toString() {
		return source.toString();
	}
}