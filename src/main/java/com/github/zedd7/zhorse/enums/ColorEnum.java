package com.github.zedd7.zhorse.enums;

import org.bukkit.ChatColor;

public enum ColorEnum {

	AQUA(ChatColor.AQUA, "&b", "<b>", "<aqua>"),
	BLACK(ChatColor.BLACK, "&0", "<0>", "<black>"),
	BLUE(ChatColor.BLUE, "&9", "<9>", "<blue>"),
	BOLD(ChatColor.BOLD, "&l", "<l>", "<bold>"),
	DARK_AQUA(ChatColor.DARK_AQUA, "&3", "<3>", "<dark_aqua>", "<darkaqua>"),
	DARK_BLUE(ChatColor.DARK_BLUE, "&1", "<1>", "<dark_blue>", "<darkblue>"),
	DARK_GRAY(ChatColor.DARK_GRAY, "&8", "<8>", "<dark_gray>", "<darkgray>"),
	DARK_GREEN(ChatColor.DARK_GREEN, "&2", "<2>", "<dark_green>", "<darkgreen>"),
	DARK_PURPLE(ChatColor.DARK_PURPLE, "&5", "<5>", "<dark_purple>", "<darkpurple>"),
	DARK_RED(ChatColor.DARK_RED, "&4", "<4>", "<dark_red>", "<darkred>"),
	GOLD(ChatColor.GOLD, "&6", "<6>", "<gold>"),
	GRAY(ChatColor.GRAY, "&7", "<7>", "<gray>", "<grey>"),
	GREEN(ChatColor.GREEN, "&a", "<a>", "<green>"),
	ITALIC(ChatColor.ITALIC, "&o", "<o>", "<italic>"),
	LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, "&d", "<d>", "<light_purple>", "<lightpurple>"),
	MAGIC(ChatColor.MAGIC, "&k", "<k>", "<magic>"),
	RED(ChatColor.RED, "&c", "<c>", "<red>"),
	RESET(ChatColor.RESET, "&r", "<r>", "<reset>"),
	STRIKETHROUGH(ChatColor.STRIKETHROUGH, "&m", "<m>", "<strikethrough>"),
	UNDERLINE(ChatColor.UNDERLINE, "&n", "<n>", "<underline>"),
	WHITE(ChatColor.WHITE, "&f", "<f>", "<white>"),
	YELLOW(ChatColor.YELLOW, "&e", "<e>", "<yellow>");

	private ChatColor color;
	private String[] codeArray;

	ColorEnum(ChatColor color, String... codeArray) {
		this.color = color;
		this.codeArray = codeArray;
	}

	public ChatColor getColor() {
		return color;
	}

	public String[] getCodeArray() {
		return codeArray;
	}
}
