package eu.reborn_minecraft.zhorse.managers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.ColorEnum;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class MessageManager {	
	private static final int AMOUNT = 0;
	private static final int COST = 0;
	private static final String HORSE = "";
	private static final String HORSE_ID = "";
	private static final String LANG = "";
	private static final int MAX = 0;
	private static final String PERM = "";
	private static final String PLAYER = "";
	private static final int SPACER = 0;
	private static final String VALUE = "";
	
	private ZHorse zh;
	
	public MessageManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public String getMessage(CommandSender s, LocaleEnum index, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageAmount(CommandSender s, LocaleEnum index, int amount, boolean hidePrefix) {
		return getMessageFull(s, index, amount, COST, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageAmountMax(CommandSender s, LocaleEnum index, int amount, int max, boolean hidePrefix) {
		return getMessageFull(s, index, amount, COST, HORSE, HORSE_ID, LANG, max, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageAmountMaxSpacer(CommandSender s, LocaleEnum index, int amount, int max, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, amount, COST, HORSE, HORSE_ID, LANG, max, PERM, PLAYER, spacer, VALUE, hidePrefix);
	}
	
	public String getMessageAmountSpacer(CommandSender s, LocaleEnum index, int amount, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, amount, COST, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, spacer, VALUE, hidePrefix);
	}
	
	public String getMessageAmountValue(CommandSender s, LocaleEnum index, int amount, String value, boolean hidePrefix) {
		return getMessageFull(s, index, amount, COST, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, SPACER, value, hidePrefix);
	}
	
	public String getMessageCostSpacerValue(CommandSender s, LocaleEnum index, int cost, int spacer, String value, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, cost, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, spacer, value, hidePrefix);
	}
	
	public String getMessageHorse(CommandSender s, LocaleEnum index, String horse, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, horse, HORSE_ID, LANG, MAX, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageHorseID(CommandSender s, LocaleEnum index, String horseID, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, horseID, LANG, MAX, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageHorseIDPlayer(CommandSender s, LocaleEnum index, String horseID, String player, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, horseID, LANG, MAX, PERM, player, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageHorseHorseIDSpacer(CommandSender s, LocaleEnum index, String horse, String horseID, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, horse, horseID, LANG, MAX, PERM, PLAYER, spacer, VALUE, hidePrefix);
	}
	
	public String getMessageHorseIDSpacer(CommandSender s, LocaleEnum index, String horseID, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, horseID, LANG, MAX, PERM, PLAYER, spacer, VALUE, hidePrefix);
	}
	
	public String getMessageHorseHorseIDSpacerValue(CommandSender s, LocaleEnum index, String horse, String horseID, int spacer, String value, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, horse, horseID, LANG, MAX, PERM, PLAYER, spacer, value, hidePrefix);
	}
	
	public String getMessageHorseMax(CommandSender s, LocaleEnum index, String horse, int max, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, horse, HORSE_ID, LANG, max, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageHorsePlayer(CommandSender s, LocaleEnum index, String horse, String player, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, horse, HORSE_ID, LANG, MAX, PERM, player, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageHorseSpacer(CommandSender s, LocaleEnum index, String horse, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, horse, HORSE_ID, LANG, MAX, PERM, PLAYER, spacer, VALUE, hidePrefix);
	}
	
	public String getMessageLang(CommandSender s, LocaleEnum index, String lang, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, HORSE_ID, lang, MAX, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageLangPlayer(CommandSender s, LocaleEnum index, String lang, String player, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, HORSE_ID, lang, MAX, PERM, player, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageLangValue(CommandSender s, LocaleEnum index, String lang, String value, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, HORSE_ID, lang, MAX, PERM, PLAYER, SPACER, value, hidePrefix);
	}
	
	public String getMessagePerm(CommandSender s, LocaleEnum index, String perm, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, HORSE_ID, LANG, MAX, perm, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessagePlayer(CommandSender s, LocaleEnum index, String player, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, HORSE_ID, LANG, MAX, PERM, player, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessagePlayerSpacer(CommandSender s, LocaleEnum index, String player, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, HORSE_ID, LANG, MAX, PERM, player, spacer, VALUE, hidePrefix);
	}
	
	public String getMessagePlayerValue(CommandSender s, LocaleEnum index, String player, String value, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, HORSE_ID, LANG, MAX, PERM, player, SPACER, value, hidePrefix);
	}
	
	public String getMessageSpacer(CommandSender s, LocaleEnum index, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, spacer, VALUE, hidePrefix);
	}
	
	public String getMessageSpacerValue(CommandSender s, LocaleEnum index, int spacer, String value, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, spacer, value, hidePrefix);
	}
	
	public String getMessageValue(CommandSender s, LocaleEnum index, String value, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, COST, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, SPACER, value, hidePrefix);
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
	
	public void sendMessageAmountSpacer(CommandSender s, LocaleEnum index, int amount, int spacer) {
		s.sendMessage(getMessageAmountSpacer(s, index, amount, spacer, false));
	}
	
	public void sendMessageAmountSpacer(CommandSender s, LocaleEnum index, int amount, int spacer, boolean hidePrefix) {
		s.sendMessage(getMessageAmountSpacer(s, index, amount, spacer, hidePrefix));
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
	
	public void sendMessageHorseHorseIDSpacer(CommandSender s, LocaleEnum index, String horse, String horseID, int spacer) {
		s.sendMessage(getMessageHorseHorseIDSpacer(s, index, horse, horseID, spacer, false));
	}
	
	public void sendMessageHorseHorseIDSpacer(CommandSender s, LocaleEnum index, String horse, String horseID, int spacer, boolean hidePrefix) {
		s.sendMessage(getMessageHorseHorseIDSpacer(s, index, horse, horseID, spacer, hidePrefix));
	}
	
	public void sendMessageHorseHorseIDSpacerValue(CommandSender s, LocaleEnum index, String horse, String horseID, int spacer, String value) {
		s.sendMessage(getMessageHorseHorseIDSpacerValue(s, index, horse, horseID, spacer, value, false));
	}
	
	public void sendMessageHorseHorseIDSpacerValue(CommandSender s, LocaleEnum index, String horse, String horseID, int spacer, String value, boolean hidePrefix) {
		s.sendMessage(getMessageHorseHorseIDSpacerValue(s, index, horse, horseID, spacer, value, hidePrefix));
	}
	
	public void sendMessageHorseID(CommandSender s, LocaleEnum index, String horseID) {
		s.sendMessage(getMessageHorseID(s, index, horseID, false));
	}
	
	public void sendMessageHorseID(CommandSender s, LocaleEnum index, String horseID, boolean hidePrefix) {
		s.sendMessage(getMessageHorseID(s, index, horseID, hidePrefix));
	}
	
	public void sendMessageHorseIDPlayer(CommandSender s, LocaleEnum index, String horseID, String player) {
		s.sendMessage(getMessageHorseIDPlayer(s, index, horseID, player, false));
	}
	
	public void sendMessageHorseIDPlayer(CommandSender s, LocaleEnum index, String horseID, String player, boolean hidePrefix) {
		s.sendMessage(getMessageHorseIDPlayer(s, index, horseID, player, hidePrefix));
	}
	
	public void sendMessageHorseIDSpacer(CommandSender s, LocaleEnum index, String horseID, int spacer) {
		s.sendMessage(getMessageHorseIDSpacer(s, index, horseID, spacer, false));
	}
	
	public void sendMessageHorseIDSpacer(CommandSender s, LocaleEnum index, String horseID, int spacer, boolean hidePrefix) {
		s.sendMessage(getMessageHorseIDSpacer(s, index, horseID, spacer, hidePrefix));
	}
	
	public void sendMessageHorseMax(CommandSender s, LocaleEnum index, String horse, int max) {
		s.sendMessage(getMessageHorseMax(s, index, horse, max, false));
	}
	
	public void sendMessageHorseMax(CommandSender s, LocaleEnum index, String horse, int max, boolean hidePrefix) {
		s.sendMessage(getMessageHorseMax(s, index, horse, max, hidePrefix));
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
	
	public void sendMessageValue(CommandSender s, LocaleEnum index, String value) {
		s.sendMessage(getMessageValue(s, index, value, false));
	}
	
	public void sendMessageValue(CommandSender s, LocaleEnum index, String value, boolean hidePrefix) {
		s.sendMessage(getMessageValue(s, index, value, hidePrefix));
	}
	
	private String getMessageFull(CommandSender s, LocaleEnum index, int amount, int cost, String horse, String horseID, String lang, int max, String perm, String player, int spacer, String value, boolean hidePrefix) {
		String rawMessage;
		if (cost <= 0) {
			rawMessage = getSpace(spacer) + getFromLocale(s, index, hidePrefix);
		}
		else {
			String costMessage = getSpace(spacer) + getMessageAmountValue(s, LocaleEnum.commandCost, cost, value, hidePrefix);
			rawMessage = getSpace(spacer) + getFromLocale(s, index, hidePrefix) + costMessage;
		}
		String message = populateFlags(rawMessage, amount, horse, horseID, lang, max, perm, player, value);
		return message;
	}
	
	private String getFromLocale(CommandSender s, LocaleEnum index, boolean hidePrefix) {
		String language = s instanceof Player ? zh.getDM().getPlayerLanguage(((Player) s).getUniqueId()) : zh.getCM().getDefaultLanguage();
		return zh.getLM().getMessage(index.getIndex(), language, hidePrefix);
	}
	
	private String getSpace(int spacer) {
		String space = "";
		while (space.length() < spacer) {
			space += " ";
		}
		return space;
	}
	
	private String populateFlags(String rawMessage, int amount, String horse, String horseID, String lang, int max, String perm, String player, String value) {
		String message = rawMessage;
		message = message.replace(KeyWordEnum.amountFlag.getValue(), Integer.toString(amount));
		message = message.replace(KeyWordEnum.horseFlag.getValue(), horse);
		message = message.replace(KeyWordEnum.horseIDFlag.getValue(), horseID);	
		message = message.replace(KeyWordEnum.langFlag.getValue(), lang);
		message = message.replace(KeyWordEnum.maxFlag.getValue(), Integer.toString(max));
		message = message.replace(KeyWordEnum.permFlag.getValue(), perm);
		message = message.replace(KeyWordEnum.playerFlag.getValue(), player);			
		message = message.replace(KeyWordEnum.valueFlag.getValue(), value);		
		message = applyColors(message);
		return message;
	}
	
	public static String applyColors(String rawMessage) {
		String message = rawMessage;
		for (ColorEnum color : ColorEnum.values()) {
			for (String code : color.getCodes()) {
				message = message.replaceAll("(?i)" + code, color.getColor().toString()); // (?i) makes replaceAll case insensitive
			}
		}
		return message;
	}
	
	public String applyColors(String message, String colorCode) {
		return applyColors(colorCode + message);
	}
	
	public String removeColors(String rawMessage) {
		String message = rawMessage;
		for (ColorEnum color : ColorEnum.values()) {
			for (String code : color.getCodes()) {
				message = message.replaceAll("(?i)" + code, ""); // (?i) makes replaceAll case insensitive
			}
		}
		return message;
	}
	
	public static boolean isColor(String colorCode) {
		return !colorCode.equals(applyColors((colorCode)));
	}
	
	public static boolean isColorized(String message) {
		return !(message.isEmpty() || message.equals(applyColors((message))));
	}

}
