package eu.reborn_minecraft.zhorse.enums;

public enum CommandFriendEnum {
	
	ADD("add", "eu.reborn_minecraft.zhorse.commands.CommandFriend"),
	LIST("list", "eu.reborn_minecraft.zhorse.commands.CommandFriend"),
	REMOVE("remove", "eu.reborn_minecraft.zhorse.commands.CommandFriend");
	
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
