package eu.reborn_minecraft.zhorse.enums;

public enum KeyWordEnum {
	adminSuffix(".admin"),
	freeSuffix(".free"),
	zhPrefix("zh.");
	
	private String value;
	
	KeyWordEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
