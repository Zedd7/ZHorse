package eu.reborn_minecraft.zhorse.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

import eu.reborn_minecraft.zhorse.ZHorse;

public class MessageManager {	
	private static String amountFlag = "<amount>";
	private static String horseFlag = "<horse>";
	private static String langFlag = "<lang>";
	private static String maxFlag = "<max>";
	private static String permFlag = "<perm>";
	private static String playerFlag = "<player>";
	private static String userIDFlag = "<id>";
	private static String valueFlag = "<value>";
	
	private String amount = "";
	private String horse = "";
	private String lang = "";
	private String max = "";
	private String perm = "";
	private String player = "";
	private String space = "";
	private String userID = "";
	private String value = "";
	
	private static Map<ChatColor, String[]> colors;
	
	private ZHorse zh;
	
	public MessageManager(ZHorse zh) {
		this.zh = zh;
		initColors();
	}
	
	private void initColors() {
		colors = new HashMap<ChatColor, String[]>();
		String[] aqua = {"<b>", "^b", "§b", "<aqua>"};
		String[] black = {"<0>", "^0", "§0", "<black>"};
		String[] blue = {"<9>", "^9", "§9", "<blue>"};
		String[] bold = {"<l>", "^l", "§l", "<bold>"};
		String[] dark_aqua = {"<3>", "^3", "§3", "<dark_aqua>", "<darkaqua>"};
		String[] dark_blue = {"<1>", "^1", "§1", "<dark_blue>", "<darkblue>"};
		String[] dark_gray = {"<8>", "^8", "§8", "<dark_gray>", "<darkgray>"};
		String[] dark_green = {"<2>", "^2", "§2", "<dark_green>", "<darkgreen>"};
		String[] dark_purple = {"<5>", "^5", "§5", "<dark_purple>", "<darkpurple>"};
		String[] dark_red = {"<4>", "^4", "§4", "<dark_red>", "<darkred>"};
		String[] gold = {"<6>", "^6", "§6", "<gold>"};
		String[] gray = {"<7>", "^7", "§7", "<gray>", "<grey>"};
		String[] green = {"<a>", "^a", "§a", "<green>"};
		String[] italic = {"<o>", "^o", "§o", "<italic>"};
		String[] light_purple = {"<d>", "^d", "§d", "<light_purple>", "<lightpurple>"};
		String[] magic = {"<k>", "^k", "§k", "<magic>"};
		String[] red = {"<c>", "^c", "§c", "<red>"};
		String[] reset = {"<r>", "^r", "§r", "<reset>"};
		String[] strikethrough = {"<m>", "^m", "§m", "<strikethrough>"};
		String[] underline = {"<n>", "^n", "§n", "<underline>"};
		String[] white = {"<f>", "^f", "§f", "<white>"};
		String[] yellow = {"<e>", "^e", "§e", "<yellow>"};
		colors.put(ChatColor.AQUA, aqua);
		colors.put(ChatColor.BLACK, black);
		colors.put(ChatColor.BLUE, blue);
		colors.put(ChatColor.BOLD, bold);
		colors.put(ChatColor.DARK_AQUA, dark_aqua);
		colors.put(ChatColor.DARK_BLUE, dark_blue);
		colors.put(ChatColor.DARK_GRAY, dark_gray);
		colors.put(ChatColor.DARK_GREEN, dark_green);
		colors.put(ChatColor.DARK_PURPLE, dark_purple);
		colors.put(ChatColor.DARK_RED, dark_red);
		colors.put(ChatColor.GOLD, gold);
		colors.put(ChatColor.GRAY, gray);
		colors.put(ChatColor.GREEN, green);
		colors.put(ChatColor.ITALIC, italic);
		colors.put(ChatColor.LIGHT_PURPLE, light_purple);
		colors.put(ChatColor.MAGIC, magic);
		colors.put(ChatColor.RED, red);
		colors.put(ChatColor.RESET, reset);
		colors.put(ChatColor.STRIKETHROUGH, strikethrough);
		colors.put(ChatColor.UNDERLINE, underline);
		colors.put(ChatColor.WHITE, white);
		colors.put(ChatColor.YELLOW, yellow);
	}
	
	public ChatColor getColor(String color) {
		for (ChatColor cc : colors.keySet()) {
			for (String colorCode : colors.get(cc)) {
				if (colorCode.equalsIgnoreCase(color)) {
					return cc;
				}
			}
		}
		return null;
	}
	
	public boolean isColor(String color) {
		return (getColor(color) != null);
	}
	
	public String getCommandDescription(String language, String space, String command, boolean hidePrefix) {
		String costMessage = "";
		return getCommandDescriptionFull(language, space, command, costMessage, amount, false, hidePrefix);
	}
	
	public String getCommandDescriptionCost(String language, String space, String command, String costIndex, String cost, boolean hidePrefix) {
		String costMessage = space + zh.getLM().getEconomyAnswer(language, costIndex, hidePrefix);
		return getCommandDescriptionFull(language, space, command, costMessage, cost, false, hidePrefix);
	}
	
	public String getCommandUsage(String language, String index, String space, String command, boolean hidePrefix) {
		String usage = zh.getLM().getCommandUsage(language, command);
		return getHeaderFull(language, index, space, player, horse, userID, amount, usage, max, lang, hidePrefix);
	}
	
	public String getEconomyAmount(String language, String index, String amount) {
		return getEconomyFull(language, index, amount);
	}
	
	public String getHeader(String language, String index, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeader(String language, String index, String space, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderAmount(String language, String index, String amount, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderAmount(String language, String index, String space, String amount, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderAmountMax(String language, String index, String amount, String max, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderAmountMax(String language, String index, String space, String amount, String max, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderHorseUserID(String language, String index, String horse, String userID, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderHorseUserID(String language, String index, String space, String horse, String userID, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderLang(String language, String index, String lang, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderLang(String language, String index, String space, String lang, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderPlayer(String language, String index, String player, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderPlayer(String language, String index, String space, String player, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderPlayerAmount(String language, String index, String player, String amount, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderPlayerAmount(String language, String index, String space, String player, String amount, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderContent(String language, String index, String contentIndex, boolean hidePrefix) {
		String content = zh.getLM().getHeaderMessage(language, contentIndex);
		return getHeaderFull(language, index, space, player, horse, userID, amount, content, max, lang, hidePrefix);
	}
	
	public String getHeaderContent(String language, String index, String space, String contentIndex, boolean hidePrefix) {
		String content = zh.getLM().getHeaderMessage(language, contentIndex);
		return getHeaderFull(language, index, space, player, horse, userID, amount, content, max, lang, hidePrefix);
	}
	
	public String getHeaderValue(String language, String index, String value, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getHeaderValue(String language, String index, String space, String value, boolean hidePrefix) {
		return getHeaderFull(language, index, space, player, horse, userID, amount, value, max, lang, hidePrefix);
	}
	
	public String getInfo(String language, String index, String space, boolean hidePrefix) {
		return getInfoFull(language, index, space, player, horse, userID, amount, value, max, hidePrefix);
	}
	
	public String getInfoAmountMax(String language, String index, String space, String amount, String max, boolean hidePrefix) {
		return getInfoFull(language, index, space, player, horse, userID, amount, value, max, hidePrefix);
	}
	
	public String getInfoHorse(String language, String index, String space, String horse, boolean hidePrefix) {
		return getInfoFull(language, index, space, player, horse, userID, amount, value, max, hidePrefix);
	}
	
	public String getInfoPlayer(String language, String index, String space, String player, boolean hidePrefix) {
		return getInfoFull(language, index, space, player, horse, userID, amount, value, max, hidePrefix);
	}
	
	public String getInfoUserID(String language, String index, String space, String userID, boolean hidePrefix) {
		return getInfoFull(language, index, space, player, horse, userID, amount, value, max, hidePrefix);
	}
	
	public String getInfoValue(String language, String index, String space, String value, boolean hidePrefix) {
		return getInfoFull(language, index, space, player, horse, userID, amount, value, max, hidePrefix);
	}

	public String getMessage(String language, String index) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}
	
	public String getMessageAmount(String language, String index, String amount) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}
	
	public String getMessageHorse(String language, String index, String horse) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}	
	
	public String getMessageHorseValue(String language, String index, String horse, String value) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}
	
	public String getMessageLang(String language, String index, String lang) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}	
	
	public String getMessagePerm(String language, String index, String perm) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}
	
	public String getMessagePlayer(String language, String index, String player) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}
	
	public String getMessagePlayerAmount(String language, String index, String player, String amount) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}
	
	public String getMessagePlayerLang(String language, String index, String player, String lang) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}
	
	public String getMessagePlayerHorse(String language, String index, String player, String horse) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}
	
	public String getMessagePlayerPerm(String language, String index, String player, String perm) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}
	
	public String getMessagePlayerUserID(String language, String index, String player, String userID) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}
	
	public String getMessageUserID(String language, String index, String userID) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}
	
	public String getMessageValue(String language, String index, String value) {
		return getMessageFull(language, index, player, horse, userID, perm, amount, value, lang);
	}
	
	public String getSettingsCommandDescription(String language, String space, String command, boolean hidePrefix) {
		String costMessage = "";
		return getCommandDescriptionFull(language, space, command, costMessage, amount, true, hidePrefix);
	}
	
	public String getSettingsCommandDescriptionCost(String language, String space, String command, String costIndex, String cost, boolean hidePrefix) {
		String costMessage = space + zh.getLM().getEconomyAnswer(language, costIndex, hidePrefix);
		return getCommandDescriptionFull(language, space, command, costMessage, cost, true, hidePrefix);
	}
	
	private String getCommandDescriptionFull(String language, String space, String command, String costMessage, String amount, boolean settingsCommand, boolean hidePrefix) {
		String rawMessage = "";
		if (!settingsCommand) {
			rawMessage = space + zh.getLM().getCommandDescription(language, command) + costMessage;
		}
		else {
			rawMessage = space + zh.getLM().getSettingsCommandDescription(language, command) + costMessage;
		}
		String message = populate(rawMessage, player, horse, userID, perm, amount, value, max, lang);
		return message;
	}
	
	private String getEconomyFull(String language, String index, String amount) {
		String rawMessage = zh.getLM().getEconomyAnswer(language, index);
		String message = populate(rawMessage, player, horse, userID, perm, amount, value, max, lang);
		return message;
	}
	
	private String getHeaderFull(String language, String index, String space, String player, String horse, String userID, String amount, String value, String max, String lang, boolean hidePrefix) {
		String rawMessage = space + zh.getLM().getHeaderMessage(language, index, hidePrefix);
		String message = populate(rawMessage, player, horse, userID, perm, amount, value, max, lang);
		return message;
	}
	
	private String getInfoFull(String language, String index, String space, String player, String horse, String userID, String amount, String value, String max, boolean hidePrefix) {
		String rawMessage = space + zh.getLM().getInformationMessage(language, index, hidePrefix);
		String message = populate(rawMessage, player, horse, userID, perm, amount, value, max, lang);
		return message;
	}
	
	private String getMessageFull(String language, String index, String player, String horse, String userID, String perm, String amount, String value, String lang) {
		String rawMessage = zh.getLM().getCommandAnswer(language, index);
		String message = populate(rawMessage, player, horse, userID, perm, amount, value, max, lang);
		return message;
	}
	
	private String populate(String rawMessage, String player, String horse, String userID, String perm, String amount, String value, String max, String lang) {
		String message = rawMessage;
		message = message.replace(MessageManager.amountFlag, amount);
		message = message.replace(MessageManager.horseFlag, horse);
		message = message.replace(MessageManager.langFlag, lang);
		message = message.replace(MessageManager.maxFlag, max);
		message = message.replace(MessageManager.permFlag, perm);
		message = message.replace(MessageManager.playerFlag, player);		
		message = message.replace(MessageManager.userIDFlag, userID);		
		message = message.replace(MessageManager.valueFlag, value);		
		message = populateColors(message);
		return message;
	}

	private String populateColors(String rawMessage) {
		String message = rawMessage;
		for (ChatColor cc : colors.keySet()) {
			for (String colorCode : colors.get(cc)) {
				message = message.replace(colorCode, cc.toString());
			}
		}
		return message;
	}

}
