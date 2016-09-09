package eu.reborn_minecraft.zhorse.managers;

import java.io.IOException;

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

}