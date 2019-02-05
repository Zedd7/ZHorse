package com.github.zedd7.zhorse.enums;

import java.util.ArrayList;
import java.util.List;

public enum FriendSubCommandEnum {

	ADD("add"),
	LIST("list"),
	REMOVE("remove");

	private String name;

	FriendSubCommandEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static List<String> getNameList() {
		List<String> subCommandNameList = new ArrayList<String>();
		for (FriendSubCommandEnum subCommand : FriendSubCommandEnum.values()) {
			subCommandNameList.add(subCommand.getName());
		}
		return subCommandNameList;
	}

}
