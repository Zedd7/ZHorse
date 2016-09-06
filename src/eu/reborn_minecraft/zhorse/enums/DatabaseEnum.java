package eu.reborn_minecraft.zhorse.enums;

public enum DatabaseEnum {
	
	MYSQL("MySQL"),
	SQLITE("SQLite");
	
	private String name;
	
	private DatabaseEnum(String name) {
		this.name= name;
	}
	
	public String getName() {
		return name;
	}

}
