package eu.reborn_minecraft.zhorse.enums;

public enum CommandEnum {
	/* main commands */
	admin("eu.reborn_minecraft.zhorse.commands.ZAdmin", "admin"),
	claim("eu.reborn_minecraft.zhorse.commands.ZClaim", "claim"),
	free("eu.reborn_minecraft.zhorse.commands.ZFree", "free"),
	give("eu.reborn_minecraft.zhorse.commands.ZGive", "give"),
	heal("eu.reborn_minecraft.zhorse.commands.ZHeal", "heal"),
	help("eu.reborn_minecraft.zhorse.commands.ZHelp", "help"),
	here("eu.reborn_minecraft.zhorse.commands.ZHere", "here"),
	info("eu.reborn_minecraft.zhorse.commands.ZInfo", "info"),
	kill("eu.reborn_minecraft.zhorse.commands.ZKill", "kill"),
	list("eu.reborn_minecraft.zhorse.commands.ZList", "list"),
	lock("eu.reborn_minecraft.zhorse.commands.ZLock", "lock"),
	rename("eu.reborn_minecraft.zhorse.commands.ZRename", "rename"),
	protect("eu.reborn_minecraft.zhorse.commands.ZProtect", "protect"),
	reload("eu.reborn_minecraft.zhorse.commands.ZReload", "reload"),
	settings("eu.reborn_minecraft.zhorse.commands.ZSettings", "settings"),
	share("eu.reborn_minecraft.zhorse.commands.ZShare", "share"),
	tame("eu.reborn_minecraft.zhorse.commands.ZTame", "tame"),
	tp("eu.reborn_minecraft.zhorse.commands.ZTp", "tp"),
	
	/* admin commands */
	clear("eu.reborn_minecraft.zhorse.commands.ZSettings", "clear"),
	
	/* settings commands */
	favorite("eu.reborn_minecraft.zhorse.commands.ZSettings", "favorite"),
	language("eu.reborn_minecraft.zhorse.commands.ZSettings", "language");
	
	private String classPath;
	private String name;
	
	CommandEnum(String classPath, String name) {
		this.classPath = classPath;
		this.name = name;
	}
	
	public String getClassPath() {
		return classPath;
	}
	
	public String getName() {
		return name;
	}
}
