package com.gmail.xibalbazedd.zhorse.enums;

public enum CommandFriendEnum {
	
	ADD("add"),
	LIST("list"),
	REMOVE("remove");
	
	private final String name;
	
	CommandFriendEnum(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
