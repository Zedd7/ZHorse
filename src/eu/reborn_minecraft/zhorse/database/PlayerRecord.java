package eu.reborn_minecraft.zhorse.database;

public class PlayerRecord {
	
	private String uuid;
	private String name;
	private String language;
	private Integer favorite;
	
	public PlayerRecord(String uuid, String name, String language, Integer favorite) {
		this.uuid = uuid;
		this.name = name;
		this.language = language;
		this.favorite = favorite;
	}

	public String getUUID() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getLanguage() {
		return language;
	}

	public Integer getFavorite() {
		return favorite;
	}

}
