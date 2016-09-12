package eu.reborn_minecraft.zhorse.managers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

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
	
	public int getHorseCount(UUID ownerUUID) {
		String query = String.format("SELECT COUNT(1) FROM horse WHERE owner = \"%s\"", ownerUUID);
		return db.getIntegerResult(query);
	}
	
	public Integer getHorseID(UUID ownerUUID, String horseName) {
		String query = String.format("SELECT id FROM horse WHERE owner = \"%s\" AND name = \"%s\"", ownerUUID, horseName);
		return db.getIntegerResult(query);
	}
	
	public String getHorseName(UUID horseUUID) {
		String query = String.format("SELECT name FROM horse WHERE uuid = \"%s\"", horseUUID);
		return db.getStringResult(query);
	}
	
	public String getHorseName(UUID ownerUUID, String horseID) {
		String query = String.format("SELECT name FROM horse WHERE owner = \"%s\" AND id = %d", ownerUUID, Integer.parseInt(horseID));
		return db.getStringResult(query);
	}
	
	public String getOwnerName(UUID horseUUID) {
		String query = String.format("SELECT p.name FROM player p WHERE p.uuid = (SELECT h.owner FROM horse h WHERE h.uuid = \"%s\")", horseUUID);
		return db.getStringResult(query);
	}
	
	public String getPlayerName(String targetName) {
		String query = "SELECT name FROM player";
		List<String> playerNameList = db.getStringResultList(query);
		for (String playerName : playerNameList) {
			if (targetName.equalsIgnoreCase(playerName)) {
				return playerName;
			}
		}
		return null;
	}
	
	public UUID getPlayerUUID(String playerName) {
		String query = String.format("SELECT uuid FROM player WHERE name = \"%s\"", playerName);
		return UUID.fromString(db.getStringResult(query));
	}
	
	public boolean isHorseOwnedBy(UUID ownerUUID, UUID horseUUID) {
		String query = String.format("SELECT 1 FROM horse WHERE uuid = \"%s\" AND owner = \"%s\"", horseUUID, ownerUUID);
		return db.getBooleanResult(query);
	}
	
	public boolean isHorseRegistered(UUID horseUUID) {
		String query = String.format("SELECT 1 FROM horse WHERE uuid = \"%s\"", horseUUID);
		return db.getBooleanResult(query);
	}
	
	public boolean isHorseRegistered(UUID ownerUUID, String horseID) {
		String query = String.format("SELECT 1 FROM horse WHERE owner = \"%s\" AND id = %d", ownerUUID, horseID);
		return db.getBooleanResult(query);
	}
	
	public boolean isPlayerRegistered(String playerName) {
		String query = String.format("SELECT 1 FROM player WHERE name = \"%s\"", playerName);
		return db.getBooleanResult(query);
	}
	
	public boolean isPlayerRegistered(UUID playerUUID) {
		String query = String.format("SELECT 1 FROM player WHERE uuid = \"%s\"", playerUUID);
		return db.getBooleanResult(query);
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

}