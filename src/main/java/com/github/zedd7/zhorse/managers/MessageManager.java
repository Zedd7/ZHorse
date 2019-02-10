package com.github.zedd7.zhorse.managers;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.database.PendingMessageRecord;
import com.github.zedd7.zhorse.enums.ColorEnum;
import com.github.zedd7.zhorse.enums.KeyWordEnum;
import com.github.zedd7.zhorse.utils.CompoundMessage;
import com.github.zedd7.zhorse.utils.MessageConfig;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {

	public static final SimpleDateFormat DATE_FORMAT_TIMESTAMP = new SimpleDateFormat("(HH:mm - dd/MM/yyyy)");
	public static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("dd/MM/yyyy");
	public static final int PAGE_LENGTH = 10;
	public static final int LINE_LENGTH = 53;

	private ZHorse zh;
	private boolean displayConsole;

	public MessageManager(ZHorse zh) {
		this.zh = zh;
	}

	public void setDisplayConsole(boolean displayConsole) {
		this.displayConsole = displayConsole;
	}

	public void sendMessage(Player player, String message) {
		sendMessage((CommandSender) player, message);
	}

	public void sendMessage(CommandSender recipient, String message) {
		if (displayConsole && !isEmpty(message)) {
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
			if (!isEmpty(message)) {
				recipient.sendMessage(message);
			}
		}
	}

	public void sendPendingMessage(OfflinePlayer recipient, String message) {
		if (!isEmpty(message)) {
			if (recipient.isOnline()) {
				((Player) recipient).sendMessage(message);
			}
			else {
				UUID recipientUUID = recipient.getUniqueId();
				PendingMessageRecord messageRecord = new PendingMessageRecord(recipientUUID.toString(), message);
				zh.getDM().registerPendingMessage(messageRecord, false, null);
			}
		}
	}

	public void sendPendingMessage(UUID recipientUUID, MessageConfig messageConfig) {
		String language = getLanguage(recipientUUID);
		String message = getMessage(language, messageConfig, false);
		OfflinePlayer recipient = zh.getServer().getOfflinePlayer(recipientUUID);
		sendPendingMessage(recipient, message);
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

	public String getMessage(CompoundMessage compoundMessage, int pageNumber) {
		String message = "";
		String header = compoundMessage.getHeader(pageNumber);
		if (header != null) {
			message += header + '\n';
		}
		for (String line : compoundMessage.getPage(pageNumber)) {
			if (line != null) {
				message += line + '\n';
			}
		}
		return message;
	}

	private String getLanguage(CommandSender recipient) {
		return recipient instanceof Player ? getLanguage(((Player) recipient).getUniqueId()) : zh.getCM().getDefaultLanguage();
	}

	private String getLanguage(UUID recipientUUID) {
		return zh.getDM().getPlayerLanguage(recipientUUID, true, null);
	}

	private String getRawMessage(MessageConfig messageConfig, String language, boolean hidePrefix) {
		return zh.getLM().getMessage(messageConfig.getIndex(), language, hidePrefix);
	}

	private boolean isEmpty(String message) {
		return message.trim().length() == 0;
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
		message = populateFlag(message, KeyWordEnum.AMOUNT_FLAG, messageConfig.getAmountList());
		message = populateFlag(message, KeyWordEnum.CURRENCY_SYMBOL_FLAG, messageConfig.getCurrencySymbolList());
		message = populateFlag(message, KeyWordEnum.HORSE_NAME_FLAG, messageConfig.getHorseNameList());
		message = populateFlag(message, KeyWordEnum.HORSE_ID_FLAG, messageConfig.getHorseIDList());
		message = populateFlag(message, KeyWordEnum.LANGUAGE_FLAG, messageConfig.getLanguageList());
		message = populateFlag(message, KeyWordEnum.MAX_FLAG, messageConfig.getMaxList());
		message = populateFlag(message, KeyWordEnum.PERMISSION_FLAG, messageConfig.getPermissionList());
		message = populateFlag(message, KeyWordEnum.PLAYER_NAME_FLAG, messageConfig.getPlayerNameList());
		message = populateFlag(message, KeyWordEnum.VALUE_FLAG, messageConfig.getValueList());

		message = applyColors(message);
		return message;
	}

	private String populateFlag(String rawMessage, KeyWordEnum flag, List<String> flagContentList) {
		String populatedMessage = rawMessage;
		for (String flagContent : flagContentList) {
			String nonRegexFlagValue = Pattern.quote(flag.getValue());
			String nonRegexFlagContent = Matcher.quoteReplacement(flagContent);
			populatedMessage = populatedMessage.replaceFirst(nonRegexFlagValue, nonRegexFlagContent);
		}
		return populatedMessage;
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

	public static String removeColorCodes(String coloredMessage) {
		String rawMessage = coloredMessage;
		for (ColorEnum color : ColorEnum.values()) {
			for (String code : color.getCodeArray()) {
				rawMessage = rawMessage.replaceAll("(?i)" + code, ""); // (?i) makes replaceAll case insensitive
			}
		}
		return rawMessage;
	}

	public static String removeChatColors(String coloredMessage) {
		return ChatColor.stripColor(coloredMessage);
	}

	public static boolean isColor(String colorCode) {
		return !colorCode.equals(applyColors((colorCode)));
	}

	public static boolean isColorized(String message) {
		return !(message.isEmpty() || message.equals(applyColors((message))));
	}

}
