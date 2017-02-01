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
	
	protected static final String PREFIX_CODE = "prefix_";
	
	protected ZHorse zh;
	protected Connection connection;
	protected boolean connected;
	protected String tablePrefix = "";
	
	public SQLDatabaseConnector(ZHorse zh) {
		this.zh = zh;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public String applyTablePrefix(String str) {
		if (!tablePrefix.isEmpty()) {
			return str.replaceAll(PREFIX_CODE, tablePrefix + "_");
		}
		else {
			return str.replaceAll(PREFIX_CODE, "");
		}
	}
	
	public void closeConnection() {									
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			zh.getLogger().severe("Failed to close connection with database !");
			e.printStackTrace();
		}
	}
	
	public boolean executeUpdate(String update) {
		update = applyTablePrefix(update);
		boolean result = false;
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(update);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Boolean getBooleanResult(String query) {
		query = applyTablePrefix(query);
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt(1) == 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public HorseStats getHorseStats(String query) {
		query = applyTablePrefix(query);
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				HorseStats horseStats = new HorseStats(
					resultSet.getString("uuid"),
					resultSet.getInt("age"),
					resultSet.getInt("canBreed") == 1,
					resultSet.getInt("canPickupItems") == 1,
					resultSet.getString("color"),
					resultSet.getInt("domestication"),
					resultSet.getInt("fireTicks"),
					resultSet.getDouble("health"),
					resultSet.getInt("isCustomNameVisible") == 1,
					resultSet.getInt("isGlowing") == 1,
					resultSet.getInt("isTamed") == 1,
					resultSet.getDouble("jumpStrength"),
					resultSet.getDouble("maxHealth"),
					resultSet.getInt("noDamageTicks"),
					resultSet.getInt("remainingAir"),
					resultSet.getDouble("speed"),
					resultSet.getInt("strength"),
					resultSet.getString("style"),
					resultSet.getInt("ticksLived"),
					resultSet.getString("type")
				);
				return horseStats;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Integer getIntegerResult(String query) {
		query = applyTablePrefix(query);
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Location getLocationResult(String query) {
		query = applyTablePrefix(query);
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				int x = resultSet.getInt("locationX");
				int y = resultSet.getInt("locationY");
				int z = resultSet.getInt("locationZ");
				String worldName = resultSet.getString("locationWorld");
				World world = zh.getServer().getWorld(worldName);
				return new Location(world, x, y, z);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getStringResult(String query) {
		query = applyTablePrefix(query);
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<String> getStringResultList(String query) {
		query = applyTablePrefix(query);
		List<String> resultList = new ArrayList<String>();
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				resultList.add(resultSet.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	public boolean hasResult(String query) {
		query = applyTablePrefix(query);
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			return resultSet.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}