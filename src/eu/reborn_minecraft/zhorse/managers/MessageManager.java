package eu.reborn_minecraft.zhorse.managers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.ColorEnum;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class MessageManager {	
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
	
	private ZHorse zh;
	
	public MessageManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public ChatColor getColor(String colorCode) {
		for (ColorEnum color : ColorEnum.values()) {
			for (String code : color.getCodes()) {
				if (code.equalsIgnoreCase(colorCode)) {
					return color.getColor();
				}
			}
		}
		return null;
	}

	public boolean isColor(String colorCode) {
		return (getColor(colorCode) != null);
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
	
	public String getMessageAmountMaxSpacer(CommandSender s, LocaleEnum index, int amount, int max, int spacer, boolean hidePrefix) {
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
	
	public String getMessageHorsePlayer(CommandSender s, LocaleEnum index, String horse, String player, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageHorseSpacer(CommandSender s, LocaleEnum index, String horse, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageHorseSpacerUserID(CommandSender s, LocaleEnum index, String horse, int spacer, String userID, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageHorseSpacerUserIDValue(CommandSender s, LocaleEnum index, String horse, int spacer, String userID, String value, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageLang(CommandSender s, LocaleEnum index, String lang, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageLangPlayer(CommandSender s, LocaleEnum index, String lang, String player, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessageLangValue(CommandSender s, LocaleEnum index, String lang, String value, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessagePerm(CommandSender s, LocaleEnum index, String perm, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessagePlayer(CommandSender s, LocaleEnum index, String player, boolean hidePrefix) {
		return getMessageFull(s, index, amount, cost, horse, lang, max, perm, player, spacer, userID, value, hidePrefix);
	}
	
	public String getMessagePlayerSpacer(CommandSender s, LocaleEnum index, String player, int spacer, boolean hidePrefix) {
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
	
	public String getMessageSpacerUserID(CommandSender s, LocaleEnum index, int spacer, String userID, boolean hidePrefix) {
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
	
	public void sendMessageAmountMaxSpacer(CommandSender s, LocaleEnum index, int amount, int max, int spacer) {
		s.sendMessage(getMessageAmountMaxSpacer(s, index, amount, max, spacer, false));
	}
	
	public void sendMessageAmountMaxSpacer(CommandSender s, LocaleEnum index, int amount, int max, int spacer, boolean hidePrefix) {
		s.sendMessage(getMessageAmountMaxSpacer(s, index, amount, max, spacer, hidePrefix));
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
	
	public void sendMessageHorsePlayer(CommandSender s, LocaleEnum index, String horse, String player) {
		s.sendMessage(getMessageHorsePlayer(s, index, horse, player, false));
	}
	
	public void sendMessageHorsePlayer(CommandSender s, LocaleEnum index, String horse, String player, boolean hidePrefix) {
		s.sendMessage(getMessageHorsePlayer(s, index, horse, player, hidePrefix));
	}
	
	public void sendMessageHorseSpacer(CommandSender s, LocaleEnum index, String horse, int spacer) {
		s.sendMessage(getMessageHorseSpacer(s, index, horse, spacer, false));
	}
	
	public void sendMessageHorseSpacer(CommandSender s, LocaleEnum index, String horse, int spacer, boolean hidePrefix) {
		s.sendMessage(getMessageHorseSpacer(s, index, horse, spacer, hidePrefix));
	}
	
	public void sendMessageHorseSpacerUserID(CommandSender s, LocaleEnum index, String horse, int spacer, String userID) {
		s.sendMessage(getMessageHorseSpacerUserID(s, index, horse, spacer, userID, false));
	}
	
	public void sendMessageHorseSpacerUserID(CommandSender s, LocaleEnum index, String horse, int spacer, String userID, boolean hidePrefix) {
		s.sendMessage(getMessageHorseSpacerUserID(s, index, horse, spacer, userID, hidePrefix));
	}
	
	public void sendMessageHorseSpacerUserIDValue(CommandSender s, LocaleEnum index, String horse, int spacer, String userID, String value) {
		s.sendMessage(getMessageHorseSpacerUserIDValue(s, index, horse, spacer, userID, value, false));
	}
	
	public void sendMessageHorseSpacerUserIDValue(CommandSender s, LocaleEnum index, String horse, int spacer, String userID, String value, boolean hidePrefix) {
		s.sendMessage(getMessageHorseSpacerUserIDValue(s, index, horse, spacer, userID, value, hidePrefix));
	}
	
	public void sendMessageLang(CommandSender s, LocaleEnum index, String lang) {
		s.sendMessage(getMessageLang(s, index, lang, false));
	}
	
	public void sendMessageLang(CommandSender s, LocaleEnum index, String lang, boolean hidePrefix) {
		s.sendMessage(getMessageLang(s, index, lang, hidePrefix));
	}
	
	public void sendMessageLangPlayer(CommandSender s, LocaleEnum index, String lang, String player) {
		s.sendMessage(getMessageLangPlayer(s, index, lang, player, false));
	}
	
	public void sendMessageLangPlayer(CommandSender s, LocaleEnum index, String lang, String player, boolean hidePrefix) {
		s.sendMessage(getMessageLangPlayer(s, index, lang, player, hidePrefix));
	}
	
	public void sendMessageLangValue(CommandSender s, LocaleEnum index, String lang, String value) {
		s.sendMessage(getMessageLangValue(s, index, lang, value, false));
	}
	
	public void sendMessageLangValue(CommandSender s, LocaleEnum index, String lang, String value, boolean hidePrefix) {
		s.sendMessage(getMessageLangValue(s, index, lang, value, hidePrefix));
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
	
	public void sendMessagePlayerSpacer(CommandSender s, LocaleEnum index, String player, int spacer) {
		s.sendMessage(getMessagePlayerSpacer(s, index, player, spacer, false));
	}
	
	public void sendMessagePlayerSpacer(CommandSender s, LocaleEnum index, String player, int spacer, boolean hidePrefix) {
		s.sendMessage(getMessagePlayerSpacer(s, index, player, spacer, hidePrefix));
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
	
	public void sendMessageSpacerUserID(CommandSender s, LocaleEnum index, int spacer, String userID) {
		s.sendMessage(getMessageSpacerUserID(s, index, spacer, userID, false));
	}
	
	public void sendMessageSpacerUserID(CommandSender s, LocaleEnum index, int spacer, String userID, boolean hidePrefix) {
		s.sendMessage(getMessageSpacerUserID(s, index, spacer, userID, hidePrefix));
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
		for (ColorEnum color : ColorEnum.values()) {
			for (String code : color.getCodes()) {
				message = message.replace(code, color.getColor().toString());
			}
		}
		return message;
	}
	
	private String populateFlags(String rawMessage, int amount, String horse, String lang, int max, String perm, String player, String userID, String value) {
		String message = rawMessage;
		message = message.replace(KeyWordEnum.amountFlag.getValue(), Integer.toString(amount));
		message = message.replace(KeyWordEnum.horseFlag.getValue(), horse);
		message = message.replace(KeyWordEnum.langFlag.getValue(), lang);
		message = message.replace(KeyWordEnum.maxFlag.getValue(), Integer.toString(max));
		message = message.replace(KeyWordEnum.permFlag.getValue(), perm);
		message = message.replace(KeyWordEnum.playerFlag.getValue(), player);		
		message = message.replace(KeyWordEnum.userIDFlag.getValue(), userID);		
		message = message.replace(KeyWordEnum.valueFlag.getValue(), value);		
		message = populateColors(message);
		return message;
	}

}
