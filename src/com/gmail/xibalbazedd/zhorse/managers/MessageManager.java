package com.gmail.xibalbazedd.zhorse.managers;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.PendingMessageRecord;
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
	
	public void sendPendingMessage(UUID recipientUUID, MessageConfig messageConfig) {
		String language = getLanguage(recipientUUID);
		String message = getMessage(language, messageConfig, false);
		OfflinePlayer recipient = zh.getServer().getOfflinePlayer(recipientUUID);
		if (recipient.isOnline()) {
			((Player) recipient).sendMessage(message);
		}
		else {
			PendingMessageRecord messageRecord = new PendingMessageRecord(recipientUUID.toString(), message);
			zh.getDM().registerPendingMessage(messageRecord);
		}
	}
	
	public String getMessage(CommandSender recipient, MessageConfig messageConfig, boolean hidePrefix) {
		String language = getLanguage(recipient);
		return getMessage(language, messageConfig, hidePrefix);
	}
	
	public String getMessage(String language, MessageConfig messageConfig, boolean hidePrefix) {
		String rawMessage = craftSpaces(messageConfig.getSpaceCount()) + getRawMessage(messageConfig, language, hidePrefix);
		String message = populateFlags(rawMessage, messageConfig);
		return message;
	}
	
	private String getLanguage(CommandSender recipient) {
		return recipient instanceof Player ? getLanguage(((Player) recipient).getUniqueId()) : zh.getCM().getDefaultLanguage();
	}
	
	private String getLanguage(UUID recipientUUID) {
		return zh.getDM().getPlayerLanguage(recipientUUID);
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

}
