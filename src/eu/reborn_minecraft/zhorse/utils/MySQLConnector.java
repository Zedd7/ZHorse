package eu.reborn_minecraft.zhorse.utils;

import java.sql.DriverManager;

import eu.reborn_minecraft.zhorse.ZHorse;

public class MySQLConnector extends SQLDatabaseConnector {

	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String JDBC_URL = "jdbc:mysql://%s:%d/%s";

	public MySQLConnector(ZHorse zh) {
		super(zh);
		String host = zh.getCM().getDatabaseHost();
		int port = zh.getCM().getDatabasePort();
		String user = zh.getCM().getDatabaseUser();
		String password = zh.getCM().getDatabasePassword();
		String name = zh.getCM().getDatabaseName();
		if (host != null && port != 0 && user != null && password != null && name != null) {
			String url = String.format(JDBC_URL, host, port, name);			 
			openConnection(url, user, password);
		}
		else {
			zh.getLogger().severe("Could not open database because your config is incomplete !");
		}
		
	}
	
	public void openConnection(String url, String user, String password) {									
		try {
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			zh.getLogger().severe(String.format("Failed to open connection with %s !", url));
		}
	}
	
}