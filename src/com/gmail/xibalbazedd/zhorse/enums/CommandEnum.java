package com.gmail.xibalbazedd.zhorse.enums;

import java.util.ArrayList;
import java.util.List;

public enum CommandEnum {
	
	ADMIN("admin", "com.gmail.xibalbazedd.zhorse.commands.CommandAdmin"),
	BUY("buy", "com.gmail.xibalbazedd.zhorse.commands.CommandBuy"),
	CLAIM("claim", "com.gmail.xibalbazedd.zhorse.commands.CommandClaim"),
	FREE("free", "com.gmail.xibalbazedd.zhorse.commands.CommandFree"),
	FRIEND("friend", "com.gmail.xibalbazedd.zhorse.commands.CommandFriend"),
	GIVE("give", "com.gmail.xibalbazedd.zhorse.commands.CommandGive"),
	HEAL("heal", "com.gmail.xibalbazedd.zhorse.commands.CommandHeal"),
	HELP("help", "com.gmail.xibalbazedd.zhorse.commands.CommandHelp"),
	HERE("here", "com.gmail.xibalbazedd.zhorse.commands.CommandHere"),
	INFO("info", "com.gmail.xibalbazedd.zhorse.commands.CommandInfo"),
	KILL("kill", "com.gmail.xibalbazedd.zhorse.commands.CommandKill"),
	LIST("list", "com.gmail.xibalbazedd.zhorse.commands.CommandList"),
	LOCK("lock", "com.gmail.xibalbazedd.zhorse.commands.CommandLock"),
	PROTECT("protect", "com.gmail.xibalbazedd.zhorse.commands.CommandProtect"),
	RENAME("rename", "com.gmail.xibalbazedd.zhorse.commands.CommandRename"),
	RELOAD("reload", "com.gmail.xibalbazedd.zhorse.commands.CommandReload"),
	SELL("sell", "com.gmail.xibalbazedd.zhorse.commands.CommandSell"),
	SETTINGS("settings", "com.gmail.xibalbazedd.zhorse.commands.CommandSettings"),
	SHARE("share", "com.gmail.xibalbazedd.zhorse.commands.CommandShare"),
	SPAWN("spawn", "com.gmail.xibalbazedd.zhorse.commands.CommandSpawn"),
	TAME("tame", "com.gmail.xibalbazedd.zhorse.commands.CommandTame"),
	TP("tp", "com.gmail.xibalbazedd.zhorse.commands.CommandTp");
	
	private final String name;
	private final String classPath;
	
	CommandEnum(final String name, final String classPath) {
		this.name = name;
		this.classPath = classPath;
	}
	
	public String getName() {
		return name;
	}
	
	public String getClassPath() {
		return classPath;
	}
	
	public static List<String> getCommandNameList() {
		List<String> commandNameList = new ArrayList<String>();
		for (CommandEnum command : CommandEnum.values()) {
			commandNameList.add(command.getName());
		}
		return commandNameList;
	}
}
