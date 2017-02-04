package eu.reborn_minecraft.zhorse.database;

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
		Boolean result = null;
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				result = resultSet.getInt(1) == 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Integer getIntegerResult(String query) {
		query = applyTablePrefix(query);
		Integer result = null;
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Location getLocationResult(String query) {
		query = applyTablePrefix(query);
		Location location = null;
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				int x = resultSet.getInt("locationX");
				int y = resultSet.getInt("locationY");
				int z = resultSet.getInt("locationZ");
				String worldName = resultSet.getString("locationWorld");
				World world = zh.getServer().getWorld(worldName);
				location = new Location(world, x, y, z);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}
	
	public String getStringResult(String query) {
		query = applyTablePrefix(query);
		String result = null;
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				result = resultSet.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<String> getStringResultList(String query) {
		query = applyTablePrefix(query);
		List<String> resultList = new ArrayList<>();
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
		boolean hasResult = false;
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			hasResult = resultSet.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasResult;
	}
	
	public List<FriendRecord> getFriendRecordList(String query) {
		query = applyTablePrefix(query);
		List<FriendRecord> friendRecordList = new ArrayList<>();
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				friendRecordList.add(new FriendRecord(
					resultSet.getString("requester"),
					resultSet.getString("recipient")
				));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return friendRecordList;
	}
	
	public FriendRecord getFriendRecord(String query) {
		query = applyTablePrefix(query);
		FriendRecord friendRecord = null;
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				friendRecord = new FriendRecord(
					resultSet.getString("requester"),
					resultSet.getString("recipient")
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return friendRecord;
	}
	
	public HorseRecord getHorseRecord(String query) {
		query = applyTablePrefix(query);
		HorseRecord horseRecord = null;
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				horseRecord = new HorseRecord(
					resultSet.getString("uuid"),
					resultSet.getString("owner"),
					resultSet.getInt("id"),
					resultSet.getString("name"),
					resultSet.getInt("locked") == 1,
					resultSet.getInt("protected") == 1,
					resultSet.getInt("shared") == 1,
					resultSet.getString("locationWorld"),
					resultSet.getInt("locationX"),
					resultSet.getInt("locationY"),
					resultSet.getInt("locationZ")
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return horseRecord;
	}
	
	public HorseStatsRecord getHorseStatsRecord(String query) {
		query = applyTablePrefix(query);
		HorseStatsRecord horseStatsRecord = null;
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				horseStatsRecord = new HorseStatsRecord(
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return horseStatsRecord;
	}
	
	public HorseInventoryRecord getHorseInventoryRecord(String query) {
		query = applyTablePrefix(query);
		HorseInventoryRecord horseInventoryRecord = null;
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				horseInventoryRecord = new HorseInventoryRecord(
					resultSet.getString("uuid"),
					resultSet.getString("inventory")
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return horseInventoryRecord;
	}
	
	public PlayerRecord getPlayerRecord(String query) {
		query = applyTablePrefix(query);
		PlayerRecord playerRecord = null;
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				playerRecord = new PlayerRecord(
					resultSet.getString("uuid"),
					resultSet.getString("name"),
					resultSet.getString("language"),
					resultSet.getInt("favorite")
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerRecord;
	}
	
}