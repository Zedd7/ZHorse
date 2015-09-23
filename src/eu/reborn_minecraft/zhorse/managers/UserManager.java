package eu.reborn_minecraft.zhorse.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class UserManager {
	private static String DOT = ".";
	private static String FAVORITE = "1";
	private static String HORSES = "Horses";
	private static String LANGUAGE = "Language";
	private static String LOCATION = "Location";
	private static String LOCKED = "Locked";
	private static String NAME = "Name";
	private static String PLAYERS = "Players";
	private static String PROTECTED = "Protected";
	private static String SHARED = "Shared";
	private static String UNIQUEID = "UUID";
	private static String WORLD = "World";
	private static String X = "X";
	private static String Y = "Y";
	private static String Z = "Z";
	private ZHorse zh;
	
	public UserManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public int getClaimsAmount(UUID playerUUID) {
		if (isRegistered(playerUUID)) {
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath(playerUUID, HORSES));
			if (cs != null) {
				return cs.getKeys(false).size();
			}
		}
		return 0;
	}
	
	public Horse getFavoriteHorse(UUID playerUUID) {
		return getHorse(playerUUID, FAVORITE);
	}
	
	public String getFavoriteUserID(UUID playerUUID) {
		// TODO
		return FAVORITE;
	}
	
	public Horse getHorse(UUID playerUUID, String userID) {
		if (playerUUID != null && userID != null) {
			UUID horseUUID = getHorseUUID(playerUUID, userID);
			if (horseUUID != null) {
				Location location = getLocation(playerUUID, userID);
				Chunk chunk = location.getChunk();
				Horse horse = getHorseInChunk(chunk, horseUUID);
				if (horse != null) {
					return horse;
				}
				else {
					List<Chunk> neighboringChunks = getNeighboringChunks(location);
					for (Chunk neighboringChunk : neighboringChunks) {
						horse = getHorseInChunk(neighboringChunk, horseUUID);
						if (horse != null) {
							return horse;
						}
					}
				}
				List<World> worlds = zh.getServer().getWorlds();
				for (World world : worlds) {
					List<LivingEntity> livingEntities = world.getLivingEntities();
					for (LivingEntity livingEntity : livingEntities) {
						if (livingEntity.getUniqueId().equals(horseUUID)) {
							return (Horse)livingEntity;
						}
					}
					List<Entity> entities = world.getEntities();
					for (Entity entity : entities) {
						if (entity.getUniqueId().equals(horseUUID)) {
							return (Horse)entity;
						}
					}
				}
			}
		}
		return null;
	}
	
	public Horse getHorseInChunk(Chunk chunk, UUID horseUUID) {
		boolean unloadChunk = false;
		if (!chunk.isLoaded()) {
			chunk.load();
			unloadChunk = true;
		}
		for (Entity entity : chunk.getEntities()) {
			if (entity.getUniqueId().equals(horseUUID)) {
				return (Horse)entity;
			}
		}
		if (unloadChunk) {
			chunk.unload(true, true);
		}
		return null;
	}
	
	public List<String> getHorseList(UUID playerUUID) {
		List<String> horseList = new ArrayList<String>();
		if (isRegistered(playerUUID)) {
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath(playerUUID, HORSES));
			if (cs != null) {
				for (String userID : cs.getKeys(false)) {
					horseList.add(getHorseName(playerUUID, userID));
				}
			}
		}
		return horseList;
	}
	
	public String getHorseName(Horse horse) {
		UUID playerUUID = getPlayerUUID(horse);
		return getHorseName(playerUUID, horse);
	}
	
	public String getHorseName(UUID playerUUID, Horse horse) {
		return getHorseName(playerUUID, getUserID(playerUUID, horse));
	}

	public String getHorseName(UUID playerUUID, String userID) {
		return getHorseData(playerUUID, userID, NAME, null);
	}
	
	private String getHorsePath(UUID playerUUID, String userID) {
		String fullPath = PLAYERS + DOT + playerUUID.toString() + DOT + HORSES + DOT + userID;
		return fullPath;
	}
	
	private String getHorsePath(UUID playerUUID, String userID, String path) {
		return getHorsePath(playerUUID, userID) + DOT + path;
	}
	
	private String getHorseData(UUID playerUUID, String userID, String path, String defaultValue) {
		return zh.getUsers().getString(getHorsePath(playerUUID, userID, path), defaultValue);
	}
	
	private boolean getHorseData(UUID playerUUID, String userID, String path, boolean defaultValue) {
		return zh.getUsers().getBoolean(getHorsePath(playerUUID, userID, path), defaultValue);
	}
	
	public UUID getHorseUUID(UUID playerUUID, String userID) {
		String horseUUID = getHorseData(playerUUID, userID, UNIQUEID, null);
		if (horseUUID != null) {
			return UUID.fromString(horseUUID);
		}
		return null;
	}
	
	public String getLanguage(CommandSender s) {
		if (s instanceof Player) {
			return getLanguage(((Player)s).getUniqueId());
		}
		else {
			return zh.getCM().getDefaultLanguage();
		}
	}
	
	public String getLanguage(UUID playerUUID) {
		String language = getPlayerData(playerUUID, LANGUAGE, null);
		if (language == null) {
			language = zh.getCM().getDefaultLanguage();
		}
		return language;
	}
	
	public Location getLocation(UUID playerUUID, String userID) {
		if (playerUUID != null && userID != null) {
			String worldName = getHorseData(playerUUID, userID, LOCATION + DOT + WORLD, null);
			World world = zh.getServer().getWorld(worldName);
			if (world != null) {
				double x = Double.parseDouble(getHorseData(playerUUID, userID, LOCATION + DOT + X, null));
				double y = Double.parseDouble(getHorseData(playerUUID, userID, LOCATION + DOT + Y, null));
				double z = Double.parseDouble(getHorseData(playerUUID, userID, LOCATION + DOT + Z, null));
				return new Location(world, x, y, z);
			}
		}
		return null;
	}
	
	private List<Chunk> getNeighboringChunks(Location loc) {
		List<Chunk> neighboringChunks = new ArrayList<Chunk>();
		neighboringChunks.add(loc.getWorld().getChunkAt(loc.add(0, 0, -16)));
		neighboringChunks.add(loc.getWorld().getChunkAt(loc.add(-16, 0, 0)));
		neighboringChunks.add(loc.getWorld().getChunkAt(loc.add(0, 0, 16)));
		neighboringChunks.add(loc.getWorld().getChunkAt(loc.add(0, 0, 16)));
		neighboringChunks.add(loc.getWorld().getChunkAt(loc.add(16, 0, 0)));
		neighboringChunks.add(loc.getWorld().getChunkAt(loc.add(16, 0, 0)));
		neighboringChunks.add(loc.getWorld().getChunkAt(loc.add(0, 0, -16)));
		neighboringChunks.add(loc.getWorld().getChunkAt(loc.add(0, 0, -16)));
		return neighboringChunks;
	}
	
	public String getNextUserID(UUID playerUUID) {
        int userID = 1;
		if (isRegistered(playerUUID)) {
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath(playerUUID, HORSES));
			if (cs != null) {
				while(cs.getKeys(false).contains(Integer.toString(userID))) {
					userID++;
				}
			}
		}
		return Integer.toString(userID);
	}
	
	public String getPlayerName(String targetName) {
        ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath());
        if (cs != null) {
        	for (String player : cs.getKeys(false)) {
 	    	  UUID playerUUID = UUID.fromString(player);
 	    	  String playerName = getPlayerData(playerUUID, NAME, null);
 	    	  if (targetName.equalsIgnoreCase(playerName)) {
	    		   return playerName;
	    	  }
        	}
	    }
		return targetName;
	}
	
	public String getPlayerName(UUID playerUUID) {
		return getPlayerData(playerUUID, NAME, null);
	}
	
	public String getPlayerName(Horse horse) {
        UUID playerUUID = getPlayerUUID(horse);
		return getPlayerName(playerUUID);
	}
	
	public UUID getPlayerUUID(Horse horse) {
		if (horse != null) {
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath());
	        if (cs != null) {
	        	for (String player : cs.getKeys(false)) {
	 	    	   UUID playerUUID = UUID.fromString(player);
	 	    	   ConfigurationSection subCS = cs.getConfigurationSection(playerUUID + DOT + HORSES);
	 	    	   if (subCS != null) {
	 	    		   for (String userID : subCS.getKeys(false)) {
	 	    			   if (getHorseUUID(playerUUID, userID).equals(horse.getUniqueId())) {
	 	    				   return playerUUID;
	 	    			   }
	 	    		   }
	 	    	   }
	        	}
		    }
		}
		return null;
	}
	
	public UUID getPlayerUUID(String playerName) {
        ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath());
        if (cs != null) {
        	for (String playerUUID : cs.getKeys(false)) {
	    	   if (playerName.equalsIgnoreCase(getPlayerData(UUID.fromString(playerUUID), NAME, null))) {
	    		   return UUID.fromString(playerUUID);
	    	   }
	        }
        }
		return null;
	}
	
	private String getPlayerPath() {
		return PLAYERS;
	}
	
	private String getPlayerPath(UUID playerUUID) {
		if (playerUUID != null) {
			return PLAYERS + DOT + playerUUID.toString();
		}
		return null;
	}
	
	private String getPlayerPath(UUID playerUUID, String path) {
		return getPlayerPath(playerUUID) + DOT + path;
	}
	
	private String getPlayerData(UUID playerUUID, String path, String defaultValue) {
		return zh.getUsers().getString(getPlayerPath(playerUUID, path), defaultValue);
	}
	
	@SuppressWarnings("unused")
	private boolean getPlayerData(UUID playerUUID, String path, boolean defaultValue) {
		return zh.getUsers().getBoolean(getPlayerPath(playerUUID, path), defaultValue);
	}
	
	public String getUserID(Horse horse) {
		UUID playerUUID = getPlayerUUID(horse);
		return getUserID(playerUUID, horse);
	}
	
	public String getUserID(UUID playerUUID, Horse horse) {
		if (isRegistered(playerUUID)) {
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath(playerUUID, HORSES));
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
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath(playerUUID, HORSES));
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
	
	public boolean isClaimedBy(UUID playerUUID, Horse horse) {
		if (isRegistered(playerUUID)) {
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath(playerUUID, HORSES));
			if (cs != null) {
				for (String userID : cs.getKeys(false)) {
					if (getHorseUUID(playerUUID, userID).equals(horse.getUniqueId())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean isLocked(Horse horse) {
		UUID playerUUID = getPlayerUUID(horse);
        return isLocked(playerUUID, horse);
	}
	
	public boolean isLocked(UUID playerUUID, Horse horse) {
        String userID = getUserID(playerUUID, horse);
        return getHorseData(playerUUID, userID, LOCKED, false);
	}
	
	public boolean isLocked(UUID playerUUID, String userID) { 
        return getHorseData(playerUUID, userID, LOCKED, false);
	}
	
	public boolean isProtected(Horse horse) {
		UUID playerUUID = getPlayerUUID(horse);
        return isProtected(playerUUID, horse);
	}
	
	public boolean isProtected(UUID playerUUID, Horse horse) {
        String userID = getUserID(playerUUID, horse);
        return isProtected(playerUUID, userID);
	}
	
	public boolean isProtected(UUID playerUUID, String userID) {
		return getHorseData(playerUUID, userID, PROTECTED, false);
	}
	
	public boolean isRegistered(String playerName) {
		ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath());
		if (cs != null) {
			for (String playerUUID : cs.getKeys(false)) {
				if (playerName.equalsIgnoreCase(getPlayerName(UUID.fromString(playerUUID)))) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isRegistered(UUID playerUUID) {
		return playerUUID != null && zh.getUsers().contains(getPlayerPath(playerUUID));
	}
	
	public boolean isRegistered(Horse horse) {
		return horse != null && getPlayerUUID(horse) != null;
	}
	
	public boolean isRegistered(UUID playerUUID, String userID) {
		return playerUUID != null && userID != null && zh.getUsers().contains(getHorsePath(playerUUID, userID));
	}
	
	public boolean isShared(Horse horse) {
		UUID playerUUID = getPlayerUUID(horse);
		return isShared(playerUUID, horse);
	}
	
	public boolean isShared(UUID playerUUID, Horse horse) {
        String userID = getUserID(playerUUID, horse);
		return isShared(playerUUID, userID);
	}
	
	public boolean isShared(UUID playerUUID, String userID) {
		return getHorseData(playerUUID, userID, SHARED, false);
	}
	
	public boolean isSpawned(UUID playerUUID, String userID) {
		return getHorse(playerUUID, userID) != null;
	}
	
	public void lock(UUID playerUUID, Horse horse) {
        String userId = getUserID(playerUUID, horse);
        setHorseData(playerUUID, userId, LOCKED, true, true);
	}
	
	public void unLock(UUID playerUUID, Horse horse) {
        String userId = getUserID(playerUUID, horse);
        setHorseData(playerUUID, userId, LOCKED, false, true);
	}
	
	public void protect(UUID playerUUID, Horse horse) {
        String userID = getUserID(playerUUID, horse);
        setHorseData(playerUUID, userID, PROTECTED, true, true);
	}
	
	public void unProtect(UUID playerUUID, Horse horse) {
        String userID = getUserID(playerUUID, horse);
        setHorseData(playerUUID, userID, PROTECTED, false, true);
	}
	
	public void registerHorse(UUID playerUUID, Horse horse, String horseName, boolean lock, boolean protect, boolean share) {
		if (playerUUID != null && horse != null && horseName != null) {
			if (!isRegistered(playerUUID)) {
				registerPlayer(playerUUID);
			}
			if (isRegistered(horse)) {
				remove(horse);
			}
			String userID = getNextUserID(playerUUID);
			UUID horseUUID = horse.getUniqueId();
			setHorseData(playerUUID, userID, NAME, horseName, false);
			setHorseData(playerUUID, userID, LOCKED, lock, false);
			setHorseData(playerUUID, userID, PROTECTED, protect, false);
			setHorseData(playerUUID, userID, SHARED, share, false);
			setHorseData(playerUUID, userID, UNIQUEID, horseUUID.toString(), false);
			saveLocation(playerUUID, horse, userID);
		}
	}
	
	public void registerPlayer(UUID playerUUID) {
		if (playerUUID != null) {
			String playerName = zh.getServer().getOfflinePlayer(playerUUID).getName();
			String language = zh.getCM().getDefaultLanguage();
			if (playerName != null && language != null) {
				setPlayerData(playerUUID, NAME, playerName, false);
				setPlayerData(playerUUID, LANGUAGE, language, false);
				zh.saveUsers();
			}
		}
	}
	
	public boolean remove(Horse horse) {
		if (horse != null) {
			UUID playerUUID = getPlayerUUID(horse);
			if (playerUUID != null) {
				String userID = getUserID(playerUUID, horse);
				if (userID != null) {
					return remove(playerUUID, userID);
				}
			}
		}
		return false;
	}
	
	public boolean remove(UUID playerUUID, Horse horse) {
		if (horse != null) {
			String userID = getUserID(playerUUID, horse);
			if (playerUUID != null) {
				if (userID != null) {
					return remove(playerUUID, userID);
				}
			}
		}
		return false;
	}
	
	public boolean remove(UUID playerUUID, String userID) {
		int claimsAmount = getClaimsAmount(playerUUID);
		if (claimsAmount <= 1) {
			setPlayerData(playerUUID, HORSES, null, true);
		}
		else {
			for (int i=Integer.valueOf(userID); i<claimsAmount; i++) {
				ConfigurationSection cs = zh.getUsers().getConfigurationSection(getHorsePath(playerUUID, Integer.toString(i+1)));
				setHorseData(playerUUID, Integer.toString(i), cs, false);
			}
			setHorseData(playerUUID, Integer.toString(claimsAmount), null, true);
		}
		return true;
	}
	
	public void rename(UUID playerUUID, Horse horse, String horseName) {
        String userID = getUserID(playerUUID, horse);
        setHorseData(playerUUID, userID, NAME, horseName, true);
	}
	
	public void saveLanguage(UUID playerUUID, String language) {
		setPlayerData(playerUUID, LANGUAGE, language, true);
	}
	
	public void saveLocation(Horse horse) {
		UUID playerUUID = getPlayerUUID(horse);
		String userID = getUserID(playerUUID, horse);
		saveLocation(playerUUID, horse, userID);
	}
	
	public void saveLocation(UUID playerUUID, Horse horse, String userID) {
		String world = horse.getWorld().getName();
		Double xLoc = horse.getLocation().getX();
		Double yLoc = horse.getLocation().getY();
		Double zLoc = horse.getLocation().getZ();
		String xPos = Integer.toString(xLoc.intValue());
		String yPos = Integer.toString(yLoc.intValue());
		String zPos = Integer.toString(zLoc.intValue());
		setHorseData(playerUUID, userID, LOCATION + DOT + WORLD, world, false);
		setHorseData(playerUUID, userID, LOCATION + DOT + X, xPos, false);
		setHorseData(playerUUID, userID, LOCATION + DOT + Y, yPos, false);
		setHorseData(playerUUID, userID, LOCATION + DOT + Z, zPos, false);
		zh.saveUsers();
	}
	
	private void setHorseData(UUID playerUUID, String userID, String path, String value, boolean save) {
		setStringData(getHorsePath(playerUUID, userID, path), value, save);
	}
	
	private void setHorseData(UUID playerUUID, String userID, String path, boolean value, boolean save) {
		setBooleanData(getHorsePath(playerUUID, userID, path), value, save);
	}
	
	private void setHorseData(UUID playerUUID, String userID, ConfigurationSection value, boolean save) {
		setConfigurationSectionData(getHorsePath(playerUUID, userID), value, save);
	}
	
	private void setPlayerData(UUID playerUUID, String path, String value, boolean save) {
		zh.getUsers().set(getPlayerPath(playerUUID, path), value);
		zh.saveUsers();
	}
	
	@SuppressWarnings("unused")
	private void setPlayerData(UUID playerUUID, String path, boolean value, boolean save) {
		setBooleanData(getPlayerPath(playerUUID, path), value, save);
	}
	
	@SuppressWarnings("unused")
	private void setPlayerData(UUID playerUUID, ConfigurationSection value, boolean save) {
		setConfigurationSectionData(getPlayerPath(playerUUID), value, save);
	}
	
	private void setBooleanData(String path, boolean value, boolean save) {
		zh.getUsers().set(path, value);
		if (save) {
			zh.saveUsers();
		}
	}
	
	private void setConfigurationSectionData(String path, ConfigurationSection value, boolean save) {
		zh.getUsers().set(path, value);
		if (save) {
			zh.saveUsers();
		}
	}
	
	private void setStringData(String path, String value, boolean save) {
		zh.getUsers().set(path, value);
		if (save) {
			zh.saveUsers();
		}
	}
	
	public void share(UUID playerUUID, Horse horse) {
        String userID = getUserID(playerUUID, horse);
        setHorseData(playerUUID, userID, SHARED, true, true);
	}
	
	public void unShare(UUID playerUUID, Horse horse) {
        String userID = getUserID(playerUUID, horse);
        setHorseData(playerUUID, userID, SHARED, false, true);
	}
	
	public void updatePlayer(Player p) {
		setPlayerData(p.getUniqueId(), NAME, p.getName(), true);
	}
	
}
