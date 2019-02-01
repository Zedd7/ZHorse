package com.github.zedd7.zhorse.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.utils.CallbackListener;
import com.github.zedd7.zhorse.utils.CallbackResponse;

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

	protected void checkConnection() throws SQLException {
		if (connection.isClosed()) {
			openConnection();
		}
	}

	public String applyTablePrefix(String formatableQuery) {
		if (!tablePrefix.isEmpty()) {
			return formatableQuery.replaceAll(PREFIX_CODE, tablePrefix + "_");
		}
		else {
			return formatableQuery.replaceAll(PREFIX_CODE, "");
		}
	}

	public PreparedStatement getPreparedStatement(String formatableQuery) throws SQLException {
		checkConnection();
		String query = applyTablePrefix(formatableQuery);
		return connection.prepareStatement(query);
	}

	public boolean executeUpdate(String update, boolean sync, CallbackListener<Boolean> listener) {
		return executeUpdate(update, false, sync, listener);
	}

	public boolean executeUpdate(final String formatableUpdate, final boolean hideExceptions, boolean sync, final CallbackListener<Boolean> listener) {
		CallbackResponse<Boolean> response = new CallbackResponse<>();
		BukkitRunnable task = new BukkitRunnable() {

			@Override
			public void run() {
				boolean success = false;
				try (PreparedStatement statement = getPreparedStatement(formatableUpdate)) {
					statement.executeUpdate();
					success = true;
				} catch (SQLException e) {
					if (!hideExceptions) {
						e.printStackTrace();
						String update = applyTablePrefix(formatableUpdate);
						zh.getLogger().warning(String.format("SQLException caught on following (%s) execution attempt : %s", sync ? "sync" : "async", update));
					}
				}
				response.setResult(success);
				if (listener != null) {
					performCallback(response, sync, listener);
				}
			}

		};
		if (sync) {
			task.run(); // Use run() instead of runTask() to run on the same tick
			return response.getResult();
		}
		else {
			task.runTaskAsynchronously(zh);
			return true;
		}
	}

	public boolean hasResult(String query, boolean sync, CallbackListener<Boolean> listener) {
		Boolean result = getResult(query, resultSet -> hasResult(resultSet), sync, listener);
		return result != null && result; // Must be checked by caller if async
	}

	public Boolean getBooleanResult(String query, boolean sync, CallbackListener<Boolean> listener) {
		return getResult(query, resultSet -> getBooleanResult(resultSet), sync, listener);
	}

	public Integer getIntegerResult(String query, boolean sync, CallbackListener<Integer> listener) {
		return getResult(query, resultSet -> getIntegerResult(resultSet), sync, listener);
	}

	public Location getLocationResult(String query, boolean sync, CallbackListener<Location> listener) {
		return getResult(query, resultSet -> getLocationResult(resultSet), sync, listener);
	}

	public String getStringResult(String query, boolean sync, CallbackListener<String> listener) {
		return getResult(query, resultSet -> getStringResult(resultSet), sync, listener);
	}

	public List<String> getStringResultList(String query, boolean sync, CallbackListener<List<String>> listener) {
		return getResultList(query, resultSet -> getStringResult(resultSet), sync, listener);
	}

	public UUID getUUIDResult(String query, boolean sync, CallbackListener<UUID> listener) {
		return getResult(query, resultSet -> getUUIDResult(resultSet), sync, listener);
	}

	public List<UUID> getUUIDResultList(String query, boolean sync, CallbackListener<List<UUID>> listener) {
		return getResultList(query, resultSet -> getUUIDResult(resultSet), sync, listener);
	}

	public FriendRecord getFriendRecord(String query, boolean sync, CallbackListener<FriendRecord> listener) {
		return getResult(query, resultSet -> getFriendRecord(resultSet), sync, listener);
	}

	public List<FriendRecord> getFriendRecordList(String query, boolean sync, CallbackListener<List<FriendRecord>> listener) {
		return getResultList(query, resultSet -> getFriendRecord(resultSet), sync, listener);
	}

	public HorseRecord getHorseRecord(String query, boolean sync, CallbackListener<HorseRecord> listener) {
		return getResult(query, resultSet -> getHorseRecord(resultSet), sync, listener);
	}

	public List<HorseRecord> getHorseRecordList(String query, boolean sync, CallbackListener<List<HorseRecord>> listener) {
		return getResultList(query, resultSet -> getHorseRecord(resultSet), sync, listener);
	}

	public HorseDeathRecord getHorseDeathRecord(String query, boolean sync, CallbackListener<HorseDeathRecord> listener) {
		return getResult(query, resultSet -> getHorseDeathRecord(resultSet), sync, listener);
	}

	public List<HorseDeathRecord> getHorseDeathRecordList(String query, boolean sync, CallbackListener<List<HorseDeathRecord>> listener) {
		return getResultList(query, resultSet -> getHorseDeathRecord(resultSet), sync, listener);
	}

	public HorseInventoryRecord getHorseInventoryRecord(String query, boolean sync, CallbackListener<HorseInventoryRecord> listener) {
		return getResult(query, resultSet -> getHorseInventoryRecord(resultSet), sync, listener);
	}

	public List<HorseInventoryRecord> getHorseInventoryRecordList(String query, boolean sync, CallbackListener<List<HorseInventoryRecord>> listener) {
		return getResultList(query, resultSet -> getHorseInventoryRecord(resultSet), sync, listener);
	}

	public HorseStableRecord getHorseStableRecord(String query, boolean sync, CallbackListener<HorseStableRecord> listener) {
		return getResult(query, resultSet -> getHorseStableRecord(resultSet), sync, listener);
	}

	public List<HorseStableRecord> getHorseStableRecordList(String query, boolean sync, CallbackListener<List<HorseStableRecord>> listener) {
		return getResultList(query, resultSet -> getHorseStableRecord(resultSet), sync, listener);
	}

	public HorseStatsRecord getHorseStatsRecord(String query, boolean sync, CallbackListener<HorseStatsRecord> listener) {
		return getResult(query, resultSet -> getHorseStatsRecord(resultSet), sync, listener);
	}

	public List<HorseStatsRecord> getHorseStatsRecordList(String query, boolean sync, CallbackListener<List<HorseStatsRecord>> listener) {
		return getResultList(query, resultSet -> getHorseStatsRecord(resultSet), sync, listener);
	}

	public PendingMessageRecord getPendingMessageRecord(String query, boolean sync, CallbackListener<PendingMessageRecord> listener) {
		return getResult(query, resultSet -> getPendingMessageRecord(resultSet), sync, listener);
	}

	public List<PendingMessageRecord> getPendingMessageRecordList(String query, boolean sync, CallbackListener<List<PendingMessageRecord>> listener) {
		return getResultList(query, resultSet -> getPendingMessageRecord(resultSet), sync, listener);
	}

	public PlayerRecord getPlayerRecord(String query, boolean sync, CallbackListener<PlayerRecord> listener) {
		return getResult(query, resultSet -> getPlayerRecord(resultSet), sync, listener);
	}

	public List<PlayerRecord> getPlayerRecordList(String query, boolean sync, CallbackListener<List<PlayerRecord>> listener) {
		return getResultList(query, resultSet -> getPlayerRecord(resultSet), sync, listener);
	}

	public SaleRecord getSaleRecord(String query, boolean sync, CallbackListener<SaleRecord> listener) {
		return getResult(query, resultSet -> getSaleRecord(resultSet), sync, listener);
	}

	public List<SaleRecord> getSaleRecordList(String query, boolean sync, CallbackListener<List<SaleRecord>> listener) {
		return getResultList(query, resultSet -> getSaleRecord(resultSet), sync, listener);
	}

	private Boolean hasResult(ResultSet resultSet) throws SQLException {
		return true;
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

	private UUID getUUIDResult(ResultSet resultSet) throws SQLException {
		String stringUUID = resultSet.getString(1);
		return stringUUID != null ? UUID.fromString(stringUUID) : null;
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

	private HorseInventoryRecord getHorseInventoryRecord(ResultSet resultSet) throws SQLException {
		return new HorseInventoryRecord(
			resultSet.getString("uuid"),
			resultSet.getString("serial")
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

	private <T> T getResult(String formatableQuery, CheckedFunction<ResultSet, T> mapper, boolean sync, CallbackListener<T> listener) { // TODO merge with getResultList()
		CallbackResponse<T> response = new CallbackResponse<>();
		BukkitRunnable task = new BukkitRunnable() {

			@Override
			public void run() {
				T result = null;
				try (PreparedStatement statement = getPreparedStatement(formatableQuery)) {
					ResultSet resultSet = statement.executeQuery();
					if (resultSet.next()) {
						result = mapper.apply(resultSet);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					String query = applyTablePrefix(formatableQuery);
					zh.getLogger().warning(String.format("SQLException caught on following (%s) execution attempt : %s", sync ? "sync" : "async", query));
				}
				response.setResult(result);
				if (listener != null) {
					performCallback(response, sync, listener);
				}
			}

		};
		if (sync) {
			task.run(); // Use run() instead of runTask() to run on the same tick
			return response.getResult();
		}
		else {
			task.runTaskAsynchronously(zh);
			return null;
		}
	}

	private <T> List<T> getResultList(String query, CheckedFunction<ResultSet, T> mapper, boolean sync, CallbackListener<List<T>> listener) { // TODO merge with getResult()
		CallbackResponse<List<T>> response = new CallbackResponse<>();
		BukkitRunnable task = new BukkitRunnable() {

			@Override
			public void run() {
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
				response.setResult(resultList);
				if (listener != null) {
					performCallback(response, sync, listener);
				}
			}

		};
		if (sync) {
			task.run(); // Use run() instead of runTask() to run on the same tick
			return response.getResult();
		}
		else {
			task.runTaskAsynchronously(zh);
			return null;
		}

	}

	private <T> void performCallback(CallbackResponse<T> response, boolean sync, CallbackListener<T> listener) {
		BukkitRunnable task = new BukkitRunnable() { // Go back to main (sync) loop

			@Override
			public void run() {
				listener.callback(response);
			}

		};
		if (sync) {
			task.run(); // Use run() to stay on the same tick
		}
		else {
			task.runTask(zh); // Use runTask() to sync with the next tick
		}
	}

	@FunctionalInterface
	private interface CheckedFunction<T, R> {
	   R apply(T t) throws SQLException;
	}

}