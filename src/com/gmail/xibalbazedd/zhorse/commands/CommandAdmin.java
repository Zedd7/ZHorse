package com.gmail.xibalbazedd.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.MySQLImporter;
import com.gmail.xibalbazedd.zhorse.database.SQLiteImporter;
import com.gmail.xibalbazedd.zhorse.database.YAMLImporter;
import com.gmail.xibalbazedd.zhorse.enums.CommandAdminEnum;
import com.gmail.xibalbazedd.zhorse.enums.DatabaseEnum;
import com.gmail.xibalbazedd.zhorse.enums.KeyWordEnum;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;

import net.md_5.bungee.api.ChatColor;

public class CommandAdmin extends AbstractCommand {
	
	private String fullCommand;
	private String subCommand;

	public CommandAdmin(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {			
			if (!idMode) {
				if (isOnHorse(true)) { // select horse w/ or w/o target
					horse = (AbstractHorse) p.getVehicle();
					if (isOwner(targetUUID, true, true)) {
						idMode = true;
						Integer horseIDInt = zh.getDM().getHorseID(horse.getUniqueId());
						horseID = horseIDInt != null ? horseIDInt.toString() : null;
					}
				}
				execute();
			}
			else if (isRegistered(targetUUID, horseID)) {
				execute();
			}
		}
	}

	private void execute() {
		if (zh.getEM().canAffordCommand(p, command)) {
			if (!argument.isEmpty()) {
				subCommand = argument.contains(" ") ? argument.substring(0, argument.indexOf(" ")) : argument;
				if (subCommand.equalsIgnoreCase((CommandAdminEnum.CLEAR.getName()))) {
					clear();
				}
				else if (subCommand.equalsIgnoreCase(CommandAdminEnum.IMPORT.getName())) {
					importDB();
				}
				else {
					if (displayConsole) {
						zh.getMM().sendMessageValue(s, LocaleEnum.UNKNOWN_ADMIN_COMMAND, subCommand);
					}
					sendCommandAdminDescriptionList();
				}
			}
			else {
				sendCommandAdminDescriptionList();
			}
		}
	}

	private void clear() {
		fullCommand = command + KeyWordEnum.DOT.getValue() + CommandAdminEnum.CLEAR.getName();
		if (hasPermission(s, fullCommand , true, false)) {
			if (argument.split(" ").length >= 2) {
				targetMode = true;
				String subArgument = argument.substring(argument.indexOf(" ") + 1);
				if (subArgument.split(" ").length >= 2) {
					idMode = true;
					targetName = subArgument.substring(0, subArgument.indexOf(" "));
					horseID = subArgument.substring(subArgument.indexOf(" ") + 1);
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
						boolean success = true;
						for (int horseID = 1; horseID <= zh.getDM().getHorseCount(targetUUID); horseID++) {
							AbstractHorse horse = zh.getHM().getHorse(targetUUID, horseID);
							if (horse != null) {
								horse.setCustomName(null);
								horse.setCustomNameVisible(false);
							}
							UUID horseUUID = zh.getDM().getHorseUUID(targetUUID, horseID);
							zh.getHM().untrackHorse(horseUUID);
							if (!zh.getDM().removeHorse(horseUUID, targetUUID, horseID)) success = false;
							if (!zh.getDM().removeHorseInventory(horseUUID)) success = false;
							if (!zh.getDM().removeHorseStats(horseUUID)) success = false;
							if (!zh.getDM().removeSale(horseUUID)) success = false;
						}
						if (success) {
							if (samePlayer) {
								zh.getMM().sendMessage(s, LocaleEnum.PLAYED_CLEARED);
							}
							else {
								zh.getMM().sendMessagePlayer(s, LocaleEnum.PLAYER_CLEARED_OTHER, targetName);
							}
							zh.getEM().payCommand(p, command);
						}
					}
				}
				else if (isRegistered(targetUUID, horseID)) {
					AbstractHorse horse = zh.getHM().getHorse(targetUUID, Integer.parseInt(horseID));
					if (horse != null) {
						horse.setCustomName(null);
						horse.setCustomNameVisible(false);
					}
					UUID horseUUID = zh.getDM().getHorseUUID(targetUUID, Integer.parseInt(horseID));
					zh.getHM().untrackHorse(horseUUID);
					boolean success = zh.getDM().removeHorse(horseUUID, targetUUID, Integer.parseInt(horseID));
					success &= zh.getDM().removeHorseInventory(horseUUID);
					success &= zh.getDM().removeHorseStats(horseUUID);
					success &= zh.getDM().removeSale(horseUUID);
					if (success) {
						if (samePlayer) {
							zh.getMM().sendMessageHorse(s, LocaleEnum.HORSE_CLEARED, horseName);
						}
						else {
							zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.HORSE_CLEARED_OTHER, horseName, targetName);
						}
						zh.getEM().payCommand(p, command);
					}
				}
			}
			else if (displayConsole) {
				zh.getMM().sendMessage(s, LocaleEnum.MISSING_TARGET);
				sendCommandUsage(subCommand, true, true);
			}
		}
	}
	
	private void importDB() {
		fullCommand = command + KeyWordEnum.DOT.getValue() + CommandAdminEnum.IMPORT.getName().toLowerCase();
		if (hasPermission(s, fullCommand , true, false)) {
			if (argument.split(" ").length >= 2) {
				String databaseName = argument.substring(argument.indexOf(" ") + 1);
				boolean success = false;
				if (databaseName.equalsIgnoreCase(DatabaseEnum.MYSQL.getName())) {
					zh.getMM().sendMessageValue(s, LocaleEnum.DATABASE_IMPORT_STARTED, databaseName);
					success = MySQLImporter.importData(zh);
				}
				else if (databaseName.equalsIgnoreCase(DatabaseEnum.SQLITE.getName())) {
					zh.getMM().sendMessageValue(s, LocaleEnum.DATABASE_IMPORT_STARTED, databaseName);
					success = SQLiteImporter.importData(zh);
				}
				else if (databaseName.equalsIgnoreCase(DatabaseEnum.YAML.getName())) {
					zh.getMM().sendMessageValue(s, LocaleEnum.DATABASE_IMPORT_STARTED, databaseName);
					success = YAMLImporter.importData(zh);
				}
				else if (displayConsole) {
					displayAvailableDatabases(LocaleEnum.UNKNOWN_DATABASE);
				}
				if (displayConsole) {
					if (success) {
						zh.getMM().sendMessageValue(s, LocaleEnum.DATABASE_IMPORT_SUCCESS, databaseName);
					}
					else {
						zh.getMM().sendMessageValue(s, LocaleEnum.DATABASE_IMPORT_FAILURE, databaseName);
					}
				}
			}
			else if (displayConsole) {
				displayAvailableDatabases(LocaleEnum.MISSING_DATABASE);
				sendCommandUsage(subCommand, true, true);
			}
		}
	}
	
	private void displayAvailableDatabases(LocaleEnum index) {
		DatabaseEnum[] availableDatabaseArray = DatabaseEnum.values();
		String availableDatabasesMessage = "";
		for (int i = 0; i < availableDatabaseArray.length; ++i) {
			availableDatabasesMessage += zh.getMM().getMessageValue(s, LocaleEnum.AVAILABLE_OPTION_FORMAT, availableDatabaseArray[i].getName(), true);
			if (i < availableDatabaseArray.length - 1) {
				availableDatabasesMessage += ", ";
			}
		}
		availableDatabasesMessage += ChatColor.RESET;
		zh.getMM().sendMessageValue(s, index, availableDatabasesMessage);
	}

}
