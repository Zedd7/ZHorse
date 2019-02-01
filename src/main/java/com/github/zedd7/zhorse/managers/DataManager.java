package com.github.zedd7.zhorse.managers;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.database.FriendRecord;
import com.github.zedd7.zhorse.database.HorseDeathRecord;
import com.github.zedd7.zhorse.database.HorseInventoryRecord;
import com.github.zedd7.zhorse.database.HorseRecord;
import com.github.zedd7.zhorse.database.HorseStableRecord;
import com.github.zedd7.zhorse.database.HorseStatsRecord;
import com.github.zedd7.zhorse.database.MySQLConnector;
import com.github.zedd7.zhorse.database.PendingMessageRecord;
import com.github.zedd7.zhorse.database.PlayerRecord;
import com.github.zedd7.zhorse.database.SQLDatabaseConnector;
import com.github.zedd7.zhorse.database.SQLiteConnector;
import com.github.zedd7.zhorse.database.SaleRecord;
import com.github.zedd7.zhorse.enums.DatabaseEnum;
import com.github.zedd7.zhorse.utils.CallbackListener;
import com.github.zedd7.zhorse.utils.CallbackResponse;

public class DataManager {

	public static final String[] TABLE_ARRAY = {"player", "friend", "pending_message", "horse", "horse_death", "horse_inventory", "horse_stable", "horse_stats", "sale"};
	public static final String[] PATCH_ARRAY = {"1.6.6", "1.6.10"};

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final int DEFAULT_HORSE_ID = 1;
	private static final int DEFAULT_DEAD_HORSE_ID = -1;
	private static final int DEFAULT_FAVORITE_HORSE_ID = DEFAULT_HORSE_ID;

	private ZHorse zh;
	private SQLDatabaseConnector db;
	private List<String> tableScriptList;
	private List<String> patchScriptList;
	private boolean connected = false;

	public DataManager(ZHorse zh) {
		this.zh = zh;
	}

	public void openDatabase() {
		DatabaseEnum database = zh.getCM().getDatabaseType();
		switch (database) {
		case MYSQL:
			db = new MySQLConnector(zh);
			break;
		case SQLITE:
			db = new SQLiteConnector(zh);
			break;
		default:
			String databaseType = database != null ? database.getName() : "Unknown database";
			zh.getLogger().severe(String.format("The database %s is not supported !", databaseType));
		}
		connected = db != null && db.isConnected();
		if (connected) {
			if (!executeScripts()) {
				zh.getLogger().severe("An error occured when initializing the tables. Check that the database is not corrupted.");
			}
		}
	}

	public void closeDatabase() {
		if (connected) {
			db.closeConnection();
		}
	}

	public void setScriptLists(List<String> tableScriptList, List<String> patchScriptList) {
		this.tableScriptList = tableScriptList;
		this.patchScriptList = patchScriptList;
	}

	public boolean executeScripts() {
		boolean success = true;
		for (String tableScript : tableScriptList) {
			success &= executeSQLScript(tableScript, false, true, null);
		}
		for (String patchScript : patchScriptList) {
			executeSQLScript(patchScript, true, true, null); // Always return false if patch already applied
		}
		return success;
	}

	private boolean executeSQLScript(String update, boolean hideExceptions, boolean sync, CallbackListener<Boolean> listener) {
		return db.executeUpdate(update, hideExceptions, sync, listener);
	}

	public Integer getDefaultHorseID() {
		return DEFAULT_HORSE_ID;
	}

	public Integer getDefaultDeadHorseID() {
		return DEFAULT_DEAD_HORSE_ID;
	}

	public Integer getDefaultFavoriteHorseID() {
		return DEFAULT_FAVORITE_HORSE_ID;
	}

	public List<String> getFriendNameList(UUID playerUUID, boolean sync, CallbackListener<List<String>> listener) {
		String query = String.format("SELECT name FROM prefix_player WHERE uuid IN (SELECT recipient FROM prefix_friend WHERE requester = \"%s\") ORDER BY NAME ASC", playerUUID);
		return db.getStringResultList(query, sync, listener);
	}

	public List<String> getFriendNameReverseList(UUID playerUUID, boolean sync, CallbackListener<List<String>> listener) {
		String query = String.format("SELECT name FROM prefix_player WHERE uuid IN (SELECT requester FROM prefix_friend WHERE recipient = \"%s\") ORDER BY NAME ASC", playerUUID);
		return db.getStringResultList(query, sync, listener);
	}

	public Integer getAliveHorseCount(UUID ownerUUID, boolean sync, CallbackListener<Integer> listener) {
		String query = String.format("SELECT COUNT(1) FROM prefix_horse h WHERE owner = \"%s\" AND h.uuid NOT IN (SELECT hd.uuid FROM prefix_horse_death hd)", ownerUUID);
		return db.getIntegerResult(query, sync, listener);
	}

	public Integer getDeadHorseCount(UUID ownerUUID, boolean sync, CallbackListener<Integer> listener) {
		String query = String.format("SELECT COUNT(1) FROM prefix_horse h, prefix_horse_death hd WHERE owner = \"%s\" AND h.uuid = hd.uuid", ownerUUID);
		return db.getIntegerResult(query, sync, listener);
	}

	public Integer getHorseID(UUID horseUUID, boolean sync, CallbackListener<Integer> listener) {
		String query = String.format("SELECT id FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getIntegerResult(query, sync, listener);
	}

	public Integer getHorseID(UUID ownerUUID, String horseName, boolean sync, CallbackListener<Integer> listener) {
		String query = String.format("SELECT h.id FROM prefix_horse h WHERE h.owner = \"%s\" AND h.name = \"%s\" AND h.uuid NOT IN (SELECT hd.uuid FROM prefix_horse_death hd)", ownerUUID, horseName);
		return db.getIntegerResult(query, sync, listener);
	}

	public Location getHorseLocation(UUID horseUUID, boolean sync, CallbackListener<Location> listener) {
		String query = String.format("SELECT locationWorld, locationX, locationY, locationZ FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getLocationResult(query, sync, listener);
	}

	public String getHorseName(UUID horseUUID, boolean sync, CallbackListener<String> listener) {
		String query = String.format("SELECT name FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getStringResult(query, sync, listener);
	}

	public String getHorseName(UUID ownerUUID, int horseID, boolean sync, CallbackListener<String> listener) {
		String query = String.format("SELECT name FROM prefix_horse WHERE owner = \"%s\" AND id = %d", ownerUUID, horseID);
		return db.getStringResult(query, sync, listener);
	}

	public String getHorseName(UUID ownerUUID, String wrongCaseHorseName) {
		String query = String.format("SELECT name FROM prefix_horse WHERE owner = \"%s\"", ownerUUID);
		List<String> horseNameList = db.getStringResultList(query, true, null);
		for (String horseName : horseNameList) {
			if (wrongCaseHorseName.equalsIgnoreCase(horseName)) {
				return horseName;
			}
		}
		return wrongCaseHorseName;
	}

	public String getHorseType(UUID horseUUID, boolean sync, CallbackListener<String> listener) {
		String query = String.format("SELECT type FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.getStringResult(query, sync, listener);
	}

	public UUID getHorseUUID(UUID ownerUUID, int horseID, boolean sync, CallbackListener<UUID> listener) {
		String query = String.format("SELECT uuid FROM prefix_horse WHERE owner = \"%s\" AND id = %d", ownerUUID, horseID);
		return db.getUUIDResult(query, sync, listener);
	}

	public List<UUID> getHorseUUIDList(UUID ownerUUID, boolean includeDeadHorses, boolean sync, CallbackListener<List<UUID>> listener) {
		String query;
		if (includeDeadHorses) {
			query = String.format("SELECT uuid FROM prefix_horse WHERE owner = \"%s\" ORDER BY id ASC", ownerUUID);
		}
		else {
			query = String.format("SELECT h.uuid FROM prefix_horse h WHERE h.owner = \"%s\" AND h.uuid NOT IN (SELECT hd.uuid FROM prefix_horse_death hd) ORDER BY h.id ASC", ownerUUID);
		}
		return db.getUUIDResultList(query, sync, listener);
	}

	public List<UUID> getHorseUUIDList(Chunk chunk, boolean sync, CallbackListener<List<UUID>> listener) {
		Location NWCorner = chunk.getBlock(0, 0, 0).getLocation();
		Location SECorner = chunk.getBlock(15, 0, 15).getLocation();
		String query = String.format("SELECT uuid FROM prefix_horse WHERE locationX >= %d AND locationX <= %d AND locationZ >= %d AND locationZ <= %d",
				NWCorner.getBlockX(), SECorner.getBlockX(), NWCorner.getBlockZ(), SECorner.getBlockZ());
		return db.getUUIDResultList(query, sync, listener);
	}

	public Location getHorseStableLocation(UUID horseUUID, boolean sync, CallbackListener<Location> listener) {
		String query = String.format("SELECT locationWorld, locationX, locationY, locationZ FROM prefix_horse_stable WHERE uuid = \"%s\"", horseUUID);
		return db.getLocationResult(query, sync, listener);
	}

	public Integer getNextHorseID(UUID ownerUUID) {
		String query = String.format("SELECT MAX(h.id) FROM prefix_horse h WHERE h.owner = \"%s\" AND h.uuid NOT IN (SELECT hd.uuid FROM prefix_horse_death hd)", ownerUUID);
		Integer horseID = db.getIntegerResult(query, true, null);
		if (horseID == null || horseID < DEFAULT_HORSE_ID) {
			return DEFAULT_HORSE_ID;
		}
		else {
			return horseID + 1;
		}
	}

	public UUID getLatestHorseDeathUUID(UUID ownerUUID, boolean sync, CallbackListener<UUID> listener) {
		String query = String.format(
				"SELECT hd1.uuid FROM prefix_horse_death hd1 WHERE hd1.date = (SELECT MAX(hd2.date) FROM prefix_horse_death hd2 WHERE hd2.uuid IN (SELECT h.uuid FROM prefix_horse h WHERE h.owner = \"%s\"))",
				ownerUUID);
		return db.getUUIDResult(query, sync, listener);
	}

	public UUID getOldestHorseDeathUUID(UUID ownerUUID, boolean sync, CallbackListener<UUID> listener) {
		String query = String.format(
				"SELECT hd1.uuid FROM prefix_horse_death hd1 WHERE hd1.date = (SELECT MIN(hd2.date) FROM prefix_horse_death hd2 WHERE hd2.uuid IN (SELECT h.uuid FROM prefix_horse h WHERE h.owner = \"%s\"))",
				ownerUUID);
		return db.getUUIDResult(query, sync, listener);
	}

	public String getOwnerName(UUID horseUUID, boolean sync, CallbackListener<String> listener) {
		String query = String.format("SELECT p.name FROM prefix_player p WHERE p.uuid = (SELECT h.owner FROM prefix_horse h WHERE h.uuid = \"%s\")", horseUUID);
		return db.getStringResult(query, sync, listener);
	}

	public UUID getOwnerUUID(UUID horseUUID, boolean sync, CallbackListener<UUID> listener) {
		String query = String.format("SELECT owner FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getUUIDResult(query, sync, listener);
	}

	public Integer getPlayerFavoriteHorseID(UUID ownerUUID, boolean sync, CallbackListener<Integer> listener) {
		String query = String.format("SELECT favorite FROM prefix_player WHERE uuid = \"%s\"", ownerUUID);
		return db.getIntegerResult(query, sync, listener);
	}

	public String getPlayerLanguage(UUID playerUUID, boolean sync, CallbackListener<String> listener) {
		String query = String.format("SELECT language FROM prefix_player WHERE uuid = \"%s\"", playerUUID);
		return db.getStringResult(query, sync, listener);
	}

	public String getPlayerName(String wrongCasePlayerName) {
		String query = "SELECT name FROM prefix_player";
		List<String> playerNameList = db.getStringResultList(query, true, null);
		for (String playerName : playerNameList) {
			if (wrongCasePlayerName.equalsIgnoreCase(playerName)) {
				return playerName;
			}
		}
		return wrongCasePlayerName;
	}

	public String getPlayerName(UUID playerUUID, boolean sync, CallbackListener<String> listener) {
		String query = String.format("SELECT name FROM prefix_player WHERE uuid = \"%s\"", playerUUID);
		return db.getStringResult(query, sync, listener);
	}

	public UUID getPlayerUUID(String playerName, boolean sync, CallbackListener<UUID> listener) {
		String query = String.format("SELECT uuid FROM prefix_player WHERE name = \"%s\"", playerName);
		return db.getUUIDResult(query, sync, listener);
	}

	public Integer getSalePrice(UUID horseUUID, boolean sync, CallbackListener<Integer> listener) {
		String query = String.format("SELECT price FROM prefix_sale WHERE uuid = \"%s\"", horseUUID);
		return db.getIntegerResult(query, sync, listener);
	}

	public Integer getTotalHorsesCount(boolean sync, CallbackListener<Integer> listener) {
		String query = "SELECT COUNT(1) FROM prefix_horse";
		return db.getIntegerResult(query, sync, listener);
	}

	public Integer getTotalOwnersCount(boolean sync, CallbackListener<Integer> listener) {
		String query = "SELECT COUNT(1) FROM prefix_player p WHERE EXISTS (SELECT h.uuid FROM prefix_horse h WHERE h.owner = p.uuid)";
		return db.getIntegerResult(query, sync, listener);
	}

	public Integer getTotalPlayersCount(boolean sync, CallbackListener<Integer> listener) {
		String query = "SELECT COUNT(1) FROM prefix_player";
		return db.getIntegerResult(query, sync, listener);
	}

	public HorseRecord getHorseRecord(UUID horseUUID, boolean sync, CallbackListener<HorseRecord> listener) {
		String query = String.format("SELECT * FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getHorseRecord(query, sync, listener);
	}

	public List<HorseRecord> getHorseRecordList(UUID ownerUUID, boolean includeDeadHorses, boolean sync, CallbackListener<List<HorseRecord>> listener) {
		String query;
		if (includeDeadHorses) {
			query = String.format("SELECT * FROM prefix_horse WHERE owner = \"%s\" ORDER BY id ASC", ownerUUID);
		}
		else {
			query = String.format("SELECT * FROM prefix_horse h WHERE h.owner = \"%s\" AND h.uuid NOT IN (SELECT hd.uuid FROM prefix_horse_death hd) ORDER BY h.id ASC", ownerUUID);
		}
		return db.getHorseRecordList(query, sync, listener);
	}

	public List<HorseDeathRecord> getHorseDeathRecordList(UUID ownerUUID, boolean sync, CallbackListener<List<HorseDeathRecord>> listener) {
		String query = String.format("SELECT * FROM prefix_horse_death hd WHERE uuid IN (SELECT h.uuid FROM prefix_horse h WHERE owner = \"%s\") ORDER BY hd.date DESC", ownerUUID);
		return db.getHorseDeathRecordList(query, sync, listener);
	}

	public HorseInventoryRecord getHorseInventoryRecord(UUID horseUUID, boolean sync, CallbackListener<HorseInventoryRecord> listener) {
		String query = String.format("SELECT * FROM prefix_horse_inventory WHERE uuid = \"%s\"", horseUUID);
		return db.getHorseInventoryRecord(query, sync, listener);
	}

	public HorseStableRecord getHorseStableRecord(UUID horseUUID, boolean sync, CallbackListener<HorseStableRecord> listener) {
		String query = String.format("SELECT * FROM prefix_horse_stable WHERE uuid = \"%s\"", horseUUID);
		return db.getHorseStableRecord(query, sync, listener);
	}

	public HorseStatsRecord getHorseStatsRecord(UUID horseUUID, boolean sync, CallbackListener<HorseStatsRecord> listener) {
		String query = String.format("SELECT * FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.getHorseStatsRecord(query, sync, listener);
	}

	public PlayerRecord getPlayerRecord(UUID playerUUID, boolean sync, CallbackListener<PlayerRecord> listener) {
		String query = String.format("SELECT * FROM prefix_player WHERE uuid = \"%s\"", playerUUID);
		return db.getPlayerRecord(query, sync, listener);
	}

	public List<PendingMessageRecord> getPendingMessageRecordList(UUID playerUUID, boolean sync, CallbackListener<List<PendingMessageRecord>> listener) {
		String query = String.format("SELECT * FROM prefix_pending_message WHERE uuid = \"%s\" ORDER BY date ASC", playerUUID);
		return db.getPendingMessageRecordList(query, sync, listener);
	}

	private boolean hasLocationChanged(UUID horseUUID, Location newLocation) {
		Location oldLocation = getHorseLocation(horseUUID, true, null);
		if (oldLocation != null) {
			return oldLocation.getWorld().getName() != newLocation.getWorld().getName()
					|| oldLocation.getBlockX() != newLocation.getBlockX()
					|| oldLocation.getBlockY() != newLocation.getBlockY()
					|| oldLocation.getBlockZ() != newLocation.getBlockZ();
		}
		return true;
	}

	public boolean isFriendOf(UUID requesterUUID, UUID recipientUUID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT 1 FROM prefix_friend WHERE requester = \"%s\" AND recipient = \"%s\"", requesterUUID, recipientUUID);
		return db.hasResult(query, sync, listener);
	}

	public boolean isHorseForSale(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT 1 FROM prefix_sale WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query, sync, listener);
	}

	public boolean isHorseLocked(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT locked FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getBooleanResult(query, sync, listener);
	}

	public boolean isHorseOfType(UUID horseUUID, String type, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT 1 FROM prefix_horse h WHERE h.uuid IN (SELECT hs.uuid FROM prefix_horse_stats hs WHERE hs.uuid = \"%s\" AND type = \"%s\")", horseUUID, type);
		return db.hasResult(query, sync, listener);
	}

	public boolean isHorseOwnedBy(UUID ownerUUID, UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT 1 FROM prefix_horse WHERE uuid = \"%s\" AND owner = \"%s\"", horseUUID, ownerUUID);
		return db.hasResult(query, sync, listener);
	}

	public boolean isHorseProtected(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT protected FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getBooleanResult(query, sync, listener);
	}

	public boolean isHorseRegistered(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT 1 FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query, sync, listener);
	}

	public boolean isHorseRegistered(UUID ownerUUID, int horseID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT 1 FROM prefix_horse h WHERE h.owner = \"%s\" AND h.id = %d AND h.uuid NOT IN (SELECT hd.uuid FROM prefix_horse_death hd)", ownerUUID, horseID);
		return db.hasResult(query, sync, listener);
	}

	public boolean isHorseShared(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT shared FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
		return db.getBooleanResult(query, sync, listener);
	}

	public boolean isHorseInventoryRegistered(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT 1 FROM prefix_horse_inventory WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query, sync, listener);
	}

	public boolean isHorseDeathRegistered(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT 1 FROM prefix_horse_death WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query, sync, listener);
	}

	public boolean isHorseStableRegistered(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT 1 FROM prefix_horse_stable WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query, sync, listener);
	}

	public boolean isHorseStatsRegistered(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT 1 FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.hasResult(query, sync, listener);
	}

	public boolean isPendingMessageRegistered(UUID playerUUID, Date date, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT 1 FROM prefix_pending_message WHERE uuid = \"%s\" AND date = \"%s\"", playerUUID, DATE_FORMAT.format(date));
		return db.hasResult(query, sync, listener);
	}

	public boolean isPlayerDisplayingExactStats(UUID playerUUID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT display_exact_stats FROM prefix_player WHERE uuid = \"%s\"", playerUUID);
		return db.getBooleanResult(query, sync, listener);
	}

	public boolean isPlayerRegistered(String playerName, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT 1 FROM prefix_player WHERE name = \"%s\"", playerName);
		return db.hasResult(query, sync, listener);
	}

	public boolean isPlayerRegistered(UUID playerUUID, boolean sync, CallbackListener<Boolean> listener) {
		String query = String.format("SELECT 1 FROM prefix_player WHERE uuid = \"%s\"", playerUUID);
		return db.hasResult(query, sync, listener);
	}

	public boolean registerFriend(FriendRecord friendRecord, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("INSERT INTO prefix_friend VALUES (\"%s\", \"%s\")", friendRecord.getRequester(), friendRecord.getRecipient());
		return db.executeUpdate(update, sync, listener);
	}

	public boolean registerHorse(HorseRecord horseRecord, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("INSERT INTO prefix_horse VALUES (\"%s\", \"%s\", %d, \"%s\", %d, %d, %d, \"%s\", %d, %d, %d)",
			horseRecord.getUUID(),
			horseRecord.getOwner(),
			horseRecord.getId(),
			horseRecord.getName(),
			horseRecord.isLocked() ? 1 : 0,
			horseRecord.isProtected() ? 1 : 0,
			horseRecord.isShared() ? 1 : 0,
			horseRecord.getLocationWorld(),
			horseRecord.getLocationX(),
			horseRecord.getLocationY(),
			horseRecord.getLocationZ()
		);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean registerHorseDeath(HorseDeathRecord horseDeathRecord, boolean sync, CallbackListener<Boolean> listener) {
		UUID horseUUID = UUID.fromString(horseDeathRecord.getUUID());
		getOwnerUUID(horseUUID, sync, new CallbackListener<UUID>() {

			@Override
			public void callback(CallbackResponse<UUID> response) {
				if (response.getResult() != null) {
					UUID ownerUUID = response.getResult();
					getHorseID(horseUUID, sync, new CallbackListener<Integer>() {

						@Override
						public void callback(CallbackResponse<Integer> response) {
							if (response.getResult() != null) {
								int horseID = response.getResult();
								int maxDeadHorseCount = zh.getCM().getRezStackMaxSize();
								if (maxDeadHorseCount > 0) {
									getDeadHorseCount(ownerUUID, sync, new CallbackListener<Integer>() {

										@Override
										public void callback(CallbackResponse<Integer> response) {
											if (response.getResult() != null) {
												int deadHorseCount = response.getResult();
												if (deadHorseCount >= maxDeadHorseCount) {
													getOldestHorseDeathUUID(ownerUUID, sync, new CallbackListener<UUID>() {

														@Override
														public void callback(CallbackResponse<UUID> response) {
															if (response.getResult() != null) {
																UUID oldestHorseDeathUUID = response.getResult();
																removeHorse(oldestHorseDeathUUID, ownerUUID, null, sync, null);
															}
														}

													});
												}
												updateHorseIDMapping(ownerUUID, horseID, sync, new CallbackListener<Boolean>() {

													@Override
													public void callback(CallbackResponse<Boolean> response) {
														String horseUpdate = String.format("UPDATE prefix_horse SET id = %s WHERE uuid = \"%s\"", DEFAULT_DEAD_HORSE_ID, horseUUID);
														if (response.getResult()) db.executeUpdate(horseUpdate, sync, null);
													}

												});
												String horseDeathUpdate = String.format("INSERT INTO prefix_horse_death VALUES (\"%s\", \"%s\")", horseDeathRecord.getUUID(), DATE_FORMAT.format(horseDeathRecord.getDate()));
												db.executeUpdate(horseDeathUpdate, sync, listener);
											}
										}

									});
								}
								else {
									removeHorse(horseUUID, ownerUUID, horseID, sync, listener);
								}
							}
						}

					});
				}
			}

		});

		return true;
	}

	public boolean registerHorseInventory(HorseInventoryRecord horseInventoryRecord, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("INSERT INTO prefix_horse_inventory VALUES (\"%s\", \"%s\")",
			horseInventoryRecord.getUUID(),
			horseInventoryRecord.getSerial()
		);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean registerHorseStable(HorseStableRecord horseStableRecord, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("INSERT INTO prefix_horse_stable VALUES (\"%s\", \"%s\", %d, %d, %d)",
			horseStableRecord.getUUID(),
			horseStableRecord.getLocationWorld(),
			horseStableRecord.getLocationX(),
			horseStableRecord.getLocationY(),
			horseStableRecord.getLocationZ()
		);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean registerHorseStats(HorseStatsRecord horseStatsRecord, boolean sync, CallbackListener<Boolean> listener) {
		String color = horseStatsRecord.getColor();
		String style = horseStatsRecord.getStyle();
		String update = String.format(Locale.US, "INSERT INTO prefix_horse_stats VALUES (\"%s\", %d, %d, %d, %s, \"%s\", %d, %d, %f, %d, %d, %d, %d, %f, %f, %d, %d, %f, %d, %s, %d, \"%s\")",
			horseStatsRecord.getUUID(),
			horseStatsRecord.getAge(),
			horseStatsRecord.canBreed() ? 1 : 0,
			horseStatsRecord.canPickupItems() ? 1 : 0,
			color != null ? "\"" + color + "\"" : null,
			horseStatsRecord.getCustomName(),
			horseStatsRecord.getDomestication(),
			horseStatsRecord.getFireTicks(),
			horseStatsRecord.getHealth(),
			horseStatsRecord.isCarryingChest() ? 1 : 0,
			horseStatsRecord.isCustomNameVisible() ? 1 : 0,
			horseStatsRecord.isGlowing() ? 1 : 0,
			horseStatsRecord.isTamed() ? 1 : 0,
			horseStatsRecord.getJumpStrength(),
			horseStatsRecord.getMaxHealth(),
			horseStatsRecord.getNoDamageTicks(),
			horseStatsRecord.getRemainingAir(),
			horseStatsRecord.getSpeed(),
			horseStatsRecord.getStrength(),
			style != null ? "\"" + style + "\"" : null,
			horseStatsRecord.getTicksLived(),
			horseStatsRecord.getType()
		);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean registerPendingMessage(PendingMessageRecord messageRecord, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("INSERT INTO prefix_pending_message VALUES (\"%s\", \"%s\", \"%s\")",
				messageRecord.getUUID(), DATE_FORMAT.format(messageRecord.getDate()), messageRecord.getMessage());
		return db.executeUpdate(update, sync, listener);
	}

	public boolean registerPlayer(PlayerRecord playerRecord, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("INSERT INTO prefix_player VALUES (\"%s\", \"%s\", \"%s\", %d, %d)",
			playerRecord.getUUID(), playerRecord.getName(), playerRecord.getLanguage(), playerRecord.getFavorite(), playerRecord.displayExactStats() ? 1 : 0);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean registerSale(SaleRecord saleRecord, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("INSERT INTO prefix_sale VALUES (\"%s\", %d)", saleRecord.getUUID(), saleRecord.getPrice());
		return db.executeUpdate(update, sync, listener);
	}

	public boolean removeFriend(UUID requesterUUID, UUID recipientUUID, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("DELETE FROM prefix_friend WHERE requester = \"%s\" AND recipient = \"%s\"", requesterUUID, recipientUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public void removeHorse(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		getOwnerUUID(horseUUID, sync, new CallbackListener<UUID>() {

			@Override
			public void callback(CallbackResponse<UUID> response) {
				if (response.getResult() != null) {
					UUID ownerUUID = response.getResult();
					removeHorse(horseUUID, ownerUUID, sync, listener);
				}
			}}

		);
	}

	public void removeHorse(UUID horseUUID, UUID ownerUUID, boolean sync, CallbackListener<Boolean> listener) {
		getHorseID(horseUUID, sync, new CallbackListener<Integer>() {

			@Override
			public void callback(CallbackResponse<Integer> response) {
				if (response.getResult() != null) {
					int horseID = response.getResult();
					removeHorse(horseUUID, ownerUUID, horseID, sync, listener);
				}
			}

		});
	}

	public void removeHorse(UUID horseUUID, UUID ownerUUID, Integer horseID, boolean sync, CallbackListener<Boolean> listener) {
		final CallbackListener<Boolean> updateHorseIDMappingListener = new CallbackListener<Boolean>() {
			@Override
			public void callback(CallbackResponse<Boolean> response) {
				if (response.getResult()) removeHorseDeath(horseUUID, sync, new CallbackListener<Boolean>() {
					@Override
					public void callback(CallbackResponse<Boolean> response) {
						if (response.getResult()) removeHorseInventory(horseUUID, sync, new CallbackListener<Boolean>() {
							@Override
							public void callback(CallbackResponse<Boolean> response) {
								if (response.getResult()) removeHorseStable(horseUUID, sync, new CallbackListener<Boolean>() {
									@Override
									public void callback(CallbackResponse<Boolean> response) {
										if (response.getResult()) removeHorseStats(horseUUID, sync, new CallbackListener<Boolean>() {
											@Override
											public void callback(CallbackResponse<Boolean> response) {
												if (response.getResult()) removeSale(horseUUID, sync, new CallbackListener<Boolean>() {
													@Override
													public void callback(CallbackResponse<Boolean> response) {
														if (response.getResult()) {
															String update = String.format("DELETE FROM prefix_horse WHERE uuid = \"%s\"", horseUUID);
															db.executeUpdate(update, sync, listener);
														}
													}
												});
											}
										});
									}
								});
							}
						});
					}
				});
			}
		};

		if (horseID != null) {
			updateHorseIDMapping(ownerUUID, horseID, sync, updateHorseIDMappingListener);
		}
		else {
			updateHorseIDMappingListener.callback(new CallbackResponse<>(true));
		}
	}

	public boolean removeHorseDeath(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("DELETE FROM prefix_horse_death WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean removeHorseInventory(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("DELETE FROM prefix_horse_inventory WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean removeHorseStable(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("DELETE FROM prefix_horse_stable WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean removeHorseStats(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("DELETE FROM prefix_horse_stats WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean removePendingMessages(UUID playerUUID, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("DELETE FROM prefix_pending_message WHERE uuid = \"%s\"", playerUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean removeSale(UUID horseUUID, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("DELETE FROM prefix_sale WHERE uuid = \"%s\"", horseUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updateHorseHealth(UUID horseUUID, double health, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("UPDATE prefix_horse_stats SET health = %f WHERE uuid = \"%s\"", health, horseUUID); /// TODO fix
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updateHorseID(UUID horseUUID, int horseID, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("UPDATE prefix_horse SET id = %d WHERE uuid = \"%s\"", horseID, horseUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updateHorseIDMapping(UUID ownerUUID, int removedHorseID, boolean sync, CallbackListener<Boolean> listener) {
		int favoriteHorseID = getPlayerFavoriteHorseID(ownerUUID, true, null);
		if (removedHorseID == favoriteHorseID) {
			updatePlayerFavoriteHorseID(ownerUUID, DEFAULT_FAVORITE_HORSE_ID, sync, null);
		}
		else if (removedHorseID < favoriteHorseID && removedHorseID != DEFAULT_DEAD_HORSE_ID) {
			updatePlayerFavoriteHorseID(ownerUUID, favoriteHorseID - 1, sync, null);
		}
		String update = String.format("UPDATE prefix_horse SET id = id - 1 WHERE owner = \"%s\" AND id > %d AND %d <> %d", ownerUUID, removedHorseID, removedHorseID, DEFAULT_DEAD_HORSE_ID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updateHorseIsCarryingChest(UUID horseUUID, boolean isCarryingChest, boolean sync, CallbackListener<Boolean> listener) {
		int isCarryingChestFlag = isCarryingChest ? 1 : 0;
		String update = String.format("UPDATE prefix_horse_stats SET isCarryingChest = %d WHERE uuid = \"%s\"", isCarryingChestFlag, horseUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updateHorseLocation(UUID horseUUID, Location location, boolean checkForChanges, boolean sync, CallbackListener<Boolean> listener) {
		if (checkForChanges && !hasLocationChanged(horseUUID, location)) {
			return true;
		}
		String update = String.format("UPDATE prefix_horse SET locationWorld = \"%s\", locationX = %d, locationY = %d, locationZ = %d WHERE uuid = \"%s\"",
				location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), horseUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updateHorseLocked(UUID horseUUID, boolean locked, boolean sync, CallbackListener<Boolean> listener) {
		int lockedFlag = locked ? 1 : 0;
		String update = String.format("UPDATE prefix_horse SET locked = %d WHERE uuid = \"%s\"", lockedFlag, horseUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updateHorseName(UUID horseUUID, String name, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("UPDATE prefix_horse SET name = \"%s\" WHERE uuid = \"%s\"", name, horseUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updateHorseOwner(UUID horseUUID, UUID ownerUUID, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("UPDATE prefix_horse SET owner = \"%s\" WHERE uuid = \"%s\"", ownerUUID, horseUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updateHorseProtected(UUID horseUUID, boolean protected_, boolean sync, CallbackListener<Boolean> listener) {
		int protectedFlag = protected_ ? 1 : 0;
		String update = String.format("UPDATE prefix_horse SET protected = %d WHERE uuid = \"%s\"", protectedFlag, horseUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updateHorseShared(UUID horseUUID, boolean shared, boolean sync, CallbackListener<Boolean> listener) {
		int sharedFlag = shared ? 1 : 0;
		String update = String.format("UPDATE prefix_horse SET shared = %d WHERE uuid = \"%s\"", sharedFlag, horseUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public void updateHorseUUID(UUID oldHorseUUID, UUID newHorseUUID, boolean sync, CallbackListener<Boolean> listener) {
		final String horseDeathUpdate = String.format("UPDATE prefix_horse_death SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		final String horseInventoryUpdate = String.format("UPDATE prefix_horse_inventory SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		final String horseStableUpdate = String.format("UPDATE prefix_horse_stable SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		final String horseStatsUpdate = String.format("UPDATE prefix_horse_stats SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		final String saleUpdate = String.format("UPDATE prefix_sale SET uuid = \"%s\" WHERE uuid = \"%s\"", newHorseUUID, oldHorseUUID);
		final String horseUpdate = String.format("DELETE FROM prefix_horse WHERE uuid = \"%s\"", oldHorseUUID);

		getHorseRecord(oldHorseUUID, sync, new CallbackListener<HorseRecord>() {
			@Override
			public void callback(CallbackResponse<HorseRecord> response) {
				if (response.getResult() != null) {
					HorseRecord horseRecord = response.getResult();
					horseRecord.setUUID(newHorseUUID.toString());
					registerHorse(horseRecord, sync, new CallbackListener<Boolean>() {
						@Override
						public void callback(CallbackResponse<Boolean> response) {
							if (response.getResult()) db.executeUpdate(horseDeathUpdate, sync, new CallbackListener<Boolean>() {
								@Override
								public void callback(CallbackResponse<Boolean> response) {
									if (response.getResult()) db.executeUpdate(horseInventoryUpdate, sync, new CallbackListener<Boolean>() {
										@Override
										public void callback(CallbackResponse<Boolean> response) {
											if (response.getResult()) db.executeUpdate(horseStableUpdate, sync, new CallbackListener<Boolean>() {
												@Override
												public void callback(CallbackResponse<Boolean> response) {
													if (response.getResult()) db.executeUpdate(horseStatsUpdate, sync, new CallbackListener<Boolean>() {
														@Override
														public void callback(CallbackResponse<Boolean> response) {
															if (response.getResult()) db.executeUpdate(saleUpdate, sync, new CallbackListener<Boolean>() {
																@Override
																public void callback(CallbackResponse<Boolean> response) {
																	if (response.getResult()) db.executeUpdate(horseUpdate, sync, listener);
																}
															});
														}
													});
												}
											});
										}
									});
								}
							});
						}

					});
				}
			}

		});
	}

	public boolean updateHorseInventory(HorseInventoryRecord horseInventoryRecord, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("UPDATE prefix_horse_inventory SET serial = \"%s\" WHERE uuid = \"%s\"",
				horseInventoryRecord.getSerial(),
				horseInventoryRecord.getUUID()
		);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updateHorseStats(HorseStatsRecord horseStatsRecord, boolean sync, CallbackListener<Boolean> listener) {
		String color = horseStatsRecord.getColor();
		String style = horseStatsRecord.getStyle();
		String update = String.format(Locale.US, "UPDATE prefix_horse_stats SET age = %d, canBreed = %d, canPickupItems = %d, color = %s, customName = \"%s\",domestication = %d, fireTicks = %d,"
				+ "health = %f, isCarryingChest = %d, isCustomNameVisible = %d, isGlowing = %d, isTamed = %d, jumpStrength = %f, maxHealth = %f, noDamageTicks = %d, remainingAir = %d, speed = %f,"
				+ "strength = %d, style = %s, ticksLived = %d, type = \"%s\" WHERE uuid = \"%s\"",
			horseStatsRecord.getAge(),
			horseStatsRecord.canBreed() ? 1 : 0,
			horseStatsRecord.canPickupItems() ? 1 : 0,
			color != null ? "\"" + color + "\"" : null,
			horseStatsRecord.getCustomName(),
			horseStatsRecord.getDomestication(),
			horseStatsRecord.getFireTicks(),
			horseStatsRecord.getHealth(),
			horseStatsRecord.isCarryingChest() ? 1 : 0,
			horseStatsRecord.isCustomNameVisible() ? 1 : 0,
			horseStatsRecord.isGlowing() ? 1 : 0,
			horseStatsRecord.isTamed() ? 1 : 0,
			horseStatsRecord.getJumpStrength(),
			horseStatsRecord.getMaxHealth(),
			horseStatsRecord.getNoDamageTicks(),
			horseStatsRecord.getRemainingAir(),
			horseStatsRecord.getSpeed(),
			horseStatsRecord.getStrength(),
			style != null ? "\"" + style + "\"" : null,
			horseStatsRecord.getTicksLived(),
			horseStatsRecord.getType(),
			horseStatsRecord.getUUID()
		);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updatePlayerDisplayExactStats(UUID playerUUID, boolean displayExactStats, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("UPDATE prefix_player SET display_exact_stats = %d WHERE uuid = \"%s\"", displayExactStats ? 1 : 0, playerUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updatePlayerFavoriteHorseID(UUID playerUUID, int favorite, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("UPDATE prefix_player SET favorite = %d WHERE uuid = \"%s\"", favorite, playerUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updatePlayerLanguage(UUID playerUUID, String language, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("UPDATE prefix_player SET language = \"%s\" WHERE uuid = \"%s\"", language, playerUUID);
		return db.executeUpdate(update, sync, listener);
	}

	public boolean updatePlayerName(UUID playerUUID, String name, boolean sync, CallbackListener<Boolean> listener) {
		String update = String.format("UPDATE prefix_player SET name = \"%s\" WHERE uuid = \"%s\"", name, playerUUID);
		return db.executeUpdate(update, sync, listener);
	}

}