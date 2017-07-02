package com.github.xibalba.zhorse.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.github.xibalba.zhorse.ZHorse;
import com.github.xibalba.zhorse.database.HorseDeathRecord;
import com.github.xibalba.zhorse.database.MySQLImporter;
import com.github.xibalba.zhorse.database.SQLiteImporter;
import com.github.xibalba.zhorse.database.YAMLImporter;
import com.github.xibalba.zhorse.enums.AdminSubCommandEnum;
import com.github.xibalba.zhorse.enums.DatabaseEnum;
import com.github.xibalba.zhorse.enums.KeyWordEnum;
import com.github.xibalba.zhorse.enums.LocaleEnum;
import com.github.xibalba.zhorse.utils.MessageConfig;

import net.md_5.bungee.api.ChatColor;

public class CommandAdmin extends AbstractCommand {
	
	private String fullCommand;
	private String subCommand;

	public CommandAdmin(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled()) {
			if (!variantMode || isRegistered(horseVariant)) {
				if (!idMode) {
					if (isOnHorse(true)) { // Select horse w/ or w/o target
						horse = (AbstractHorse) p.getVehicle();
						if (isOwner(targetUUID, false, true, true)) {
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
	}

	private void execute() {
		if (!args.isEmpty()) {
			subCommand = args.get(0);
			if (subCommand.equalsIgnoreCase(AdminSubCommandEnum.BURIAL.name())) {
				fullCommand = command + KeyWordEnum.DOT.getValue() + AdminSubCommandEnum.BURIAL.name().toLowerCase();
				burial();
			}
			else if (subCommand.equalsIgnoreCase(AdminSubCommandEnum.CLEAR.name())) {
				fullCommand = command + KeyWordEnum.DOT.getValue() + AdminSubCommandEnum.CLEAR.name().toLowerCase();
				clear();
			}
			else if (subCommand.equalsIgnoreCase(AdminSubCommandEnum.IMPORT.name())) {
				fullCommand = command + KeyWordEnum.DOT.getValue() + AdminSubCommandEnum.IMPORT.name().toLowerCase();
				importDB();
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_SUB_COMMAND) {{ setValue(subCommand); setValue(command); }});
				sendSubCommandDescriptionList(AdminSubCommandEnum.class);
			}
		}
		else {
			sendSubCommandDescriptionList(AdminSubCommandEnum.class);
		}
	}
	
	private void burial() {
		if (hasPermission(s, fullCommand , true, false)) {
			if (!idMode) {
				if (args.size() == 2) {
					targetMode = true;
					targetName = args.get(1);
					targetUUID = getPlayerUUID(targetName);
					samePlayer = playerCommand && p.getUniqueId().equals(targetUUID);
				}
				if (targetMode) {
					if (isRegistered(targetUUID)) {
						boolean success = true;
						List<HorseDeathRecord> horseDeathRecordList = zh.getDM().getHorseDeathRecordList(targetUUID);
						for (HorseDeathRecord deathRecord : horseDeathRecordList) {
							UUID horseUUID = UUID.fromString(deathRecord.getUUID());
							if (!variantMode || zh.getDM().isHorseOfType(horseUUID, variant)) {
								if (!zh.getDM().removeHorse(horseUUID, targetUUID, null)) success = false;
							}
						}
						if (success) {
							if (samePlayer) {
								zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.DEAD_HORSES_CLEARED));
							}
							else {
								zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.DEAD_HORSES_CLEARED_OTHER) {{ setPlayerName(targetName); }});
							}
							zh.getCmdM().updateCommandHistory(s, command);
							zh.getEM().payCommand(p, command);
						}
					}
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.MISSING_TARGET));
					sendCommandUsage(subCommand, true, true);
				}
			}
			else {
				sendCommandUsage(subCommand, true, false);
			}
		}
	}

	private void clear() {
		if (hasPermission(s, fullCommand , true, false)) {
			if (args.size() >= 2) {
				targetMode = true;
				targetName = args.get(1);
				targetUUID = getPlayerUUID(targetName);
				samePlayer = playerCommand && p.getUniqueId().equals(targetUUID);
			}
			if (args.size() >= 3) {
				idMode = true;					
				horseID = args.get(2);
			}
			if (targetMode) {
				if (!idMode) {
					if (isRegistered(targetUUID)) {
						boolean success = true;
						for (UUID horseUUID : zh.getDM().getHorseUUIDList(targetUUID)) {
							if (!variantMode || zh.getDM().isHorseOfType(horseUUID, variant)) {
								int horseID = zh.getDM().getHorseID(horseUUID);
								if (!clearLivingHorse(targetUUID, horseID, variantMode)) success = false;
							}
						}
						if (success) {
							if (samePlayer) {
								zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.LIVING_HORSES_CLEARED));
							}
							else {
								zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.LIVING_HORSES_CLEARED_OTHER) {{ setPlayerName(targetName); }});
							}
							zh.getCmdM().updateCommandHistory(s, command);
							zh.getEM().payCommand(p, command);
						}
					}
				}
				else if (isRegistered(targetUUID, horseID)) {
					boolean success = clearLivingHorse(targetUUID, Integer.parseInt(horseID), true);
					if (success) {
						if (samePlayer) {
							zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_CLEARED) {{ setHorseName(horseName); }});
						}
						else {
							zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_CLEARED_OTHER) {{ setHorseName(horseName); setPlayerName(targetName); }});
						}
						zh.getCmdM().updateCommandHistory(s, command);
						zh.getEM().payCommand(p, command);
					}
				}
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.MISSING_TARGET));
				sendCommandUsage(subCommand, true, true);
			}
		}
	}
	
	private boolean clearLivingHorse(UUID ownerUUID, int horseID, boolean updateHorseIDMapping) {
		AbstractHorse horse = zh.getHM().getHorse(ownerUUID, horseID);
		if (horse != null) {
			horse.setCustomName(null);
			horse.setCustomNameVisible(false);
		}
		UUID horseUUID = zh.getDM().getHorseUUID(ownerUUID, horseID);
		zh.getHM().untrackHorse(horseUUID);
		return zh.getDM().removeHorse(horseUUID, ownerUUID, updateHorseIDMapping ? horseID : null);
	}
	
	private void importDB() {
		if (hasPermission(s, fullCommand , true, false)) {
			if (args.size() >= 2) {
				String databaseName = args.get(1);
				boolean success = false;
				if (databaseName.equalsIgnoreCase(DatabaseEnum.MYSQL.getName())) {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.DATABASE_IMPORT_STARTED) {{ setValue(databaseName); }});
					success = MySQLImporter.importData(zh);
				}
				else if (databaseName.equalsIgnoreCase(DatabaseEnum.SQLITE.getName())) {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.DATABASE_IMPORT_STARTED) {{ setValue(databaseName); }});
					success = SQLiteImporter.importData(zh);
				}
				else if (databaseName.equalsIgnoreCase(DatabaseEnum.YAML.getName())) {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.DATABASE_IMPORT_STARTED) {{ setValue(databaseName); }});
					success = YAMLImporter.importData(zh);
				}
				else {
					displayAvailableDatabases(LocaleEnum.UNKNOWN_DATABASE);
				}
				if (success) {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.DATABASE_IMPORT_SUCCESS) {{ setValue(databaseName); }});
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.DATABASE_IMPORT_FAILURE) {{ setValue(databaseName); }});
				}
				zh.getCmdM().updateCommandHistory(s, command);
				zh.getEM().payCommand(p, command);
			}
			else {
				displayAvailableDatabases(LocaleEnum.MISSING_DATABASE);
				sendCommandUsage(subCommand, true, true);
			}
		}
	}
	
	private void displayAvailableDatabases(LocaleEnum index) {
		DatabaseEnum[] availableDatabaseArray = DatabaseEnum.values();
		String availableDatabasesMessage = "";
		for (int i = 0; i < availableDatabaseArray.length; ++i) {
			final String availableDatabase = availableDatabaseArray[i].getName();
			availableDatabasesMessage += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.AVAILABLE_OPTION_FORMAT) {{ setValue(availableDatabase); }}, true);
			if (i < availableDatabaseArray.length - 1) {
				availableDatabasesMessage += ", ";
			}
		}
		availableDatabasesMessage += ChatColor.RESET;
		final String message = availableDatabasesMessage;
		zh.getMM().sendMessage(s, new MessageConfig(index) {{ setValue(message); }});
	}

}
