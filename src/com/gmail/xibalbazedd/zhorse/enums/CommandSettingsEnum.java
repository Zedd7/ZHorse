package com.gmail.xibalbazedd.zhorse.enums;

public enum CommandSettingsEnum {
	
	FAVORITE("favorite"),
	LANGUAGE("language"),
	STATS("stats"),
	SWAP("swap");
	
	private final String name;
	
	CommandSettingsEnum(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
