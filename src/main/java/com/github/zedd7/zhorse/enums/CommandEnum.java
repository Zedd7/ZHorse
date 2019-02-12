package com.github.zedd7.zhorse.enums;

import java.util.ArrayList;
import java.util.List;

public enum CommandEnum {

	ADMIN("admin", true,true),
	BUY("buy", false,true),
	CLAIM("claim", false,true),
	EDIT("edit", false,true),
	FREE("free", false,true),
	FRIEND("friend", true,true),
	GIVE("give", false,true),
	HEAL("heal", false,true),
	HELP("help",false,false),
	HERE("here", false,true),
	INFO("info", false,true),
	KILL("kill", false,true),
	LIST("list", false,true),
	LOCK("lock", false,true),
	PROTECT("protect", false,true),
	RELOAD("reload",false,false),
	RENAME("rename", false,true),
	REZ("rez", false,true),
	SELL("sell", false,true),
	SETTINGS("settings", true,true),
	SHARE("share", false,true),
	SPAWN("spawn", false,true),
	STABLE("stable", true,true),
	TAME("tame", false,true),
	TP("tp", false,true);

	private static final String COMMANDS_PACKAGE = "com.github.zedd7.zhorse.commands";
	private static final String ENUMS_PACKAGE = "com.github.zedd7.zhorse.enums";
	private static final String CLASS_NAME_FORMAT = "Command%s";
	private static final String SUB_COMMANDS_ENUM_FORMAT = "%sSubCommandEnum";

	private String name;
	private boolean isComplex;
	private boolean isPlayerOnly;

	CommandEnum(String name, boolean isComplex, boolean isPlayerOnly) {
		this.name = name;
		this.isComplex = isComplex;
		this.isPlayerOnly = isPlayerOnly;
	}

	public String getName() {
		return name;
	}

	public boolean isComplex() {
		return isComplex;
	}

	public boolean isPlayerOnly() {
		return isPlayerOnly;
	}

	public static boolean isComplex(String name) {
		return CommandEnum.valueOf(name).isComplex();
	}

	public static boolean isPlayerOnly(String name) {
		return CommandEnum.valueOf(name).isPlayerOnly();
	}

	public static CommandEnum getCommand(String name) {
		CommandEnum command = null;
		try {
			command = CommandEnum.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {}
		return command;
	}

	public static List<String> getNameList() {
		List<String> commandNameList = new ArrayList<String>();
		for (CommandEnum command : CommandEnum.values()) {
			commandNameList.add(command.getName());
		}
		return commandNameList;
	}

	public static String getCommandClassPath(String name) {
		return COMMANDS_PACKAGE + "." + String.format(CLASS_NAME_FORMAT, name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
	}

	public static String getSubCommandEnumPath(String name) {
		return ENUMS_PACKAGE + "." + String.format(SUB_COMMANDS_ENUM_FORMAT, name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
	}

}
