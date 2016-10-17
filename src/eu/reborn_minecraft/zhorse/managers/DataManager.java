package eu.reborn_minecraft.zhorse.managers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.bukkit.Location;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.DatabaseEnum;
import eu.reborn_minecraft.zhorse.utils.MySQLConnector;
import eu.reborn_minecraft.zhorse.utils.SQLDatabaseConnector;
import eu.reborn_minecraft.zhorse.utils.SQLiteConnector;

public class DataManager {
	
	private static final String TABLE_SCRIPTS_PATH = "res\\sql\\%s-table.sql";
	private static final String[] TABLE_ARRAY = {"player", "horse", "friend"};
	
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
		connected = db != null && updateTables();
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
		String query = String.format("SELECT name FROM player WHERE uuid IN (SELECT recipient FROM friend WHERE requester = \"%s\") ORDER BY name ASC", playerUUID);
		return db.getStringResultList(query);
	}
	
	public List<String> getFriendNameReverseList(UUID playerUUID) {
		String query = String.format("SELECT name FROM player WHERE uuid IN (SELECT requester FROM friend WHERE recipient = \"%s\") ORDER BY name ASC", playerUUID);
		return db.getStringResultList(query);
	}
	
	public Integer getHorseCount(UUID ownerUUID) {
		String query = String.format("SELECT COUNT(1) FROM horse WHERE owner = \"%s\"", ownerUUID);
		return db.getIntegerResult(query);
	}
	
	public Integer getHorseID(UUID horseUUID) {
		String query = String.format("SELECT id FROM horse WHERE uuid = \"%s\"", horseUUID);
		return db.getIntegerResult(query);
	}
	
	public Integer getHorseID(UUID ownerUUID, String horseName) {
		String query = String.format("SELECT id FROM horse WHERE owner = \"%s\" AND name = \"%s\"", ownerUUID, horseName);
		return db.getIntegerResult(query);
	}
	
	public Location getHorseLocation(UUID horseUUID) {
		String query = String.format("SELECT locationWorld, locationX, locationY, locationZ FROM horse WHERE uuid = \"%s\"", horseUUID);
		return db.getLocationResult(query);
	}
	
	public Location getHorseLocation(UUID ownerUUID, Integer horseID) {
		UUID horseUUID = getHorseUUID(ownerUUID, horseID);
		return getHorseLocation(horseUUID);
	}
	
	public String getHorseName(UUID horseUUID) {
		String query = String.format("SELECT name FROM horse WHERE uuid = \"%s\"", horseUUID);
		return db.getStringResult(query);
	}
	
	public String getHorseName(UUID ownerUUID, int horseID) {
		String query = String.format("SELECT name FROM horse WHERE owner = \"%s\" AND id = %d", ownerUUID, horseID);
		return db.getStringResult(query);
	}
	
	public String getHorseName(UUID ownerUUID, String wrongCaseHorseName) {
		String query = String.format("SELECT name FROM horse WHERE owner = \"%s\"", ownerUUID);
		List<String> horseNameList = db.getStringResultList(query);
		for (String horseName : horseNameList) {
			if (wrongCaseHorseName.equalsIgnoreCase(horseName)) {
				return horseName;
			}
		}
		return wrongCaseHorseName;
	}
	
	public List<String> getHorseNameList(UUID ownerUUID) {
		String query = String.format("SELECT name FROM horse WHERE owner = \"%s\" ORDER BY id ASC", ownerUUID);
		return db.getStringResultList(query);
	}
	
	public UUID getHorseUUID(UUID ownerUUID, int horseID) {
		String query = String.format("SELECT uuid FROM horse WHERE owner = \"%s\" AND id = %d", ownerUUID, horseID);
		return UUID.fromString(db.getStringResult(query));
	}
	
	public Integer getNextHorseID(UUID ownerUUID) {
		String query = String.format("SELECT MAX(id) FROM horse WHERE owner = \"%s\"", ownerUUID);
		Integer horseID = db.getIntegerResult(query);
		if (horseID == null) {
			horseID = 0;
		}
		return horseID + 1;
	}
	
	public String getOwnerName(UUID horseUUID) {
		String query = String.format("SELECT p.name FROM player p WHERE p.uuid = (SELECT h.owner FROM horse h WHERE h.uuid = \"%s\")", horseUUID);
		return db.getStringResult(query);
	}
	
	public UUID getOwnerUUID(UUID horseUUID) {
		String query = String.format("SELECT owner FROM horse WHERE uuid = \"%s\"", horseUUID);
		return UUID.fromString(db.getStringResult(query));
	}
	
	public Integer getPlayerFavoriteHorseID(UUID ownerUUID) {
		String query = String.format("SELECT favorite FROM player WHERE uuid = \"%s\"", ownerUUID);
		return db.getIntegerResult(query);
	}
	
	public String getPlayerLanguage(UUID playerUUID) {
		String query = String.format("SELECT language FROM player WHERE uuid = \"%s\"", playerUUID);
		return db.getStringResult(query);
	}
	
	public String getPlayerName(String wrongCasePlayerName) {
		String query = "SELECT name FROM player";
		List<String> playerNameList = db.getStringResultList(query);
		for (String playerName : playerNameList) {
			if (wrongCasePlayerName.equalsIgnoreCase(playerName)) {
				return playerName;
			}
		}
		return wrongCasePlayerName;
	}
	
	public String getPlayerName(UUID playerUUID) {
		String query = String.format("SELECT name FROM player WHERE uuid = \"%s\"", playerUUID);
		return db.getStringResult(query);
	}
	
	public UUID getPlayerUUID(String playerName) {
		String query = String.format("SELECT uuid FROM player WHERE name = \"%s\"", playerName);
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
		String query = String.format("SELECT 1 FROM friend WHERE requester = \"%s\" AND recipient = \"%s\"", requesterUUID, recipientUUID);
		return db.hasResult(query);
	}
	
	public boolean isFriendOfOwner(UUID playerUUID, UUID horseUUID) {
		UUID ownerUUID = getOwnerUUID(horseUUID);
		return isFriendOf(ownerUUID, playerUUID);
	}
	
	public boolean isHorseLocked(UUID horseUUID) {
		String query = String.format("SELECT locked FROM horse WHERE uuid = \"%s\"", horseUUID);
		return db.getBooleanResult(query);
	}
	
	public boolean isHorseOwnedBy(UUID ownerUUID, UUID horseUUID) {
		String query = String.format("SELECT 1 FROM horse WHERE uuid = \"%s\" AND owner = \"%s\"", horseUUID, ownerUUID);
		return db.hasResult(query);
	}
	
	public boolean isHorseProtected(UUID horseUUID) {
		String query = String.format("SELECT protected FROM horse WHERE uuid = \"%s\"", horseUUID);
		return db.getBooleanResult(query);
	}
	
	public boolean isHorseRegistered(UUID horseUUID) {
		String query = String.format("SELECT 1 FROM horse WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query);
	}
	
	public boolean isHorseRegistered(UUID ownerUUID, int horseID) {
		String query = String.format("SELECT 1 FROM horse WHERE owner = \"%s\" AND id = %d", ownerUUID, horseID);
		return db.hasResult(query);
	}
	
	public boolean isHorseShared(UUID horseUUID) {
		String query = String.format("SELECT shared FROM horse WHERE uuid = \"%s\"", horseUUID);
		return db.getBooleanResult(query);
	}
	
	public boolean isPlayerRegistered(String playerName) {
		String query = String.format("SELECT 1 FROM player WHERE name = \"%s\"", playerName);
		return db.hasResult(query);
	}
	
	public boolean isPlayerRegistered(UUID playerUUID) {
		String query = String.format("SELECT 1 FROM player WHERE uuid = \"%s\"", playerUUID);
		return db.hasResult(query);
	}
	
	public boolean registerFriend(UUID requesterUUID, UUID recipientUUID) {
		String update = String.format("INSERT INTO friend VALUES (\"%s\", \"%s\")", requesterUUID, recipientUUID);
		return db.executeUpdate(update);
	}
	
	public boolean registerHorse(UUID horseUUID, UUID ownerUUID, String horseName, boolean modeLocked, boolean modeProtected, boolean modeShared, Location location) {
		int horseID = getNextHorseID(ownerUUID);
		String locationWorld = location.getWorld().getName();
		int locationX = location.getBlockX();
		int locationY = location.getBlockY();
		int locationZ = location.getBlockZ();
		return registerHorse(horseUUID, ownerUUID, horseID, horseName, modeLocked, modeProtected, modeShared, locationWorld, locationX, locationY, locationZ);
	}

	public boolean registerHorse(UUID horseUUID, UUID ownerUUID, int horseID, String horseName,
			boolean modeLocked, boolean modeProtected, boolean modeShared, String locationWorld, int locationX, int locationY, int locationZ) {
		if (isHorseRegistered(horseUUID)) { // if horse was given, unregister it from giver's list
			removeHorse(horseUUID);
		}
		int lockedFlag = modeLocked ? 1 : 0;
		int protectedFlag = modeProtected ? 1 : 0;
		int sharedFlag = modeShared ? 1 : 0;
		String update = String.format("INSERT INTO horse VALUES (\"%s\", \"%s\", %d, \"%s\", %d, %d, %d, \"%s\", %d, %d, %d)",
				horseUUID, ownerUUID, horseID, horseName, lockedFlag, protectedFlag, sharedFlag, locationWorld, locationX, locationY, locationZ);
		return db.executeUpdate(update);
	}
	
	public boolean registerPlayer(UUID playerUUID, String playerName, String language, int favorite) {
		String update = String.format("INSERT INTO player VALUES (\"%s\", \"%s\", \"%s\", %d)", playerUUID, playerName, language, favorite);
		return db.executeUpdate(update);
	}
	
	public boolean removeFriend(UUID requesterUUID, UUID recipientUUID) {
		String update = String.format("DELETE FROM friend WHERE requester = \"%s\" AND recipient = \"%s\"", requesterUUID, recipientUUID);
		return db.executeUpdate(update);
	}
	
	public boolean removeHorse(UUID horseUUID) {
		UUID ownerUUID = getOwnerUUID(horseUUID);
		int horseID = getHorseID(horseUUID);
		return removeHorse(horseUUID, ownerUUID, horseID);
	}
	
	public boolean removeHorse(UUID ownerUUID, int horseID) {
		UUID horseUUID = getHorseUUID(ownerUUID, horseID);
		return removeHorse(horseUUID, ownerUUID, horseID);
	}
	
	public boolean removeHorse(UUID horseUUID, UUID ownerUUID, int horseID) {
		zh.getHM().unloadHorse(horseUUID);
		int favorite = getPlayerFavoriteHorseID(ownerUUID);
		if (horseID == favorite) {
			updatePlayerFavorite(ownerUUID, getDefaultFavoriteHorseID());
		}
		else if (horseID < favorite) {
			updatePlayerFavorite(ownerUUID, favorite - 1);
		}
		String deleteUpdate = String.format("DELETE FROM horse WHERE uuid = \"%s\"", horseUUID);
		String idUpdate = String.format("UPDATE horse SET id = id - 1 WHERE owner = \"%s\" AND id > %d", ownerUUID, horseID);
		return db.executeUpdate(deleteUpdate) && db.executeUpdate(idUpdate);
	}
	
	public boolean updateHorseLocation(UUID horseUUID, Location location, boolean checkForChanges) {
		if (checkForChanges && !hasLocationChanged(horseUUID, location)) {
			return true;
		}
		String update = String.format("UPDATE horse SET locationWorld = \"%s\", locationX = %d, locationY = %d, locationZ = %d WHERE uuid = \"%s\"",
				location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseLocked(UUID horseUUID, boolean modeLocked) {
		int lockedFlag = modeLocked ? 1 : 0;
		String update = String.format("UPDATE horse SET locked = %d WHERE uuid = \"%s\"", lockedFlag, horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseName(UUID horseUUID, String name) {
		String update = String.format("UPDATE horse SET name = \"%s\" WHERE uuid = \"%s\"", name, horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseProtected(UUID horseUUID, boolean modeProtected) {
		int protectedFlag = modeProtected ? 1 : 0;
		String update = String.format("UPDATE horse SET protected = %d WHERE uuid = \"%s\"", protectedFlag, horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseShared(UUID horseUUID, boolean modeShared) {
		int sharedFlag = modeShared ? 1 : 0;
		String update = String.format("UPDATE horse SET shared = %d WHERE uuid = \"%s\"", sharedFlag, horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseUUID(UUID oldHorseUUID, UUID newHorseUUID) {
		String update = String.format("UPDATE horse SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updatePlayerFavorite(UUID playerUUID, int favorite) {
		String update = String.format("UPDATE player SET favorite = %d WHERE uuid = \"%s\"", favorite, playerUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updatePlayerLanguage(UUID playerUUID, String language) {
		String update = String.format("UPDATE player SET language = \"%s\" WHERE uuid = \"%s\"", language, playerUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updatePlayerName(UUID playerUUID, String name) {
		String update = String.format("UPDATE player SET name = \"%s\" WHERE uuid = \"%s\"", name, playerUUID);
		return db.executeUpdate(update);
	}

}