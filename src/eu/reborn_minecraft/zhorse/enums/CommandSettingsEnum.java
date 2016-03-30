package eu.reborn_minecraft.zhorse.enums;

public enum CommandSettingsEnum {
	favorite("eu.reborn_minecraft.zhorse.commands.ZSettings", "favorite"),
	language("eu.reborn_minecraft.zhorse.commands.ZSettings", "language");
	
	private String classPath;
	private String name;
	
	CommandSettingsEnum(String classPath, String name) {
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
