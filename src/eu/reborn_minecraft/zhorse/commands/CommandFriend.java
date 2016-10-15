package eu.reborn_minecraft.zhorse.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.CommandFriendEnum;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class CommandFriend extends AbstractCommand {
	
	private String fullCommand;
	private String subCommand;

	public CommandFriend(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			if (!idMode) {
				execute();
			}
			else if (displayConsole) {
				sendCommandUsage();
			}
		}
	}
	
	private void execute() {
		if (zh.getEM().canAffordCommand(p, command)) {
			if (!argument.isEmpty()) {
				argument = argument.toLowerCase();
				subCommand = argument.contains(" ") ? argument.substring(0, argument.indexOf(" ")) : argument;
				if (subCommand.equals(CommandFriendEnum.ADD.getName())) {
					addFriend();
				}
				else if (subCommand.equals(CommandFriendEnum.LIST.getName())) {
					sendFriendList();
				}
				else if (subCommand.equals(CommandFriendEnum.REMOVE.getName())) {
					removeFriend();
				}
				else {
					if (displayConsole) {
						zh.getMM().sendMessageValue(s, LocaleEnum.unknownFriendCommand, subCommand);
					}
					sendCommandFriendDescriptionList();
				}
			}
			else {
				sendCommandFriendDescriptionList();
			}
		}
	}
	
	private void addFriend() {
		fullCommand = command + KeyWordEnum.dot.getValue() + CommandFriendEnum.ADD.getName();
		if (hasPermission(s, fullCommand , true, false)) {
			if (argument.split(" ").length >= 2) {
				/*
				String language = argument.substring(argument.indexOf(" ")+1).toUpperCase();
				if (zh.getCM().isLanguageAvailable(language)) {
					if (!zh.getDM().getPlayerLanguage(targetUUID).equals(language)) {
						zh.getDM().updatePlayerLanguage(targetUUID, language);
						if (samePlayer) {
							zh.getMM().sendMessageLang(s, LocaleEnum.languageEdited, language);
						}
						else {
							zh.getMM().sendMessageLangPlayer(s, LocaleEnum.languageEditedOther, language, targetName);
							if (isPlayerOnline(targetUUID, true)) {
								Player target = zh.getServer().getPlayer(targetUUID);
								zh.getMM().sendMessageLang((CommandSender)target, LocaleEnum.languageEdited, language);
							}
						}
						zh.getEM().payCommand(p, command);
					}
					else if (displayConsole) {
						if (samePlayer) {
							zh.getMM().sendMessageLang(s, LocaleEnum.languageAlreadyUsed, language);
						}
						else {
							zh.getMM().sendMessageLangPlayer(s, LocaleEnum.languageAlreadyUsedOther, language, targetName);
						}
					}
				}
				else if (displayConsole) {
					displayAvailableLanguages(LocaleEnum.unknownLanguage, language);
				}
				*/
			}
			else {
				sendCommandUsage(subCommand, true, false);
			}
		}
	}
	
	private void removeFriend() {
		// TODO Auto-generated method stub
		
	}

	private void sendFriendList() {
		List<String> friendNameList = zh.getDM().getFriendNameList(targetUUID);
		if (displayConsole) {
			if (friendNameList.size() > 0) {
				String friendListHeader;
				if (samePlayer) {
					friendListHeader = zh.getMM().getMessage(s, LocaleEnum.friendListHeader, true);
				}
				else {
					friendListHeader = zh.getMM().getMessagePlayer(s, LocaleEnum.friendListOtherHeader, targetName, true);
				}
				zh.getMM().sendMessageValue(s, LocaleEnum.headerFormat, friendListHeader, true);
				for (String friendName : friendNameList) {	
					zh.getMM().sendMessagePlayerSpacer(s, LocaleEnum.friendListFormat, friendName, 1, true);
				}
			}
			else {
				if (samePlayer) {
					zh.getMM().sendMessage(s, LocaleEnum.noFriend);
				}
				else {
					zh.getMM().sendMessagePlayer(s, LocaleEnum.noFriendOther, targetName);
				}
			}
		}
		zh.getEM().payCommand(p, command);
	}
	

}
