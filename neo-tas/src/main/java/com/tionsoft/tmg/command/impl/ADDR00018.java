package com.tionsoft.tmg.command.impl;

import java.util.Map;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.tionsoft.tmg.command.Command;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.util.FileIo;
import com.tionsoft.tmg.util.LgdCommon;

/**
 * 주소 상세보기
 * @author 서버개발실 이주용
 */
public class ADDR00018 extends Command {
	@Override
	protected void doExecute(TasRequest request, TasResponse response, JSONObject responseBody) throws TmgException {
		JSONObject jsonHeader, jsonBody;
		String accountId, groupCode, locale, addressId, photoUrl;
		byte [] photo = null;
		try {
			jsonHeader	= JSONObject.fromObject(request.getHeader("Header_JSON", String.class));
			jsonBody	= JSONObject.fromObject(request.getBody("In_JSON", String.class));
			
			groupCode	= jsonHeader.getString("GroupCode");
			locale		= jsonHeader.getString("Language");
			accountId		= jsonHeader.getString("AccountId");
			
			// 2013-12-30 민동원 : 특정 단말 로케일이 "KO_" 로 들어오는 경우 있어 땜빵 
			locale 		= LgdCommon.correctLocale(locale);
			addressId	= jsonBody.getString("Address_ID");
			
			Map<String, Object> address = commandService.getAddress(accountId, locale, groupCode, addressId);
			responseBody.putAll(address);
			
			if (address.get("Picture") == null) {
				photo = new byte[] {(byte) 0x00};
			} else {
				// 포탈에 없는 이미지일 경우 별도 처리
				if (address.get("Picture").toString().equalsIgnoreCase("BITIMGPHOTODOWN")) {
					photoUrl = configuration.getString("configuration.address.photo.localUrl");
					photo = FileIo.urlToByte(String.format("%s%s.jpg", photoUrl, address.get("EmployNo").toString().split("[|]")[0]), true);
				} else {
					photoUrl = configuration.getString("configuration.address.photo.baseUrl");
					photo = FileIo.urlToByte(String.format("%s%s", photoUrl, address.get("Picture")), true);
				}
			}
			address.put("Picture", "");
			response.setBody("Out_BYTE", photo);
		} catch (TmgException e) {
			throw e;
		} catch (JSONException e) {
			throw new TmgException(TmgExceptionType.MISSING_REQUIRED_ITEMS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION);
		} finally {
			if (photo == null) response.setBody("Out_BYTE", new byte[] {0});
		}
	}
}
