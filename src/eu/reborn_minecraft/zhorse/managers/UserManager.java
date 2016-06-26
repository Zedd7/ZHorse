package eu.reborn_minecraft.zhorse.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.utils.Utf8YamlConfiguration;

public class UserManager {
	
	private static final String USERS_PATH = "users.yml";
	
	private ZHorse zh;
	private FileConfiguration users;
	private Map<UUID, UUID> cachedPlayerUUID = new HashMap<>(); // <horseUUID, playerUUID>
	private Map<UUID, UUID> cachedIsClaimedBy = new HashMap<>();  // <horseUUID, playerUUID>
	
	public UserManager(ZHorse zh) {
		this.zh = zh;
		File usersFile = new File(zh.getDataFolder(), USERS_PATH);    
		if (!usersFile.exists()) {
			zh.getLogger().info(USERS_PATH + " is missing... Creating it.");
			zh.saveResource(USERS_PATH, false);			
		}
		users = Utf8YamlConfiguration.loadConfiguration(usersFile);
	}
	
	private void saveUsers() {
		File usersFile = new File(zh.getDataFolder(), USERS_PATH);
        try {
			users.save(usersFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getClaimsAmount(UUID playerUUID) {
		if (isRegistered(playerUUID)) {
			ConfigurationSection cs = users.getConfigurationSection(getPlayerPath(playerUUID, KeyWordEnum.horses.getValue()));
			if (cs != null) {
				return cs.getKeys(false).size();
			}
		}
		return 0;
	}
	
	public String getDefaultFavoriteUserID() {
		return "1";
	}
	
	public String getFavoriteUserID(UUID playerUUID) {
		if (playerUUID != null) {
			return getPlayerData(playerUUID, KeyWordEnum.favorite.getValue(), getDefaultFavoriteUserID());
		}
		return getDefaultFavoriteUserID();
	}
	
	public List<String> getHorseNameList(UUID playerUUID) {
		if (isRegistered(playerUUID)) {
			List<String> horseList = new ArrayList<String>();
			ConfigurationSection cs = users.getConfigurationSection(getPlayerPath(playerUUID, KeyWordEnum.horses.getValue()));
			if (cs != null) {
				for (String userID : cs.getKeys(false)) {
					horseList.add(getHorseName(playerUUID, userID));
				}
			}
			return horseList;
		}
		return null;
	}
	
	public String getHorseName(Horse horse) {
		if (horse != null) {
			UUID playerUUID = getPlayerUUID(horse);
			return getHorseName(playerUUID, horse);
		}
		return null;
	}
	
	public String getHorseName(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			return getHorseName(playerUUID, getUserID(playerUUID, horse));
		}
		return null;
	}

	public String getHorseName(UUID playerUUID, String userID) {
		if (playerUUID != null && userID != null) {
			return getHorseData(playerUUID, userID, KeyWordEnum.name.getValue(), null);
		}
		return null;
	}
	
	private String getHorsePath(UUID playerUUID, String userID) {
		if (playerUUID != null && userID != null) {
			String fullPath = KeyWordEnum.players.getValue()
					+ KeyWordEnum.dot.getValue()
					+ playerUUID.toString()
					+ KeyWordEnum.dot.getValue()
					+ KeyWordEnum.horses.getValue()
					+ KeyWordEnum.dot.getValue()
					+ userID;
			return fullPath;
		}
		return null;
	}
	
	private String getHorsePath(UUID playerUUID, String userID, String path) {
		if (playerUUID != null && userID != null && path != null) {
			return getHorsePath(playerUUID, userID) + KeyWordEnum.dot.getValue() + path;
		}
		return null;
	}
	
	private String getHorseData(UUID playerUUID, String userID, String path, String defaultValue) {
		if (playerUUID != null && userID != null && path != null) {
			return users.getString(getHorsePath(playerUUID, userID, path), defaultValue);
		}
		return defaultValue;
	}
	
	private boolean getHorseData(UUID playerUUID, String userID, String path, boolean defaultValue) {
		if (playerUUID != null && userID != null && path != null) {
			return users.getBoolean(getHorsePath(playerUUID, userID, path), defaultValue);
		}
		return defaultValue;
	}
	
	public UUID getHorseUUID(UUID playerUUID, String userID) {
		if (playerUUID != null && userID != null) {
			String horseUUID = getHorseData(playerUUID, userID, KeyWordEnum.uuid.getValue(), null);
			if (horseUUID != null) {
				return UUID.fromString(horseUUID);
			}
		}
		return null;
	}
	
	public String getLanguage(CommandSender s) {
		if (s != null && s instanceof Player) {
			return getLanguage(((Player)s).getUniqueId());
		}
		else {
			return zh.getCM().getDefaultLanguage();
		}
	}
	
	public String getLanguage(UUID playerUUID) {
		if (playerUUID != null) {
			return getPlayerData(playerUUID, KeyWordEnum.language.getValue(), zh.getCM().getDefaultLanguage());
		}
		return null;
	}
	
	public Location getLocation(UUID playerUUID, String userID) {
		if (playerUUID != null && userID != null) {
			String worldName = getHorseData(playerUUID, userID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.world.getValue(), null);
			if (worldName != null) {
				World world = zh.getServer().getWorld(worldName);
				if (world != null) {
					double x = Double.parseDouble(getHorseData(playerUUID, userID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.x.getValue(), null));
					double y = Double.parseDouble(getHorseData(playerUUID, userID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.y.getValue(), null));
					double z = Double.parseDouble(getHorseData(playerUUID, userID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.z.getValue(), null));
					return new Location(world, x, y, z);
				}
			}
		}
		return null;
	}
	
	public String getNextUserID(UUID playerUUID) {
        int userID = 1;
		if (isRegistered(playerUUID)) {
			ConfigurationSection cs = users.getConfigurationSection(getPlayerPath(playerUUID, KeyWordEnum.horses.getValue()));
			if (cs != null) {
				while(cs.getKeys(false).contains(Integer.toString(userID))) {
					userID++;
				}
			}
		}
		return Integer.toString(userID);
	}
	
	public String getPlayerName(String targetName) {
		if (targetName != null) {
			ConfigurationSection cs = users.getConfigurationSection(getPlayerPath());
	        if (cs != null) {
	        	for (String player : cs.getKeys(false)) {
	 	    	  UUID playerUUID = UUID.fromString(player);
	 	    	  String playerName = getPlayerData(playerUUID, KeyWordEnum.name.getValue(), null);
	 	    	  if (targetName.equalsIgnoreCase(playerName)) {
		    		   return playerName;
		    	  }
	        	}
		    }
			return targetName;
		}
        return null;
	}
	
	public String getPlayerName(UUID playerUUID) {
		if (playerUUID != null) {
			return getPlayerData(playerUUID, KeyWordEnum.name.getValue(), null);
		}
		return null;
	}
	
	public String getPlayerName(Horse horse) {
		if (horse != null) {
			UUID playerUUID = getPlayerUUID(horse);
			return getPlayerName(playerUUID);
		}
		return null;
	}
	
	public UUID getPlayerUUID(Horse horse) {
		if (horse != null) {
			if (cachedPlayerUUID.containsKey(horse.getUniqueId())) {
				return cachedPlayerUUID.get(horse.getUniqueId());
			}
			ConfigurationSection cs = users.getConfigurationSection(getPlayerPath());
			if (cs != null) {
				synchronized(cs) {
					for (String player : cs.getKeys(false)) {
						UUID playerUUID = UUID.fromString(player);
						ConfigurationSection subCS = cs.getConfigurationSection(playerUUID + KeyWordEnum.dot.getValue() + KeyWordEnum.horses.getValue());
						if (subCS != null) {
							for (String userID : subCS.getKeys(false)) {
								UUID horseUUID = getHorseUUID(playerUUID, userID);
								if (horseUUID != null && horseUUID.equals(horse.getUniqueId())) {
									cachedPlayerUUID.put(horse.getUniqueId(), playerUUID);
									return playerUUID;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public UUID getPlayerUUID(String playerName) {
		if (playerName != null) {
			ConfigurationSection cs = users.getConfigurationSection(getPlayerPath());
	        if (cs != null) {
	        	for (String playerUUID : cs.getKeys(false)) {
		    	   if (playerName.equalsIgnoreCase(getPlayerData(UUID.fromString(playerUUID), KeyWordEnum.name.getValue(), null))) {
		    		   return UUID.fromString(playerUUID);
		    	   }
		        }
	        }
		}
		return null;
	}
	
	private String getPlayerPath() {
		return KeyWordEnum.players.getValue();
	}
	
	private String getPlayerPath(UUID playerUUID) {
		if (playerUUID != null) {
			return KeyWordEnum.players.getValue() + KeyWordEnum.dot.getValue() + playerUUID.toString();
		}
		return null;
	}
	
	private String getPlayerPath(UUID playerUUID, String path) {
		if (playerUUID != null && path != null) {
			return getPlayerPath(playerUUID) + KeyWordEnum.dot.getValue() + path;
		}
		return null;
	}
	
	private String getPlayerData(UUID playerUUID, String path, String defaultValue) {
		if (playerUUID != null && path != null) {
			return users.getString(getPlayerPath(playerUUID, path), defaultValue);
		}
		return defaultValue;
	}
	
	@SuppressWarnings("unused")
	private boolean getPlayerData(UUID playerUUID, String path, boolean defaultValue) {
		if (playerUUID != null && path != null) {
			return users.getBoolean(getPlayerPath(playerUUID, path), defaultValue);
		}
		return defaultValue;
	}
	
	public String getUserID(Horse horse) {
		if (horse != null) {
			UUID playerUUID = getPlayerUUID(horse);
			return getUserID(playerUUID, horse);
		}
		return null;
	}
	
	public String getUserID(UUID playerUUID, Horse horse) {
		if (isRegistered(playerUUID)) {
			ConfigurationSection cs = users.getConfigurationSection(getPlayerPath(playerUUID, KeyWordEnum.horses.getValue()));
			if (cs != null) {
				for (String userID : cs.getKeys(false)) {
					if (getHorseUUID(playerUUID, userID).equals(horse.getUniqueId())) {
						return userID;
					}
				}
			}
		}
		return null;
	}
	
	public String getUserID(UUID playerUUID, String horseName) {
		if (isRegistered(playerUUID)) {
			ConfigurationSection cs = users.getConfigurationSection(getPlayerPath(playerUUID, KeyWordEnum.horses.getValue()));
			if (cs != null) {
				for (String userID : cs.getKeys(false)) {
					if (getHorseName(playerUUID, userID).equalsIgnoreCase(horseName)) {
						return userID;
					}
				}
			}
		}
		return null;
	}
	
	private boolean hasLocationChanged(Location oldLoc, Location newLoc) {
		if (oldLoc != null && newLoc != null) {
			int oldX = oldLoc.getBlockX();
			int oldY = oldLoc.getBlockY();
			int oldZ = oldLoc.getBlockZ();
			
			int newX = newLoc.getBlockX();
			int newY = newLoc.getBlockY();
			int newZ = newLoc.getBlockZ();
			
			return oldX != newX || oldY != newY || oldZ != newZ;
		}
		return true;
	}
	
	public boolean isClaimedBy(UUID playerUUID, Horse horse) {
		if (cachedIsClaimedBy.containsKey(horse.getUniqueId()) && cachedIsClaimedBy.get(horse.getUniqueId()).equals(playerUUID)) {
			return true;
		}
		if (isRegistered(playerUUID)) {
			ConfigurationSection cs = users.getConfigurationSection(getPlayerPath(playerUUID, KeyWordEnum.horses.getValue()));
			if (cs != null) {
				for (String userID : cs.getKeys(false)) {
					if (getHorseUUID(playerUUID, userID).equals(horse.getUniqueId())) {
						cachedIsClaimedBy.put(horse.getUniqueId(), playerUUID);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean isLocked(Horse horse) {
		return horse != null && isLocked(getPlayerUUID(horse), horse);
	}
	
	public boolean isLocked(UUID playerUUID, Horse horse) {
		return playerUUID != null && horse != null && isLocked(playerUUID, getUserID(playerUUID, horse));
	}
	
	public boolean isLocked(UUID playerUUID, String userID) { 
		return playerUUID != null && userID != null && getHorseData(playerUUID, userID, KeyWordEnum.modeLocked.getValue(), false);
	}
	
	public boolean isProtected(Horse horse) {
		return horse != null && isProtected(getPlayerUUID(horse), horse);
	}
	
	public boolean isProtected(UUID playerUUID, Horse horse) {
		return playerUUID != null && horse != null && isProtected(playerUUID, getUserID(playerUUID, horse));
	}
	
	public boolean isProtected(UUID playerUUID, String userID) {
		return playerUUID != null && userID != null && getHorseData(playerUUID, userID, KeyWordEnum.modeProtected.getValue(), false);
	}
	
	public boolean isRegistered(String playerName) {
		if (playerName != null) {
			ConfigurationSection cs = users.getConfigurationSection(getPlayerPath());
			if (cs != null) {
				for (String playerUUID : cs.getKeys(false)) {
					if (playerName.equalsIgnoreCase(getPlayerName(UUID.fromString(playerUUID)))) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean isRegistered(UUID playerUUID) {
		return playerUUID != null && users.contains(getPlayerPath(playerUUID));
	}
	
	public boolean isRegistered(Horse horse) {
		return horse != null && getPlayerUUID(horse) != null;
	}
	
	public boolean isRegistered(UUID playerUUID, String userID) {
		return playerUUID != null && userID != null && users.contains(getHorsePath(playerUUID, userID));
	}
	
	public boolean isShared(Horse horse) {
		return horse != null && isShared(getPlayerUUID(horse), horse);
	}
	
	public boolean isShared(UUID playerUUID, Horse horse) {
		return playerUUID != null && horse != null && isShared(playerUUID, getUserID(playerUUID, horse));
	}
	
	public boolean isShared(UUID playerUUID, String userID) {
		return playerUUID != null && userID != null && getHorseData(playerUUID, userID, KeyWordEnum.modeShared.getValue(), false);
	}
	
	public boolean isSpawned(UUID playerUUID, String userID) {
		return playerUUID != null && userID != null && zh.getHM().getLoadedHorse(getHorseUUID(playerUUID, userID)) != null;
	}
	
	public void lock(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			String userID = getUserID(playerUUID, horse);
			if (!isLocked(playerUUID, userID)) {
				setHorseData(playerUUID, userID, KeyWordEnum.modeLocked.getValue(), true);
				saveUsers();
			}
		}
	}
	
	public void unLock(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			String userID = getUserID(playerUUID, horse);
			if (isLocked(playerUUID, userID)) {
				setHorseData(playerUUID, userID, KeyWordEnum.modeLocked.getValue(), false);
				saveUsers();
			}
		}
	}
	
	public void protect(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			String userID = getUserID(playerUUID, horse);
			if (!isProtected(playerUUID, userID)) {
				setHorseData(playerUUID, userID, KeyWordEnum.modeProtected.getValue(), true);
				saveUsers();
			}
		}
	}
	
	public void unProtect(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			String userID = getUserID(playerUUID, horse);
			if (isProtected(playerUUID, userID)) {
				setHorseData(playerUUID, userID, KeyWordEnum.modeProtected.getValue(), false);
				saveUsers();
			}
		}
	}
	
	public void registerHorse(UUID playerUUID, Horse horse, String horseName, boolean lock, boolean protect, boolean share) {
		if (playerUUID != null && horse != null && horseName != null) {
			if (!isRegistered(playerUUID)) {
				registerPlayer(playerUUID);
			}
			if (isRegistered(horse)) { // if horse is a gift, unregister it from giver's list
				unRegisterHorse(horse);
			}
			String userID = getNextUserID(playerUUID);
			UUID horseUUID = horse.getUniqueId();
			setHorseData(playerUUID, userID, KeyWordEnum.name.getValue(), horseName);
			setHorseData(playerUUID, userID, KeyWordEnum.modeLocked.getValue(), lock);
			setHorseData(playerUUID, userID, KeyWordEnum.modeProtected.getValue(), protect);
			setHorseData(playerUUID, userID, KeyWordEnum.modeShared.getValue(), share);
			setHorseData(playerUUID, userID, KeyWordEnum.uuid.getValue(), horseUUID.toString());
			saveLocation(playerUUID, horse, userID);
			
			zh.getHM().loadHorse(horse);
		}
	}
	
	public void registerPlayer(UUID playerUUID) {
		if (playerUUID != null) {
			String playerName = zh.getServer().getOfflinePlayer(playerUUID).getName();
			String language = zh.getCM().getDefaultLanguage();
			String favorite = getDefaultFavoriteUserID();
			if (playerName != null && language != null) {
				setPlayerData(playerUUID, KeyWordEnum.name.getValue(), playerName);
				setPlayerData(playerUUID, KeyWordEnum.language.getValue(), language);
				setPlayerData(playerUUID, KeyWordEnum.favorite.getValue(), favorite);
				saveUsers();
			}
		}
	}
	
	public void rename(UUID playerUUID, Horse horse, String horseName) {
		if (playerUUID != null && horse != null && horseName != null) {
			String userID = getUserID(playerUUID, horse);
			if (!getHorseName(playerUUID, userID).equals(horseName)) {
				setHorseData(playerUUID, userID, KeyWordEnum.name.getValue(), horseName);
				saveUsers();
			}
		}
	}
	
	public void saveFavorite(UUID playerUUID, String userID) {
		if (playerUUID != null && userID != null) {
			if (!getFavoriteUserID(playerUUID).equals(userID)) {
				setPlayerData(playerUUID, KeyWordEnum.favorite.getValue(), userID);
				saveUsers();
			}
		}
	}
	
	public void saveLanguage(UUID playerUUID, String language) {
		if (playerUUID != null && language != null) {
			if (!getLanguage(playerUUID).equals(language)) {
				setPlayerData(playerUUID, KeyWordEnum.language.getValue(), language);
				saveUsers();
			}
		}
	}
	
	public void saveLocation(Horse horse) {
		if (horse != null) {
			UUID playerUUID = getPlayerUUID(horse);
			String userID = getUserID(playerUUID, horse);
			saveLocation(playerUUID, horse, userID);
		}
	}
	
	public void saveLocation(UUID playerUUID, Horse horse, String userID) {
		if (playerUUID != null && horse != null && userID != null) {
			if (hasLocationChanged(getLocation(playerUUID, userID), horse.getLocation())) {
				String world = horse.getWorld().getName();
				Double xLoc = horse.getLocation().getX();
				Double yLoc = horse.getLocation().getY();
				Double zLoc = horse.getLocation().getZ();
				String xPos = Integer.toString(xLoc.intValue());
				String yPos = Integer.toString(yLoc.intValue());
				String zPos = Integer.toString(zLoc.intValue());
				setHorseData(playerUUID, userID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.world.getValue(), world);
				setHorseData(playerUUID, userID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.x.getValue(), xPos);
				setHorseData(playerUUID, userID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.y.getValue(), yPos);
				setHorseData(playerUUID, userID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.z.getValue(), zPos);
				saveUsers();
			}
		}
	}

	private void setHorseData(UUID playerUUID, String userID, String path, String value) {
		if (playerUUID != null && userID != null && path != null) {
			setStringData(getHorsePath(playerUUID, userID, path), value);
		}
	}
	
	private void setHorseData(UUID playerUUID, String userID, String path, boolean value) {
		if (playerUUID != null && userID != null && path != null) {
			setBooleanData(getHorsePath(playerUUID, userID, path), value);
		}
	}
	
	private void setHorseData(UUID playerUUID, String userID, ConfigurationSection value) {
		if (playerUUID != null && userID != null) {
			setConfigurationSectionData(getHorsePath(playerUUID, userID), value);
		}
	}
	
	private void setPlayerData(UUID playerUUID, String path, String value) {
		if (playerUUID != null && path != null) {
			users.set(getPlayerPath(playerUUID, path), value);
		}
	}
	
	@SuppressWarnings("unused")
	private void setPlayerData(UUID playerUUID, String path, boolean value) {
		if (playerUUID != null && path != null) {
			setBooleanData(getPlayerPath(playerUUID, path), value);
		}
	}
	
	@SuppressWarnings("unused")
	private void setPlayerData(UUID playerUUID, ConfigurationSection value) {
		if (playerUUID != null) {
			setConfigurationSectionData(getPlayerPath(playerUUID), value);
		}
	}
	
	private void setBooleanData(String path, boolean value) {
		if (path != null) {
			users.set(path, value);
		}
	}
	
	private void setConfigurationSectionData(String path, ConfigurationSection value) {
		if (path != null) {
			users.set(path, value);
		}
	}
	
	private void setStringData(String path, String value) {
		if (path != null) {
			users.set(path, value);
		}
	}
	
	public void share(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			String userID = getUserID(playerUUID, horse);
			if (!isShared(playerUUID, userID)) {
				setHorseData(playerUUID, userID, KeyWordEnum.modeShared.getValue(), true);
				saveUsers();
			}
		}
	}
	
	public void unShare(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			String userID = getUserID(playerUUID, horse);
			if (isShared(playerUUID, userID)) {
				setHorseData(playerUUID, userID, KeyWordEnum.modeShared.getValue(), false);
				saveUsers();
			}
		}
	}
	
	public void updatePlayer(Player p) {
		if (p != null) {
			setPlayerData(p.getUniqueId(), KeyWordEnum.name.getValue(), p.getName());
			saveUsers();
		}
	}
	
	public void updateHorse(UUID playerUUID, String userID, Horse horse) {
		if (playerUUID != null && userID != null && horse != null) {
			setHorseData(playerUUID, userID, KeyWordEnum.uuid.getValue(), horse.getUniqueId().toString());
			saveLocation(playerUUID, horse, userID);
		}
	}
	
	public boolean unRegisterHorse(Horse horse) {
		if (horse != null) {
			UUID playerUUID = getPlayerUUID(horse);
			if (playerUUID != null) {
				String userID = getUserID(playerUUID, horse);
				if (userID != null) {
					return unRegisterHorse(playerUUID, userID);
				}
			}
		}
		return false;
	}
	
	public boolean unRegisterHorse(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			String userID = getUserID(playerUUID, horse);
			if (playerUUID != null) {
				if (userID != null) {
					return unRegisterHorse(playerUUID, userID);
				}
			}
		}
		return false;
	}
	
	public boolean unRegisterHorse(UUID playerUUID, String userID) {
		if (playerUUID != null && userID != null) {
			zh.getHM().unloadHorse(getHorseUUID(playerUUID, userID));
			int claimsAmount = getClaimsAmount(playerUUID);
			if (claimsAmount <= 1) {
				removeCachedEntries(playerUUID);
				setPlayerData(playerUUID, KeyWordEnum.horses.getValue(), null);
			}
			else {
				removeCachedEntry(getHorseUUID(playerUUID, userID));
				if (userID.equals(getFavoriteUserID(playerUUID))) { // update favorite horse ID if horse was freed
					saveFavorite(playerUUID, getDefaultFavoriteUserID());
				}
				else if (Integer.valueOf(userID) < Integer.valueOf(getFavoriteUserID(playerUUID))) { // update favorite horse ID if horse ID has changed
					saveFavorite(playerUUID, String.valueOf(Integer.valueOf(getFavoriteUserID(playerUUID))-1));
				}
				for (int i=Integer.valueOf(userID); i<claimsAmount; i++) { // i := horseID = {1,...,n}
					ConfigurationSection cs = users.getConfigurationSection(getHorsePath(playerUUID, Integer.toString(i+1)));
					setHorseData(playerUUID, Integer.toString(i), cs);
				}
				setHorseData(playerUUID, Integer.toString(claimsAmount), null);				
			}
			saveUsers();
			return true;
		}
		return false;
	}
	
	private void removeCachedEntry(UUID horseUUID) {
		cachedPlayerUUID.remove(horseUUID);
		cachedIsClaimedBy.remove(horseUUID);
	}
	
	private void removeCachedEntries(UUID playerUUID) {
		removeCachedEntries(cachedPlayerUUID, playerUUID);
		removeCachedEntries(cachedIsClaimedBy, playerUUID);
	}

	private void removeCachedEntries(Map<UUID, UUID> cachedEntries, UUID playerUUID) {
		Iterator<Map.Entry<UUID, UUID>> cachedEntriesItr = cachedEntries.entrySet().iterator();
		while (cachedEntriesItr.hasNext()) {
		    Map.Entry<UUID, UUID> entry = cachedEntriesItr.next();
		    if (entry.getValue().equals(playerUUID)) {
		    	cachedEntriesItr.remove();
			}
		}
	}
	
}
