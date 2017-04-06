package com.gmail.xibalbazedd.zhorse.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;

import com.gmail.xibalbazedd.zhorse.ZHorse;

public abstract class SQLDatabaseConnector {
	
	protected static final String PREFIX_CODE = "prefix_";
	
	protected ZHorse zh;
	protected Connection connection;
	protected boolean connected;
	protected String tablePrefix = "";
	
	public SQLDatabaseConnector(ZHorse zh) {
		this.zh = zh;
	}
	
	protected abstract void openConnection() throws SQLException;
	
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
	
	public boolean isConnected() {
		return connected;
	}
	
	protected void reconnect() throws SQLException {
		if (connection.isClosed()) {
			openConnection();
		}
	}
	
	public String applyTablePrefix(String str) {
		if (!tablePrefix.isEmpty()) {
			return str.replaceAll(PREFIX_CODE, tablePrefix + "_");
		}
		else {
			return str.replaceAll(PREFIX_CODE, "");
		}
	}
	
	public PreparedStatement getPreparedStatement(String query) throws SQLException {
		String prefixedQuery = applyTablePrefix(query);
		reconnect();
		return connection.prepareStatement(prefixedQuery);
	}
	
	public boolean executeUpdate(String update) {
		boolean result = false;
		try (PreparedStatement statement = getPreparedStatement(update)) {
			statement.executeUpdate();
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Boolean getBooleanResult(String query) {
		Boolean result = null;
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				result = resultSet.getInt(1) == 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Integer getIntegerResult(String query) {
		Integer result = null;
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Location getLocationResult(String query) {
		Location location = null;
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				int x = resultSet.getInt("locationX");
				int y = resultSet.getInt("locationY");
				int z = resultSet.getInt("locationZ");
				String worldName = resultSet.getString("locationWorld");
				World world = zh.getServer().getWorld(worldName);
				location = new Location(world, x, y, z);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return location;
	}
	
	public List<String> getStringResultList(String query) {
		List<String> resultList = new ArrayList<>();
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				resultList.add(getStringResult(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	public String getStringResult(String query) {
		String result = null;
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				result = getStringResult(resultSet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String getStringResult(ResultSet resultSet) throws SQLException {
		return resultSet.getString(1);
	}
	
	public boolean hasResult(String query) {
		boolean hasResult = false;
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			hasResult = resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hasResult;
	}
	
	public List<FriendRecord> getFriendRecordList(String query) {
		List<FriendRecord> friendRecordList = new ArrayList<>();
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				friendRecordList.add(getFriendRecord(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return friendRecordList;
	}
	
	public FriendRecord getFriendRecord(String query) {
		FriendRecord friendRecord = null;
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				friendRecord = getFriendRecord(resultSet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return friendRecord;
	}
	
	public List<HorseRecord> getHorseRecordList(String query) {
		List<HorseRecord> horseRecordList = new ArrayList<>();
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				horseRecordList.add(getHorseRecord(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return horseRecordList;
	}
	
	public HorseRecord getHorseRecord(String query) {
		HorseRecord horseRecord = null;
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				horseRecord = getHorseRecord(resultSet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return horseRecord;
	}
	
	public List<HorseInventoryRecord> getHorseInventoryRecordList(String query) {
		List<HorseInventoryRecord> horseInventoryRecordList = new ArrayList<>();
		Map<String, List<InventoryItemRecord>> inventoryItemRecordMap = new HashMap<>();
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				InventoryItemRecord inventoryItemRecord = getInventoryItemRecord(resultSet);
				String horseUUID = inventoryItemRecord.getUUID();
				if (!inventoryItemRecordMap.containsKey(horseUUID)) {
					inventoryItemRecordMap.put(horseUUID, new ArrayList<>());
				}
				inventoryItemRecordMap.get(horseUUID).add(inventoryItemRecord);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (List<InventoryItemRecord> inventoryItemRecordList : inventoryItemRecordMap.values()) {
			horseInventoryRecordList.add(new HorseInventoryRecord(inventoryItemRecordList));
		}
		return horseInventoryRecordList;
	}
	
	public HorseInventoryRecord getHorseInventoryRecord(String query) {
		List<InventoryItemRecord> inventoryItemRecordList = new ArrayList<>();
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				inventoryItemRecordList.add(getInventoryItemRecord(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new HorseInventoryRecord(inventoryItemRecordList);
	}
	
	public List<HorseStatsRecord> getHorseStatsRecordList(String query) {
		List<HorseStatsRecord> horseStatsRecordList = new ArrayList<>();
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				horseStatsRecordList.add(getHorseStatsRecord(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return horseStatsRecordList;
	}
	
	public HorseStatsRecord getHorseStatsRecord(String query) {
		HorseStatsRecord horseStatsRecord = null;
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				horseStatsRecord = getHorseStatsRecord(resultSet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return horseStatsRecord;
	}
	
	public List<PlayerRecord> getPlayerRecordList(String query) {
		List<PlayerRecord> playerRecordList = new ArrayList<>();
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				playerRecordList.add(getPlayerRecord(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return playerRecordList;
	}
	
	public PlayerRecord getPlayerRecord(String query) {
		PlayerRecord playerRecord = null;
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				playerRecord = getPlayerRecord(resultSet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return playerRecord;
	}
	
	public List<SaleRecord> getSaleRecordList(String query) {
		List<SaleRecord> saleRecordList = new ArrayList<>();
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				saleRecordList.add(getSaleRecord(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return saleRecordList;
	}
	
	public SaleRecord getSaleRecord(String query) {
		SaleRecord saleRecord = null;
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				saleRecord = getSaleRecord(resultSet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return saleRecord;
	}
	
	private FriendRecord getFriendRecord(ResultSet resultSet) throws SQLException {
		return new FriendRecord(
			resultSet.getString("requester"),
			resultSet.getString("recipient")
		);
	}
	
	private HorseRecord getHorseRecord(ResultSet resultSet) throws SQLException {
		return new HorseRecord(
			resultSet.getString("uuid"),
			resultSet.getString("OWNER"),
			resultSet.getInt("ID"),
			resultSet.getString("NAME"),
			resultSet.getInt("locked") == 1,
			resultSet.getInt("protected") == 1,
			resultSet.getInt("shared") == 1,
			resultSet.getString("locationWorld"),
			resultSet.getInt("locationX"),
			resultSet.getInt("locationY"),
			resultSet.getInt("locationZ")
		);
	}
	
	private InventoryItemRecord getInventoryItemRecord(ResultSet resultSet) throws SQLException {
		return new InventoryItemRecord(
			resultSet.getString("uuid"),
			resultSet.getInt("slot"),
			resultSet.getString("data")
		);
	}
	
	private HorseStatsRecord getHorseStatsRecord(ResultSet resultSet) throws SQLException {
		return new HorseStatsRecord(
			resultSet.getString("uuid"),
			resultSet.getInt("age"),
			resultSet.getInt("canBreed") == 1,
			resultSet.getInt("canPickupItems") == 1,
			resultSet.getString("color"),
			resultSet.getString("customName"),
			resultSet.getInt("domestication"),
			resultSet.getInt("fireTicks"),
			resultSet.getDouble("HEALTH"),
			resultSet.getInt("isCarryingChest") == 1,
			resultSet.getInt("isCustomNameVisible") == 1,
			resultSet.getInt("isGlowing") == 1,
			resultSet.getInt("isTamed") == 1,
			resultSet.getDouble("jumpStrength"),
			resultSet.getDouble("maxHealth"),
			resultSet.getInt("noDamageTicks"),
			resultSet.getInt("remainingAir"),
			resultSet.getDouble("SPEED"),
			resultSet.getInt("STRENGTH"),
			resultSet.getString("style"),
			resultSet.getInt("ticksLived"),
			resultSet.getString("type")
		);
	}
	
	private PlayerRecord getPlayerRecord(ResultSet resultSet) throws SQLException {
		return new PlayerRecord(
			resultSet.getString("uuid"),
			resultSet.getString("NAME"),
			resultSet.getString("language"),
			resultSet.getInt("favorite")
		);
	}
	
	private SaleRecord getSaleRecord(ResultSet resultSet) throws SQLException {
		return new SaleRecord(
			resultSet.getString("uuid"),
			resultSet.getInt("price")
		);
	}
	
	// TODO pass lambda fonction instead of type of class
	// TODO use it
	@Deprecated
	@SuppressWarnings({ "unchecked", "unused" })
	private <T> List<T> getResultList(String query, Class<T> type) {
		List<T> resultList = new ArrayList<>();
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				T result = null;
				if (type.equals(FriendRecord.class)) {
					result = (T) getFriendRecord(resultSet);
				}
				else if (type.equals(HorseRecord.class)) {
					result = (T) getHorseRecord(resultSet);
				}
				else if (type.equals(HorseStatsRecord.class)) {
					result = (T) getHorseStatsRecord(resultSet);
				}
				else if (type.equals(PlayerRecord.class)) {
					result = (T) getPlayerRecord(resultSet);
				}
				else if (type.equals(SaleRecord.class)) {
					result = (T) getSaleRecord(resultSet);
				}
				resultList.add(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
}