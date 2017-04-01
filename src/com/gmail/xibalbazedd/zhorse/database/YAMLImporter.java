package com.gmail.xibalbazedd.zhorse.database;

import java.io.File;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.utils.Utf8YamlConfiguration;

public class YAMLImporter {
	
	private static final String DB_FILE_NAME = "users.yml";

	public static boolean importData(ZHorse zh) {
		FileConfiguration db = openDatabase(zh);
		if (db != null) {
			return importPlayers(zh, db);
		}
		return false;
	}

	private static FileConfiguration openDatabase(ZHorse zh) {
		File dbFile = new File(zh.getDataFolder(), DB_FILE_NAME);
		if (!dbFile.exists()) {
			zh.getLogger().severe(String.format("No file could be found at \"%s\" !", dbFile.getPath()));
			return null;
		}
		return Utf8YamlConfiguration.loadConfiguration(dbFile);
	}
	
	private static boolean importPlayers(ZHorse zh, FileConfiguration db) {
		boolean success = true;
		ConfigurationSection cs = db.getConfigurationSection("Players");
		if (cs != null) {
			for (String playerUUID : cs.getKeys(false)) {
				success &= importPlayer(zh, db, playerUUID);
				success &= importHorses(zh, db, playerUUID);
			}
		}
		return success;
	}
	
	private static boolean importPlayer(ZHorse zh, FileConfiguration db, String playerUUID) {
		if (zh.getDM().isPlayerRegistered(playerUUID)) {
			return true;
		}
		String playerName = getPlayerData(db, playerUUID, "Name", null);
		String language = getPlayerData(db, playerUUID, "Language", zh.getCM().getDefaultLanguage());
		String favoriteString = getPlayerData(db, playerUUID, "Favorite", null);
		Integer favorite = favoriteString != null ? Integer.parseInt(favoriteString) : zh.getDM().getDefaultFavoriteHorseID();
		PlayerRecord playerRecord = new PlayerRecord(playerUUID, playerName, language, favorite);
		return zh.getDM().registerPlayer(playerRecord);
	}

	private static String getPlayerData(FileConfiguration db, String playerUUID, String valuePath, String defaultValue) {
		String dataPath = "Players" + "." + playerUUID + "." + valuePath;
		return db.getString(dataPath, defaultValue);
	}
	
	private static boolean importHorses(ZHorse zh, FileConfiguration db, String ownerUUID) {
		boolean success = true;
		String horsesPath = "Players" + "." + ownerUUID + "." + "Horses";
		ConfigurationSection cs = db.getConfigurationSection(horsesPath);
		if (cs != null) {
			for (String horseKey : cs.getKeys(false)) {
				int horseID = Integer.parseInt(horseKey);
				success &= importHorse(zh, db, ownerUUID, horseID);
			}
		}
		return success;
	}
	
	private static boolean importHorse(ZHorse zh, FileConfiguration db, String ownerUUID, int horseID) {
		String horseUUID = getHorseStringData(db, ownerUUID, horseID, "UUID", null);
		if (zh.getDM().isHorseRegistered(UUID.fromString(horseUUID))) {
			return true;
		}
		String horseName = getHorseStringData(db, ownerUUID, horseID, "Name", null);
		boolean modeLocked = getHorseBooleanData(db, ownerUUID, horseID, "Locked", false);
		boolean modeProtected = getHorseBooleanData(db, ownerUUID, horseID, "Protected", false);
		boolean modeShared = getHorseBooleanData(db, ownerUUID, horseID, "Shared", false);
		String locationWorld = getHorseStringData(db, ownerUUID, horseID, "Location" + "." + "World", null);
		int locationX = Integer.parseInt(getHorseStringData(db, ownerUUID, horseID, "Location" + "." + "X", null));
		int locationY = Integer.parseInt(getHorseStringData(db, ownerUUID, horseID, "Location" + "." + "Y", null));
		int locationZ = Integer.parseInt(getHorseStringData(db, ownerUUID, horseID, "Location" + "." + "Z", null));
		HorseRecord horseRecord = new HorseRecord(horseUUID, ownerUUID, horseID, horseName, modeLocked, modeProtected, modeShared, locationWorld, locationX, locationY, locationZ);
		return zh.getDM().registerHorse(horseRecord);
	}
	
	private static boolean getHorseBooleanData(FileConfiguration db, String playerUUID, int horseID, String valuePath, boolean defaultValue) {
		String horsesPath = "Players" + "." + playerUUID + "." + "Horses";
		String dataPath = horsesPath + "." + horseID + "." + valuePath;
		return db.getBoolean(dataPath, defaultValue);
	}

	private static String getHorseStringData(FileConfiguration db, String ownerUUID, int horseID, String valuePath, String defaultValue) {
		String horsesPath = "Players" + "." + ownerUUID + "." + "Horses";
		String dataPath = horsesPath + "." + horseID + "." + valuePath;
		return db.getString(dataPath, defaultValue);
	}

}
