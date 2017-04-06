package com.gmail.xibalbazedd.zhorse.managers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.enums.ColorEnum;
import com.gmail.xibalbazedd.zhorse.enums.KeyWordEnum;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

public class MessageManager {
	
	private ZHorse zh;
	private boolean displayConsole;
	
	public MessageManager(ZHorse zh) {
		this.zh = zh;
		displayConsole = !zh.getCM().isConsoleMuted();
	}
	
	public void sendMessage(Player player, String message) {
		sendMessage((CommandSender) player, message);
	}
	
	public void sendMessage(CommandSender recipient, String message) {
		if (displayConsole) {
			recipient.sendMessage(message);
		}
	}
	
	public void sendMessage(Player recipient, MessageConfig messageConfig) {
		sendMessage(recipient, messageConfig, false);
	}
	
	public void sendMessage(Player recipient, MessageConfig messageConfig, boolean hidePrefix) {
		sendMessage((CommandSender) recipient, messageConfig, hidePrefix);
	}
	
	public void sendMessage(CommandSender recipient, MessageConfig messageConfig) {
		sendMessage(recipient, messageConfig, false);
	}
	
	public void sendMessage(CommandSender recipient, MessageConfig messageConfig, boolean hidePrefix) {
		if (displayConsole) {
			String message = getMessage(recipient, messageConfig, hidePrefix);
			recipient.sendMessage(message);
		}
	}
	
	public String getMessage(CommandSender recipient, MessageConfig messageConfig, boolean hidePrefix) {
		String language = getLanguage(recipient);
		String rawMessage = craftSpaces(messageConfig.getSpaceCount()) + getRawMessage(messageConfig, language, hidePrefix);
		String message = populateFlags(rawMessage, messageConfig);
		return message;
	}
	
	private String getLanguage(CommandSender recipient) {
		return recipient instanceof Player ? zh.getDM().getPlayerLanguage(((Player) recipient).getUniqueId()) : zh.getCM().getDefaultLanguage();
	}
	
	private String getRawMessage(MessageConfig messageConfig, String language, boolean hidePrefix) {
		return zh.getLM().getMessage(messageConfig.getIndex(), language, hidePrefix);
	}
	
	private String craftSpaces(int spaceCount) {
		String spaces = "";
		while (spaces.length() < spaceCount) {
			spaces += " ";
		}
		return spaces;
	}
	
	private String populateFlags(String rawMessage, MessageConfig messageConfig) {
		String message = rawMessage;
		message = populateFlag(message, KeyWordEnum.AMOUNT_FLAG, messageConfig.getAmount());
		message = populateFlag(message, KeyWordEnum.CURRENCY_SYMBOL_FLAG, messageConfig.getCurrencySymbol());
		message = populateFlag(message, KeyWordEnum.HORSE_NAME_FLAG, messageConfig.getHorseName());
		message = populateFlag(message, KeyWordEnum.HORSE_ID_FLAG, messageConfig.getHorseID());	
		message = populateFlag(message, KeyWordEnum.LANGUAGE_FLAG, messageConfig.getLanguage());
		message = populateFlag(message, KeyWordEnum.MAX_FLAG, messageConfig.getMax());
		message = populateFlag(message, KeyWordEnum.PERMISSION_FLAG, messageConfig.getPermission());
		message = populateFlag(message, KeyWordEnum.PLAYER_NAME_FLAG, messageConfig.getPlayerName());			
		message = populateFlag(message, KeyWordEnum.VALUE_FLAG, messageConfig.getValue());		
		
		message = applyColors(message);
		return message;
	}
	
	private String populateFlag(String rawMessage, KeyWordEnum flag, String flagContent) {
		return rawMessage.replace(flag.getValue(), flagContent);
	}
	
	public static String applyColors(String message, String colorCode) {
		return applyColors(colorCode + message);
	}
	
	public static String applyColors(String rawMessage) {
		String message = rawMessage;
		for (ColorEnum color : ColorEnum.values()) {
			for (String code : color.getCodeArray()) {
				message = message.replaceAll("(?i)" + code, color.getColor().toString()); // (?i) makes replaceAll case insensitive
			}
		}
		return message;
	}
	
	public static String removeColors(String rawMessage) {
		String message = rawMessage;
		for (ColorEnum color : ColorEnum.values()) {
			for (String code : color.getCodeArray()) {
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
	
	/*public String getMessage(CommandSender s, LocaleEnum index, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageAmount(CommandSender s, LocaleEnum index, int amount, boolean hidePrefix) {
		return getMessageFull(s, index, amount, CURRENCY, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageAmountCurrency(CommandSender s, LocaleEnum index, int amount, String currency, boolean hidePrefix) {
		return getMessageFull(s, index, amount, currency, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageAmountCurrencyHorse(CommandSender s, LocaleEnum index, int amount, String currency, String horse, boolean hidePrefix) {
		return getMessageFull(s, index, amount, currency, horse, HORSE_ID, LANG, MAX, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageAmountCurrencyHorsePlayer(CommandSender s, LocaleEnum index, int amount, String currency, String horse, String player, boolean hidePrefix) {
		return getMessageFull(s, index, amount, currency, horse, HORSE_ID, LANG, MAX, PERM, player, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageAmountCurrencySpacer(CommandSender s, LocaleEnum index, int amount, String currency, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, amount, currency, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, spacer, VALUE, hidePrefix);
	}
	
	public String getMessageAmountHorsePlayerValue(CommandSender s, LocaleEnum index, int amount, String horse, String player, String value, boolean hidePrefix) {
		return getMessageFull(s, index, amount, CURRENCY, horse, HORSE_ID, LANG, MAX, PERM, player, SPACER, value, hidePrefix);
	}
	
	public String getMessageAmountHorseValue(CommandSender s, LocaleEnum index, int amount, String horse, String value, boolean hidePrefix) {
		return getMessageFull(s, index, amount, CURRENCY, horse, HORSE_ID, LANG, MAX, PERM, PLAYER, SPACER, value, hidePrefix);
	}
	
	public String getMessageAmountMax(CommandSender s, LocaleEnum index, int amount, int max, boolean hidePrefix) {
		return getMessageFull(s, index, amount, CURRENCY, HORSE, HORSE_ID, LANG, max, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageAmountMaxSpacer(CommandSender s, LocaleEnum index, int amount, int max, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, amount, CURRENCY, HORSE, HORSE_ID, LANG, max, PERM, PLAYER, spacer, VALUE, hidePrefix);
	}
	
	public String getMessageAmountSpacer(CommandSender s, LocaleEnum index, int amount, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, amount, CURRENCY, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, spacer, VALUE, hidePrefix);
	}
	
	public String getMessageAmountSpacerValue(CommandSender s, LocaleEnum index, int amount, int spacer, String value, boolean hidePrefix) {
		return getMessageFull(s, index, amount, CURRENCY, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, spacer, value, hidePrefix);
	}
	
	public String getMessageAmountValue(CommandSender s, LocaleEnum index, int amount, String value, boolean hidePrefix) {
		return getMessageFull(s, index, amount, CURRENCY, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, SPACER, value, hidePrefix);
	}
	
	public String getMessageHorse(CommandSender s, LocaleEnum index, String horse, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, horse, HORSE_ID, LANG, MAX, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageHorseID(CommandSender s, LocaleEnum index, String horseID, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, horseID, LANG, MAX, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageHorseIDPlayer(CommandSender s, LocaleEnum index, String horseID, String player, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, horseID, LANG, MAX, PERM, player, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageHorseHorseIDSpacer(CommandSender s, LocaleEnum index, String horse, String horseID, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, horse, horseID, LANG, MAX, PERM, PLAYER, spacer, VALUE, hidePrefix);
	}
	
	public String getMessageHorseIDSpacer(CommandSender s, LocaleEnum index, String horseID, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, horseID, LANG, MAX, PERM, PLAYER, spacer, VALUE, hidePrefix);
	}
	
	public String getMessageHorseHorseIDSpacerValue(CommandSender s, LocaleEnum index, String horse, String horseID, int spacer, String value, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, horse, horseID, LANG, MAX, PERM, PLAYER, spacer, value, hidePrefix);
	}
	
	public String getMessageHorseMax(CommandSender s, LocaleEnum index, String horse, int max, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, horse, HORSE_ID, LANG, max, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageHorsePlayer(CommandSender s, LocaleEnum index, String horse, String player, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, horse, HORSE_ID, LANG, MAX, PERM, player, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageHorseSpacer(CommandSender s, LocaleEnum index, String horse, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, horse, HORSE_ID, LANG, MAX, PERM, PLAYER, spacer, VALUE, hidePrefix);
	}
	
	public String getMessageLang(CommandSender s, LocaleEnum index, String lang, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, HORSE_ID, lang, MAX, PERM, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageLangPlayer(CommandSender s, LocaleEnum index, String lang, String player, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, HORSE_ID, lang, MAX, PERM, player, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessageLangValue(CommandSender s, LocaleEnum index, String lang, String value, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, HORSE_ID, lang, MAX, PERM, PLAYER, SPACER, value, hidePrefix);
	}
	
	public String getMessagePerm(CommandSender s, LocaleEnum index, String perm, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, HORSE_ID, LANG, MAX, perm, PLAYER, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessagePlayer(CommandSender s, LocaleEnum index, String player, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, HORSE_ID, LANG, MAX, PERM, player, SPACER, VALUE, hidePrefix);
	}
	
	public String getMessagePlayerSpacer(CommandSender s, LocaleEnum index, String player, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, HORSE_ID, LANG, MAX, PERM, player, spacer, VALUE, hidePrefix);
	}
	
	public String getMessagePlayerValue(CommandSender s, LocaleEnum index, String player, String value, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, HORSE_ID, LANG, MAX, PERM, player, SPACER, value, hidePrefix);
	}
	
	public String getMessageSpacer(CommandSender s, LocaleEnum index, int spacer, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, spacer, VALUE, hidePrefix);
	}
	
	public String getMessageSpacerValue(CommandSender s, LocaleEnum index, int spacer, String value, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, spacer, value, hidePrefix);
	}
	
	public String getMessageValue(CommandSender s, LocaleEnum index, String value, boolean hidePrefix) {
		return getMessageFull(s, index, AMOUNT, CURRENCY, HORSE, HORSE_ID, LANG, MAX, PERM, PLAYER, SPACER, value, hidePrefix);
	}*/
	
	/*public void sendMessage(CommandSender s, LocaleEnum index) {
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
	
	public void sendMessageAmountCurrency(CommandSender s, LocaleEnum index, int amount, String currency) {
		s.sendMessage(getMessageAmountCurrency(s, index, amount, currency, false));
	}
	
	public void sendMessageAmountCurrency(CommandSender s, LocaleEnum index, int amount, String currency, boolean hidePrefix) {
		s.sendMessage(getMessageAmountCurrency(s, index, amount, currency, hidePrefix));
	}
	
	public void sendMessageAmountCurrencyHorse(CommandSender s, LocaleEnum index, int amount, String currency, String horse) {
		s.sendMessage(getMessageAmountCurrencyHorse(s, index, amount, currency, horse, false));
	}
	
	public void sendMessageAmountCurrencyHorse(CommandSender s, LocaleEnum index, int amount, String currency, String horse, boolean hidePrefix) {
		s.sendMessage(getMessageAmountCurrencyHorse(s, index, amount, currency, horse, hidePrefix));
	}
	
	public void sendMessageAmountCurrencyHorsePlayer(CommandSender s, LocaleEnum index, int amount, String currency, String horse, String player) {
		s.sendMessage(getMessageAmountCurrencyHorsePlayer(s, index, amount, currency, horse, player, false));
	}
	
	public void sendMessageAmountCurrencyHorsePlayer(CommandSender s, LocaleEnum index, int amount, String currency, String horse, String player, boolean hidePrefix) {
		s.sendMessage(getMessageAmountCurrencyHorsePlayer(s, index, amount, currency, horse, player, hidePrefix));
	}
	
	public void sendMessageAmountCurrencySpacer(CommandSender s, LocaleEnum index, int amount, String currency, int spacer) {
		s.sendMessage(getMessageAmountCurrencySpacer(s, index, amount, currency, spacer, false));
	}
	
	public void sendMessageAmountCurrencySpacer(CommandSender s, LocaleEnum index, int amount, String currency, int spacer, boolean hidePrefix) {
		s.sendMessage(getMessageAmountCurrencySpacer(s, index, amount, currency, spacer, hidePrefix));
	}
	
	public void sendMessageAmountHorsePlayerValue(CommandSender s, LocaleEnum index, int amount, String horse, String player, String value) {
		s.sendMessage(getMessageAmountHorsePlayerValue(s, index, amount, horse, player, value, false));
	}
	
	public void sendMessageAmountHorsePlayerValue(CommandSender s, LocaleEnum index, int amount, String horse, String player, String value, boolean hidePrefix) {
		s.sendMessage(getMessageAmountHorsePlayerValue(s, index, amount, horse, player, value, hidePrefix));
	}
	
	public void sendMessageAmountHorseValue(CommandSender s, LocaleEnum index, int amount, String horse, String value) {
		s.sendMessage(getMessageAmountHorseValue(s, index, amount, horse, value, false));
	}
	
	public void sendMessageAmountHorseValue(CommandSender s, LocaleEnum index, int amount, String horse, String value, boolean hidePrefix) {
		s.sendMessage(getMessageAmountHorseValue(s, index, amount, horse, value, hidePrefix));
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
	
	public void sendMessageAmountSpacerValue(CommandSender s, LocaleEnum index, int amount, int spacer, String value) {
		s.sendMessage(getMessageAmountSpacerValue(s, index, amount, spacer, value, false));
	}
	
	public void sendMessageAmountSpacerValue(CommandSender s, LocaleEnum index, int amount, int spacer, String value, boolean hidePrefix) {
		s.sendMessage(getMessageAmountSpacerValue(s, index, amount, spacer, value, hidePrefix));
	}
	
	public void sendMessageAmountValue(CommandSender s, LocaleEnum index, int amount, String value) {
		s.sendMessage(getMessageAmountValue(s, index, amount, value, false));
	}
	
	public void sendMessageAmountValue(CommandSender s, LocaleEnum index, int amount, String value, boolean hidePrefix) {
		s.sendMessage(getMessageAmountValue(s, index, amount, value, hidePrefix));
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
	}*/

}
