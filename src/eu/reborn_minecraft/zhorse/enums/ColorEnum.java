package eu.reborn_minecraft.zhorse.enums;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public enum ColorEnum {
	aqua(ChatColor.AQUA, "<b>", "^b", "§b", "<aqua>"),
	black(ChatColor.BLACK, "<0>", "^0", "§0", "<black>"),
	blue(ChatColor.BLUE, "<9>", "^9", "§9", "<blue>"),
	bold(ChatColor.BOLD, "<l>", "^l", "§l", "<bold>"),
	dark_aqua(ChatColor.DARK_AQUA, "<3>", "^3", "§3", "<dark_aqua>", "<darkaqua>"),
	darkBlue(ChatColor.DARK_BLUE, "<1>", "^1", "§1", "<dark_blue>", "<darkblue>"),
	darkGray(ChatColor.DARK_GRAY, "<8>", "^8", "§8", "<dark_gray>", "<darkgray>"),
	darkGreen(ChatColor.DARK_GREEN, "<2>", "^2", "§2", "<dark_green>", "<darkgreen>"),
	darkPurple(ChatColor.DARK_PURPLE, "<5>", "^5", "§5", "<dark_purple>", "<darkpurple>"),
	darkRed(ChatColor.DARK_RED, "<4>", "^4", "§4", "<dark_red>", "<darkred>"),
	gold(ChatColor.GOLD, "<6>", "^6", "§6", "<gold>"),
	gray(ChatColor.GRAY, "<7>", "^7", "§7", "<gray>", "<grey>"),
	green(ChatColor.GREEN, "<a>", "^a", "§a", "<green>"),
	italic(ChatColor.ITALIC, "<o>", "^o", "§o", "<italic>"),
	lightPurple(ChatColor.LIGHT_PURPLE, "<d>", "^d", "§d", "<light_purple>", "<lightpurple>"),
	magic(ChatColor.MAGIC, "<k>", "^k", "§k", "<magic>"),
	red(ChatColor.RED, "<c>", "^c", "§c", "<red>"),
	reset(ChatColor.RESET, "<r>", "^r", "§r", "<reset>"),
	strikethrough(ChatColor.STRIKETHROUGH, "<m>", "^m", "§m", "<strikethrough>"),
	underline(ChatColor.UNDERLINE, "<n>", "^n", "§n", "<underline>"),
	white(ChatColor.WHITE, "<f>", "^f", "§f", "<white>"),
	yellow(ChatColor.YELLOW, "<e>", "^e", "§e", "<yellow>");
	
	private ChatColor color;
	private String code1;
	private String code2;
	private String code3;
	private String code4;
	private String code5;

	ColorEnum(ChatColor color, String code1, String code2, String code3, String code4) {
		this(color, code1, code2, code3, code4, null);
	}
	
	ColorEnum(ChatColor color, String code1, String code2, String code3, String code4, String code5) {
		this.color = color;
		this.code1 = code1;
		this.code2 = code2;
		this.code3 = code3;
		this.code4 = code4;
		this.code5 = code5;
	}
	
	public List<String> getCodes() {
		List<String> codes = new ArrayList<String>();
		codes.add(code1);
		codes.add(code2);
		codes.add(code3);
		codes.add(code4);
		if (code5 != null) {
			codes.add(code5);
		}
		return codes;
	}
	
	public ChatColor getColor() {
		return color;
	}
}
