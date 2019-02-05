package com.github.zedd7.zhorse.enums;

import java.util.ArrayList;
import java.util.List;

public enum AdminSubCommandEnum {

	BURIAL("burial"),
	CLEAR("clear"),
	IMPORT("import");

	private String name;

	AdminSubCommandEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static List<String> getNameList() {
		List<String> subCommandNameList = new ArrayList<String>();
		for (AdminSubCommandEnum subCommand : AdminSubCommandEnum.values()) {
			subCommandNameList.add(subCommand.getName());
		}
		return subCommandNameList;
	}
}