package com.gmail.xibalbazedd.zhorse.enums;

public enum CommandFriendEnum {
	
	ADD("add", "com.gmail.xibalbazedd.zhorse.commands.CommandFriend"),
	LIST("list", "com.gmail.xibalbazedd.zhorse.commands.CommandFriend"),
	REMOVE("remove", "com.gmail.xibalbazedd.zhorse.commands.CommandFriend");
	
	private final String name;
	private final String classPath;
	
	CommandFriendEnum(final String name, final String classPath) {
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
