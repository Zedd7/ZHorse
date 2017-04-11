package com.gmail.xibalbazedd.zhorse.enums;

public enum CommandAdminEnum {
	
	CLEAR("clear"),
	IMPORT("import");
	
	private final String name;
	
	CommandAdminEnum(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}