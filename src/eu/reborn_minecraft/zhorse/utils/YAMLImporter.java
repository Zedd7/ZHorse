package eu.reborn_minecraft.zhorse.utils;

import java.io.File;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;

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
		ConfigurationSection cs = db.getConfigurationSection(KeyWordEnum.players.getValue());
		if (cs != null) {
			for (String playerKey : cs.getKeys(false)) {
				UUID playerUUID = UUID.fromString(playerKey);
				success &= importPlayer(zh, db, playerUUID);
				success &= importHorses(zh, db, playerUUID);
			}
		}
		return success;
	}
	
	private static boolean importPlayer(ZHorse zh, FileConfiguration db, UUID playerUUID) {
		if (zh.getDM().isPlayerRegistered(playerUUID)) {
			return true;
		}
		String playerName = getPlayerData(db, playerUUID, KeyWordEnum.name.getValue(), null);
		String language = getPlayerData(db, playerUUID, KeyWordEnum.language.getValue(), zh.getCM().getDefaultLanguage());
		String favoriteString = getPlayerData(db, playerUUID, KeyWordEnum.favorite.getValue(), null);
		Integer favorite = favoriteString != null ? Integer.parseInt(favoriteString) : zh.getDM().getDefaultFavoriteHorseID();
		return zh.getDM().registerPlayer(playerUUID, playerName, language, favorite);
	}

	private static String getPlayerData(FileConfiguration db, UUID playerUUID, String valuePath, String defaultValue) {
		String dataPath = KeyWordEnum.players.getValue() + KeyWordEnum.dot.getValue() + playerUUID + KeyWordEnum.dot.getValue() + valuePath;
		return db.getString(dataPath, defaultValue);
	}
	
	private static boolean importHorses(ZHorse zh, FileConfiguration db, UUID playerUUID) {
		boolean success = true;
		String horsesPath = KeyWordEnum.players.getValue() + KeyWordEnum.dot.getValue() + playerUUID + KeyWordEnum.dot.getValue() + KeyWordEnum.horses.getValue();
		ConfigurationSection cs = db.getConfigurationSection(horsesPath);
		if (cs != null) {
			for (String horseKey : cs.getKeys(false)) {
				int horseID = Integer.parseInt(horseKey);
				success &= importHorse(zh, db, playerUUID, horseID);
			}
		}
		return success;
	}
	
	private static boolean importHorse(ZHorse zh, FileConfiguration db, UUID ownerUUID, int horseID) {
		UUID horseUUID = UUID.fromString(getHorseStringData(db, ownerUUID, horseID, KeyWordEnum.uuid.getValue(), null));
		if (zh.getDM().isHorseRegistered(horseUUID)) {
			return true;
		}
		String horseName = getHorseStringData(db, ownerUUID, horseID, KeyWordEnum.name.getValue(), null);
		boolean modeLocked = getHorseBooleanData(db, ownerUUID, horseID, KeyWordEnum.modeLocked.getValue(), false);
		boolean modeProtected = getHorseBooleanData(db, ownerUUID, horseID, KeyWordEnum.modeProtected.getValue(), false);
		boolean modeShared = getHorseBooleanData(db, ownerUUID, horseID, KeyWordEnum.modeShared.getValue(), false);
		String locationWorld = getHorseStringData(db, ownerUUID, horseID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.world.getValue(), null);
		int locationX = Integer.parseInt(getHorseStringData(db, ownerUUID, horseID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.x.getValue(), null));
		int locationY = Integer.parseInt(getHorseStringData(db, ownerUUID, horseID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.y.getValue(), null));
		int locationZ = Integer.parseInt(getHorseStringData(db, ownerUUID, horseID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.z.getValue(), null));
		return zh.getDM().registerHorse(horseUUID, ownerUUID, horseID, horseName, modeLocked, modeProtected, modeShared, locationWorld, locationX, locationY, locationZ);
	}
	
	private static boolean getHorseBooleanData(FileConfiguration db, UUID playerUUID, int horseID, String valuePath, boolean defaultValue) {
		String horsesPath = KeyWordEnum.players.getValue() + KeyWordEnum.dot.getValue() + playerUUID + KeyWordEnum.dot.getValue() + KeyWordEnum.horses.getValue();
		String dataPath = horsesPath + KeyWordEnum.dot.getValue() + horseID + KeyWordEnum.dot.getValue() + valuePath;
		return db.getBoolean(dataPath, defaultValue);
	}

	private static String getHorseStringData(FileConfiguration db, UUID ownerUUID, int horseID, String valuePath, String defaultValue) {
		String horsesPath = KeyWordEnum.players.getValue() + KeyWordEnum.dot.getValue() + ownerUUID + KeyWordEnum.dot.getValue() + KeyWordEnum.horses.getValue();
		String dataPath = horsesPath + KeyWordEnum.dot.getValue() + horseID + KeyWordEnum.dot.getValue() + valuePath;
		return db.getString(dataPath, defaultValue);
	}

}
