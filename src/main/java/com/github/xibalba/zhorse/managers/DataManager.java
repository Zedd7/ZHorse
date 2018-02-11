package com.github.xibalba.zhorse.managers;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;

import com.github.xibalba.zhorse.ZHorse;
import com.github.xibalba.zhorse.database.FriendRecord;
import com.github.xibalba.zhorse.database.HorseDeathRecord;
import com.github.xibalba.zhorse.database.HorseInventoryRecord;
import com.github.xibalba.zhorse.database.HorseRecord;
import com.github.xibalba.zhorse.database.HorseStableRecord;
import com.github.xibalba.zhorse.database.HorseStatsRecord;
import com.github.xibalba.zhorse.database.MySQLConnector;
import com.github.xibalba.zhorse.database.PendingMessageRecord;
import com.github.xibalba.zhorse.database.PlayerRecord;
import com.github.xibalba.zhorse.database.SQLDatabaseConnector;
import com.github.xibalba.zhorse.database.SQLiteConnector;
import com.github.xibalba.zhorse.database.SaleRecord;
import com.github.xibalba.zhorse.enums.DatabaseEnum;

public class DataManager {

	public static final String[] TABLE_ARRAY = {"player", "friend", "pending_message", "horse", "horse_death", "horse_inventory", "horse_stable", "horse_stats", "sale"};
	public static final String[] PATCH_ARRAY = {"1.6.6"};

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final int DEFAULT_DEAD_HORSE_ID = -1;
	private static final int DEFAULT_FAVORITE_HORSE_ID = 1;

	private ZHorse zh;
	private SQLDatabaseConnector db;
	private List<String> tableScriptList;
	private List<String> patchScriptList;
	private boolean connected = false;

	public DataManager(ZHorse zh) {
		this.zh = zh;
	}

	public void openDatabase() {
		DatabaseEnum database = zh.getCM().getDatabaseType();
		switch (database) {
		case MYSQL:
			db = new MySQLConnector(zh);
			break;
		case SQLITE:
			db = new SQLiteConnector(zh);
			break;
		default:
			String databaseType = database != null ? database.getName() : "Unknown database";
			zh.getLogger().severe(String.format("The database %s is not supported !", databaseType));
		}
		connected = db != null && db.isConnected();
		if (connected) {
			executeScripts();
		}
	}

	public void closeDatabase() {
		if (connected) {
			db.closeConnection();
		}
	}

	public void setScriptLists(List<String> tableScriptList, List<String> patchScriptList) {
		this.tableScriptList = tableScriptList;
		this.patchScriptList = patchScriptList;
	}

	public void executeScripts() {
		for (String tableScript : tableScriptList) {
			executeSQLScript(tableScript, false);
		}
		for (String patchScript : patchScriptList) {
			executeSQLScript(patchScript, true);
		}
	}

	private void executeSQLScript(String update, boolean hideExceptions) {
		String prefixedUpdate = db.applyTablePrefix(update);
		db.executeUpdate(prefixedUpdate, hideExceptions);
	}

	public Integer getDefaultDeadHorseID() {
		return DEFAULT_DEAD_HORSE_ID;
	}

	public Integer getDefaultFavoriteHorseID() {
		return DEFAULT_FAVORITE_HORSE_ID;
	}

	public List<String> getFriendNameList(UUID playerUUID) {
		String query = String.format("SELECT name FROM prefix_player WHERE uuid IN (SELECT recipient FROM prefix_friend WHERE requester = \"%s\") ORDER BY NAME ASC", playerUUID);
		return db.getStringResultList(query);
	}

	public List<String> getFriendNameReverseList(UUID playerUUID) {
		String query = String.format("SELECT name FROM prefix_player WHERE uuid IN (SELECT requester FROM prefix_friend WHERE recipient = \"%s\") ORDER BY NAME ASC", playerUUID);
		return db.getStringResultList(query);
	}

	public Integer getAliveHorseCount(UUID ownerUUID) {
		String query = String.format("SELECT COUNT(1) FROM prefix_horse h WHERE owner = \"%s\" AND h.uuid NOT IN (SELECT hd.uuid FROM prefix_horse_death hd)", ownerUUID);
		return db.getIntegerResult(query);
	}

	public Integer getDeadHorseCount(UUID ownerUUID) {
		String query = String.format("SELECT COUNT(1) FROM prefix_horse h, prefix_horse_death hd WHERE owner = \"%s\" AND h.uuid = hd.uuid", ownerUUID);
		return db.getIntegerResult(query);
	}

	public Integer getHorseID(UUID horseUUID) {
		String query = String.format("SELECT id FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getIntegerResult(query);
	}

	public Integer getHorseID(UUID ownerUUID, String horseName) {
		String query = String.format("SELECT h.id FROM prefix_horse h WHERE h.owner = \"%s\" AND h.name = \"%s\" AND h.uuid NOT IN (SELECT hd.uuid FROM prefix_horse_death hd)", ownerUUID, horseName);
		return db.getIntegerResult(query);
	}

	public Location getHorseLocation(UUID horseUUID) {
		String query = String.format("SELECT locationWorld, locationX, locationY, locationZ FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getLocationResult(query);
	}

	public Location getHorseLocation(UUID ownerUUID, Integer horseID) {
		UUID horseUUID = getHorseUUID(ownerUUID, horseID);
		return getHorseLocation(horseUUID);
	}

	public String getHorseName(UUID horseUUID) {
		String query = String.format("SELECT name FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getStringResult(query);
	}

	public String getHorseName(UUID ownerUUID, int horseID) {
		String query = String.format("SELECT name FROM prefix_horse WHERE owner = \"%s\" AND id = %d", ownerUUID, horseID);
		return db.getStringResult(query);
	}

	public String getHorseName(UUID ownerUUID, String wrongCaseHorseName) {
		String query = String.format("SELECT name FROM prefix_horse WHERE owner = \"%s\"", ownerUUID);
		List<String> horseNameList = db.getStringResultList(query);
		for (String horseName : horseNameList) {
			if (wrongCaseHorseName.equalsIgnoreCase(horseName)) {
				return horseName;
			}
		}
		return wrongCaseHorseName;
	}

	public String getHorseType(UUID horseUUID) {
		String query = String.format("SELECT type FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.getStringResult(query);
	}

	public UUID getHorseUUID(UUID ownerUUID, int horseID) {
		String query = String.format("SELECT uuid FROM prefix_horse WHERE owner = \"%s\" AND id = %d", ownerUUID, horseID);
		return UUID.fromString(db.getStringResult(query));
	}

	public List<UUID> getHorseUUIDList(UUID ownerUUID) {
		String query = String.format("SELECT uuid FROM prefix_horse WHERE owner = \"%s\"", ownerUUID);
		List<String> stringUUIDList = db.getStringResultList(query);
		List<UUID> horseUUIDList = new ArrayList<>();
		for (String stringUUID : stringUUIDList) {
			horseUUIDList.add(UUID.fromString(stringUUID));
		}
		return horseUUIDList;
	}

	public List<UUID> getHorseUUIDList(Chunk chunk) {
		Location NWCorner = chunk.getBlock(0, 0, 0).getLocation();
		Location SECorner = chunk.getBlock(15, 0, 15).getLocation();
		String query = String.format("SELECT uuid FROM prefix_horse WHERE locationX >= %d AND locationX <= %d AND locationZ >= %d AND locationZ <= %d",
				NWCorner.getBlockX(), SECorner.getBlockX(), NWCorner.getBlockZ(), SECorner.getBlockZ());
		List<String> stringUUIDList = db.getStringResultList(query);
		List<UUID> horseUUIDList = new ArrayList<>();
		for (String stringUUID : stringUUIDList) {
			horseUUIDList.add(UUID.fromString(stringUUID));
		}
		return horseUUIDList;
	}

	public Location getHorseStableLocation(UUID horseUUID) {
		String query = String.format("SELECT locationWorld, locationX, locationY, locationZ FROM prefix_horse_stable WHERE uuid = \"%s\"", horseUUID);
		return db.getLocationResult(query);
	}

	public Integer getNextHorseID(UUID ownerUUID) {
		String query = String.format("SELECT MAX(h.id) FROM prefix_horse h WHERE h.owner = \"%s\" AND h.uuid NOT IN (SELECT hd.uuid FROM prefix_horse_death hd)", ownerUUID);
		Integer horseID = db.getIntegerResult(query);
		if (horseID == null) {
			horseID = 0;
		}
		return horseID + 1;
	}

	public UUID getLatestHorseDeathUUID(UUID ownerUUID) {
		String query = String.format(
				"SELECT hd1.uuid FROM prefix_horse_death hd1 WHERE hd1.date = (SELECT MAX(hd2.date) FROM prefix_horse_death hd2 WHERE hd2.uuid IN (SELECT h.uuid FROM prefix_horse h WHERE h.owner = \"%s\"))",
				ownerUUID);
		String result = db.getStringResult(query);
		return result != null ? UUID.fromString(result) : null;
	}

	public UUID getOldestHorseDeathUUID(UUID ownerUUID) {
		String query = String.format(
				"SELECT hd1.uuid FROM prefix_horse_death hd1 WHERE hd1.date = (SELECT MIN(hd2.date) FROM prefix_horse_death hd2 WHERE hd2.uuid IN (SELECT h.uuid FROM prefix_horse h WHERE h.owner = \"%s\"))",
				ownerUUID);
		String result = db.getStringResult(query);
		return result != null ? UUID.fromString(result) : null;
	}

	public String getOwnerName(UUID horseUUID) {
		String query = String.format("SELECT p.name FROM prefix_player p WHERE p.uuid = (SELECT h.owner FROM prefix_horse h WHERE h.uuid = \"%s\")", horseUUID);
		return db.getStringResult(query);
	}

	public UUID getOwnerUUID(UUID horseUUID) {
		String query = String.format("SELECT owner FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		String result = db.getStringResult(query);
		return result != null ? UUID.fromString(result) : null;
	}

	public Integer getPlayerFavoriteHorseID(UUID ownerUUID) {
		String query = String.format("SELECT favorite FROM prefix_player WHERE uuid = \"%s\"", ownerUUID);
		return db.getIntegerResult(query);
	}

	public String getPlayerLanguage(UUID playerUUID) {
		String query = String.format("SELECT language FROM prefix_player WHERE uuid = \"%s\"", playerUUID);
		return db.getStringResult(query);
	}

	public String getPlayerName(String wrongCasePlayerName) {
		String query = "SELECT name FROM prefix_player";
		List<String> playerNameList = db.getStringResultList(query);
		for (String playerName : playerNameList) {
			if (wrongCasePlayerName.equalsIgnoreCase(playerName)) {
				return playerName;
			}
		}
		return wrongCasePlayerName;
	}

	public String getPlayerName(UUID playerUUID) {
		String query = String.format("SELECT name FROM prefix_player WHERE uuid = \"%s\"", playerUUID);
		return db.getStringResult(query);
	}

	public UUID getPlayerUUID(String playerName) {
		String query = String.format("SELECT uuid FROM prefix_player WHERE name = \"%s\"", playerName);
		return UUID.fromString(db.getStringResult(query));
	}

	public Integer getSalePrice(UUID horseUUID) {
		String query = String.format("SELECT price FROM prefix_sale WHERE uuid = \"%s\"", horseUUID);
		return db.getIntegerResult(query);
	}

	public Integer getTotalHorsesCount() {
		String query = "SELECT COUNT(1) FROM prefix_horse";
		return db.getIntegerResult(query);
	}

	public Integer getTotalOwnersCount() {
		String query = "SELECT COUNT(1) FROM prefix_player p WHERE EXISTS (SELECT h.uuid FROM prefix_horse h WHERE h.owner = p.uuid)";
		return db.getIntegerResult(query);
	}

	public Integer getTotalPlayersCount() {
		String query = "SELECT COUNT(1) FROM prefix_player";
		return db.getIntegerResult(query);
	}

	public HorseRecord getHorseRecord(UUID horseUUID) {
		String query = String.format("SELECT * FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getHorseRecord(query);
	}

	public List<HorseRecord> getHorseRecordList(UUID ownerUUID, boolean includeDeadHorses) {
		String query;
		if (includeDeadHorses) {
			query = String.format("SELECT * FROM prefix_horse WHERE owner = \"%s\" ORDER BY id ASC", ownerUUID);
		}
		else {
			query = String.format("SELECT * FROM prefix_horse h WHERE owner = \"%s\" AND h.uuid NOT IN (SELECT hd.uuid FROM prefix_horse_death hd) ORDER BY h.id ASC", ownerUUID);
		}
		return db.getHorseRecordList(query);
	}

	public List<HorseDeathRecord> getHorseDeathRecordList(UUID ownerUUID) {
		String query = String.format("SELECT * FROM prefix_horse_death hd WHERE uuid IN (SELECT h.uuid FROM prefix_horse h WHERE owner = \"%s\") ORDER BY hd.date DESC", ownerUUID);
		return db.getHorseDeathRecordList(query);
	}

	public HorseInventoryRecord getHorseInventoryRecord(UUID horseUUID) {
		String query = String.format("SELECT * FROM prefix_horse_inventory WHERE uuid = \"%s\"", horseUUID);
		return db.getHorseInventoryRecord(query);
	}

	public HorseStableRecord getHorseStableRecord(UUID horseUUID) {
		String query = String.format("SELECT * FROM prefix_horse_stable WHERE uuid = \"%s\"", horseUUID);
		return db.getHorseStableRecord(query);
	}

	public HorseStatsRecord getHorseStatsRecord(UUID horseUUID) {
		String query = String.format("SELECT * FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.getHorseStatsRecord(query);
	}

	public PlayerRecord getPlayerRecord(UUID playerUUID) {
		String query = String.format("SELECT * FROM prefix_player WHERE uuid = \"%s\"", playerUUID);
		return db.getPlayerRecord(query);
	}

	public List<PendingMessageRecord> getPendingMessageRecordList(UUID playerUUID) {
		String query = String.format("SELECT * FROM prefix_pending_message WHERE uuid = \"%s\" ORDER BY date ASC", playerUUID);
		return db.getPendingMessageRecordList(query);
	}

	private boolean hasLocationChanged(UUID horseUUID, Location newLocation) {
		Location oldLocation = getHorseLocation(horseUUID);
		if (oldLocation != null) {
			return oldLocation.getWorld().getName() != newLocation.getWorld().getName()
					|| oldLocation.getBlockX() != newLocation.getBlockX()
					|| oldLocation.getBlockY() != newLocation.getBlockY()
					|| oldLocation.getBlockZ() != newLocation.getBlockZ();
		}
		return true;
	}

	public boolean isFriendOf(UUID requesterUUID, UUID recipientUUID) {
		String query = String.format("SELECT 1 FROM prefix_friend WHERE requester = \"%s\" AND recipient = \"%s\"", requesterUUID, recipientUUID);
		return db.hasResult(query);
	}

	public boolean isFriendOfOwner(UUID playerUUID, UUID horseUUID) {
		UUID ownerUUID = getOwnerUUID(horseUUID);
		return isFriendOf(ownerUUID, playerUUID);
	}

	public boolean isHorseForSale(UUID horseUUID) {
		String query = String.format("SELECT 1 FROM prefix_sale WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query);
	}

	public boolean isHorseLocked(UUID horseUUID) {
		String query = String.format("SELECT locked FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getBooleanResult(query);
	}

	public boolean isHorseOfType(UUID horseUUID, String type) {
		String query = String.format("SELECT 1 FROM prefix_horse h WHERE h.uuid IN (SELECT hs.uuid FROM prefix_horse_stats hs WHERE hs.uuid = \"%s\" AND type = \"%s\")", horseUUID, type);
		return db.hasResult(query);
	}

	public boolean isHorseOwnedBy(UUID ownerUUID, UUID horseUUID) {
		String query = String.format("SELECT 1 FROM prefix_horse WHERE uuid = \"%s\" AND owner = \"%s\"", horseUUID, ownerUUID);
		return db.hasResult(query);
	}

	public boolean isHorseProtected(UUID horseUUID) {
		String query = String.format("SELECT protected FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getBooleanResult(query);
	}

	public boolean isHorseRegistered(UUID horseUUID) {
		String query = String.format("SELECT 1 FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query);
	}

	public boolean isHorseRegistered(UUID ownerUUID, int horseID) {
		String query = String.format("SELECT 1 FROM prefix_horse h WHERE h.owner = \"%s\" AND h.id = %d AND h.uuid NOT IN (SELECT hd.uuid FROM prefix_horse_death hd)", ownerUUID, horseID);
		return db.hasResult(query);
	}

	public boolean isHorseShared(UUID horseUUID) {
		String query = String.format("SELECT shared FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getBooleanResult(query);
	}

	public boolean isHorseInventoryRegistered(UUID horseUUID) {
		String query = String.format("SELECT 1 FROM prefix_horse_inventory WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query);
	}

	public boolean isHorseDeathRegistered(UUID horseUUID) {
		String query = String.format("SELECT 1 FROM prefix_horse_death WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query);
	}

	public boolean isHorseStableRegistered(UUID horseUUID) {
		String query = String.format("SELECT 1 FROM prefix_horse_stable WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query);
	}

	public boolean isHorseStatsRegistered(UUID horseUUID) {
		String query = String.format("SELECT 1 FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query);
	}

	public boolean isPendingMessageRegistered(UUID playerUUID, Date date) {
		String query = String.format("SELECT 1 FROM prefix_pending_message WHERE uuid = \"%s\" AND date = \"%s\"", playerUUID, DATE_FORMAT.format(date));
		return db.hasResult(query);
	}

	public boolean isPlayerDisplayingExactStats(UUID playerUUID) {
		String query = String.format("SELECT display_exact_stats FROM prefix_player WHERE uuid = \"%s\"", playerUUID);
		return db.getBooleanResult(query);
	}

	public boolean isPlayerRegistered(String playerName) {
		String query = String.format("SELECT 1 FROM prefix_player WHERE name = \"%s\"", playerName);
		return db.hasResult(query);
	}

	public boolean isPlayerRegistered(UUID playerUUID) {
		String query = String.format("SELECT 1 FROM prefix_player WHERE uuid = \"%s\"", playerUUID);
		return db.hasResult(query);
	}

	public boolean registerFriend(FriendRecord friendRecord) {
		String update = String.format("INSERT INTO prefix_friend VALUES (\"%s\", \"%s\")", friendRecord.getRequester(), friendRecord.getRecipient());
		return db.executeUpdate(update);
	}

	public boolean registerHorse(HorseRecord horseRecord) {
		String update = String.format("INSERT INTO prefix_horse VALUES (\"%s\", \"%s\", %d, \"%s\", %d, %d, %d, \"%s\", %d, %d, %d)",
			horseRecord.getUUID(),
			horseRecord.getOwner(),
			horseRecord.getId(),
			horseRecord.getName(),
			horseRecord.isLocked() ? 1 : 0,
			horseRecord.isProtected() ? 1 : 0,
			horseRecord.isShared() ? 1 : 0,
			horseRecord.getLocationWorld(),
			horseRecord.getLocationX(),
			horseRecord.getLocationY(),
			horseRecord.getLocationZ()
		);
		return db.executeUpdate(update);
	}

	public boolean registerHorseDeath(HorseDeathRecord horseDeathRecord) {
		boolean success = true;
		UUID horseUUID = UUID.fromString(horseDeathRecord.getUUID());
		UUID ownerUUID = getOwnerUUID(horseUUID);
		int horseID = getHorseID(horseUUID);
		int maxDeadHorseCount = zh.getCM().getRezStackMaxSize();
		if (maxDeadHorseCount > 0) {
			int deadHorseCount = getDeadHorseCount(ownerUUID);
			if (deadHorseCount >= maxDeadHorseCount) {
				UUID oldestHorseDeathUUID = getOldestHorseDeathUUID(ownerUUID);
				success &= removeHorse(oldestHorseDeathUUID, ownerUUID);
			}
			String horseUpdate = String.format("UPDATE prefix_horse SET id = %s WHERE uuid = \"%s\"", DEFAULT_DEAD_HORSE_ID, horseUUID);
			String horseDeathUpdate = String.format("INSERT INTO prefix_horse_death VALUES (\"%s\", \"%s\")", horseDeathRecord.getUUID(), DATE_FORMAT.format(horseDeathRecord.getDate()));
			success &= updateHorseIDMapping(ownerUUID, horseID);
			success &= db.executeUpdate(horseUpdate);
			success &= db.executeUpdate(horseDeathUpdate);
		}
		else {
			success &= removeHorse(horseUUID, ownerUUID, horseID);
		}
		return success;
	}

	public boolean registerHorseInventory(HorseInventoryRecord horseInventoryRecord) {
		String update = String.format("INSERT INTO prefix_horse_inventory VALUES (\"%s\", \"%s\")",
			horseInventoryRecord.getUUID(),
			horseInventoryRecord.getSerial()
		);
		return db.executeUpdate(update);
	}

	public boolean registerHorseStable(HorseStableRecord horseStableRecord) {
		String update = String.format("INSERT INTO prefix_horse_stable VALUES (\"%s\", \"%s\", %d, %d, %d)",
			horseStableRecord.getUUID(),
			horseStableRecord.getLocationWorld(),
			horseStableRecord.getLocationX(),
			horseStableRecord.getLocationY(),
			horseStableRecord.getLocationZ()
		);
		return db.executeUpdate(update);
	}

	public boolean registerHorseStats(HorseStatsRecord horseStatsRecord) {
		String color = horseStatsRecord.getColor();
		String style = horseStatsRecord.getStyle();
		String update = String.format(Locale.US, "INSERT INTO prefix_horse_stats VALUES (\"%s\", %d, %d, %d, %s, \"%s\", %d, %d, %f, %d, %d, %d, %d, %f, %f, %d, %d, %f, %d, %s, %d, \"%s\")",
			horseStatsRecord.getUUID(),
			horseStatsRecord.getAge(),
			horseStatsRecord.canBreed() ? 1 : 0,
			horseStatsRecord.canPickupItems() ? 1 : 0,
			color != null ? "\"" + color + "\"" : null,
			horseStatsRecord.getCustomName(),
			horseStatsRecord.getDomestication(),
			horseStatsRecord.getFireTicks(),
			horseStatsRecord.getHealth(),
			horseStatsRecord.isCarryingChest() ? 1 : 0,
			horseStatsRecord.isCustomNameVisible() ? 1 : 0,
			horseStatsRecord.isGlowing() ? 1 : 0,
			horseStatsRecord.isTamed() ? 1 : 0,
			horseStatsRecord.getJumpStrength(),
			horseStatsRecord.getMaxHealth(),
			horseStatsRecord.getNoDamageTicks(),
			horseStatsRecord.getRemainingAir(),
			horseStatsRecord.getSpeed(),
			horseStatsRecord.getStrength(),
			style != null ? "\"" + style + "\"" : null,
			horseStatsRecord.getTicksLived(),
			horseStatsRecord.getType()
		);
		return db.executeUpdate(update);
	}

	public boolean registerPendingMessage(PendingMessageRecord messageRecord) {
		String update = String.format("INSERT INTO prefix_pending_message VALUES (\"%s\", \"%s\", \"%s\")",
				messageRecord.getUUID(), DATE_FORMAT.format(messageRecord.getDate()), messageRecord.getMessage());
		return db.executeUpdate(update);
	}

	public boolean registerPlayer(PlayerRecord playerRecord) {
		String update = String.format("INSERT INTO prefix_player VALUES (\"%s\", \"%s\", \"%s\", %d, %d)",
			playerRecord.getUUID(), playerRecord.getName(), playerRecord.getLanguage(), playerRecord.getFavorite(), playerRecord.displayExactStats() ? 1 : 0);
		return db.executeUpdate(update);
	}

	public boolean registerSale(SaleRecord saleRecord) {
		String update = String.format("INSERT INTO prefix_sale VALUES (\"%s\", %d)", saleRecord.getUUID(), saleRecord.getPrice());
		return db.executeUpdate(update);
	}

	public boolean removeFriend(UUID requesterUUID, UUID recipientUUID) {
		String update = String.format("DELETE FROM prefix_friend WHERE requester = \"%s\" AND recipient = \"%s\"", requesterUUID, recipientUUID);
		return db.executeUpdate(update);
	}

	public boolean removeHorse(UUID horseUUID) {
		UUID ownerUUID = getOwnerUUID(horseUUID);
		int horseID = getHorseID(horseUUID);
		return removeHorse(horseUUID, ownerUUID, horseID);
	}

	public boolean removeHorse(UUID horseUUID, UUID ownerUUID) {
		int horseID = getHorseID(horseUUID);
		return removeHorse(horseUUID, ownerUUID, horseID);
	}

	public boolean removeHorse(UUID horseUUID, UUID ownerUUID, Integer horseID) {
		String update = String.format("DELETE FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		boolean success = true;
		if (horseID != null) success &= updateHorseIDMapping(ownerUUID, horseID);
		success &= removeHorseDeath(horseUUID);
		success &= removeHorseInventory(horseUUID);
		success &= removeHorseStable(horseUUID);
		success &= removeHorseStats(horseUUID);
		success &= removeSale(horseUUID);
		success &= db.executeUpdate(update);
		return success;
	}

	public boolean removeHorseDeath(UUID horseUUID) {
		String update = String.format("DELETE FROM prefix_horse_death WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update);
	}

	public boolean removeHorseInventory(UUID horseUUID) {
		String update = String.format("DELETE FROM prefix_horse_inventory WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update);
	}

	public boolean removeHorseStable(UUID horseUUID) {
		String update = String.format("DELETE FROM prefix_horse_stable WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update);
	}

	public boolean removeHorseStats(UUID horseUUID) {
		String update = String.format("DELETE FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update);
	}

	public boolean removePendingMessages(UUID playerUUID) {
		String update = String.format("DELETE FROM prefix_pending_message WHERE uuid = \"%s\"", playerUUID);
		return db.executeUpdate(update);
	}

	public boolean removeSale(UUID horseUUID) {
		String update = String.format("DELETE FROM prefix_sale WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update);
	}

	public boolean updateHorseID(UUID horseUUID, int horseID) {
		String update = String.format("UPDATE prefix_horse SET id = %d WHERE uuid = \"%s\"", horseID, horseUUID);
		return db.executeUpdate(update);
	}

	public boolean updateHorseIDMapping(UUID ownerUUID, int removedHorseID) {
		int favoriteHorseID = getPlayerFavoriteHorseID(ownerUUID);
		if (removedHorseID == favoriteHorseID) {
			updatePlayerFavoriteHorseID(ownerUUID, DEFAULT_FAVORITE_HORSE_ID);
		}
		else if (removedHorseID < favoriteHorseID && removedHorseID != DEFAULT_DEAD_HORSE_ID) {
			updatePlayerFavoriteHorseID(ownerUUID, favoriteHorseID - 1);
		}
		String update = String.format("UPDATE prefix_horse SET id = id - 1 WHERE owner = \"%s\" AND id > %d AND %d <> %d", ownerUUID, removedHorseID, removedHorseID, DEFAULT_DEAD_HORSE_ID);
		return db.executeUpdate(update);
	}

	public boolean updateHorseIsCarryingChest(UUID horseUUID, boolean isCarryingChest) {
		int isCarryingChestFlag = isCarryingChest ? 1 : 0;
		String update = String.format("UPDATE prefix_horse_stats SET isCarryingChest = %d WHERE uuid = \"%s\"", isCarryingChestFlag, horseUUID);
		return db.executeUpdate(update);
	}

	public boolean updateHorseLocation(UUID horseUUID, Location location, boolean checkForChanges) {
		if (checkForChanges && !hasLocationChanged(horseUUID, location)) {
			return true;
		}
		String update = String.format("UPDATE prefix_horse SET locationWorld = \"%s\", locationX = %d, locationY = %d, locationZ = %d WHERE uuid = \"%s\"",
				location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), horseUUID);
		return db.executeUpdate(update);
	}

	public boolean updateHorseLocked(UUID horseUUID, boolean locked) {
		int lockedFlag = locked ? 1 : 0;
		String update = String.format("UPDATE prefix_horse SET locked = %d WHERE uuid = \"%s\"", lockedFlag, horseUUID);
		return db.executeUpdate(update);
	}

	public boolean updateHorseName(UUID horseUUID, String name) {
		String update = String.format("UPDATE prefix_horse SET name = \"%s\" WHERE uuid = \"%s\"", name, horseUUID);
		return db.executeUpdate(update);
	}

	public boolean updateHorseOwner(UUID horseUUID, UUID ownerUUID) {
		String update = String.format("UPDATE prefix_horse SET owner = \"%s\" WHERE uuid = \"%s\"", ownerUUID, horseUUID);
		return db.executeUpdate(update);
	}

	public boolean updateHorseProtected(UUID horseUUID, boolean protected_) {
		int protectedFlag = protected_ ? 1 : 0;
		String update = String.format("UPDATE prefix_horse SET protected = %d WHERE uuid = \"%s\"", protectedFlag, horseUUID);
		return db.executeUpdate(update);
	}

	public boolean updateHorseShared(UUID horseUUID, boolean shared) {
		int sharedFlag = shared ? 1 : 0;
		String update = String.format("UPDATE prefix_horse SET shared = %d WHERE uuid = \"%s\"", sharedFlag, horseUUID);
		return db.executeUpdate(update);
	}

	public boolean updateHorseUUID(UUID oldHorseUUID, UUID newHorseUUID) {
		HorseRecord horseRecord = getHorseRecord(oldHorseUUID);
		UUID ownerUUID = UUID.fromString(horseRecord.getOwner());
		horseRecord.setUUID(newHorseUUID.toString());
		String horseDeathUpdate = String.format("UPDATE prefix_horse_death SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		String horseInventoryUpdate = String.format("UPDATE prefix_horse_inventory SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		String horseStableUpdate = String.format("UPDATE prefix_horse_stable SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		String horseStatsUpdate = String.format("UPDATE prefix_horse_stats SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		String saleUpdate = String.format("UPDATE prefix_sale SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		boolean success = true;
		success &= registerHorse(horseRecord);
		success &= db.executeUpdate(horseDeathUpdate);
		success &= db.executeUpdate(horseInventoryUpdate);
		success &= db.executeUpdate(horseStableUpdate);
		success &= db.executeUpdate(horseStatsUpdate);
		success &= db.executeUpdate(saleUpdate);
		success &= removeHorse(oldHorseUUID, ownerUUID, null);
		return success;
	}

	public boolean updateHorseInventory(HorseInventoryRecord horseInventoryRecord) {
		if (isHorseInventoryRegistered(UUID.fromString(horseInventoryRecord.getUUID()))) {
			removeHorseInventory(UUID.fromString(horseInventoryRecord.getUUID()));
		}
		return registerHorseInventory(horseInventoryRecord);
	}

	public boolean updateHorseStats(HorseStatsRecord horseStatsRecord) {
		if (isHorseStatsRegistered(UUID.fromString(horseStatsRecord.getUUID()))) {
			removeHorseStats(UUID.fromString(horseStatsRecord.getUUID()));
		}
		return registerHorseStats(horseStatsRecord);
	}

	public boolean updatePlayerDisplayExactStats(UUID playerUUID, boolean displayExactStats) {
		String update = String.format("UPDATE prefix_player SET display_exact_stats = %d WHERE uuid = \"%s\"", displayExactStats ? 1 : 0, playerUUID);
		return db.executeUpdate(update);
	}

	public boolean updatePlayerFavoriteHorseID(UUID playerUUID, int favorite) {
		String update = String.format("UPDATE prefix_player SET favorite = %d WHERE uuid = \"%s\"", favorite, playerUUID);
		return db.executeUpdate(update);
	}

	public boolean updatePlayerLanguage(UUID playerUUID, String language) {
		String update = String.format("UPDATE prefix_player SET language = \"%s\" WHERE uuid = \"%s\"", language, playerUUID);
		return db.executeUpdate(update);
	}

	public boolean updatePlayerName(UUID playerUUID, String name) {
		String update = String.format("UPDATE prefix_player SET name = \"%s\" WHERE uuid = \"%s\"", name, playerUUID);
		return db.executeUpdate(update);
	}

}