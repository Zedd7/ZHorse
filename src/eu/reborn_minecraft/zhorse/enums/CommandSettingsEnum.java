package eu.reborn_minecraft.zhorse.enums;

public enum CommandSettingsEnum {
	
	FAVORITE("favorite", "eu.reborn_minecraft.zhorse.commands.ZSettings"),
	LANGUAGE("language", "eu.reborn_minecraft.zhorse.commands.ZSettings");
	
	private final String name;
	private final String classPath;
	
	CommandSettingsEnum(final String name, final String classPath) {
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
