package com.github.zedd7.zhorse.database;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import com.github.zedd7.zhorse.ZHorse;

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
		List<PlayerRecord> playerRecordList = db.getPlayerRecordList(query, true, null);
		for (PlayerRecord playerRecord : playerRecordList) {
			UUID playerUUID = UUID.fromString(playerRecord.getUUID());
			if (!zh.getDM().isPlayerRegistered(playerUUID, true, null)) {
				success = zh.getDM().registerPlayer(playerRecord, true, null) && success;
			}
		}
		return success;
	}

	private static boolean importFriends(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_friend";
		List<FriendRecord> friendRecordList = db.getFriendRecordList(query, true, null);
		for (FriendRecord friendRecord : friendRecordList) {
			UUID requesterUUID = UUID.fromString(friendRecord.getRequester());
			UUID recipientUUID = UUID.fromString(friendRecord.getRecipient());
			if (!zh.getDM().isFriendOf(requesterUUID, recipientUUID, true, null)) {
				success = zh.getDM().registerFriend(friendRecord, true, null) && success;
			}
		}
		return success;
	}

	private static boolean importPendingMessages(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_pending_message";
		List<PendingMessageRecord> messageRecordList = db.getPendingMessageRecordList(query, true, null);
		for (PendingMessageRecord messageRecord : messageRecordList) {
			UUID playerUUID = UUID.fromString(messageRecord.getUUID());
			Date date = messageRecord.getDate();
			if (!zh.getDM().isPendingMessageRegistered(playerUUID, date, true, null)) {
				success = zh.getDM().registerPendingMessage(messageRecord, true, null) && success;
			}
		}
		return success;
	}

	private static boolean importHorses(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_horse";
		List<HorseRecord> horseRecordList = db.getHorseRecordList(query, true, null);
		for (HorseRecord horseRecord : horseRecordList) {
			UUID horseUUID = UUID.fromString(horseRecord.getUUID());
			if (!zh.getDM().isHorseRegistered(horseUUID, true, null)) {
				success = zh.getDM().registerHorse(horseRecord, true, null) && success;
			}
		}
		return success;
	}

	private static boolean importHorseDeaths(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_horse_death";
		List<HorseDeathRecord> horseDeathRecordList = db.getHorseDeathRecordList(query, true, null);
		for (HorseDeathRecord horseDeathRecord : horseDeathRecordList) {
			UUID horseUUID = UUID.fromString(horseDeathRecord.getUUID());
			if (zh.getDM().isHorseRegistered(horseUUID, true, null) && !zh.getDM().isHorseDeathRegistered(horseUUID, true, null)) {
				success = zh.getDM().registerHorseDeath(horseDeathRecord, true, null) && success;
			}
		}
		return success;
	}

	private static boolean importHorseStables(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_horse_stable";
		List<HorseStableRecord> horseStableRecordList = db.getHorseStableRecordList(query, true, null);
		for (HorseStableRecord horseStableRecord : horseStableRecordList) {
			UUID horseUUID = UUID.fromString(horseStableRecord.getUUID());
			if (zh.getDM().isHorseRegistered(horseUUID, true, null) && !zh.getDM().isHorseStableRegistered(horseUUID, true, null)) {
				success = zh.getDM().registerHorseStable(horseStableRecord, true, null) && success;
			}
		}
		return success;
	}

	private static boolean importHorseStats(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_horse_stats";
		List<HorseStatsRecord> horseStatsRecordList = db.getHorseStatsRecordList(query, true, null);
		for (HorseStatsRecord horseStatsRecord : horseStatsRecordList) {
			UUID horseUUID = UUID.fromString(horseStatsRecord.getUUID());
			if (zh.getDM().isHorseRegistered(horseUUID, true, null) && !zh.getDM().isHorseStatsRegistered(horseUUID, true, null)) {
				success = zh.getDM().registerHorseStats(horseStatsRecord, true, null) && success;
			}
		}
		return success;
	}

	private static boolean importHorseInventories(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_horse_inventory";
		List<HorseInventoryRecord> horseInventoryRecordList = db.getHorseInventoryRecordList(query, true, null);
		for (HorseInventoryRecord horseInventoryRecord : horseInventoryRecordList) {
			UUID horseUUID = UUID.fromString(horseInventoryRecord.getUUID());
			if (zh.getDM().isHorseRegistered(horseUUID, true, null) && !zh.getDM().isHorseInventoryRegistered(horseUUID, true, null)) {
				success = zh.getDM().registerHorseInventory(horseInventoryRecord, true, null) && success;
			}
		}
		return success;
	}

	private static boolean importSales(ZHorse zh, SQLDatabaseConnector db) {
		boolean success = true;
		String query = "SELECT * FROM prefix_sale";
		List<SaleRecord> saleRecordList = db.getSaleRecordList(query, true, null);
		for (SaleRecord saleRecord : saleRecordList) {
			UUID horseUUID = UUID.fromString(saleRecord.getUUID());
			if (zh.getDM().isHorseRegistered(horseUUID, true, null) && !zh.getDM().isHorseForSale(horseUUID, true, null)) {
				success = zh.getDM().registerSale(saleRecord, true, null) && success;
			}
		}
		return success;
	}

}
