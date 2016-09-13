package eu.reborn_minecraft.zhorse.managers;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.DatabaseEnum;
import eu.reborn_minecraft.zhorse.utils.MySQLConnector;
import eu.reborn_minecraft.zhorse.utils.SQLDatabaseConnector;
import eu.reborn_minecraft.zhorse.utils.SQLiteConnector;

public class DataManager {
	
	private static final String UPDATE_TABLES_SCRIPT_PATH = "res\\sql\\update-tables.sql";
	
	private ZHorse zh;
	private SQLDatabaseConnector db;
	
	public DataManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public void openDatabase() {
		DatabaseEnum database = zh.getCM().getDatabase();
		switch (database) {
		case MYSQL:
			db = new MySQLConnector(zh);
			break;
		case SQLITE:
			db = new SQLiteConnector(zh);
			break;
		default:
			zh.getLogger().severe(String.format("The database %s is not supported ! Disabling %s...", database.getName(), zh.getDescription().getName()));
			zh.getServer().getPluginManager().disablePlugin(zh);
		}
		updateTables();
	}
	
	public void closeDatabase() {
		db.closeConnection();
	}
	
	private boolean updateTables() {
		String update = "";
		try {
			String scriptPath = UPDATE_TABLES_SCRIPT_PATH.replace('\\', '/'); // Dark Magic Industries
			update = IOUtils.toString(zh.getResource(scriptPath), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return db.executeUpdate(update);
	}
	
	public Integer getFavoriteHorseID(UUID ownerUUID) {
		String query = String.format("SELECT favorite FROM player WHERE uuid = \"%s\"", ownerUUID);
		return db.getIntegerResult(query);
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
	
	public Location getHorseLocation(UUID ownerUUID, Integer horseID) {
		String query = String.format("SELECT locationWorld, locationX, locationY, location Z FROM horse WHERE owner = \"%s\" AND id = %d", ownerUUID, horseID);
		ResultSet resultSet = db.executeQuery(query);
		try {
			if (resultSet.next()) {
				String worldName = resultSet.getString("locationWorld");
				int x = resultSet.getInt("locationX");
				int y = resultSet.getInt("locationY");
				int z = resultSet.getInt("locationZ");
				World world = zh.getServer().getWorld(worldName);
				return new Location(world, x, y, z);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getHorseName(UUID horseUUID) {
		String query = String.format("SELECT name FROM horse WHERE uuid = \"%s\"", horseUUID);
		return db.getStringResult(query);
	}
	
	public String getHorseName(UUID ownerUUID, int horseID) {
		String query = String.format("SELECT name FROM horse WHERE owner = \"%s\" AND id = %d", ownerUUID, horseID);
		return db.getStringResult(query);
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
	
	public String getPlayerLanguage(UUID playerUUID) {
		String query = String.format("SELECT language FROM player WHERE uuid = \"%s\"", playerUUID);
		return db.getStringResult(query);
	}
	
	public String getPlayerName(String approximatePlayerName) {
		String query = "SELECT name FROM player";
		List<String> playerNameList = db.getStringResultList(query);
		for (String playerName : playerNameList) {
			if (approximatePlayerName.equalsIgnoreCase(playerName)) {
				return playerName;
			}
		}
		return null;
	}
	
	public UUID getPlayerUUID(String playerName) {
		String query = String.format("SELECT uuid FROM player WHERE name = \"%s\"", playerName);
		return UUID.fromString(db.getStringResult(query));
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
	
	public boolean registerHorse(Horse horse, UUID ownerUUID, String horseName, boolean modeLocked, boolean modeProtected, boolean modeShared) {
		UUID horseUUID = horse.getUniqueId();
		int horseID = getNextHorseID(ownerUUID);
		Location horseLocation = horse.getLocation();
		String locationWorld = horseLocation.getWorld().getName();
		int locationX = horseLocation.getBlockX();
		int locationY = horseLocation.getBlockY();
		int locationZ = horseLocation.getBlockZ();
		return registerHorse(horseUUID, ownerUUID, horseID, horseName, modeLocked, modeProtected, modeShared, locationWorld, locationX, locationY, locationZ);
	}

	public boolean registerHorse(UUID horseUUID, UUID ownerUUID, int horseID, String horseName,
			boolean modeLocked, boolean modeProtected, boolean modeShared, String locationWorld, int locationX, int locationY, int locationZ) {
		String update = String.format("INSERT INTO horse VALUES (\"%s\", \"%s\", %d, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", %d, %d, %d)",
				horseUUID, ownerUUID, horseID, horseName, modeLocked, modeProtected, modeShared, locationWorld, locationX, locationY, locationZ);
		return db.executeUpdate(update);
	}
	
	public boolean registerPlayer(UUID playerUUID, String playerName, String language, Integer favorite) {
		String update = String.format("INSERT INTO player VALUES (\"%s\", \"%s\", \"%s\", %d)",
				playerUUID, playerName, language, favorite);
		return db.executeUpdate(update);
	}
	
	public boolean removeHorse(UUID horseUUID) {
		String update = String.format("DELETE FROM horse WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean removeHorse(UUID ownerUUID, int horseID) {
		String update = String.format("DELETE FROM horse WHERE owner = \"%s\" AND id = %d", ownerUUID, horseID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseLocation(UUID horseUUID, Location location) {
		String update = String.format("UPDATE horse SET locationWorld = \"%s\", locationX = %d, locationY = %d, locationZ = %d WHERE uuid = \"%s\"",
				location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), horseUUID);
		return db.executeUpdate(update);
	}
	
	public boolean updateHorseUUID(UUID oldHorseUUID, UUID newHorseUUID) {
		String update = String.format("UPDATE horse SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		return db.executeUpdate(update);
	}

}