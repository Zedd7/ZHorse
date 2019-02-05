package com.github.zedd7.zhorse.enums;

import java.util.ArrayList;
import java.util.List;

public enum CommandEnum {

	ADMIN("admin", true),
	BUY("buy", true),
	CLAIM("claim", true),
	EDIT("edit", true),
	FREE("free", true),
	FRIEND("friend", true),
	GIVE("give", true),
	HEAL("heal", true),
	HELP("help", true),
	HERE("here", true),
	INFO("info", true),
	KILL("kill", true),
	LIST("list", true),
	LOCK("lock", true),
	PROTECT("protect", true),
	RELOAD("reload", false),
	RENAME("rename", true),
	REZ("rez", true),
	SELL("sell", true),
	SETTINGS("settings", true),
	SHARE("share", true),
	SPAWN("spawn", true),
	STABLE("stable", true),
	TAME("tame", true),
	TP("tp", true);

	private static final String PACKAGE = "com.github.zedd7.zhorse.commands";
	private static final String CLASS_NAME_FORMAT = "Command%s";

	private String name;
	private boolean isPlayerOnly;

	CommandEnum(String name, boolean isPlayerOnly) {
		this.name = name;
		this.isPlayerOnly = isPlayerOnly;
	}

	public String getName() {
		return name;
	}

	public boolean isPlayerOnly() {
		return isPlayerOnly;
	}

	public String getClassPath() {
		return PACKAGE + "." + String.format(CLASS_NAME_FORMAT, name.substring(0, 1).toUpperCase() + name.substring(1));
	}

	public static List<String> getNameList() {
		List<String> commandNameList = new ArrayList<String>();
		for (CommandEnum command : CommandEnum.values()) {
			commandNameList.add(command.getName());
		}
		return commandNameList;
	}
}
