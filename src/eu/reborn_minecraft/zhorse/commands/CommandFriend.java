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
				subCommand = argument.contains(" ") ? argument.substring(0, argument.indexOf(" ")) : argument;
				if (subCommand.equalsIgnoreCase(CommandFriendEnum.ADD.getName())) {
					addFriend();
				}
				else if (subCommand.equalsIgnoreCase(CommandFriendEnum.LIST.getName())) {
					sendFriendList();
				}
				else if (subCommand.equalsIgnoreCase(CommandFriendEnum.REMOVE.getName())) {
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
			argument = argument.split(" ").length >= 2 ? argument.substring(argument.indexOf(" ") + 1) : "";
			if (applyArgumentToTarget()) {
				if (targetMode) {
					if (isRegistered(targetUUID)) {
						if (isPlayerDifferent()) {
							if (!zh.getDM().isFriendOf(p.getUniqueId(), targetUUID)) {
								zh.getDM().registerFriend(p.getUniqueId(), targetUUID);
								zh.getMM().sendMessagePlayer(s, LocaleEnum.friendAdded, targetName);
								zh.getEM().payCommand(p, command);
							}
							else if (displayConsole) {
								zh.getMM().sendMessagePlayer(s, LocaleEnum.friendAlreadyAdded, targetName);
							}
						}
					}
				}
				else {
					sendCommandUsage(subCommand, true, false);
				}
			}
		}
	}
	
	private void removeFriend() {
		fullCommand = command + KeyWordEnum.dot.getValue() + CommandFriendEnum.REMOVE.getName();
		if (hasPermission(s, fullCommand , true, false)) {
			argument = argument.split(" ").length >= 2 ? argument.substring(argument.indexOf(" ") + 1) : "";
			if (applyArgumentToTarget()) {
				if (targetMode) {
					if (isPlayerDifferent()) {
						if (isRegistered(targetUUID)) {
							if (zh.getDM().isFriendOf(p.getUniqueId(), targetUUID)) {
								zh.getDM().removeFriend(p.getUniqueId(), targetUUID);
								zh.getMM().sendMessagePlayer(s, LocaleEnum.friendRemoved, targetName);
								zh.getEM().payCommand(p, command);
							}
							else if (displayConsole) {
								zh.getMM().sendMessagePlayer(s, LocaleEnum.unknownFriend, targetName);
							}
						}
					}
				}
				else {
					sendCommandUsage(subCommand, true, false);
				}
			}
		}
	}

	private void sendFriendList() {
		fullCommand = command + KeyWordEnum.dot.getValue() + CommandFriendEnum.LIST.getName();
		if (hasPermission(s, fullCommand , true, false)) {
			argument = argument.split(" ").length >= 2 ? argument.substring(argument.indexOf(" ") + 1) : "";
			if (applyArgumentToTarget()) {
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
	}
	

}
