package eu.reborn_minecraft.zhorse.managers;

import java.io.File;
import java.sql.*;

import eu.reborn_minecraft.zhorse.ZHorse;

public class DataManager {
		
	private ZHorse zh;
	private SQLDatabaseConnector db;
	
	public DataManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public void openDatabase() {
		switch (zh.getCM().getDatabaseType()) {
		case MYSQL:
			// TODO
			break;
		case SQLITE:
			db = new SQLiteConnector();
			break;
		}
		createTables();
	}
	
	public void closeDatabase() {
		db.closeConnection();
	}
	
	private boolean createTables() { // TEMP
		String update = "CREATE TABLE IF NOT EXISTS PLAYER (ID INT PRIMARY KEY NOT NULL, NAME TEXT NOT NULL)";
		return db.executeUpdate(update);
	}
	
	class SQLDatabaseConnector {
		
		protected Connection connection;
		protected Statement statement;
		
		public void closeConnection() {									
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				zh.getLogger().severe("Failed to close connection with database !");
				e.printStackTrace();
			}
		}
		
		public ResultSet executeQuery(String query) {
			ResultSet resultSet = null;
			try {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(query);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return resultSet;
		}
		
		public boolean executeUpdate(String update) {
			boolean result = false;
			try {
				statement = connection.createStatement();
				statement.executeUpdate(update);
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		
	}
	
	class SQLiteConnector extends SQLDatabaseConnector {
		
		private static final String DATABASE_NAME = "zhorse.db";
		private static final String JDBC_DRIVER = "org.sqlite.JDBC";

		
		public SQLiteConnector() {
			String url = new File(zh.getDataFolder(), DATABASE_NAME).getPath();			 
	    	openConnection(url);
		}
		
		public void openConnection(String url) {									
			try {
				Class.forName(JDBC_DRIVER);
				connection = DriverManager.getConnection("jdbc:sqlite:" + url);
			} catch (Exception e) {
				zh.getLogger().severe(String.format("Failed to open %s ! Disabling %s...", url, zh.getDescription().getName()));
		    	zh.getServer().getPluginManager().disablePlugin(zh);
			}
		}
		
	}

}