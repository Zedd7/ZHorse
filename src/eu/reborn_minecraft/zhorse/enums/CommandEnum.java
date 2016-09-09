package eu.reborn_minecraft.zhorse.enums;

import java.util.ArrayList;
import java.util.List;

public enum CommandEnum {
	ADMIN("admin", "eu.reborn_minecraft.zhorse.commands.ZAdmin"),
	CLAIM("claim", "eu.reborn_minecraft.zhorse.commands.ZClaim"),
	FREE("free", "eu.reborn_minecraft.zhorse.commands.ZFree"),
	GIVE("give", "eu.reborn_minecraft.zhorse.commands.ZGive"),
	HEAL("heal", "eu.reborn_minecraft.zhorse.commands.ZHeal"),
	HELP("help", "eu.reborn_minecraft.zhorse.commands.ZHelp"),
	HERE("here", "eu.reborn_minecraft.zhorse.commands.ZHere"),
	INFO("info", "eu.reborn_minecraft.zhorse.commands.ZInfo"),
	KILL("kill", "eu.reborn_minecraft.zhorse.commands.ZKill"),
	LIST("list", "eu.reborn_minecraft.zhorse.commands.ZList"),
	LOCK("lock", "eu.reborn_minecraft.zhorse.commands.ZLock"),
	PROTECT("protect", "eu.reborn_minecraft.zhorse.commands.ZProtect"),
	RENAME("rename", "eu.reborn_minecraft.zhorse.commands.ZRename"),
	RELOAD("reload", "eu.reborn_minecraft.zhorse.commands.ZReload"),
	SETTINGS("settings", "eu.reborn_minecraft.zhorse.commands.ZSettings"),
	SHARE("share", "eu.reborn_minecraft.zhorse.commands.ZShare"),
	SPAWN("spawn", "eu.reborn_minecraft.zhorse.commands.ZSpawn"),
	TAME("tame", "eu.reborn_minecraft.zhorse.commands.ZTame"),
	TP("tp", "eu.reborn_minecraft.zhorse.commands.ZTp");
	
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
