package com.tionsoft.mas.tas.protocol.codec.encoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.codehaus.jackson.map.ObjectMapper;

import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.*;
import com.tionsoft.mas.tas.exception.*;
import com.tionsoft.mas.tas.protocol.codec.encoder.ByteBufferEncoder.BodyEncoder;
import com.tionsoft.mas.tas.protocol.definition.FieldDefinition;
import com.tionsoft.mas.tas.protocol.definition.SectionDefinition;

public class JsonBodyEncoder implements BodyEncoder {
	private PlatformHeader platformHeader;
	
	public final int encode(TasBean bean, SectionDefinition sectionDefinition, IoSession session,PlatformHeader platformHeader) {
		this.platformHeader = platformHeader;
		
		FieldDefinition fieldDefinition;
		int length = 0;
		for(String fieldName : sectionDefinition.getFieldNames()) {
			fieldDefinition = sectionDefinition.getFieldDef(fieldName);
			length += encodeJsonField(bean, fieldDefinition, session);
		}
		return length;
	}

	private final int encodeJsonField(TasBean bean, FieldDefinition fieldDef, IoSession session) {
		switch(fieldDef.getType()) {
		case FieldDefinition.FIELD_TYPE_STRING :
			Object object = bean.getValue(fieldDef.getName());
			String json;
			IoBuffer tmpBuffer = null;
			if(object instanceof Collection) {
				Collection<TasBean> beanList = bean.getValue(fieldDef.getName(), Collection.class); 
				json = encodeCollection(beanList);
			} else if(object instanceof TasBean) {
				TasBean subBean = bean.getValue(fieldDef.getName(), TasBean.class); 
				json = encodeObject(subBean);
			} else { // TasClient 에서 사용하는 경우
				json = bean.getValue(fieldDef.getName(), String.class); 
			}
			if (session != null) {
				tmpBuffer = IoBuffer.allocate(4 + json.getBytes().length, false);
				tmpBuffer.putInt(json.getBytes().length);
				tmpBuffer.put(json.getBytes());
				tmpBuffer.flip();
				session.write(tmpBuffer);
			}
			return 4 + json.getBytes().length;
		default : 
			ErrorType errorType = new ErrorType(platformHeader,ErrorType.ERROR_CODE_RESPONSE_JSON_BODYTYPE_NOT_STRING, "RESPONSE Illegal JSON Configuration Exception : JSON field must be string type : Name[" + fieldDef.getName()+"]");
			throw new TasException(errorType,new Throwable("RESPONSE Illegal JSON Configuration Exception"));

		}
	}
	
	public final String encodeCollection(Collection<TasBean> beans) {
		ObjectMapper mapper = new ObjectMapper();
		Collection<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(TasBean bean: beans) {
			Map<String, Object> map = encodeTasBean(bean);
			list.add(map);
		}
		
		try {
			return mapper.writeValueAsString(list);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public String encodeObject(TasBean bean) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = encodeTasBean(bean);
		
		try {
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private Map<String, Object> encodeTasBean(TasBean bean) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Set<String> keys = bean.getParams().keySet();
		for(String key : keys) {
			Object value = bean.getValue(key);
			if(value instanceof TasBean) {
				TasBean subBean = bean.getValue(key, TasBean.class);
				map.put(key, encodeTasBean(subBean));
			} else {
				map.put(key, value);
			}
			
		}
		return map;
	}
	
}