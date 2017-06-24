package com.gmail.xibalbazedd.zhorse.database;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import com.gmail.xibalbazedd.zhorse.ZHorse;

public abstract class SQLDatabaseImporter {
	
	protected static boolean fullImport(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		success &= importPlayers(zh, db);
		success &= importFriends(zh, db);
		success &= importPendingMessages(zh, db);
		success &= importHorses(zh, db);
		success &= importHorseDeaths(zh, db);
		success &= importHorseStables(zh, db);
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
	
	private static boolean importPendingMessages(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_pending_message";
		List<PendingMessageRecord> messageRecordList = db.getPendingMessageRecordList(query);
		for (PendingMessageRecord messageRecord : messageRecordList) {
			UUID playerUUID = UUID.fromString(messageRecord.getUUID());
			Date date = messageRecord.getDate();
			if (!zh.getDM().isPendingMessageRegistered(playerUUID, date)) {
				success = zh.getDM().registerPendingMessage(messageRecord) && success;
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
	
	private static boolean importHorseDeaths(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_horse_death";
		List<HorseDeathRecord> horseDeathRecordList = db.getHorseDeathRecordList(query);
		for (HorseDeathRecord horseDeathRecord : horseDeathRecordList) {
			UUID horseUUID = UUID.fromString(horseDeathRecord.getUUID());
			if (zh.getDM().isHorseRegistered(horseUUID) && !zh.getDM().isHorseDeathRegistered(horseUUID)) {
				success = zh.getDM().registerHorseDeath(horseDeathRecord) && success;
			}
		}
		return success;
	}
	
	private static boolean importHorseStables(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_horse_stable";
		List<HorseStableRecord> horseStableRecordList = db.getHorseStableRecordList(query);
		for (HorseStableRecord horseStableRecord : horseStableRecordList) {
			UUID horseUUID = UUID.fromString(horseStableRecord.getUUID());
			if (zh.getDM().isHorseRegistered(horseUUID) && !zh.getDM().isHorseStableRegistered(horseUUID)) {
				success = zh.getDM().registerHorseStable(horseStableRecord) && success;
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
			if (zh.getDM().isHorseRegistered(horseUUID) && !zh.getDM().isHorseForSale(horseUUID)) {
				success = zh.getDM().registerSale(saleRecord) && success;
			}
		}
		return success;
	}

}
