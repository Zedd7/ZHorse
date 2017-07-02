package com.github.xibalba.zhorse.utils;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.xibalba.zhorse.enums.LocaleEnum;

public class MessageConfig {
	
	private LocaleEnum index;
	
	private int arithmeticPrecision = 0;
	private int spaceCount = 0;
	private boolean usePercentage = false;
	
	private List<Number> amountList = new ArrayList<>();
	private List<String> currencySymbolList = new ArrayList<>();
	private List<String> horseNameList = new ArrayList<>();
	private List<String> horseIDList = new ArrayList<>();
	private List<String> languageList = new ArrayList<>();
	private List<Number> maxList = new ArrayList<>();
	private List<String> permissionList = new ArrayList<>();
	private List<String> playerNameList = new ArrayList<>();	
	private List<String> valueList = new ArrayList<>();
	
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

	public List<String> getAmountList() {
		return getFlagContentList(amountList);
	}

	public void setAmount(Number amount) {
		amountList.add(amount);
	}

	public List<String> getCurrencySymbolList() {
		return getFlagContentList(currencySymbolList);
	}

	public void setCurrencySymbol(String currencySymbol) {
		currencySymbolList.add(currencySymbol);
	}

	public List<String> getHorseNameList() {
		return getFlagContentList(horseNameList);
	}

	public void setHorseName(String horseName) {
		horseNameList.add(horseName);
	}

	public List<String> getHorseIDList() {
		return getFlagContentList(horseIDList);
	}
	
	public void setHorseID(Integer horseID) {
		setHorseID(Integer.toString(horseID));
	}

	public void setHorseID(String horseID) {
		horseIDList.add(horseID);
	}

	public List<String> getLanguageList() {
		return getFlagContentList(languageList);
	}

	public void setLanguage(String language) {
		languageList.add(language);
	}

	public List<String> getMaxList() {
		return getFlagContentList(maxList);
	}

	public void setMax(Number max) {
		maxList.add(max);
	}
	
	public List<String> getPermissionList() {
		return getFlagContentList(permissionList);
	}

	public void setPermission(String permission) {
		permissionList.add(permission);
	}

	public List<String> getPlayerNameList() {
		return getFlagContentList(playerNameList);
	}

	public void setPlayerName(String playerName) {
		playerNameList.add(playerName);
	}

	public List<String> getValueList() {
		return getFlagContentList(valueList);
	}

	public void setValue(String value) {
		valueList.add(value);
	}
	
	private <T> List<String> getFlagContentList(List<T> tokenList) {
		List<String> flagContentList = new ArrayList<>();
		for (T token : tokenList) {
			String flagContent;
			if (token instanceof Number) {
				NumberFormat numberFormatter = usePercentage ? NumberFormat.getPercentInstance(Locale.US) : NumberFormat.getNumberInstance(Locale.US);
				numberFormatter.setMaximumFractionDigits(arithmeticPrecision);
				numberFormatter.setMinimumFractionDigits(arithmeticPrecision);
				numberFormatter.setRoundingMode(RoundingMode.HALF_UP);
				flagContent = numberFormatter.format(token);
			}
			else {
				flagContent = token.toString();
			}
			flagContentList.add(flagContent);
		}
		return flagContentList;
	}

}
