package com.tionsoft.mas.tas.protocol.definition;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SectionDefinition {
	
	private final Map<String, FieldDefinition> fieldDefs;

	public SectionDefinition() {
		this.fieldDefs = Collections.synchronizedMap(new LinkedHashMap<String, FieldDefinition>());
	}
	
	public void addFieldDef(FieldDefinition fieldDef) {
		fieldDefs.put(fieldDef.getName(), fieldDef);
	}
	
	public FieldDefinition getFieldDef(String name) {
		return fieldDefs.get(name);
	}
	
	public Collection<String> getFieldNames() {
		return fieldDefs.keySet();
	}
	
	public int getFieldNameCount() {
		return fieldDefs.keySet().size();
	}
	
}