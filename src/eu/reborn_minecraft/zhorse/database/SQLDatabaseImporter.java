package eu.reborn_minecraft.zhorse.database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
		if (zh.getDM().isPlayerRegistered(playerUUID)) { // Don't overwrite if already exists
			return true;
		}
		PlayerRecord playerRecord = db.getPlayerRecord(String.format("SELECT * FROM prefix_player WHERE uuid = \"%s\"", playerUUID));
		return zh.getDM().registerPlayer(playerRecord);
	}
	
	private static boolean importHorses(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		for (UUID horseUUID : getHorseUUIDList(db)) {
			success &= importHorse(zh, db, horseUUID);
		}
		return success;
	}
	
	private static boolean importHorse(ZHorse zh, SQLDatabaseConnector db, UUID horseUUID) {
		if (zh.getDM().isHorseRegistered(horseUUID)) { // Don't overwrite if already exists
			return true;
		}
		HorseRecord horseRecord = db.getHorseRecord(String.format("SELECT * FROM prefix_horse WHERE uuid = \"%s\"", horseUUID));
		return zh.getDM().registerHorse(horseRecord);
	}
	
	private static List<UUID> getPlayerUUIDList(SQLDatabaseConnector db) {
		String query = "SELECT uuid FROM prefix_player";
		List<String> playerUUIDStringList = db.getStringResultList(query);
		List<UUID> playerUUIDList = new ArrayList<UUID>();
		for (String playerUUIDString : playerUUIDStringList) {
			playerUUIDList.add(UUID.fromString(playerUUIDString));
		}
		return playerUUIDList;
	}
	
	private static List<UUID> getHorseUUIDList(SQLDatabaseConnector db) {
		String query = "SELECT uuid FROM prefix_horse";
		List<String> horseUUIDStringList = db.getStringResultList(query);
		List<UUID> horseUUIDList = new ArrayList<UUID>();
		for (String horseUUIDString : horseUUIDStringList) {
			horseUUIDList.add(UUID.fromString(horseUUIDString));
		}
		return horseUUIDList;
	}

}
