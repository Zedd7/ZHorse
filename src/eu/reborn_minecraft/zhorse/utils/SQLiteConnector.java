package eu.reborn_minecraft.zhorse.utils;

import java.io.File;
import java.sql.DriverManager;

import eu.reborn_minecraft.zhorse.ZHorse;

public class SQLiteConnector extends SQLDatabaseConnector {
	
	private static final String JDBC_DRIVER = "org.sqlite.JDBC";
	private static final String FILE_EXTENSION = ".db";

	public SQLiteConnector(ZHorse zh) {
		super(zh);
		String filename = zh.getCM().getDatabaseFileName();
		if (filename != null) {
			filename += FILE_EXTENSION;
			String url = new File(zh.getDataFolder(), filename).getPath();			 
			openConnection(url);
		}
		
	}
	
	public void openConnection(String url) {									
		try {
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection("jdbc:sqlite:" + url);
		} catch (Exception e) {
			zh.getLogger().severe(String.format("Failed to open connection with %s !", url));
		}
	}
	
}