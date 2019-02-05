package com.github.zedd7.zhorse.enums;

import java.util.ArrayList;
import java.util.List;

public enum StableSubCommandEnum {

	GO("go"),
	SET("set"),
	UNSET("unset");

	private String name;

	StableSubCommandEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static List<String> getNameList() {
		List<String> subCommandNameList = new ArrayList<String>();
		for (StableSubCommandEnum subCommand : StableSubCommandEnum.values()) {
			subCommandNameList.add(subCommand.getName());
		}
		return subCommandNameList;
	}

}