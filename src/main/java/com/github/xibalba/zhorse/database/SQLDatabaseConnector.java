package com.github.xibalba.zhorse.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;

import com.github.xibalba.zhorse.ZHorse;

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
		reconnect();
		String prefixedQuery = applyTablePrefix(query);
		return connection.prepareStatement(prefixedQuery);
	}
	
	public boolean executeUpdate(String update) {
		return executeUpdate(update, false);
	}
	
	public boolean executeUpdate(String update, boolean hideExceptions) {
		boolean result = false;
		try (PreparedStatement statement = getPreparedStatement(update)) {
			statement.executeUpdate();
			result = true;
		} catch (SQLException e) {
			if (!hideExceptions) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public boolean hasResult(String query) {
		Boolean result = getResult(query, resultSet -> true);
		return result != null && result;
	}
	
	public Boolean getBooleanResult(String query) {
		return getResult(query, resultSet -> getBooleanResult(resultSet));
	}
	
	public Integer getIntegerResult(String query) {
		return getResult(query, resultSet -> getIntegerResult(resultSet));
	}
	
	public Location getLocationResult(String query) {
		return getResult(query, resultSet -> getLocationResult(resultSet));
	}
	
	public List<String> getStringResultList(String query) {
		return getResultList(query, resultSet -> getStringResult(resultSet));
	}
	
	public String getStringResult(String query) {
		return getResult(query, resultSet -> getStringResult(resultSet));
	}
	
	public List<FriendRecord> getFriendRecordList(String query) {
		return getResultList(query, resultSet -> getFriendRecord(resultSet));
	}
	
	public FriendRecord getFriendRecord(String query) {
		return getResult(query, resultSet -> getFriendRecord(resultSet));
	}
	
	public List<HorseRecord> getHorseRecordList(String query) {
		return getResultList(query, resultSet -> getHorseRecord(resultSet));
	}
	
	public HorseRecord getHorseRecord(String query) {
		return getResult(query, resultSet -> getHorseRecord(resultSet));
	}
	
	public List<HorseDeathRecord> getHorseDeathRecordList(String query) {
		return getResultList(query, resultSet -> getHorseDeathRecord(resultSet));
	}
	
	public HorseDeathRecord getHorseDeathRecord(String query) {
		return getResult(query, resultSet -> getHorseDeathRecord(resultSet));
	}
	
	public List<HorseInventoryRecord> getHorseInventoryRecordList(String query) {
		List<HorseInventoryRecord> inventoryRecordList = new ArrayList<>();
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
			String horseUUID = inventoryItemRecordList.get(0).getUUID();
			inventoryRecordList.add(new HorseInventoryRecord(horseUUID, inventoryItemRecordList));
		}
		return inventoryRecordList;
	}
	
	public HorseInventoryRecord getHorseInventoryRecord(String query, UUID inventoryHolderUUID) {
		HorseInventoryRecord inventoryRecord = null;
		List<InventoryItemRecord> inventoryItemRecordList = new ArrayList<>();
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				inventoryItemRecordList.add(getInventoryItemRecord(resultSet));
			}
			String horseUUID = !inventoryItemRecordList.isEmpty() ? inventoryItemRecordList.get(0).getUUID() : inventoryHolderUUID.toString();
			inventoryRecord = new HorseInventoryRecord(horseUUID, inventoryItemRecordList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return inventoryRecord;
	}
	
	public List<HorseStableRecord> getHorseStableRecordList(String query) {
		return getResultList(query, resultSet -> getHorseStableRecord(resultSet));
	}
	
	public HorseStableRecord getHorseStableRecord(String query) {
		return getResult(query, resultSet -> getHorseStableRecord(resultSet));
	}
	
	public List<HorseStatsRecord> getHorseStatsRecordList(String query) {
		return getResultList(query, resultSet -> getHorseStatsRecord(resultSet));
	}
	
	public HorseStatsRecord getHorseStatsRecord(String query) {
		return getResult(query, resultSet -> getHorseStatsRecord(resultSet));
	}
	
	public List<PendingMessageRecord> getPendingMessageRecordList(String query) {
		return getResultList(query, resultSet -> getPendingMessageRecord(resultSet));
	}
	
	public PendingMessageRecord getPendingMessageRecord(String query) {
		return getResult(query, resultSet -> getPendingMessageRecord(resultSet));
	}
	
	public List<PlayerRecord> getPlayerRecordList(String query) {
		return getResultList(query, resultSet -> getPlayerRecord(resultSet));
	}
	
	public PlayerRecord getPlayerRecord(String query) {
		return getResult(query, resultSet -> getPlayerRecord(resultSet));
	}
	
	public List<SaleRecord> getSaleRecordList(String query) {
		return getResultList(query, resultSet -> getSaleRecord(resultSet));
	}
	
	public SaleRecord getSaleRecord(String query) {
		return getResult(query, resultSet -> getSaleRecord(resultSet));
	}
	
	private <T> List<T> getResultList(String query, CheckedFunction<ResultSet, T> mapper) {
		List<T> resultList = new ArrayList<>();
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				T result = mapper.apply(resultSet);
				resultList.add(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	private <T> T getResult(String query, CheckedFunction<ResultSet, T> mapper) {
		T result = null;
		try (PreparedStatement statement = getPreparedStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				result = mapper.apply(resultSet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@FunctionalInterface
	private interface CheckedFunction<T, R> {
	   R apply(T t) throws SQLException;
	}
	
	private Boolean getBooleanResult(ResultSet resultSet) throws SQLException {
		return resultSet.getInt(1) == 1;
	}
	
	private Integer getIntegerResult(ResultSet resultSet) throws SQLException {
		return resultSet.getInt(1);
	}
	
	private String getStringResult(ResultSet resultSet) throws SQLException {
		return resultSet.getString(1);
	}
	
	private Location getLocationResult(ResultSet resultSet) throws SQLException {
		return new Location(
			zh.getServer().getWorld(resultSet.getString("locationWorld")),
			resultSet.getInt("locationX"),
			resultSet.getInt("locationY"),
			resultSet.getInt("locationZ")
		);
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
	
	private HorseDeathRecord getHorseDeathRecord(ResultSet resultSet) throws SQLException {
		return new HorseDeathRecord(
			resultSet.getString("uuid"),
			new Date(resultSet.getTimestamp("date").getTime())
		);
	}
	
	private HorseStableRecord getHorseStableRecord(ResultSet resultSet) throws SQLException {
		return new HorseStableRecord(
			resultSet.getString("uuid"),
			resultSet.getString("locationWorld"),
			resultSet.getInt("locationX"),
			resultSet.getInt("locationY"),
			resultSet.getInt("locationZ")
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
			resultSet.getDouble("health"),
			resultSet.getInt("isCarryingChest") == 1,
			resultSet.getInt("isCustomNameVisible") == 1,
			resultSet.getInt("isGlowing") == 1,
			resultSet.getInt("isTamed") == 1,
			resultSet.getDouble("jumpStrength"),
			resultSet.getDouble("maxHealth"),
			resultSet.getInt("noDamageTicks"),
			resultSet.getInt("remainingAir"),
			resultSet.getDouble("SPEED"),
			resultSet.getInt("strength"),
			resultSet.getString("style"),
			resultSet.getInt("ticksLived"),
			resultSet.getString("type")
		);
	}
	
	private InventoryItemRecord getInventoryItemRecord(ResultSet resultSet) throws SQLException {
		return new InventoryItemRecord(
			resultSet.getString("uuid"),
			resultSet.getInt("slot"),
			resultSet.getString("data")
		);
	}
	
	private PendingMessageRecord getPendingMessageRecord(ResultSet resultSet) throws SQLException {
		return new PendingMessageRecord(
			resultSet.getString("uuid"),
			new Date(resultSet.getTimestamp("date").getTime()),
			resultSet.getString("message")
		);
	}
	
	private PlayerRecord getPlayerRecord(ResultSet resultSet) throws SQLException {
		return new PlayerRecord(
			resultSet.getString("uuid"),
			resultSet.getString("name"),
			resultSet.getString("language"),
			resultSet.getInt("favorite"),
			resultSet.getInt("display_exact_stats") == 1
		);
	}
	
	private SaleRecord getSaleRecord(ResultSet resultSet) throws SQLException {
		return new SaleRecord(
			resultSet.getString("uuid"),
			resultSet.getInt("price")
		);
	}
	
}