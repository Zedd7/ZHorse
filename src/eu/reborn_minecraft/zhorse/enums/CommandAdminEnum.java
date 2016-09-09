package eu.reborn_minecraft.zhorse.enums;

public enum CommandAdminEnum {
	
	CLEAR("clear", "eu.reborn_minecraft.zhorse.commands.ZAdmin"),
	IMPORT("import", "eu.reborn_minecraft.zhorse.commands.ZAdmin");
	
	private final String name;
	private final String classPath;
	
	CommandAdminEnum(final String name, final String classPath) {
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