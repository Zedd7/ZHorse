package eu.reborn_minecraft.zhorse.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

import eu.reborn_minecraft.zhorse.ZHorse;

public class SQLDatabaseImporter {
	
	protected static boolean fullImport(ZHorse zh, SQLDatabaseConnector db) {
		return importPlayers(zh, db) && importHorses(zh, db);
	}
	
	private static boolean importPlayers(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		for (UUID playerUUID : getPlayerUUIDList(db)) {
			success &= importPlayer(zh, db, playerUUID);
		}
		return success;
	}
	
	private static boolean importPlayer(ZHorse zh, SQLDatabaseConnector db, UUID playerUUID) {
		if (zh.getDM().isPlayerRegistered(playerUUID)) {
			return true;
		}
		String playerName = db.getStringResult(String.format("SELECT name FROM player WHERE uuid = \"%s\"", playerUUID));
		String language = db.getStringResult(String.format("SELECT language FROM player WHERE uuid = \"%s\"", playerUUID));
		Integer favorite = db.getIntegerResult(String.format("SELECT favorite FROM player WHERE uuid = \"%s\"", playerUUID));
		return zh.getDM().registerPlayer(playerUUID, playerName, language, favorite);
	}
	
	private static boolean importHorses(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		for (UUID horseUUID : getHorseUUIDList(db)) {
			success &= importHorse(zh, db, horseUUID);
		}
		return success;
	}
	
	private static boolean importHorse(ZHorse zh, SQLDatabaseConnector db, UUID horseUUID) {
		if (zh.getDM().isHorseRegistered(horseUUID)) {
			return true;
		}
		UUID ownerUUID = UUID.fromString(db.getStringResult(String.format("SELECT owner FROM horse WHERE uuid = \"%s\"", horseUUID)));
		int horseID = db.getIntegerResult(String.format("SELECT id FROM horse WHERE uuid = \"%s\"", horseUUID));
		String horseName = db.getStringResult(String.format("SELECT name FROM horse WHERE uuid = \"%s\"", horseUUID));
		boolean modeLocked = db.getBooleanResult(String.format("SELECT locked FROM horse WHERE uuid = \"%s\"", horseUUID));
		boolean modeProtected = db.getBooleanResult(String.format("SELECT protected FROM horse WHERE uuid = \"%s\"", horseUUID));
		boolean modeShared = db.getBooleanResult(String.format("SELECT shared FROM horse WHERE uuid = \"%s\"", horseUUID));
		Location location = db.getLocationResult(String.format("SELECT locationWorld, locationX, locationY, locationZ FROM horse WHERE uuid = \"%s\"", horseUUID));
		String locationWorld = location.getWorld().getName();
		int locationX = location.getBlockX();
		int locationY = location.getBlockY();
		int locationZ = location.getBlockZ();
		return zh.getDM().registerHorse(horseUUID, ownerUUID, horseID, horseName, modeLocked, modeProtected, modeShared, locationWorld, locationX, locationY, locationZ);
	}
	
	private static List<UUID> getPlayerUUIDList(SQLDatabaseConnector db) {
		String query = "SELECT uuid FROM player";
		List<String> playerUUIDStringList = db.getStringResultList(query);
		List<UUID> playerUUIDList = new ArrayList<UUID>();
		for (String playerUUIDString : playerUUIDStringList) {
			playerUUIDList.add(UUID.fromString(playerUUIDString));
		}
		return playerUUIDList;
	}
	
	private static List<UUID> getHorseUUIDList(SQLDatabaseConnector db) {
		String query = "SELECT uuid FROM horse";
		List<String> horseUUIDStringList = db.getStringResultList(query);
		List<UUID> horseUUIDList = new ArrayList<UUID>();
		for (String horseUUIDString : horseUUIDStringList) {
			horseUUIDList.add(UUID.fromString(horseUUIDString));
		}
		return horseUUIDList;
	}

}
