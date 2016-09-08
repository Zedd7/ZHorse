package eu.reborn_minecraft.zhorse.utils;

import java.io.File;
import java.sql.DriverManager;

import eu.reborn_minecraft.zhorse.ZHorse;

public class SQLiteConnector extends SQLDatabaseConnector {
	
	private static final String DATABASE_NAME = "zhorse.db";
	private static final String JDBC_DRIVER = "org.sqlite.JDBC";

	public SQLiteConnector(ZHorse zh) {
		super(zh);
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