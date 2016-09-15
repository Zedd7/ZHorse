package eu.reborn_minecraft.zhorse.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

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
	
	public Boolean getBooleanResult(String query) {
		ResultSet resultSet = executeQuery(query);
		try {
			if (resultSet.next()) {
				return resultSet.getInt(1) == 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Integer getIntegerResult(String query) {
		ResultSet resultSet = executeQuery(query);
		try {
			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Location getLocationResult(String query) {
		ResultSet resultSet = executeQuery(query);
		try {
			if (resultSet.next()) {
				String worldName = resultSet.getString(1);
				int x = resultSet.getInt(2);
				int y = resultSet.getInt(3);
				int z = resultSet.getInt(4);
				World world = zh.getServer().getWorld(worldName);
				return new Location(world, x, y, z);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getStringResult(String query) {
		ResultSet resultSet = executeQuery(query);
		try {
			if (resultSet.next()) {
				return resultSet.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<String> getStringResultList(String query) {
		List<String> resultList = new ArrayList<String>();
		ResultSet resultSet = executeQuery(query);
		try {
			while (resultSet.next()) {
				resultList.add(resultSet.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	public boolean hasResult(String query) {
		ResultSet resultSet = executeQuery(query);
		try {
			return resultSet.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}