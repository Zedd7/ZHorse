package com.gmail.xibalbazedd.zhorse.utils;

import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;

public class MessageConfig {
	
	private LocaleEnum index;
	
	private Integer amount;
	private String currencySymbol;
	private String horseName;
	private String horseID;
	private String language;
	private Integer max;
	private String permission;
	private String playerName;
	private Integer spaceCount;
	private String value;
	
	public MessageConfig(LocaleEnum index) {
		this.index = index;
	}

	public String getIndex() {
		return index.getIndex();
	}

	public void setIndex(LocaleEnum index) {
		this.index = index;
	}

	public String getAmount() {
		return getFlagContent(amount);
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getCurrencySymbol() {
		return getFlagContent(currencySymbol);
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public String getHorseName() {
		return getFlagContent(horseName);
	}

	public void setHorseName(String horseName) {
		this.horseName = horseName;
	}

	public String getHorseID() {
		return getFlagContent(horseID);
	}

	public void setHorseID(String horseID) {
		this.horseID = horseID;
	}

	public String getLanguage() {
		return getFlagContent(language);
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getMax() {
		return getFlagContent(max);
	}

	public void setMax(Integer max) {
		this.max = max;
	}
	
	public String getPermission() {
		return getFlagContent(permission);
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getPlayerName() {
		return getFlagContent(playerName);
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getSpaceCount() {
		return spaceCount;
	}

	public void setSpaceCount(Integer spaceCount) {
		this.spaceCount = spaceCount;
	}

	public String getValue() {
		return getFlagContent(value);
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	private String getFlagContent(Object token) {
		return token != null ? token.toString() : "";
	}

}
