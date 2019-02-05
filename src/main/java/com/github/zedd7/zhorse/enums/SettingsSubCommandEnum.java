package com.github.zedd7.zhorse.enums;

import java.util.ArrayList;
import java.util.List;

public enum SettingsSubCommandEnum {

	FAVORITE("favorite"),
	LANGUAGE("language"),
	STATS("stats"),
	SWAP("swap");

	private String name;

	SettingsSubCommandEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static List<String> getNameList() {
		List<String> subCommandNameList = new ArrayList<String>();
		for (SettingsSubCommandEnum subCommand : SettingsSubCommandEnum.values()) {
			subCommandNameList.add(subCommand.getName());
		}
		return subCommandNameList;
	}

}
