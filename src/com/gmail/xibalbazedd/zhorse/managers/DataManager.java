package com.gmail.xibalbazedd.zhorse.managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.FriendRecord;
import com.gmail.xibalbazedd.zhorse.database.HorseInventoryRecord;
import com.gmail.xibalbazedd.zhorse.database.HorseRecord;
import com.gmail.xibalbazedd.zhorse.database.HorseStatsRecord;
import com.gmail.xibalbazedd.zhorse.database.InventoryItemRecord;
import com.gmail.xibalbazedd.zhorse.database.MySQLConnector;
import com.gmail.xibalbazedd.zhorse.database.PlayerRecord;
import com.gmail.xibalbazedd.zhorse.database.SQLDatabaseConnector;
import com.gmail.xibalbazedd.zhorse.database.SQLiteConnector;
import com.gmail.xibalbazedd.zhorse.database.SaleRecord;
import com.gmail.xibalbazedd.zhorse.enums.DatabaseEnum;

public class DataManager {
	
	private static final String TABLE_SCRIPTS_PATH = "res\\sql\\%s-table.sql";
	private static final String[] TABLE_ARRAY = {"player", "friend", "horse", "horse_stats", "inventory_item", "sale"};
	
	private ZHorse zh;
	private SQLDatabaseConnector db;
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
		connected = db != null && db.isConnected() && updateTables();
	}
	
	public void closeDatabase() {
		if (connected) {
			db.closeConnection();
		}
	}
	
	private boolean updateTables() {
		boolean success = true;
		String update = "";
		String scriptsPath = TABLE_SCRIPTS_PATH.replace('\\', '/'); // Dark Magic Industries
		for (String table : TABLE_ARRAY) {
			try {
				String scriptPath = String.format(scriptsPath, table);
				update = IOUtils.toString(zh.getResource(scriptPath), "utf-8");
				update = db.applyTablePrefix(update);
			} catch (IOException e) {
				e.printStackTrace();
			}
			success &= db.executeUpdate(update);
		}
		return success;
	}
	
	public Integer getDefaultFavoriteHorseID() {
		return 1;
	}
	
	public List<String> getFriendNameList(UUID playerUUID) {
		String query = String.format("SELECT NAME FROM prefix_player WHERE uuid IN (SELECT recipient FROM prefix_friend WHERE requester = \"%s\") ORDER BY NAME ASC", playerUUID);
		return db.getStringResultList(query);
	}
	
	public List<String> getFriendNameReverseList(UUID playerUUID) {
		String query = String.format("SELECT NAME FROM prefix_player WHERE uuid IN (SELECT requester FROM prefix_friend WHERE recipient = \"%s\") ORDER BY NAME ASC", playerUUID);
		return db.getStringResultList(query);
	}
	
	public Integer getHorseCount(UUID ownerUUID) {
		String query = String.format("SELECT COUNT(1) FROM prefix_horse WHERE OWNER = \"%s\"", ownerUUID);
		return db.getIntegerResult(query);
	}
	
	public Integer getHorseID(UUID horseUUID) {
		String query = String.format("SELECT ID FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getIntegerResult(query);
	}
	
	public Integer getHorseID(UUID ownerUUID, String horseName) {
		String query = String.format("SELECT ID FROM prefix_horse WHERE OWNER = \"%s\" AND NAME = \"%s\"", ownerUUID, horseName);
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
		String query = String.format("SELECT NAME FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getStringResult(query);
	}
	
	public String getHorseName(UUID ownerUUID, int horseID) {
		String query = String.format("SELECT NAME FROM prefix_horse WHERE OWNER = \"%s\" AND ID = %d", ownerUUID, horseID);
		return db.getStringResult(query);
	}
	
	public String getHorseName(UUID ownerUUID, String wrongCaseHorseName) {
		String query = String.format("SELECT NAME FROM prefix_horse WHERE OWNER = \"%s\"", ownerUUID);
		List<String> horseNameList = db.getStringResultList(query);
		for (String horseName : horseNameList) {
			if (wrongCaseHorseName.equalsIgnoreCase(horseName)) {
				return horseName;
			}
		}
		return wrongCaseHorseName;
	}
	
	public List<String> getHorseNameList(UUID ownerUUID) {
		String query = String.format("SELECT NAME FROM prefix_horse WHERE OWNER = \"%s\" ORDER BY ID ASC", ownerUUID);
		return db.getStringResultList(query);
	}
	
	public HorseRecord getHorseRecord(UUID horseUUID) {
		String query = String.format("SELECT * FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getHorseRecord(query);
	}
	
	public UUID getHorseUUID(UUID ownerUUID, int horseID) {
		String query = String.format("SELECT uuid FROM prefix_horse WHERE OWNER = \"%s\" AND ID = %d", ownerUUID, horseID);
		return UUID.fromString(db.getStringResult(query));
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
	
	public HorseInventoryRecord getHorseInventoryRecord(UUID horseUUID) {
		String query = String.format("SELECT * FROM prefix_inventory_item WHERE uuid = \"%s\"", horseUUID);
		return db.getHorseInventoryRecord(query);
	}
	
	public HorseStatsRecord getHorseStatsRecord(UUID horseUUID) {
		String query = String.format("SELECT * FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.getHorseStatsRecord(query);
	}
	
	public Integer getNextHorseID(UUID ownerUUID) {
		String query = String.format("SELECT MAX(ID) FROM prefix_horse WHERE OWNER = \"%s\"", ownerUUID);
		Integer horseID = db.getIntegerResult(query);
		if (horseID == null) {
			horseID = 0;
		}
		return horseID + 1;
	}
	
	public String getOwnerName(UUID horseUUID) {
		String query = String.format("SELECT p.name FROM prefix_player p WHERE p.uuid = (SELECT h.owner FROM prefix_horse h WHERE h.uuid = \"%s\")", horseUUID);
		return db.getStringResult(query);
	}
	
	public UUID getOwnerUUID(UUID horseUUID) {
		String query = String.format("SELECT OWNER FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return UUID.fromString(db.getStringResult(query));
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
		String query = "SELECT NAME FROM prefix_player";
		List<String> playerNameList = db.getStringResultList(query);
		for (String playerName : playerNameList) {
			if (wrongCasePlayerName.equalsIgnoreCase(playerName)) {
				return playerName;
			}
		}
		return wrongCasePlayerName;
	}
	
	public String getPlayerName(UUID playerUUID) {
		String query = String.format("SELECT NAME FROM prefix_player WHERE uuid = \"%s\"", playerUUID);
		return db.getStringResult(query);
	}
	
	public PlayerRecord getPlayerRecord(UUID playerUUID) {
		String query = String.format("SELECT * FROM prefix_player WHERE uuid = \"%s\"", playerUUID);
		return db.getPlayerRecord(query);
	}
	
	public UUID getPlayerUUID(String playerName) {
		String query = String.format("SELECT uuid FROM prefix_player WHERE NAME = \"%s\"", playerName);
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
	
	public boolean isHorseOwnedBy(UUID ownerUUID, UUID horseUUID) {
		String query = String.format("SELECT 1 FROM prefix_horse WHERE uuid = \"%s\" AND OWNER = \"%s\"", horseUUID, ownerUUID);
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
		String query = String.format("SELECT 1 FROM prefix_horse WHERE OWNER = \"%s\" AND ID = %d", ownerUUID, horseID);
		return db.hasResult(query);
	}
	
	public boolean isHorseShared(UUID horseUUID) {
		String query = String.format("SELECT shared FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getBooleanResult(query);
	}
	
	public boolean isHorseInventoryRegistered(UUID horseUUID) {
		String query = String.format("SELECT 1 FROM prefix_inventory_item WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query);
	}
	
	public boolean isHorseStatsRegistered(UUID horseUUID) {
		String query = String.format("SELECT 1 FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query);
	}
	
	public boolean isPlayerRegistered(String playerName) {
		String query = String.format("SELECT 1 FROM prefix_player WHERE NAME = \"%s\"", playerName);
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
	
	public boolean registerHorseInventory(HorseInventoryRecord horseInventoryRecord) {
		boolean success = true;
		for (InventoryItemRecord itemRecord : horseInventoryRecord.getItemRecordList()) {
			String update = String.format("INSERT INTO prefix_inventory_item VALUES (\"%s\", %d, \"%s\")",
					itemRecord.getUUID(),
					itemRecord.getSlot(),
					itemRecord.getData()
			);
			success &= db.executeUpdate(update);
		}
		return success;
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
	
	public boolean registerPlayer(PlayerRecord playerRecord) {
		String update = String.format("INSERT INTO prefix_player VALUES (\"%s\", \"%s\", \"%s\", %d)",
			playerRecord.getUUID(), playerRecord.getName(), playerRecord.getLanguage(), playerRecord.getFavorite());
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
	
	public boolean removeHorse(UUID horseUUID, UUID ownerUUID, int horseID) {
		int favorite = getPlayerFavoriteHorseID(ownerUUID);
		if (horseID == favorite) {
			updatePlayerFavorite(ownerUUID, getDefaultFavoriteHorseID());
		}
		else if (horseID < favorite) {
			updatePlayerFavorite(ownerUUID, favorite - 1);
		}
		String saleUpdate = String.format("DELETE FROM prefix_sale WHERE uuid = \"%s\"", horseUUID);
		String horseUpdate = String.format("DELETE FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		String idUpdate = String.format("UPDATE prefix_horse SET ID = ID - 1 WHERE OWNER = \"%s\" AND ID > %d", ownerUUID, horseID);
		return db.executeUpdate(saleUpdate) && db.executeUpdate(horseUpdate) && db.executeUpdate(idUpdate);
	}
	
	public boolean removeHorseInventory(UUID horseUUID) {
		String update = String.format("DELETE FROM prefix_inventory_item WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean removeHorseStats(UUID horseUUID) {
		String update = String.format("DELETE FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean removeSale(UUID horseUUID) {
		String update = String.format("DELETE FROM prefix_sale WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseID(UUID horseUUID, int horseID) {
		String update = String.format("UPDATE prefix_horse SET ID = %d WHERE uuid = \"%s\"", horseID, horseUUID);
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
		String update = String.format("UPDATE prefix_horse SET NAME = \"%s\" WHERE uuid = \"%s\"", name, horseUUID);
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
		String update = String.format("UPDATE prefix_horse SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseInventory(HorseInventoryRecord horseInventoryRecord) {
		if (isHorseInventoryRegistered(UUID.fromString(horseInventoryRecord.getUUID()))) {
			removeHorseInventory(UUID.fromString(horseInventoryRecord.getUUID()));
		}
		return registerHorseInventory(horseInventoryRecord);
	}
	
	public boolean updateHorseInventoryUUID(UUID oldHorseUUID, UUID newHorseUUID) {
		String update = String.format("UPDATE prefix_inventory_item SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseStats(HorseStatsRecord horseStatsRecord) {
		if (isHorseStatsRegistered(UUID.fromString(horseStatsRecord.getUUID()))) {
			removeHorseStats(UUID.fromString(horseStatsRecord.getUUID()));
		}
		return registerHorseStats(horseStatsRecord);
	}
	
	public boolean updateHorseStatsUUID(UUID oldHorseUUID, UUID newHorseUUID) {
		String update = String.format("UPDATE prefix_horse_stats SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateSaleUUID(UUID oldHorseUUID, UUID newHorseUUID) {
		String update = String.format("UPDATE prefix_sale SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updatePlayerFavorite(UUID playerUUID, int favorite) {
		String update = String.format("UPDATE prefix_player SET favorite = %d WHERE uuid = \"%s\"", favorite, playerUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updatePlayerLanguage(UUID playerUUID, String language) {
		String update = String.format("UPDATE prefix_player SET language = \"%s\" WHERE uuid = \"%s\"", language, playerUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updatePlayerName(UUID playerUUID, String name) {
		String update = String.format("UPDATE prefix_player SET NAME = \"%s\" WHERE uuid = \"%s\"", name, playerUUID);
		return db.executeUpdate(update);
	}

}