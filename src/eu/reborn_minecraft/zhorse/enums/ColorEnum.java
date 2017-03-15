package eu.reborn_minecraft.zhorse.enums;

import org.bukkit.ChatColor;

public enum ColorEnum {
	
	aqua(ChatColor.AQUA, "&b", "<b>", "<aqua>"),
	black(ChatColor.BLACK, "&0", "<0>", "<black>"),
	blue(ChatColor.BLUE, "&9", "<9>", "<blue>"),
	bold(ChatColor.BOLD, "&l", "<l>", "<bold>"),
	dark_aqua(ChatColor.DARK_AQUA, "&3", "<3>", "<dark_aqua>", "<darkaqua>"),
	darkBlue(ChatColor.DARK_BLUE, "&1", "<1>", "<dark_blue>", "<darkblue>"),
	darkGray(ChatColor.DARK_GRAY, "&8", "<8>", "<dark_gray>", "<darkgray>"),
	darkGreen(ChatColor.DARK_GREEN, "&2", "<2>", "<dark_green>", "<darkgreen>"),
	darkPurple(ChatColor.DARK_PURPLE, "&5", "<5>", "<dark_purple>", "<darkpurple>"),
	darkRed(ChatColor.DARK_RED, "&4", "<4>", "<dark_red>", "<darkred>"),
	gold(ChatColor.GOLD, "&6", "<6>", "<gold>"),
	gray(ChatColor.GRAY, "&7", "<7>", "<gray>", "<grey>"),
	green(ChatColor.GREEN, "&a", "<a>", "<green>"),
	italic(ChatColor.ITALIC, "&o", "<o>", "<italic>"),
	lightPurple(ChatColor.LIGHT_PURPLE, "&d", "<d>", "<light_purple>", "<lightpurple>"),
	magic(ChatColor.MAGIC, "&k", "<k>", "<magic>"),
	red(ChatColor.RED, "&c", "<c>", "<red>"),
	reset(ChatColor.RESET, "&r", "<r>", "<reset>"),
	strikethrough(ChatColor.STRIKETHROUGH, "&m", "<m>", "<strikethrough>"),
	underline(ChatColor.UNDERLINE, "&n", "<n>", "<underline>"),
	white(ChatColor.WHITE, "&f", "<f>", "<white>"),
	yellow(ChatColor.YELLOW, "&e", "<e>", "<yellow>");
	
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
