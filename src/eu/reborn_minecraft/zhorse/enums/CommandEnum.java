package eu.reborn_minecraft.zhorse.enums;

import java.util.ArrayList;
import java.util.List;

public enum CommandEnum {
	ADMIN("admin", "eu.reborn_minecraft.zhorse.commands.CommandAdmin"),
	CLAIM("claim", "eu.reborn_minecraft.zhorse.commands.CommandClaim"),
	FREE("free", "eu.reborn_minecraft.zhorse.commands.CommandFree"),
	FRIEND("friend", "eu.reborn_minecraft.zhorse.commands.CommandFriend"),
	GIVE("give", "eu.reborn_minecraft.zhorse.commands.CommandGive"),
	HEAL("heal", "eu.reborn_minecraft.zhorse.commands.CommandHeal"),
	HELP("help", "eu.reborn_minecraft.zhorse.commands.CommandHelp"),
	HERE("here", "eu.reborn_minecraft.zhorse.commands.CommandHere"),
	INFO("info", "eu.reborn_minecraft.zhorse.commands.CommandInfo"),
	KILL("kill", "eu.reborn_minecraft.zhorse.commands.CommandKill"),
	LIST("list", "eu.reborn_minecraft.zhorse.commands.CommandList"),
	LOCK("lock", "eu.reborn_minecraft.zhorse.commands.CommandLock"),
	PROTECT("protect", "eu.reborn_minecraft.zhorse.commands.CommandProtect"),
	RENAME("rename", "eu.reborn_minecraft.zhorse.commands.CommandRename"),
	RELOAD("reload", "eu.reborn_minecraft.zhorse.commands.CommandReload"),
	SETTINGS("settings", "eu.reborn_minecraft.zhorse.commands.CommandSettings"),
	SHARE("share", "eu.reborn_minecraft.zhorse.commands.CommandShare"),
	SPAWN("spawn", "eu.reborn_minecraft.zhorse.commands.CommandSpawn"),
	TAME("tame", "eu.reborn_minecraft.zhorse.commands.CommandTame"),
	TP("tp", "eu.reborn_minecraft.zhorse.commands.CommandTp");
	
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
