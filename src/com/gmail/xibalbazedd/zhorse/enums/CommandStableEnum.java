package com.gmail.xibalbazedd.zhorse.enums;

public enum CommandStableEnum {
	
	GO("go"),
	SET("set"),
	UNSET("unset");
	
	private final String name;
	
	CommandStableEnum(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}