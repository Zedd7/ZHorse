package eu.reborn_minecraft.zhorse.enums;

public enum CommandAdminEnum {
	clear("eu.reborn_minecraft.zhorse.commands.ZAdmin", "clear");
	
	private String classPath;
	private String name;
	
	CommandAdminEnum(String classPath, String name) {
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