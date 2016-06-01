package com.tionsoft.mas.tas.protocol.definition;

public class BodyDefinition {
	
	private String name;
	private SectionDefinition reqDef;
	private SectionDefinition resDef;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public SectionDefinition getRequestDefinition() {
		return reqDef;
	}
	
	public void setReqDef(SectionDefinition reqDef) {
		this.reqDef = reqDef;
	}
	
	public SectionDefinition getResponseDefinition() {
		return resDef;
	}
	
	public void setResDef(SectionDefinition resDef) {
		this.resDef = resDef;
	}

}
