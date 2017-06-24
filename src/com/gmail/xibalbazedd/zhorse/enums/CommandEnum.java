package com.gmail.xibalbazedd.zhorse.enums;

import java.util.ArrayList;
import java.util.List;

public enum CommandEnum {
	
	ADMIN,
	BUY,
	CLAIM,
	FREE,
	FRIEND,
	GIVE,
	HEAL,
	HELP,
	HERE,
	INFO,
	KILL,
	LIST,
	LOCK,
	PROTECT,
	RELOAD,
	RENAME,
	REZ,
	SELL,
	SETTINGS,
	SHARE,
	SPAWN,
	STABLE,
	TAME,
	TP;
	
	private static final String PACKAGE = "com.gmail.xibalbazedd.zhorse.commands";
	private static final String CLASS_NAME_FORMAT = "Command%s";
	
	public String getClassPath() {
		return PACKAGE + "." + String.format(CLASS_NAME_FORMAT, name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase());
	}
	
	public static List<String> getCommandNameList() {
		List<String> commandNameList = new ArrayList<String>();
		for (CommandEnum command : CommandEnum.values()) {
			commandNameList.add(command.name().toLowerCase());
		}
		return commandNameList;
	}
}
