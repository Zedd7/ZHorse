package com.github.xibalba.zhorse.database;

public class PlayerRecord {
	
	private String uuid;
	private String name;
	private String language;
	private Integer favorite;
	private Boolean displayExactStats;
	
	public PlayerRecord(String uuid, String name, String language, Integer favorite, Boolean displayExactStats) {
		this.uuid = uuid;
		this.name = name;
		this.language = language;
		this.favorite = favorite;
		this.displayExactStats = displayExactStats;
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
	
	public Boolean displayExactStats() {
		return displayExactStats;
	}

}
