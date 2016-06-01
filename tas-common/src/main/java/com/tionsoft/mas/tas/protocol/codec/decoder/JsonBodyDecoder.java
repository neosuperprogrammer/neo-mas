package com.tionsoft.mas.tas.protocol.codec.decoder;

import java.util.*;
import java.util.Map.Entry;

import org.codehaus.jackson.*;
import org.codehaus.jackson.JsonParser.NumberType;
import org.codehaus.jackson.map.*;

import com.tionsoft.mas.tas.bean.*;
import com.tionsoft.mas.tas.bean.platform.*;
import com.tionsoft.mas.tas.exception.*;

public class JsonBodyDecoder implements BodyDecoder {
	private PlatformHeader platformHeader;
	
	public final TasBean decode(TasBean bean,PlatformHeader platformHeader) {
		this.platformHeader = platformHeader;
		
		TasBean jsonBean = new TasBean();
		Set<String> paramNames = bean.getParamNames();
		for(String paramName : paramNames) {
			Object obj = bean.getValue(paramName);
			if(!(obj instanceof String)) {
				ErrorType errorType = new ErrorType(this.platformHeader,ErrorType.ERROR_CODE_REQUEST_JSON_BODYTYPE_NOT_STRING, "REQUEST Illegal JSON Configuration Exception : JSON field must be string type : [Name : " + paramName + "]");
				throw new TasException(errorType,new Throwable("REQUEST Illegal JSON Configuration Exception"));

			}
			String json = bean.getValue(paramName, String.class);
			
			//body type이 json일때도 json string을 taslet에 전달함 
			String jsonString = System.getProperty("mas.tas.json.jsonstring");
			if(jsonString != null && jsonString.equalsIgnoreCase("true")) {
				jsonBean.setValue(paramName+PlatformHeader.JSONSTRINGDATA, json);
//				System.out.println("JsonBodyDecoder jsondata ["+paramName+PlatformHeader.JSONSTRINGDATA+"]["+json+"]");
			}
			
////          문제 있음 수정 필요			
//			String jsonParsing = System.getProperty("mas.tas.json.jsonparsing"); //mas.tas.json.jsonparsing 설정이 fasle을 제외하고는 parsing한다.
//			if(jsonParsing == null || !jsonParsing.equalsIgnoreCase("false")) {
//				Object subBean = decodeJson(paramName, json);
//				jsonBean.setValue(paramName, subBean);
//			}else{                                                  //mas.tas.json.jsonparsing 설정이 fasle이면 비어있는 tmpBean 설정한다.
//				TasBean tmpBean = new TasBean();
//				jsonBean.setValue(paramName, tmpBean);
//			}

			
			Object subBean = decodeJson(paramName, json);
			jsonBean.setValue(paramName, subBean);
			
		}
		return jsonBean;
	}
	
	private final Object decodeJson(String paramName, String json) {
		try {
			JsonFactory f = new JsonFactory();
			JsonParser jp = f.createJsonParser(json);
			jp.setCodec(new ObjectMapper());
			JsonNode jNode = jp.readValueAsTree();
			jp.close();
			
			Object result = null;
			if(jNode.isArray()) {
				result = parseJsonArray(jNode);
			} else if (jNode.isObject()){
				result = parseJsonObject(jNode);
			} 
			return result;
		} catch (Exception e) {
			ErrorType errorType = new ErrorType(this.platformHeader,ErrorType.ERROR_CODE_JSON_PARSING, "JsonParseException");
			throw new TasException(errorType,e);
			
//			throw new RuntimeException(e);
		}
	}
	
	private final Collection<TasBean> parseJsonArray(JsonNode jNode) {
		Collection<TasBean> list = new ArrayList<TasBean>();
		Iterator<JsonNode> nodeIter = jNode.getElements();
		while(nodeIter.hasNext()) {
			JsonNode subNode = nodeIter.next();
			list.add(parseJsonObject(subNode));
		}
		return list;
	}
	
	private final TasBean parseJsonObject(JsonNode jNode) {
		TasBean bean = new TasBean();
		if(jNode.isContainerNode()) {
			Iterator<Entry<String,JsonNode>> fieldIter= jNode.getFields();
			while(fieldIter.hasNext()) {
				 Entry<String, JsonNode> fieldEntry = fieldIter.next();
				 JsonNode fieldValue = fieldEntry.getValue();
				 if(fieldValue.isValueNode()) {
					 parseField(fieldEntry, bean);
				 } else if (fieldValue.isContainerNode()) {
					 TasBean subBean = parseJsonObject(fieldValue);
					 parseField(fieldEntry, subBean);
					 bean.setValue(fieldEntry.getKey(), subBean);
				 } 
			}
		} else if(jNode.isValueNode()) {
			ErrorType errorType = new ErrorType(this.platformHeader,ErrorType.ERROR_CODE_JSON_NOT_SUPPORTED_VALUES, "JSON : An ordered list of values not supported, collection of name/value pairs supported!");
			throw new TasException(errorType,new Throwable("JSON : An ordered list of values not supported, collection of name/value pairs supported!"));
			//["가", "나", 100] 과 같이 name/value pair 로 구성되지 않은 JSON 은  지원하지 않기로 한다.
			// 추가 지원이 필요한 경우 개선하기로 한다. 이렇게 쓰는 경우가 있을까?
			//parseValue(jNode, bean);
		}
		return bean;
	}
	
	// 절대 삭제하지 말 것
//	private void parseValue(JsonNode jNode, TasBean bean) {
//		if(jNode.isArray()) {
//			System.out.println("Array");
//		} else if(jNode.isBinary()) {
//			System.out.println("Binary");
//			try {
//				bean.setValue("X", jNode.getBinaryValue());
//			} catch (Exception e) {
//			}
//		} else if(jNode.isBoolean()) {
//			System.out.println("Boolean");
//			bean.setValue("X", jNode.getBooleanValue());
//		} else if(jNode.isNull()) {
//			System.out.println("Null");
//			bean.setValue("X", null);
//		} else if(jNode.isNumber()) {
//			NumberType nType = jNode.getNumberType();
//			if(nType == NumberType.BIG_DECIMAL) {
//				System.out.println("BigDecimal");
//				bean.setValue("X", jNode.getDecimalValue());
//			} else if(nType == NumberType.INT) {
//				System.out.println("Int");
//				bean.setValue("X", jNode.getIntValue());
//			} else if(nType == NumberType.LONG) {
//				System.out.println("Long");
//				bean.setValue("X", jNode.getDecimalValue());
//			} else if(nType == NumberType.BIG_INTEGER) {
//				System.out.println("BigInteger");
//				bean.setValue("X", jNode.getBigIntegerValue());
//			} else if(nType == NumberType.FLOAT) {
//				System.out.println("FloatingPointNumber");
//				bean.setValue("X", jNode.getValueAsDouble());
//			} else if(nType == NumberType.DOUBLE) {
//				System.out.println("Double");
//				bean.setValue("X", jNode.getDoubleValue());
//			} 
//		} else if(jNode.isTextual()) {
//			System.out.println("Texture");
//			bean.setValue("X", jNode.getValueAsText());
//		} 
//	}
	
	private final void parseField(Entry<String, JsonNode> fieldEntry, TasBean bean) {
		JsonNode fieldValue = fieldEntry.getValue();
		if(fieldValue.isArray()) { //array type 추가
			Object result = parseJsonArray(fieldEntry.getValue());
			bean.setValue(fieldEntry.getKey(), result);
			
		} else if(fieldValue.isBinary()) {
			try {
				bean.setValue(fieldEntry.getKey(), fieldEntry.getValue().getBinaryValue());
			} catch (Exception e) {
			}
		} else if(fieldValue.isBoolean()) {
			bean.setValue(fieldEntry.getKey(), fieldEntry.getValue().getBooleanValue());
		} else if(fieldValue.isNull()) {
			bean.setValue(fieldEntry.getKey(), null);
		} else if(fieldValue.isNumber()) {
			NumberType nType = fieldEntry.getValue().getNumberType();
			if(nType == NumberType.BIG_DECIMAL) {
				bean.setValue(fieldEntry.getKey(), fieldEntry.getValue().getDecimalValue());
			} else if(nType == NumberType.INT) {
				bean.setValue(fieldEntry.getKey(), fieldEntry.getValue().getIntValue());
			} else if(nType == NumberType.LONG) {
				bean.setValue(fieldEntry.getKey(), fieldEntry.getValue().getDecimalValue());
			} else if(nType == NumberType.BIG_INTEGER) {
				bean.setValue(fieldEntry.getKey(), fieldEntry.getValue().getBigIntegerValue());
			} else if(nType == NumberType.FLOAT) {
				bean.setValue(fieldEntry.getKey(), fieldEntry.getValue().getValueAsDouble());
			} else if(nType == NumberType.DOUBLE) {
				bean.setValue(fieldEntry.getKey(), fieldEntry.getValue().getDoubleValue());
			} 
		} else if(fieldValue.isTextual()) {
			bean.setValue(fieldEntry.getKey(), fieldEntry.getValue().getValueAsText());
		} 
	}
}