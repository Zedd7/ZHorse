package com.github.zedd7.zhorse.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.database.FriendRecord;
import com.github.zedd7.zhorse.enums.FriendSubCommandEnum;
import com.github.zedd7.zhorse.enums.KeyWordEnum;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.CallbackListener;
import com.github.zedd7.zhorse.utils.CallbackResponse;
import com.github.zedd7.zhorse.utils.MessageConfig;

import net.md_5.bungee.api.ChatColor;

public class CommandFriend extends AbstractCommand {

	private String fullCommand;
	private String subCommand;

	public CommandFriend(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled()) {
			if (!idMode) {
				execute();
			}
			else {
				sendCommandUsage();
			}
		}
	}

	private void execute() {
		if (!args.isEmpty()) {
			subCommand = args.get(0);
			args.remove(0); // Remove sub-command to allow parsing of playerName
			if (subCommand.equalsIgnoreCase(FriendSubCommandEnum.ADD.name())) {
				fullCommand = command + KeyWordEnum.DOT.getValue() + FriendSubCommandEnum.ADD.name().toLowerCase();
				addFriend();
			}
			else if (subCommand.equalsIgnoreCase(FriendSubCommandEnum.LIST.name())) {
				fullCommand = command + KeyWordEnum.DOT.getValue() + FriendSubCommandEnum.LIST.name().toLowerCase();
				sendFriendList();
			}
			else if (subCommand.equalsIgnoreCase(FriendSubCommandEnum.REMOVE.name())) {
				fullCommand = command + KeyWordEnum.DOT.getValue() + FriendSubCommandEnum.REMOVE.name().toLowerCase();
				removeFriend();
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_SUB_COMMAND) {{ setValue(subCommand); setValue(command); }});
				sendSubCommandDescriptionList(FriendSubCommandEnum.class);
			}
		}
		else {
			sendSubCommandDescriptionList(FriendSubCommandEnum.class);
		}
	}

	private void addFriend() {
		if (hasPermission(s, fullCommand , true, false)) {
			parsePlayerName();
			if (targetMode) {
				if (isRegistered(targetUUID)) {
					if (isPlayerDifferent()) {
						if (!zh.getDM().isFriendOf(p.getUniqueId(), targetUUID, true, null)) {
							zh.getDM().registerFriend(new FriendRecord(p.getUniqueId().toString(), targetUUID.toString()), false, new CallbackListener<Boolean>() {

								@Override
								public void callback(CallbackResponse<Boolean> response) {
									if (response.getResult()) {
										zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.FRIEND_ADDED) {{ setPlayerName(targetName); }});
										zh.getMM().sendPendingMessage(targetUUID, new MessageConfig(LocaleEnum.FRIEND_ADDED_REVERSE) {{ setPlayerName(p.getName()); }});
										zh.getCmdM().updateCommandHistory(s, command);
										zh.getEM().payCommand(p, command);
									}
								}

							});
						}
						else {
							zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.FRIEND_ALREADY_ADDED) {{ setPlayerName(targetName); }});
						}
					}
				}
			}
			else {
				sendCommandUsage(subCommand, true, false);
			}
		}
	}

	private void removeFriend() {
		if (hasPermission(s, fullCommand , true, false)) {
			parsePlayerName();
			if (targetMode) {
				if (isRegistered(targetUUID)) {
					if (isPlayerDifferent()) {
						if (zh.getDM().isFriendOf(p.getUniqueId(), targetUUID, true, null)) {
							zh.getDM().removeFriend(p.getUniqueId(), targetUUID, false, new CallbackListener<Boolean>() {

								@Override
								public void callback(CallbackResponse<Boolean> response) {
									if (response.getResult()) {
										zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.FRIEND_REMOVED) {{ setPlayerName(targetName); }});
										zh.getMM().sendPendingMessage(targetUUID, new MessageConfig(LocaleEnum.FRIEND_REMOVED_REVERSE) {{ setPlayerName(p.getName()); }});
										zh.getCmdM().updateCommandHistory(s, command);
										zh.getEM().payCommand(p, command);
									}
								}

							});
						}
						else {
							zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_FRIEND) {{ setPlayerName(targetName); }});
						}
					}
				}
			}
			else {
				sendCommandUsage(subCommand, true, false);
			}
		}
	}

	private void sendFriendList() {
		if (hasPermission(s, fullCommand , true, false)) {
			parsePlayerName();
			List<String> friendNameList = zh.getDM().getFriendNameList(targetUUID, true, null);
			List<String> friendNameReverseList = zh.getDM().getFriendNameReverseList(targetUUID, true, null);
			if (samePlayer) {
				if (friendNameList.size() > 0) {
					displayFriendNames(LocaleEnum.FRIEND_LIST, friendNameList);
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.NO_FRIEND));
				}
				if (friendNameReverseList.size() > 0) {
					displayFriendNames(LocaleEnum.FRIEND_LIST_REVERSE, friendNameReverseList);
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.NO_FRIEND_REVERSE));
				}
			}
			else {
				if (friendNameList.size() > 0) {
					displayFriendNames(LocaleEnum.FRIEND_LIST_OTHER, friendNameList);
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.NO_FRIEND_OTHER) {{ setPlayerName(targetName); }});
				}
				if (friendNameReverseList.size() > 0) {
					displayFriendNames(LocaleEnum.FRIEND_LIST_REVERSE_OTHER, friendNameReverseList);
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.NO_FRIEND_REVERSE_OTHER) {{ setPlayerName(targetName); }});
				}
			}
			zh.getCmdM().updateCommandHistory(s, command);
			zh.getEM().payCommand(p, command);
		}
	}

	private void displayFriendNames(LocaleEnum index, List<String> friendNameList) {
		String friendNameListMessage = "";
		for (int i = 0; i < friendNameList.size(); ++i) {
			final String friendName = friendNameList.get(i);
			friendNameListMessage += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.FRIEND_LIST_FORMAT) {{ setPlayerName(friendName); }}, true);
			if (i < friendNameList.size() - 1) {
				friendNameListMessage += ", ";
			}
		}
		friendNameListMessage += ChatColor.RESET;
		final String message = friendNameListMessage;
		if (samePlayer) {
			zh.getMM().sendMessage(s, new MessageConfig(index) {{ setValue(message); }});
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(index) {{ setPlayerName(targetName); setValue(message); }});
		}
	}


}
