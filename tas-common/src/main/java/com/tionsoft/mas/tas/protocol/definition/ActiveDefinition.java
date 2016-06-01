package com.tionsoft.mas.tas.protocol.definition;

public class ActiveDefinition {
	
	private String name;
	private SectionDefinition headerDefinition;
	private BodyDefinition bodyDefinition;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public SectionDefinition getHeaderDefinition() {
		return headerDefinition;
	}
	
	public void setHeaderDef(SectionDefinition headerDefinition) {
		this.headerDefinition = headerDefinition;
	}
	
	public BodyDefinition getBodyDefinition() {
		return bodyDefinition;
	}
	
	public void setBodyDef(BodyDefinition bodyDefinition) {
		this.bodyDefinition = bodyDefinition;
	}
}