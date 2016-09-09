package eu.reborn_minecraft.zhorse.enums;

public enum DatabaseEnum {
	
	MYSQL("MySQL", false),
	SQLITE("SQLite", false),
	YAML("YAML", true);
	
	private final String name;
	private final boolean importOnly; // false if can be set in config
	
	private DatabaseEnum(final String name, final boolean importOnly) {
		this.name= name;
		this.importOnly = importOnly;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isImportOnly() {
		return importOnly;
	}

}
