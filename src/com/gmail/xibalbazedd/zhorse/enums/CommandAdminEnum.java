package com.gmail.xibalbazedd.zhorse.enums;

public enum CommandAdminEnum {
	
	CLEAR("clear", "com.gmail.xibalbazedd.zhorse.commands.CommandAdmin"),
	IMPORT("import", "com.gmail.xibalbazedd.zhorse.commands.CommandAdmin");
	
	private final String name;
	private final String classPath;
	
	CommandAdminEnum(final String name, final String classPath) {
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