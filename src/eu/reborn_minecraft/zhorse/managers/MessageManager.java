package eu.reborn_minecraft.zhorse.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class MessageManager {	
	private static String amountFlag = "<amount>";
	private static String horseFlag = "<horse>";
	private static String langFlag = "<lang>";
	private static String maxFlag = "<max>";
	private static String permFlag = "<perm>";
	private static String playerFlag = "<player>";
	private static String userIDFlag = "<id>";
	private static String valueFlag = "<value>";
	
	private static int amount = 0;
	private static int cost = 0;
	private static String horse = "";
	private static String lang = "";
	private static int max = 0;
	private static String perm = "";
	private static String player = "";
	private static int spacer = 0;
	private static String userID = "";
	private static String value = "";
	
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
	
	public String getMessage(CommandSender s, LocaleEnum index, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageAmount(CommandSender s, LocaleEnum index, int amount, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageAmountMax(CommandSender s, LocaleEnum index, int amount, int max, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageAmountValue(CommandSender s, LocaleEnum index, int amount, String value, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageCostSpacerValue(CommandSender s, LocaleEnum index, int cost, int spacer, String value, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageHorse(CommandSender s, LocaleEnum index, String horse, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessagePerm(CommandSender s, LocaleEnum index, String perm, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessagePlayer(CommandSender s, LocaleEnum index, String player, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessagePlayerHorse(CommandSender s, LocaleEnum index, String player, String horse, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessagePlayerUserID(CommandSender s, LocaleEnum index, String player, String userID, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessagePlayerValue(CommandSender s, LocaleEnum index, String player, String value, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageSpacer(CommandSender s, LocaleEnum index, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageSpacerValue(CommandSender s, LocaleEnum index, int spacer, String value, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageUserID(CommandSender s, LocaleEnum index, String userID, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageValue(CommandSender s, LocaleEnum index, String value, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public void sendMessage(CommandSender s, LocaleEnum index) {
		s.sendMessage(getMessage(s, index, false));
	}
	
	public void sendMessage(CommandSender s, LocaleEnum index, boolean hidePrefix) {
		s.sendMessage(getMessage(s, index, hidePrefix));
	}
	
	public void sendMessageAmount(CommandSender s, LocaleEnum index, int amount) {
		s.sendMessage(getMessageAmount(s, index, amount, false));
	}
	
	public void sendMessageAmount(CommandSender s, LocaleEnum index, int amount, boolean hidePrefix) {
		s.sendMessage(getMessageAmount(s, index, amount, hidePrefix));
	}
	
	public void sendMessageAmountMax(CommandSender s, LocaleEnum index, int amount, int max) {
		s.sendMessage(getMessageAmountMax(s, index, amount, max, false));
	}
	
	public void sendMessageAmountMax(CommandSender s, LocaleEnum index, int amount, int max, boolean hidePrefix) {
		s.sendMessage(getMessageAmountMax(s, index, amount, max, hidePrefix));
	}
	
	public void sendMessageAmountValue(CommandSender s, LocaleEnum index, int amount, String value) {
		s.sendMessage(getMessageAmountValue(s, index, amount, value, false));
	}
	
	public void sendMessageAmountValue(CommandSender s, LocaleEnum index, int amount, String value, boolean hidePrefix) {
		s.sendMessage(getMessageAmountValue(s, index, amount, value, hidePrefix));
	}
	
	public void sendMessageCostSpacerValue(CommandSender s, LocaleEnum index, int cost, int spacer, String value) {
		s.sendMessage(getMessageCostSpacerValue(s, index, cost, spacer, value, false));
	}
	
	public void sendMessageCostSpacerValue(CommandSender s, LocaleEnum index, int cost, int spacer, String value, boolean hidePrefix) {
		s.sendMessage(getMessageCostSpacerValue(s, index, cost, spacer, value, hidePrefix));
	}
	
	public void sendMessageHorse(CommandSender s, LocaleEnum index, String horse) {
		s.sendMessage(getMessageHorse(s, index, horse, false));
	}
	
	public void sendMessageHorse(CommandSender s, LocaleEnum index, String horse, boolean hidePrefix) {
		s.sendMessage(getMessageHorse(s, index, horse, hidePrefix));
	}
	
	public void sendMessagePerm(CommandSender s, LocaleEnum index, String perm) {
		s.sendMessage(getMessagePerm(s, index, perm, false));
	}
	
	public void sendMessagePerm(CommandSender s, LocaleEnum index, String perm, boolean hidePrefix) {
		s.sendMessage(getMessagePerm(s, index, perm, hidePrefix));
	}
	
	public void sendMessagePlayer(CommandSender s, LocaleEnum index, String player) {
		s.sendMessage(getMessagePlayer(s, index, player, false));
	}
	
	public void sendMessagePlayer(CommandSender s, LocaleEnum index, String player, boolean hidePrefix) {
		s.sendMessage(getMessagePlayer(s, index, player, hidePrefix));
	}
	
	public void sendMessagePlayerHorse(CommandSender s, LocaleEnum index, String player, String horse) {
		s.sendMessage(getMessagePlayerHorse(s, index, player, horse, false));
	}
	
	public void sendMessagePlayerHorse(CommandSender s, LocaleEnum index, String player, String horse,  boolean hidePrefix) {
		s.sendMessage(getMessagePlayerHorse(s, index, player, horse, hidePrefix));
	}
	
	public void sendMessagePlayerUserID(CommandSender s, LocaleEnum index, String player, String userID) {
		s.sendMessage(getMessagePlayerUserID(s, index, player, userID, false));
	}
	
	public void sendMessagePlayerUserID(CommandSender s, LocaleEnum index, String player, String userID,  boolean hidePrefix) {
		s.sendMessage(getMessagePlayerUserID(s, index, player, userID, hidePrefix));
	}
	
	public void sendMessagePlayerValue(CommandSender s, LocaleEnum index, String player, String value) {
		s.sendMessage(getMessagePlayerValue(s, index, player, value, false));
	}
	
	public void sendMessagePlayerValue(CommandSender s, LocaleEnum index, String player, String value,  boolean hidePrefix) {
		s.sendMessage(getMessagePlayerValue(s, index, player, value, hidePrefix));
	}
	
	public void sendMessageSpacer(CommandSender s, LocaleEnum index, int spacer) {
		s.sendMessage(getMessageSpacer(s, index, spacer, false));
	}
	
	public void sendMessageSpacer(CommandSender s, LocaleEnum index, int spacer, boolean hidePrefix) {
		s.sendMessage(getMessageSpacer(s, index, spacer, hidePrefix));
	}
	
	public void sendMessageSpacerValue(CommandSender s, LocaleEnum index, int spacer, String value) {
		s.sendMessage(getMessageSpacerValue(s, index, spacer, value, false));
	}
	
	public void sendMessageSpacerValue(CommandSender s, LocaleEnum index, int spacer, String value, boolean hidePrefix) {
		s.sendMessage(getMessageSpacerValue(s, index, spacer, value, hidePrefix));
	}
	
	public void sendMessageUserID(CommandSender s, LocaleEnum index, String userID) {
		s.sendMessage(getMessageUserID(s, index, userID, false));
	}
	
	public void sendMessageUserID(CommandSender s, LocaleEnum index, String userID, boolean hidePrefix) {
		s.sendMessage(getMessageUserID(s, index, userID, hidePrefix));
	}
	
	public void sendMessageValue(CommandSender s, LocaleEnum index, String value) {
		s.sendMessage(getMessageValue(s, index, value, false));
	}
	
	public void sendMessageValue(CommandSender s, LocaleEnum index, String value, boolean hidePrefix) {
		s.sendMessage(getMessageValue(s, index, value, hidePrefix));
	}
	
	private String getMessageFull(CommandSender s, LocaleEnum index, int amount, int cost, String horse, String lang, int max, String perm, String player, int spacer, String userID, String value, boolean hidePrefix) {
		String rawMessage;
		if (cost <= 0) {
			rawMessage = getSpace(spacer) + getFromLocale(s, index, hidePrefix);
		}
		else {
			String costMessage = getSpace(spacer) + getMessageAmountValue(s, LocaleEnum.commandCost, cost, value, hidePrefix);
			rawMessage = getSpace(spacer) + getFromLocale(s, index, hidePrefix) + costMessage;
		}
		String message = populateFlags(rawMessage, amount, horse, lang, max, perm, player, userID, value);
		return message;
	}
	
	private String getFromLocale(CommandSender s, LocaleEnum index, boolean hidePrefix) {
		return zh.getLM().getMessage(index.getIndex(), zh.getUM().getLanguage(s), hidePrefix);
	}
	
	private String getSpace(int spacer) {
		String space = "";
		while (space.length() < spacer) {
			space += " ";
		}
		return space;
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
	
	private String populateFlags(String rawMessage, int amount, String horse, String lang, int max, String perm, String player, String userID, String value) {
		String message = rawMessage;
		message = message.replace(MessageManager.amountFlag, Integer.toString(amount));
		message = message.replace(MessageManager.horseFlag, horse);
		message = message.replace(MessageManager.langFlag, lang);
		message = message.replace(MessageManager.maxFlag, Integer.toString(max));
		message = message.replace(MessageManager.permFlag, perm);
		message = message.replace(MessageManager.playerFlag, player);		
		message = message.replace(MessageManager.userIDFlag, userID);		
		message = message.replace(MessageManager.valueFlag, value);		
		message = populateColors(message);
		return message;
	}
	
//	public String getCommandDescription(CommandSender s, int spacer, String command, boolean hidePrefix) {
//		String costMessage = "";
//		return getCommandDescriptionFull(s, spacer, command, costMessage, amount, value, false, hidePrefix);
//	}
//	
//	public String getCommandDescriptionCostValue(CommandSender s, int spacer, String command, String costIndex, String cost, String value, boolean hidePrefix) {
//		String costMessage = getSpace(spacer) + zh.getLM().getEconomyAnswer(zh.getUM().getLanguage(s), costIndex, hidePrefix);
//		return getCommandDescriptionFull(s, spacer, command, costMessage, cost, value, false, hidePrefix);
//	}
//	
//	public String getCommandUsage(CommandSender s, String index, int spacer, String command, boolean hidePrefix) {
//		String usage = zh.getLM().getCommandUsage(zh.getUM().getLanguage(s), command);
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, usage, max, lang, hidePrefix);
//	}
//	
//	public String getEconomyAmountValue(CommandSender s, String index, String amount, String value) {
//		return getEconomyFull(s, index, amount, value);
//	}
//	
//	public String getHeader(CommandSender s, String index, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeader(CommandSender s, String index, int spacer, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderAmount(CommandSender s, String index, String amount, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderAmount(CommandSender s, String index, int spacer, String amount, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderAmountMax(CommandSender s, String index, String amount, String max, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderAmountMax(CommandSender s, String index, int spacer, String amount, String max, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderHorseUserID(CommandSender s, String index, String horse, String userID, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderHorseUserID(CommandSender s, String index, int spacer, String horse, String userID, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderLang(CommandSender s, String index, String lang, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderLang(CommandSender s, String index, int spacer, String lang, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderPlayer(CommandSender s, String index, String player, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderPlayer(CommandSender s, String index, int spacer, String player, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderPlayerAmount(CommandSender s, String index, String player, String amount, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderPlayerAmount(CommandSender s, String index, int spacer, String player, String amount, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderContent(CommandSender s, String index, String contentIndex, boolean hidePrefix) {
//		String content = zh.getLM().getHeaderMessage(zh.getUM().getLanguage(s), contentIndex);
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, content, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderContent(CommandSender s, String index, int spacer, String contentIndex, boolean hidePrefix) {
//		String content = zh.getLM().getHeaderMessage(zh.getUM().getLanguage(s), contentIndex);
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, content, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderValue(CommandSender s, String index, String value, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getHeaderValue(CommandSender s, String index, int spacer, String value, boolean hidePrefix) {
//		return getHeaderFull(s, index, spacer, player, horse, userID, amount, value, max, lang, hidePrefix);
//	}
//	
//	public String getInfo(CommandSender s, String index, boolean hidePrefix) {
//		return getInfoFull(s, index, spacer, player, horse, userID, amount, value, max, hidePrefix);
//	}
//	
//	public String getInfoAmountMax(CommandSender s, String index, String amount, String max, boolean hidePrefix) {
//		return getInfoFull(s, index, spacer, player, horse, userID, amount, value, max, hidePrefix);
//	}
//	
//	public String getInfoHorse(CommandSender s, String index, String horse, boolean hidePrefix) {
//		return getInfoFull(s, index, spacer, player, horse, userID, amount, value, max, hidePrefix);
//	}
//	
//	public String getInfoPlayer(CommandSender s, String index, String player, boolean hidePrefix) {
//		return getInfoFull(s, index, spacer, player, horse, userID, amount, value, max, hidePrefix);
//	}
//	
//	public String getInfoUserID(CommandSender s, String index, String userID, boolean hidePrefix) {
//		return getInfoFull(s, index, spacer, player, horse, userID, amount, value, max, hidePrefix);
//	}
//	
//	public String getInfoValue(CommandSender s, String index, String value, boolean hidePrefix) {
//		return getInfoFull(s, index, spacer, player, horse, userID, amount, value, max, hidePrefix);
//	}
//
//	public String getMessage(CommandSender s, String index) {
//		return getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}
//	
//	public String getMessageAmount(CommandSender s, String index, String amount) {
//		return getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}
//	
//	public String getMessageHorse(CommandSender s, String index, String horse) {
//		return getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}	
//	
//	public String getMessageHorseValue(CommandSender s, String index, String horse, String value) {
//		return getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}
//	
//	public String getMessageLang(CommandSender s, String index, String lang) {
//		return getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}	
//	
//	public String getMessagePerm(CommandSender s, String index, String perm) {
//		return 	getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}
//	
//	public String getMessagePlayer(CommandSender s, String index, String player) {
//		return getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}
//	
//	public String getMessagePlayerAmount(CommandSender s, String index, String player, String amount) {
//		return getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}
//	
//	public String getMessagePlayerLang(CommandSender s, String index, String player, String lang) {
//		return getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}
//	
//	public String getMessagePlayerHorse(CommandSender s, String index, String player, String horse) {
//		return getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}
//	
//	public String getMessagePlayerPerm(CommandSender s, String index, String player, String perm) {
//		return getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}
//	
//	public String getMessagePlayerUserID(CommandSender s, String index, String player, String userID) {
//		return getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}
//	
//	public String getMessageUserID(CommandSender s, String index, String userID) {
//		return 	getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}
//	
//	public String getMessageValue(CommandSender s, String index, String value) {
//		return getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}
//	
//	public String getMessageValueLang(CommandSender s, String index, String value, String lang) {
//		return getMessageFull(s, index, player, horse, userID, perm, amount, value, lang);
//	}
//	
//	public String getSettingsCommandDescription(CommandSender s, int spacer, String command, boolean hidePrefix) {
//		return getCommandDescriptionFull(s, spacer, command, "", amount, value, true, hidePrefix);
//	}
//	
//	public String getSettingsCommandDescriptionCostValue(CommandSender s, int spacer, String command, String costIndex, String cost, String value, boolean hidePrefix) {
//		String costMessage = getSpace(spacer) + zh.getLM().getEconomyAnswer(zh.getUM().getLanguage(s), costIndex, hidePrefix);
//		return getCommandDescriptionFull(s, spacer, command, costMessage, cost, value, true, hidePrefix);
//	}
//	
//	
//	
//	public void sendCommandDescription(CommandSender s, int spacer, String command, boolean hidePrefix) {
//		s.sendMessage(getCommandDescription(s, spacer, command, hidePrefix));
//	}
//	
//	public void sendCommandDescriptionCostValue(CommandSender s, int spacer, String command, String costIndex, String cost, String value, boolean hidePrefix) {
//		s.sendMessage(getCommandDescriptionCostValue(s, spacer, command, costIndex, cost, value, hidePrefix));
//	}
//	
//	public void sendCommandUsage(CommandSender s, String index, int spacer, String command, boolean hidePrefix) {
//		s.sendMessage(getCommandUsage(s, index, spacer, command, hidePrefix));
//	}
//	
//	public void sendEconomyAmountValue(CommandSender s, String index, String amount, String value) {
//		s.sendMessage(getEconomyAmountValue(s, index, amount, value));
//	}
//	
//	public void sendHeader(CommandSender s, String index, boolean hidePrefix) {
//		s.sendMessage(getHeader(s, index, hidePrefix));
//	}
//	
//	public void sendHeader(CommandSender s, String index, int spacer, boolean hidePrefix) {
//		s.sendMessage(getHeader(s, index, spacer, hidePrefix));
//	}
//	
//	public void sendHeaderAmount(CommandSender s, String index, String amount, boolean hidePrefix) {
//		s.sendMessage(getHeaderAmount(s, index, amount, hidePrefix));
//	}
//	
//	public void sendHeaderAmount(CommandSender s, String index, int spacer, String amount, boolean hidePrefix) {
//		s.sendMessage(getHeaderAmount(s, index, spacer, amount, hidePrefix));
//	}
//	
//	public void sendHeaderAmountMax(CommandSender s, String index, String amount, String max, boolean hidePrefix) {
//		s.sendMessage(getHeaderAmountMax(s, index, amount, max, hidePrefix));
//	}
//	
//	public void sendHeaderAmountMax(CommandSender s, String index, int spacer, String amount, String max, boolean hidePrefix) {
//		s.sendMessage(getHeaderAmountMax(s, index, spacer, amount, max, hidePrefix));
//	}
//	
//	public void sendHeaderHorseUserID(CommandSender s, String index, String horse, String userID, boolean hidePrefix) {
//		s.sendMessage(getHeaderHorseUserID(s, index, horse, userID, hidePrefix));
//	}
//	
//	public void sendHeaderHorseUserID(CommandSender s, String index, int spacer, String horse, String userID, boolean hidePrefix) {
//		s.sendMessage(getHeaderHorseUserID(s, index, spacer, horse, userID, hidePrefix));
//	}
//	
//	public void sendHeaderLang(CommandSender s, String index, String lang, boolean hidePrefix) {
//		s.sendMessage(getHeaderLang(s, index, lang, hidePrefix));
//	}
//	
//	public void sendHeaderLang(CommandSender s, String index, int spacer, String lang, boolean hidePrefix) {
//		s.sendMessage(getHeaderLang(s, index, spacer, lang, hidePrefix));
//	}
//	
//	public void sendHeaderPlayer(CommandSender s, String index, String player, boolean hidePrefix) {
//		s.sendMessage(getHeaderPlayer(s, index, player, hidePrefix));
//	}
//	
//	public void sendHeaderPlayer(CommandSender s, String index, int spacer, String player, boolean hidePrefix) {
//		s.sendMessage(getHeaderPlayer(s, index, spacer, player, hidePrefix));
//	}
//	
//	public void sendHeaderPlayerAmount(CommandSender s, String index, String player, String amount, boolean hidePrefix) {
//		s.sendMessage(getHeaderPlayerAmount(s, index, player, amount, hidePrefix));
//	}
//	
//	public void sendHeaderPlayerAmount(CommandSender s, String index, int spacer, String player, String amount, boolean hidePrefix) {
//		s.sendMessage(getHeaderPlayerAmount(s, index, spacer, player, amount, hidePrefix));
//	}
//	
//	public void sendHeaderContent(CommandSender s, String index, String contentIndex, boolean hidePrefix) {
//		s.sendMessage(getHeaderContent(s, index, contentIndex, hidePrefix));
//	}
//	
//	public void sendHeaderContent(CommandSender s, String index, int spacer, String contentIndex, boolean hidePrefix) {
//		s.sendMessage(getHeaderContent(s, index, spacer, contentIndex, hidePrefix));
//	}
//	
//	public void sendHeaderValue(CommandSender s, String index, String value, boolean hidePrefix) {
//		s.sendMessage(getHeaderValue(s, index, value, hidePrefix));
//	}
//	
//	public void sendHeaderValue(CommandSender s, String index, int spacer, String value, boolean hidePrefix) {
//		s.sendMessage(getHeaderValue(s, index, spacer, value, hidePrefix));
//	}
//	
//	public void sendInfo(CommandSender s, String index, boolean hidePrefix) {
//		s.sendMessage(getInfo(s, index, hidePrefix));
//	}
//	
//	public void sendInfoAmountMax(CommandSender s, String index, String amount, String max, boolean hidePrefix) {
//		s.sendMessage(getInfoAmountMax(s, index, amount, max, hidePrefix));
//	}
//	
//	public void sendInfoHorse(CommandSender s, String index, String horse, boolean hidePrefix) {
//		s.sendMessage(getInfoHorse(s, index, horse, hidePrefix));
//	}
//	
//	public void sendInfoPlayer(CommandSender s, String index, String player, boolean hidePrefix) {
//		s.sendMessage(getInfoPlayer(s, index, player, hidePrefix));
//	}
//	
//	public void sendInfoUserID(CommandSender s, String index, String userID, boolean hidePrefix) {
//		s.sendMessage(getInfoUserID(s, index, userID, hidePrefix));
//	}
//	
//	public void sendInfoValue(CommandSender s, String index, String value, boolean hidePrefix) {
//		s.sendMessage(getInfoValue(s, index, value, hidePrefix));
//	}
//
//	public void sendMessage(CommandSender s, String index) {
//		s.sendMessage(getMessage(s, index));
//	}
//	
//	public void sendMessageAmount(CommandSender s, String index, String amount) {
//		s.sendMessage(getMessageAmount(s, index, amount));
//	}
//	
//	public void sendMessageHorse(CommandSender s, String index, String horse) {
//		s.sendMessage(getMessageHorse(s, index, horse));
//	}	
//	
//	public void sendMessageHorseValue(CommandSender s, String index, String horse, String value) {
//		s.sendMessage(getMessageHorseValue(s, index, horse, value));
//	}
//	
//	public void sendMessageLang(CommandSender s, String index, String lang) {
//		s.sendMessage(getMessageLang(s, index, lang));
//	}	
//	
//	public void sendMessagePerm(CommandSender s, String index, String perm) {
//		s.sendMessage(getMessagePerm(s, index, perm));
//	}
//	
//	public void sendMessagePlayer(CommandSender s, String index, String player) {
//		s.sendMessage(getMessagePlayer(s, index, player));
//	}
//	
//	public void sendMessagePlayerAmount(CommandSender s, String index, String player, String amount) {
//		s.sendMessage(getMessagePlayerAmount(s, index, player, amount));
//	}
//	
//	public void sendMessagePlayerLang(CommandSender s, String index, String player, String lang) {
//		s.sendMessage(getMessagePlayerLang(s, index, player, lang));
//	}
//	
//	public void sendMessagePlayerHorse(CommandSender s, String index, String player, String horse) {
//		s.sendMessage(getMessagePlayerHorse(s, index, player, horse));
//	}
//	
//	public void sendMessagePlayerPerm(CommandSender s, String index, String player, String perm) {
//		s.sendMessage(getMessagePlayerPerm(s, index, player, perm));
//	}
//	
//	public void sendMessagePlayerUserID(CommandSender s, String index, String player, String userID) {
//		s.sendMessage(getMessagePlayerUserID(s, index, player, userID));
//	}
//	
//	public void sendMessageUserID(CommandSender s, String index, String userID) {
//		s.sendMessage(getMessageUserID(s, index, userID));
//	}
//	
//	public void sendMessageValue(CommandSender s, String index, String value) {
//		s.sendMessage(getMessageValue(s, index, value));
//	}
//	
//	public void sendMessageValueLang(CommandSender s, String index, String value, String lang) {
//		s.sendMessage(getMessageValueLang(s, index, value, lang));
//	}
//	
//	public void sendSettingsCommandDescription(CommandSender s, int spacer, String command, boolean hidePrefix) {
//		s.sendMessage(getSettingsCommandDescription(s, spacer, command, hidePrefix));
//	}
//	
//	public void sendSettingsCommandDescriptionCostValue(CommandSender s, int spacer, String command, String costIndex, String cost, String value, boolean hidePrefix) {
//		s.sendMessage(getSettingsCommandDescriptionCostValue(s, spacer, command, costIndex, cost, value, hidePrefix));
//	}
//	
//	private String getCommandDescriptionFull(CommandSender s, int spacer, String command, String costMessage, String amount, String value, boolean settingsCommand, boolean hidePrefix) {
//		String rawMessage;
//		if (!settingsCommand) {
//			rawMessage = getSpace(spacer) + zh.getLM().getCommandDescription(zh.getUM().getLanguage(s), command) + costMessage;
//		}
//		else {
//			rawMessage = getSpace(spacer) + zh.getLM().getSettingsCommandDescription(zh.getUM().getLanguage(s), command) + costMessage;
//		}
//		String message = populateFlags(rawMessage, player, horse, userID, perm, amount, value, max, lang);
//		return message;
//	}
//	
//	private String getEconomyFull(CommandSender s, String index, String amount, String value) {
//		String rawMessage = zh.getLM().getEconomyAnswer(zh.getUM().getLanguage(s), index);
//		String message = populateFlags(rawMessage, player, horse, userID, perm, amount, value, max, lang);
//		return message;
//	}
//	
//	private String getHeaderFull(CommandSender s, String index, int spacer, String player, String horse, String userID, String amount, String value, String max, String lang, boolean hidePrefix) {
//		String rawMessage = getSpace(spacer) + zh.getLM().getHeaderMessage(zh.getUM().getLanguage(s), index, hidePrefix);
//		String message = populateFlags(rawMessage, player, horse, userID, perm, amount, value, max, lang);
//		return message;
//	}
//	
//	private String getInfoFull(CommandSender s, String index, int spacer, String player, String horse, String userID, String amount, String value, String max, boolean hidePrefix) {
//		String rawMessage = getSpace(spacer) + zh.getLM().getInformationMessage(zh.getUM().getLanguage(s), index, hidePrefix);
//		String message = populateFlags(rawMessage, player, horse, userID, perm, amount, value, max, lang);
//		return message;
//	}
//	
//	private String getMessageFull(CommandSender s, String index, String player, String horse, String userID, String perm, String amount, String value, String lang) {
//		String rawMessage = zh.getLM().getCommandAnswer(zh.getUM().getLanguage(s), index);
//		String message = populateFlags(rawMessage, player, horse, userID, perm, amount, value, max, lang);
//		return message;
//	}

}
