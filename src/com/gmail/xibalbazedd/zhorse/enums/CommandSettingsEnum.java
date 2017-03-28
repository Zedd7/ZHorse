package com.gmail.xibalbazedd.zhorse.enums;

public enum CommandSettingsEnum {
	
	FAVORITE("favorite", "com.gmail.xibalbazedd.zhorse.commands.CommandSettings"),
	LANGUAGE("language", "com.gmail.xibalbazedd.zhorse.commands.CommandSettings");
	
	private final String name;
	private final String classPath;
	
	CommandSettingsEnum(final String name, final String classPath) {
		this.name = name;
		this.classPath = classPath;
	}
	
	public String getName() {
		return name;
	}
	
	public String getClassPath() {
		return classPath;
	}
}
