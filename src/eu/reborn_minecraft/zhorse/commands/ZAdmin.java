package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.CommandAdminEnum;
import eu.reborn_minecraft.zhorse.enums.DatabaseEnum;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;
import eu.reborn_minecraft.zhorse.utils.YAMLImporter;
import net.md_5.bungee.api.ChatColor;

public class ZAdmin extends Command {
	String fullCommand;
	String subCommand;

	public ZAdmin(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {			
			if (!idMode) {
				if (isOnHorse(true)) { // select horse w/ or w/o target
					horse = (Horse) p.getVehicle();
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
				if (subCommand.equals(CommandAdminEnum.CLEAR.getName())) {
					clear();
				}
				else if (subCommand.equals(CommandAdminEnum.IMPORT.getName())) {
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
		fullCommand = command + KeyWordEnum.dot.getValue() + CommandAdminEnum.CLEAR.getName().toLowerCase();
		if (hasPermission(s, fullCommand , true, false)) {
			if (argument.split(" ").length >= 2) {
				targetMode = true;
				String subArgument = argument.substring(argument.indexOf(" ") + 1);
				if (subArgument.split(" ").length >= 2) {
					idMode = true;
					targetName = subArgument.substring(0, subArgument.indexOf(" "));
					userID = subArgument.substring(subArgument.indexOf(" ") + 1);
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
						for (int userID = 1; userID <= zh.getUM().getClaimsAmount(targetUUID); ++userID) {
							Horse horse = zh.getHM().getHorse(targetUUID, Integer.toString(userID));
							if (horse != null) {
								horse.setCustomName(null);
								horse.setCustomNameVisible(false);
							}
							if (!zh.getUM().unRegisterHorse(targetUUID, Integer.toString(userID))) {
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
				else if (isRegistered(targetUUID, userID)) {
					Horse horse = zh.getHM().getHorse(targetUUID, userID);
					if (horse != null) {
						horse.setCustomName(null);
						horse.setCustomNameVisible(false);
					}
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
	
	private void importDB() {
		fullCommand = command + KeyWordEnum.dot.getValue() + CommandAdminEnum.IMPORT.getName().toLowerCase();
		if (hasPermission(s, fullCommand , true, false)) {
			if (argument.split(" ").length >= 2) {
				String databaseName = argument.substring(argument.indexOf(" ") + 1);
				DatabaseEnum database = null;
				try {
					database = DatabaseEnum.valueOf(databaseName.toUpperCase());
				} catch (Exception e) {}
				if (database != null) {
					boolean success = false;
					switch (database) {
					case YAML:
						success = YAMLImporter.importData(zh);
						break;
					default:
						zh.getLogger().severe(String.format("Data import from %s database is not supported yet !", database.getName()));
					}
					if (displayConsole) {
						if (success) {
							zh.getMM().sendMessageValue(s, LocaleEnum.databaseImportSuccess, database.getName());
						}
						else {
							zh.getMM().sendMessageValue(s, LocaleEnum.databaseImportFailure, database.getName());
						}
					}
				}
				else if (displayConsole) {
					displayAvailableDatabases(LocaleEnum.unknownDatabase);
				}
			}
			else if (displayConsole) {
				displayAvailableDatabases(LocaleEnum.missingDatabase);
				sendCommandUsage(subCommand, true);
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
