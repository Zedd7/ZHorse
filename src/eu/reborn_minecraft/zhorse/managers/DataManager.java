package eu.reborn_minecraft.zhorse.managers;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.bukkit.Location;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.database.FriendRecord;
import eu.reborn_minecraft.zhorse.database.HorseInventoryRecord;
import eu.reborn_minecraft.zhorse.database.HorseRecord;
import eu.reborn_minecraft.zhorse.database.HorseStatsRecord;
import eu.reborn_minecraft.zhorse.database.MySQLConnector;
import eu.reborn_minecraft.zhorse.database.PlayerRecord;
import eu.reborn_minecraft.zhorse.database.SQLDatabaseConnector;
import eu.reborn_minecraft.zhorse.database.SQLiteConnector;
import eu.reborn_minecraft.zhorse.enums.DatabaseEnum;

public class DataManager {
	
	private static final String TABLE_SCRIPTS_PATH = "res\\sql\\%s-table.sql";
	private static final String[] TABLE_ARRAY = {"player", "horse", "friend", "horse_stats", "horse_inventory"};
	
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
		String query = String.format("SELECT name FROM prefix_player WHERE uuid IN (SELECT recipient FROM prefix_friend WHERE requester = \"%s\") ORDER BY name ASC", playerUUID);
		return db.getStringResultList(query);
	}
	
	public List<String> getFriendNameReverseList(UUID playerUUID) {
		String query = String.format("SELECT name FROM prefix_player WHERE uuid IN (SELECT requester FROM prefix_friend WHERE recipient = \"%s\") ORDER BY name ASC", playerUUID);
		return db.getStringResultList(query);
	}
	
	public Integer getHorseCount(UUID ownerUUID) {
		String query = String.format("SELECT COUNT(1) FROM prefix_horse WHERE owner = \"%s\"", ownerUUID);
		return db.getIntegerResult(query);
	}
	
	public Integer getHorseID(UUID horseUUID) {
		String query = String.format("SELECT id FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getIntegerResult(query);
	}
	
	public Integer getHorseID(UUID ownerUUID, String horseName) {
		String query = String.format("SELECT id FROM prefix_horse WHERE owner = \"%s\" AND name = \"%s\"", ownerUUID, horseName);
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
	
	public List<String> getHorseNameList(UUID ownerUUID) {
		String query = String.format("SELECT name FROM prefix_horse WHERE owner = \"%s\" ORDER BY id ASC", ownerUUID);
		return db.getStringResultList(query);
	}
	
	public UUID getHorseUUID(UUID ownerUUID, int horseID) {
		String query = String.format("SELECT uuid FROM prefix_horse WHERE owner = \"%s\" AND id = %d", ownerUUID, horseID);
		return UUID.fromString(db.getStringResult(query));
	}
	
	public HorseStatsRecord getHorseStatsRecord(UUID horseUUID) {
		String query = String.format("SELECT * FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.getHorseStatsRecord(query);
	}
	
	public HorseInventoryRecord getHorseInventoryRecord(UUID horseUUID) {
		String query = String.format("SELECT * FROM prefix_horse_inventory WHERE uuid = \"%s\"", horseUUID);
		return db.getHorseInventoryRecord(query);
	}
	
	public Integer getNextHorseID(UUID ownerUUID) {
		String query = String.format("SELECT MAX(id) FROM prefix_horse WHERE owner = \"%s\"", ownerUUID);
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
		String query = String.format("SELECT owner FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
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
	
	private boolean hasLocationChanged(UUID horseUUID, Location newLocation) {
		Location oldLocation = getHorseLocation(horseUUID);
		return oldLocation.getWorld().getName() != newLocation.getWorld().getName()
				|| oldLocation.getBlockX() != newLocation.getBlockX()
				|| oldLocation.getBlockY() != newLocation.getBlockY()
				|| oldLocation.getBlockZ() != newLocation.getBlockZ();
	}
	
	public boolean isFriendOf(UUID requesterUUID, UUID recipientUUID) {
		String query = String.format("SELECT 1 FROM prefix_friend WHERE requester = \"%s\" AND recipient = \"%s\"", requesterUUID, recipientUUID);
		return db.hasResult(query);
	}
	
	public boolean isFriendOfOwner(UUID playerUUID, UUID horseUUID) {
		UUID ownerUUID = getOwnerUUID(horseUUID);
		return isFriendOf(ownerUUID, playerUUID);
	}
	
	public boolean isHorseLocked(UUID horseUUID) {
		String query = String.format("SELECT locked FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getBooleanResult(query);
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
		String query = String.format("SELECT 1 FROM prefix_horse WHERE owner = \"%s\" AND id = %d", ownerUUID, horseID);
		return db.hasResult(query);
	}
	
	public boolean isHorseShared(UUID horseUUID) {
		String query = String.format("SELECT shared FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getBooleanResult(query);
	}
	
	public boolean isHorseStatsRegistered(UUID horseUUID) {
		String query = String.format("SELECT 1 FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query);
	}
	
	public boolean isHorseInventoryRegistered(UUID horseUUID) {
		String query = String.format("SELECT 1 FROM prefix_horse_inventory WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query);
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
			horseRecord.getModeLocked() ? 1 : 0,
			horseRecord.getModeProtected() ? 1 : 0,
			horseRecord.getModeShared() ? 1 : 0,
			horseRecord.getLocationWorld(),
			horseRecord.getLocationX(),
			horseRecord.getLocationY(),
			horseRecord.getLocationZ()
		);
		return db.executeUpdate(update);
	}
	
	public boolean registerHorseStats(HorseStatsRecord horseStatsRecord) {
		String color = horseStatsRecord.getColor();
		String style = horseStatsRecord.getStyle();
		String update = String.format(Locale.US, "INSERT INTO prefix_horse_stats VALUES (\"%s\", %d, %d, %d, %s, %d, %d, %f, %d, %d, %d, %f, %f, %d, %d, %f, %d, %s, %d, \"%s\")",
			horseStatsRecord.getUUID(),
			horseStatsRecord.getAge(),
			horseStatsRecord.canBreed() ? 1 : 0,
			horseStatsRecord.canPickupItems() ? 1 : 0,
			color != null ? "\"" + color + "\"" : null,
			horseStatsRecord.getDomestication(),
			horseStatsRecord.getFireTicks(),
			horseStatsRecord.getHealth(),
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
	
	public boolean registerHorseInventory(HorseInventoryRecord horseInventoryRecord) {
		String update = String.format("INSERT INTO prefix_horse_inventory VALUES (\"%s\", \"%s\")",
				horseInventoryRecord.getUUID(),
				horseInventoryRecord.getInventoryData()
			);
			return db.executeUpdate(update);
	}
	
	public boolean registerPlayer(PlayerRecord playerRecord) {
		String update = String.format("INSERT INTO prefix_player VALUES (\"%s\", \"%s\", \"%s\", %d)",
			playerRecord.getUUID(), playerRecord.getName(), playerRecord.getLanguage(), playerRecord.getFavorite());
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
		zh.getHM().untrackHorse(horseUUID);
		int favorite = getPlayerFavoriteHorseID(ownerUUID);
		if (horseID == favorite) {
			updatePlayerFavorite(ownerUUID, getDefaultFavoriteHorseID());
		}
		else if (horseID < favorite) {
			updatePlayerFavorite(ownerUUID, favorite - 1);
		}
		String deleteUpdate = String.format("DELETE FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		String idUpdate = String.format("UPDATE prefix_horse SET id = id - 1 WHERE owner = \"%s\" AND id > %d", ownerUUID, horseID);
		return db.executeUpdate(deleteUpdate) && db.executeUpdate(idUpdate);
	}
	
	public boolean removeHorseStats(UUID horseUUID) {
		String update = String.format("DELETE FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean removeHorseInventory(UUID horseUUID) {
		String update = String.format("DELETE FROM prefix_horse_inventory WHERE uuid = \"%s\"", horseUUID);
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
	
	public boolean updateHorseLocked(UUID horseUUID, boolean modeLocked) {
		int lockedFlag = modeLocked ? 1 : 0;
		String update = String.format("UPDATE prefix_horse SET locked = %d WHERE uuid = \"%s\"", lockedFlag, horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseName(UUID horseUUID, String name) {
		String update = String.format("UPDATE prefix_horse SET name = \"%s\" WHERE uuid = \"%s\"", name, horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseProtected(UUID horseUUID, boolean modeProtected) {
		int protectedFlag = modeProtected ? 1 : 0;
		String update = String.format("UPDATE prefix_horse SET protected = %d WHERE uuid = \"%s\"", protectedFlag, horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseShared(UUID horseUUID, boolean modeShared) {
		int sharedFlag = modeShared ? 1 : 0;
		String update = String.format("UPDATE prefix_horse SET shared = %d WHERE uuid = \"%s\"", sharedFlag, horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseUUID(UUID oldHorseUUID, UUID newHorseUUID) {
		String update = String.format("UPDATE prefix_horse SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseStatsUUID(UUID oldHorseUUID, UUID newHorseUUID) {
		String update = String.format("UPDATE prefix_horse_stats SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
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
		String update = String.format("UPDATE prefix_player SET name = \"%s\" WHERE uuid = \"%s\"", name, playerUUID);
		return db.executeUpdate(update);
	}

}