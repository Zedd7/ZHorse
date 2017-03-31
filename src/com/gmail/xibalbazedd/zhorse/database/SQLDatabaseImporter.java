package com.gmail.xibalbazedd.zhorse.database;

import java.util.List;
import java.util.UUID;

import com.gmail.xibalbazedd.zhorse.ZHorse;

public class SQLDatabaseImporter {
	
	protected static boolean fullImport(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		success &= importPlayers(zh, db);
		success &= importFriends(zh, db);
		success &= importHorses(zh, db);
		success &= importHorseStats(zh, db);
		success &= importHorseInventories(zh, db);
		success &= importSales(zh, db);
		return success;
	}
	
	private static boolean importPlayers(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_player";
		List<PlayerRecord> playerRecordList = db.getPlayerRecordList(query);
		for (PlayerRecord playerRecord : playerRecordList) {
			UUID playerUUID = UUID.fromString(playerRecord.getUUID());
			if (!zh.getDM().isPlayerRegistered(playerUUID)) {
				success = zh.getDM().registerPlayer(playerRecord) && success;
			}
		}
		return success;
	}
	
	private static boolean importFriends(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_friend";
		List<FriendRecord> friendRecordList = db.getFriendRecordList(query);
		for (FriendRecord friendRecord : friendRecordList) {
			UUID requesterUUID = UUID.fromString(friendRecord.getRequester());
			UUID recipientUUID = UUID.fromString(friendRecord.getRecipient());
			if (!zh.getDM().isFriendOf(requesterUUID, recipientUUID)) {
				success = zh.getDM().registerFriend(friendRecord) && success;
			}
		}
		return success;
	}
	
	private static boolean importHorses(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_horse";
		List<HorseRecord> horseRecordList = db.getHorseRecordList(query);
		for (HorseRecord horseRecord : horseRecordList) {
			UUID horseUUID = UUID.fromString(horseRecord.getUUID());
			if (!zh.getDM().isHorseRegistered(horseUUID)) {
				success = zh.getDM().registerHorse(horseRecord) && success;
			}
		}
		return success;
	}
	
	private static boolean importHorseStats(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_horse_stats";
		List<HorseStatsRecord> horseStatsRecordList = db.getHorseStatsRecordList(query);
		for (HorseStatsRecord horseStatsRecord : horseStatsRecordList) {
			UUID horseUUID = UUID.fromString(horseStatsRecord.getUUID());
			if (zh.getDM().isHorseRegistered(horseUUID) && !zh.getDM().isHorseStatsRegistered(horseUUID)) {
				success = zh.getDM().registerHorseStats(horseStatsRecord) && success;
			}
		}
		return success;
	}
	
	private static boolean importHorseInventories(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_inventory_item";
		List<HorseInventoryRecord> horseInventoryRecordList = db.getHorseInventoryRecordList(query);
		for (HorseInventoryRecord horseInventoryRecord : horseInventoryRecordList) {
			UUID horseUUID = UUID.fromString(horseInventoryRecord.getUUID());
			if (zh.getDM().isHorseRegistered(horseUUID) && !zh.getDM().isHorseInventoryRegistered(horseUUID)) {
				success = zh.getDM().registerHorseInventory(horseInventoryRecord) && success;
			}
		}
		return success;
	}
	
	private static boolean importSales(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_sale";
		List<SaleRecord> saleRecordList = db.getSaleRecordList(query);
		for (SaleRecord saleRecord : saleRecordList) {
			UUID horseUUID = UUID.fromString(saleRecord.getUUID());
			if (zh.getDM().isHorseRegistered(horseUUID) && !zh.getDM().isSaleRegistered(horseUUID)) {
				success = zh.getDM().registerSale(saleRecord) && success;
			}
		}
		return success;
	}

}
