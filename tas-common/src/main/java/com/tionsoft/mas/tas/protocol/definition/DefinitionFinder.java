package com.tionsoft.mas.tas.protocol.definition;

import java.util.List;
import org.apache.commons.configuration.XMLConfiguration;

import com.tionsoft.mas.tas.bean.platform.PlatformHeader;

public class DefinitionFinder {
	
	private static final String QUERY_FOR_FIELD_NAMES = "/field/@name";
	private static final String QUERY_FOR_FIELD_TYPE = "/field[@name='%s']/@type";
	private static final String QUERY_FOR_FIELD_REF = "/field[@name='%s']/@ref";
	
	private static final String QUERY_FOR_HEADER =  "protocols/protocol[@name='%s']/header";
	private static final String QUERY_FOR_REQUEST =  "protocols/protocol[@name='%s']/body[@name='%s']/request";
	private static final String QUERY_FOR_RESPONSE =  "protocols/protocol[@name='%s']/body[@name='%s']/response";
	private static final String QUERY_FOR_STRUCTURE =  "protocols/protocol[@name='%s']/structure[@name='%s']";
	
	private final XMLConfiguration protocolConfig;
	private final PlatformHeader platformHeader;
	private final static DefinitionMap definitionMap = DefinitionMap.newInstance();
	
	public DefinitionFinder(XMLConfiguration protocolConfig, PlatformHeader platformHeader) {
		this.protocolConfig = protocolConfig;
		this.platformHeader = platformHeader;
	}
	
	private SectionDefinition createHeaderDefinition() {
		
		// PS,PC 하위 버전 호환을 위한 패치[시작]
		String version = platformHeader.getValue(PlatformHeader.VERSION, String.class);
		String appId = platformHeader.getValue(PlatformHeader.APPLICATION_ID, String.class);
		
		if(version.equals("PHV100") && appId.equals("PC02")) {
			appId = "PFAP";
		}
		// PS,PC 하위 버전 호환을 위한 패치[끝]
		
		String headerQuery = String.format(QUERY_FOR_HEADER, appId);
		return createSectionDefinition(headerQuery);
	}
	
	private BodyDefinition createBodyDefinition() {
		
		String version = platformHeader.getValue(PlatformHeader.VERSION, String.class);
		String appId = platformHeader.getValue(PlatformHeader.APPLICATION_ID, String.class);
		String msgId = platformHeader.getValue(PlatformHeader.MESSAGE_ID, String.class);
		
		BodyDefinition bodyDef = new BodyDefinition();
		// PS,PC 하위 버전 호환을 위한 패치[시작]
		
		if(version.equals("PHV100") && appId.equals("PC02") && msgId.equals("M00000002")) {
			appId = "PFAP";
			msgId = "M00000001";
		} else if(version.equals("PHV100") && appId.equals("PC02") && msgId.equals("M00000007")) {
			appId = "PFAP";
			msgId = "M00000002";
		}
		// PS,PC 하위 버전 호환을 위한 패치[끝]
		
		String bodyQuery;
		bodyQuery = String.format(QUERY_FOR_REQUEST, appId, msgId);
		SectionDefinition reqDef = createSectionDefinition(bodyQuery);
		bodyQuery = String.format(QUERY_FOR_RESPONSE, appId, msgId);
		SectionDefinition resDef = createSectionDefinition(bodyQuery);
		
		bodyDef.setName(platformHeader.getValue(PlatformHeader.MESSAGE_ID, String.class));
		bodyDef.setReqDef(reqDef);
		bodyDef.setResDef(resDef);
		
		
		return bodyDef;
	}
	
	@SuppressWarnings("unchecked")
	private SectionDefinition createSectionDefinition(String fSectionQuery) {
		
		SectionDefinition sectionDef = new SectionDefinition();
		String fieldNamesQuery = fSectionQuery + QUERY_FOR_FIELD_NAMES;
		List<String> fieldNames = protocolConfig.getList(fieldNamesQuery);
		FieldDefinition fieldDef;
		
		for(String fieldName : fieldNames) {
			String fieldTypeQuery = String.format(fSectionQuery + QUERY_FOR_FIELD_TYPE, fieldName);
			String fieldRefQuery = String.format(fSectionQuery + QUERY_FOR_FIELD_REF, fieldName);
			fieldDef = createFieldDefinition(fieldName, fieldTypeQuery, fieldRefQuery);
			sectionDef.addFieldDef(fieldDef);
		}
		
		return sectionDef;
	}
	
	/**
	 * TODO Field 정의 validation check 로직 추가
	 * check 1) type 이 struct 인 경우 ref attribute 가 명시되어야 함
	 * check 2) type 이 struct 인 경우 iterable attribute 가 명시되어야 함
	 * check 3) type 이 struct 가 아닌 경우 ref attribute, iterable attribute 는 명시하면 안됨 
	 * @param fieldName
	 * @param fieldTypeQuery
	 * @param fieldRefQuery
	 * @param fieldIterQuery
	 * @return
	 */
	private FieldDefinition createFieldDefinition(String fieldName, String fieldTypeQuery, String fieldRefQuery) {
		String fieldType = protocolConfig.getString(fieldTypeQuery);
		String fieldRef = protocolConfig.getString(fieldRefQuery);

		FieldDefinition fieldDef = new FieldDefinition();
		fieldDef.setName(fieldName);
		fieldDef.setType(FieldDefinition.type2Int(fieldType));
		fieldDef.setReference(fieldRef);
		
		return fieldDef;
	}
	
	/**
	 * @return
	 */
	public ActiveDefinition getActiveDefinition() {
		
		String messageReqeustKey = getRequestMessageKey();
		ActiveDefinition activeDef = null;
		if(definitionMap.existInActiveDefinitionMap(messageReqeustKey)){
			
			activeDef = definitionMap.getActiveDefinition(messageReqeustKey);
			
		}else {
			SectionDefinition headerDef = createHeaderDefinition();
			BodyDefinition bodyDef = createBodyDefinition();
			
			activeDef = new ActiveDefinition();
			activeDef.setHeaderDef(headerDef);
			activeDef.setBodyDef(bodyDef);
			
			definitionMap.putActiveDefinition(messageReqeustKey, activeDef);
		}
		
		
		return activeDef;
	}
	
	public SectionDefinition getRefDefinition(String ref) {
		// PS,PC 하위 버전 호환을 위한 패치[시작]
		String version = platformHeader.getValue(PlatformHeader.VERSION, String.class);
		String appId = platformHeader.getValue(PlatformHeader.APPLICATION_ID, String.class);
		
		if(version.equals("PHV100") && appId.equals("PC02")) {
			appId = "PFAP";
		}
		// PS,PC 하위 버전 호환을 위한 패치[끝]
		String structureQuery = String.format(QUERY_FOR_STRUCTURE, appId, ref);
		
		
		if(definitionMap.existInSectionDefinitionMap(structureQuery))
		{
			return definitionMap.getSectionDefinition(structureQuery);
		}else {
			SectionDefinition secDef = createSectionDefinition(structureQuery);
			definitionMap.putSectionDefinition(structureQuery, secDef);
			
			return secDef;
		}
		
		
	}
	
	public PlatformHeader getPlatformHeader() {
		return platformHeader;
	}
	
	
	private String getRequestMessageKey()
	{
		String version = platformHeader.getValue(PlatformHeader.VERSION, String.class);
		String appId = platformHeader.getValue(PlatformHeader.APPLICATION_ID, String.class);
		String msgId = platformHeader.getValue(PlatformHeader.MESSAGE_ID, String.class);
		
		StringBuilder sBuilder =new StringBuilder(appId).append(".").append(version).append(".").append(msgId);
		
		return sBuilder.toString();
	}
	
}
