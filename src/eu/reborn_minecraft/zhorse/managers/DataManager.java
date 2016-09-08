package eu.reborn_minecraft.zhorse.managers;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.utils.MySQLConnector;
import eu.reborn_minecraft.zhorse.utils.SQLDatabaseConnector;
import eu.reborn_minecraft.zhorse.utils.SQLiteConnector;

public class DataManager {
	
	private static final String CREATE_TABLES_SCRIPT_PATH = "res\\sql\\create-tables.sql";
	@SuppressWarnings("unused")
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
		
	private ZHorse zh;
	private SQLDatabaseConnector db;
	
	public DataManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public void openDatabase() {
		switch (zh.getCM().getDatabaseType()) {
		case MYSQL:
			db = new MySQLConnector(zh);
			break;
		case SQLITE:
			db = new SQLiteConnector(zh);
			break;
		}
		createTables();
	}
	
	public void closeDatabase() {
		db.closeConnection();
	}
	
	private boolean createTables() {
		String update = "";
		try {
			String scriptPath = CREATE_TABLES_SCRIPT_PATH.replace('\\', '/'); // WTF Magic Industries
			update = IOUtils.toString(zh.getResource(scriptPath), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return db.executeUpdate(update);
	}

}