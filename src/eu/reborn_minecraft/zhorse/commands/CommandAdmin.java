package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.CommandAdminEnum;
import eu.reborn_minecraft.zhorse.enums.DatabaseEnum;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;
import eu.reborn_minecraft.zhorse.utils.MySQLImporter;
import eu.reborn_minecraft.zhorse.utils.SQLiteImporter;
import eu.reborn_minecraft.zhorse.utils.YAMLImporter;
import net.md_5.bungee.api.ChatColor;

public class CommandAdmin extends AbstractCommand {
	
	private String fullCommand;
	private String subCommand;

	public CommandAdmin(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {			
			if (!idMode) {
				if (isOnHorse(true)) { // select horse w/ or w/o target
					horse = (Horse) p.getVehicle();
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
				if (argument.contains(" ")) {
					subCommand = argument.substring(0, argument.indexOf(" "));
				}
				if (subCommand.equalsIgnoreCase((CommandAdminEnum.CLEAR.getName()))) {
					clear();
				}
				else if (subCommand.equalsIgnoreCase(CommandAdminEnum.IMPORT.getName())) {
					importDB();
				}
				else {
					if (displayConsole) {
						zh.getMM().sendMessageValue(s, LocaleEnum.unknownAdminCommand, subCommand);
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
		fullCommand = command + KeyWordEnum.dot.getValue() + CommandAdminEnum.CLEAR.getName();
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
						for (int horseID = 1; horseID <= zh.getDM().getHorseCount(targetUUID); ++horseID) {
							Horse horse = zh.getHM().getHorse(targetUUID, horseID);
							if (horse != null) {
								horse.setCustomName(null);
								horse.setCustomNameVisible(false);
							}
							if (!zh.getDM().removeHorse(targetUUID, horseID)) {
								success = false;
							}
						}
						if (success) {
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
				else if (isRegistered(targetUUID, horseID)) {
					Horse horse = zh.getHM().getHorse(targetUUID, Integer.parseInt(horseID));
					if (horse != null) {
						horse.setCustomName(null);
						horse.setCustomNameVisible(false);
					}
					if (zh.getDM().removeHorse(targetUUID, Integer.parseInt(horseID))) {
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
				sendCommandUsage(subCommand, true, true);
			}
		}
	}
	
	private void importDB() {
		fullCommand = command + KeyWordEnum.dot.getValue() + CommandAdminEnum.IMPORT.getName().toLowerCase();
		if (hasPermission(s, fullCommand , true, false)) {
			if (argument.split(" ").length >= 2) {
				String databaseName = argument.substring(argument.indexOf(" ") + 1);
				boolean success = false;
				if (databaseName.equalsIgnoreCase(DatabaseEnum.MYSQL.getName())) {
					zh.getMM().sendMessageValue(s, LocaleEnum.databaseImportStarted, databaseName);
					success = MySQLImporter.importData(zh);
				}
				else if (databaseName.equalsIgnoreCase(DatabaseEnum.SQLITE.getName())) {
					zh.getMM().sendMessageValue(s, LocaleEnum.databaseImportStarted, databaseName);
					success = SQLiteImporter.importData(zh);
				}
				else if (databaseName.equalsIgnoreCase(DatabaseEnum.YAML.getName())) {
					zh.getMM().sendMessageValue(s, LocaleEnum.databaseImportStarted, databaseName);
					success = YAMLImporter.importData(zh);
				}
				else if (displayConsole) {
					displayAvailableDatabases(LocaleEnum.unknownDatabase);
				}
				if (displayConsole) {
					if (success) {
						zh.getMM().sendMessageValue(s, LocaleEnum.databaseImportSuccess, databaseName);
					}
					else {
						zh.getMM().sendMessageValue(s, LocaleEnum.databaseImportFailure, databaseName);
					}
				}
			}
			else if (displayConsole) {
				displayAvailableDatabases(LocaleEnum.missingDatabase);
				sendCommandUsage(subCommand, true, true);
			}
		}
	}
	
	private void displayAvailableDatabases(LocaleEnum index) {
		DatabaseEnum[] availableDatabaseArray = DatabaseEnum.values();
		String availableDatabasesMessage = "";
		for (int i = 0; i < availableDatabaseArray.length; ++i) {
			availableDatabasesMessage += zh.getMM().getMessageValue(s, LocaleEnum.availableOptionFormat, availableDatabaseArray[i].getName(), true);
			if (i < availableDatabaseArray.length - 1) {
				availableDatabasesMessage += ", ";
			}
		}
		availableDatabasesMessage += ChatColor.RESET;
		zh.getMM().sendMessageValue(s, index, availableDatabasesMessage);
	}

}
