package com.gmail.xibalbazedd.zhorse.utils;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;

public class MessageConfig {
	
	private LocaleEnum index;
	
	private int arithmeticPrecision = 0;
	private int spaceCount = 0;
	private boolean usePercentage = false;
	
	private Number amount;
	private String currencySymbol;
	private String horseName;
	private String horseID;
	private String language;
	private Number max;
	private String permission;
	private String playerName;
	
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
	
	public int getArithmeticPrecision() {
		return arithmeticPrecision;
	}

	public void setArithmeticPrecision(int arithmeticPrecision) {
		this.arithmeticPrecision = arithmeticPrecision;
	}
	
	public int getSpaceCount() {
		return spaceCount;
	}

	public void setSpaceCount(int spaceCount) {
		this.spaceCount = spaceCount;
	}
	
	public boolean shouldUsePercentage() {
		return usePercentage;
	}

	public void setUsePercentage(boolean usePercentage) {
		this.usePercentage = usePercentage;
	}

	public String getAmount() {
		return getFlagContent(amount);
	}

	public void setAmount(Number amount) {
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
	
	public void setHorseID(Integer horseID) {
		setHorseID(Integer.toString(horseID));
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

	public void setMax(Number max) {
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

	public String getValue() {
		return getFlagContent(value);
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	private String getFlagContent(Object token) {
		String flagContent = "";
		if (token != null) {
			if (token instanceof Number) {
				NumberFormat numberFormatter = usePercentage ? NumberFormat.getPercentInstance(Locale.US) : NumberFormat.getNumberInstance(Locale.US);
				numberFormatter.setMaximumFractionDigits(arithmeticPrecision);
				numberFormatter.setMinimumFractionDigits(arithmeticPrecision);
				numberFormatter.setRoundingMode(RoundingMode.HALF_UP);
				/*if (token instanceof Double) {
					String format = "%." + arithmeticPrecision + "f";
					flagContent = String.format(Locale.US, format, token);
				}*/
				flagContent = numberFormatter.format(token);
			}
			else {
				flagContent = token.toString();
			}
		}
		return flagContent;
	}

}
