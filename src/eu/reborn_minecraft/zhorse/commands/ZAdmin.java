package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.CommandEnum;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class ZAdmin extends Command {
	String fullCommand;
	String subCommand;

	public ZAdmin(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {			
			if (!idMode) {
				if (isOnHorse(true)) { // sélection du cheval avec ou sans target
					horse = (Horse)p.getVehicle();
					if (isOwner(targetUUID, true, true)) {
						idMode = true;
						userID = zh.getUM().getUserID(targetUUID, horse);
					}
				}
				execute();
			}
			else if (isRegistered(targetUUID, userID)) {
				execute();
			}
		}
	}

	private void execute() {
		if (zh.getEM().canAffordCommand(p, command)) {
			if (!argument.isEmpty()) {
				subCommand = argument.toLowerCase();;
				if (argument.contains(" ")) {
					subCommand = argument.substring(0, argument.indexOf(" "));
				}
				if (subCommand.equals(CommandEnum.clear.name())) {
					clear();
				}
				else {
					if (displayConsole) {
						zh.getMM().sendMessageValue(s, LocaleEnum.unknownAdminCommand, subCommand);
					}
					displayCommandList(zh.getCmdM().getAdminCommandList(), zh.getMM().getMessage(s, LocaleEnum.adminCommandListHeader, true), true);
				}
			}
			else {
				displayCommandList(zh.getCmdM().getAdminCommandList(), zh.getMM().getMessage(s, LocaleEnum.adminCommandListHeader, true), true);
			}
		}
	}
	
	private void clear() {
		fullCommand = command + KeyWordEnum.dot.getValue() + CommandEnum.favorite.getName().toLowerCase();
		if (hasPermission(s, fullCommand , true, false)) {
			if (argument.split(" ").length >= 2) {
				targetMode = true;
				String subArgument = argument.substring(argument.indexOf(" ")+1);
				if (subArgument.split(" ").length >= 2) {
					idMode = true;
					targetName = subArgument.substring(0, subArgument.indexOf(" "));
					userID = subArgument.substring(subArgument.indexOf(" ")+1);
				}
				else {
					targetName = subArgument;
				}
				targetUUID = getPlayerUUID(targetName);
				samePlayer = playerCommand && p.getUniqueId().equals(targetUUID);
			}
			if (targetMode) {
				if (!idMode) {
					if (isRegistered(targetUUID)) {
						if (zh.getUM().unRegisterPlayer(targetUUID)) {
							zh.getUM().saveFavorite(targetUUID, zh.getUM().getDefaultFavoriteUserID());
							if (samePlayer) {
								zh.getMM().sendMessage(s, LocaleEnum.playerCleared);
							}
							else {
								zh.getMM().sendMessagePlayer(s, LocaleEnum.playerClearedOther, targetName);
							}
							zh.getEM().payCommand(p, command);
						}
					}
				}
				else if (isRegistered(targetUUID, userID)) {
					if (zh.getUM().unRegisterHorse(targetUUID, userID)) {
						if (samePlayer) {
							zh.getMM().sendMessageHorse(s, LocaleEnum.horseCleared, horseName);
						}
						else {
							zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.horseClearedOther, horseName, targetName);
						}
						zh.getEM().payCommand(p, command);
					}
				}
			}
			else if (displayConsole) {
				zh.getMM().sendMessage(s, LocaleEnum.missingTarget);
				sendCommandUsage(subCommand, true);
			}
		}
	}

}
