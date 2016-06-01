package com.tionsoft.mas.tas.protocol.definition;

public class FieldDefinition {
	
	public static final int FIELD_TYPE_BOOLEAN = 1;
	public static final int FIELD_TYPE_BYTE = 2;
	public static final int FIELD_TYPE_STRING = 3;
	public static final int FIELD_TYPE_SHORT = 4;
	public static final int FIELD_TYPE_INTEGER = 5;
	public static final int FIELD_TYPE_LONG = 6;
	public static final int FIELD_TYPE_FLOAT = 7;
	public static final int FIELD_TYPE_DOUBLE = 8;
	public static final int FIELD_TYPE_FILE = 9;
	public static final int FIELD_TYPE_STRUCTURE = 10;
	
	private String name;
	private int type;
	private int length;
	private String reference;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}
	
	public String getReference() {
		return reference;
	}
	
	public void setReference(String ref) {
		this.reference = ref;
	}

	public static final int type2Int(String type) {
		if(type.equalsIgnoreCase("BOOLEAN")) {
			return FIELD_TYPE_BOOLEAN;
		} else if(type.equalsIgnoreCase("BYTE")) {
				return FIELD_TYPE_BYTE;
		} else if(type.equalsIgnoreCase("STRING")) {
			return FIELD_TYPE_STRING;
		} else if(type.equalsIgnoreCase("SHORT")) {
			return FIELD_TYPE_SHORT;
		} else if(type.equalsIgnoreCase("INTEGER")) {
			return FIELD_TYPE_INTEGER;
		} else if(type.equalsIgnoreCase("LONG")) {
			return FIELD_TYPE_LONG;
		} else if(type.equalsIgnoreCase("FLOAT")) {
			return FIELD_TYPE_FLOAT;
		} else if(type.equalsIgnoreCase("DOUBLE")) {
			return FIELD_TYPE_DOUBLE;
		} else if(type.equalsIgnoreCase("FILE")) {
			return FIELD_TYPE_FILE;
		} else if(type.equalsIgnoreCase("STRUCTURE")) {
			return FIELD_TYPE_STRUCTURE;
		} else {
			return 0;
		}
	}

}
