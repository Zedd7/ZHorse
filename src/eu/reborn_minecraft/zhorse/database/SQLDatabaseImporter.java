package eu.reborn_minecraft.zhorse.database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.reborn_minecraft.zhorse.ZHorse;

public class SQLDatabaseImporter {
	
	protected static boolean fullImport(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		List<UUID> playerUUIDList = getPlayerUUIDList(db);
		List<UUID> horseUUIDList = getHorseUUIDList(db);
		success &= importPlayers(zh, db, playerUUIDList);
		success &= importFriends(zh, db);
		success &= importHorses(zh, db, horseUUIDList);
		success &= importHorsesStats(zh, db, horseUUIDList);
		success &= importHorsesInventory(zh, db, horseUUIDList);
		return success;
	}
	
	private static boolean importPlayers(ZHorse zh, SQLDatabaseConnector db, List<UUID> playerUUIDList) {
		boolean success = true;
		for (UUID playerUUID : playerUUIDList) {
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
	
	private static boolean importFriends(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		for (FriendRecord friendRecord : getFriendRecordList(db)) { // Loop over records directly because friend relation only consists of primary keys
			success &= importFriend(zh, db, friendRecord);
		}
		return success;
	}
	
	private static boolean importFriend(ZHorse zh, SQLDatabaseConnector db, FriendRecord friendRecord) {
		UUID requesterUUID = UUID.fromString(friendRecord.getRequester());
		UUID recipientUUID = UUID.fromString(friendRecord.getRecipient());
		if (zh.getDM().isFriendOf(requesterUUID, recipientUUID)) { // Don't overwrite if already exists
			return true;
		}
		return zh.getDM().registerFriend(friendRecord);
	}
	
	private static boolean importHorses(ZHorse zh, SQLDatabaseConnector db, List<UUID> horseUUIDList) {
		boolean success = true;
		for (UUID horseUUID : horseUUIDList) {
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
	
	private static boolean importHorsesStats(ZHorse zh, SQLDatabaseConnector db, List<UUID> horseUUIDList) {
		boolean success = true;
		for (UUID horseUUID : horseUUIDList) {
			success &= importHorseStats(zh, db, horseUUID);
		}
		return success;
	}
	
	private static boolean importHorseStats(ZHorse zh, SQLDatabaseConnector db, UUID horseUUID) {
		if (zh.getDM().isHorseStatsRegistered(horseUUID)) { // Don't overwrite if already exists
			return true;
		}
		HorseStatsRecord horseStatsRecord = db.getHorseStatsRecord(String.format("SELECT * FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID));
		return zh.getDM().registerHorseStats(horseStatsRecord);
	}
	
	private static boolean importHorsesInventory(ZHorse zh, SQLDatabaseConnector db, List<UUID> horseUUIDList) {
		boolean success = true;
		for (UUID horseUUID : horseUUIDList) {
			success &= importHorseInventory(zh, db, horseUUID);
		}
		return success;
	}
	
	private static boolean importHorseInventory(ZHorse zh, SQLDatabaseConnector db, UUID horseUUID) {
		if (zh.getDM().isHorseInventoryRegistered(horseUUID)) { // Don't overwrite if already exists
			return true;
		}
		HorseInventoryRecord horseInventoryRecord = db.getHorseInventoryRecord(String.format("SELECT * FROM prefix_horse_inventory WHERE uuid = \"%s\"", horseUUID));
		return zh.getDM().registerHorseInventory(horseInventoryRecord);
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
	
	private static List<FriendRecord> getFriendRecordList(SQLDatabaseConnector db) {
		String query = "SELECT * FROM prefix_friend";
		return db.getFriendRecordList(query);
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
