package com.github.xibalba.zhorse.database;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.github.xibalba.zhorse.ZHorse;

public class MySQLConnector extends SQLDatabaseConnector {

	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String JDBC_OPTIONS = "?verifyServerCertificate=false&useSSL=true&autoReconnect=true&maxReconnects=10&failOverReadOnly=false";
	private static final String JDBC_URL = "jdbc:mysql://%s:%d/%s" + JDBC_OPTIONS;
	
	private String host;
	private int port;
	private String user;
	private String password;
	private String name;
	private String url;

	public MySQLConnector(ZHorse zh) {
		super(zh);
		host = zh.getCM().getDatabaseHost();
		port = zh.getCM().getDatabasePort();
		user = zh.getCM().getDatabaseUser();
		password = zh.getCM().getDatabasePassword();
		name = zh.getCM().getDatabaseName();
		tablePrefix = zh.getCM().getDatabaseTablePrefix();
		if (host != null && port != 0 && user != null && password != null && name != null && tablePrefix != null) {
			url = String.format(JDBC_URL, host, port, name);
			try {
				Class.forName(JDBC_DRIVER);
				openConnection();
			} catch (SQLException e) {
				zh.getLogger().severe(String.format("Failed to open connection with %s !", url));
				zh.getLogger().severe("Verify that the database is created and that the user has access with given password.");
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
		connection = DriverManager.getConnection(url, user, password);
		connected = true;
	}
	
}