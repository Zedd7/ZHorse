package eu.reborn_minecraft.zhorse.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import eu.reborn_minecraft.zhorse.ZHorse;

public class SQLDatabaseConnector {
	
	protected ZHorse zh;
	protected Connection connection;
	protected Statement statement;
	
	public SQLDatabaseConnector(ZHorse zh) {
		this.zh = zh;
	}
	
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