package com.gmail.xibalbazedd.zhorse.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.FriendRecord;
import com.gmail.xibalbazedd.zhorse.enums.CommandFriendEnum;
import com.gmail.xibalbazedd.zhorse.enums.KeyWordEnum;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;

import net.md_5.bungee.api.ChatColor;

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
								zh.getDM().registerFriend(new FriendRecord(p.getUniqueId().toString(), targetUUID.toString()));
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
				if (displayConsole) {
					List<String> friendNameList = zh.getDM().getFriendNameList(targetUUID);
					List<String> friendNameReverseList = zh.getDM().getFriendNameReverseList(targetUUID);
					if (samePlayer) {
						if (friendNameList.size() > 0) {
							displayFriendNames(LocaleEnum.friendList, friendNameList);
						}
						else {
							zh.getMM().sendMessage(s, LocaleEnum.noFriend);
						}
						if (friendNameReverseList.size() > 0) {
							displayFriendNames(LocaleEnum.friendListReverse, friendNameReverseList);
						}
						else {
							zh.getMM().sendMessage(s, LocaleEnum.noFriendReverse);
						}
					}
					else {
						if (friendNameList.size() > 0) {
							displayFriendNames(LocaleEnum.friendListOther, friendNameList);
						}
						else {
							zh.getMM().sendMessagePlayer(s, LocaleEnum.noFriendOther, targetName);
						}
						if (friendNameReverseList.size() > 0) {
							displayFriendNames(LocaleEnum.friendListReverseOther, friendNameReverseList);
						}
						else {
							zh.getMM().sendMessagePlayer(s, LocaleEnum.noFriendReverseOther, targetName);
						}
					}
				}
				zh.getEM().payCommand(p, command);
			}
		}
	}
	
	private void displayFriendNames(LocaleEnum index, List<String> friendNameList) {
		String friendNameListMessage = "";
		for (int i = 0; i < friendNameList.size(); ++i) {
			friendNameListMessage += zh.getMM().getMessagePlayer(s, LocaleEnum.friendListFormat, friendNameList.get(i), true);
			if (i < friendNameList.size() - 1) {
				friendNameListMessage += ", ";
			}
		}
		friendNameListMessage += ChatColor.RESET;
		if (samePlayer) {
			zh.getMM().sendMessageValue(s, index, friendNameListMessage);
		}
		else {
			zh.getMM().sendMessagePlayerValue(s, index, targetName, friendNameListMessage);
		}
	}
	

}
