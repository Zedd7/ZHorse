package com.github.xibalba.zhorse.database;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.github.xibalba.zhorse.ZHorse;

public class SQLiteConnector extends SQLDatabaseConnector {
	
	private static final String JDBC_DRIVER = "org.sqlite.JDBC";
	private static final String JDBC_URL = "jdbc:sqlite:%s";
	private static final String FILE_EXTENSION = ".db";
	
	private String filename;
	private String filePath;
	private String url;

	public SQLiteConnector(ZHorse zh) {
		super(zh);
		filename = zh.getCM().getDatabaseFileName();
		if (filename != null) {
			filename += FILE_EXTENSION;
			filePath = new File(zh.getDataFolder(), filename).getPath();		
			url = String.format(JDBC_URL, filePath);
			try {
				Class.forName(JDBC_DRIVER);
				openConnection();
			} catch (SQLException e) {
				zh.getLogger().severe(String.format("Failed to open connection with %s !", url));
				e.printStackTrace();
				connected = false;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		else {
			zh.getLogger().severe("Could not connect to the database because your config is incomplete !");
		}
		
	}
	
	
	@Override
	public void openConnection() throws SQLException {
		connection = DriverManager.getConnection(url);
		connected = true;
	}
	
}