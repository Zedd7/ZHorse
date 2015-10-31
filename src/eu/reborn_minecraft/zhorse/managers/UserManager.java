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
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;

public class UserManager {
	private ZHorse zh;
	
	public UserManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public int getClaimsAmount(UUID playerUUID) {
		if (isRegistered(playerUUID)) {
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath(playerUUID, KeyWordEnum.horses.getValue()));
			if (cs != null) {
				return cs.getKeys(false).size();
			}
		}
		return 0;
	}
	
	private String getDefaultFavoriteUserID() {
		return "1";
	}
	
	public Horse getFavoriteHorse(UUID playerUUID) {
		if (playerUUID != null) {
			return getHorse(playerUUID, getFavoriteUserID(playerUUID));
		}
		return null;
	}
	
	public String getFavoriteUserID(UUID playerUUID) {
		if (playerUUID != null) {
			return getPlayerData(playerUUID, KeyWordEnum.favorite.getValue(), getDefaultFavoriteUserID());
		}
		return getDefaultFavoriteUserID();
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
		if (isRegistered(playerUUID)) {
			List<String> horseList = new ArrayList<String>();
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath(playerUUID, KeyWordEnum.horses.getValue()));
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
			return zh.getUsers().getString(getHorsePath(playerUUID, userID, path), defaultValue);
		}
		return defaultValue;
	}
	
	private boolean getHorseData(UUID playerUUID, String userID, String path, boolean defaultValue) {
		if (playerUUID != null && userID != null && path != null) {
			return zh.getUsers().getBoolean(getHorsePath(playerUUID, userID, path), defaultValue);
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
			World world = zh.getServer().getWorld(worldName);
			if (world != null) {
				double x = Double.parseDouble(getHorseData(playerUUID, userID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.x.getValue(), null));
				double y = Double.parseDouble(getHorseData(playerUUID, userID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.y.getValue(), null));
				double z = Double.parseDouble(getHorseData(playerUUID, userID, KeyWordEnum.location.getValue() + KeyWordEnum.dot.getValue() + KeyWordEnum.z.getValue(), null));
				return new Location(world, x, y, z);
			}
		}
		return null;
	}
	
	private List<Chunk> getNeighboringChunks(Location loc) {
		if (loc != null) {
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
		return null;
	}
	
	public String getNextUserID(UUID playerUUID) {
        int userID = 1;
		if (isRegistered(playerUUID)) {
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath(playerUUID, KeyWordEnum.horses.getValue()));
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
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath());
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
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath());
	        if (cs != null) {
	        	for (String player : cs.getKeys(false)) {
	 	    	   UUID playerUUID = UUID.fromString(player);
	 	    	   ConfigurationSection subCS = cs.getConfigurationSection(playerUUID + KeyWordEnum.dot.getValue() + KeyWordEnum.horses.getValue());
	 	    	   if (subCS != null) {
	 	    		   for (String userID : subCS.getKeys(false)) {
	 	    			   UUID horseUUID = getHorseUUID(playerUUID, userID);
	 	    			   if (horseUUID != null && horseUUID.equals(horse.getUniqueId())) {
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
		if (playerName != null) {
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath());
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
			return zh.getUsers().getString(getPlayerPath(playerUUID, path), defaultValue);
		}
		return defaultValue;
	}
	
	@SuppressWarnings("unused")
	private boolean getPlayerData(UUID playerUUID, String path, boolean defaultValue) {
		if (playerUUID != null && path != null) {
			return zh.getUsers().getBoolean(getPlayerPath(playerUUID, path), defaultValue);
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
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath(playerUUID, KeyWordEnum.horses.getValue()));
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
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath(playerUUID, KeyWordEnum.horses.getValue()));
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
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath(playerUUID, KeyWordEnum.horses.getValue()));
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
			ConfigurationSection cs = zh.getUsers().getConfigurationSection(getPlayerPath());
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
		return playerUUID != null && zh.getUsers().contains(getPlayerPath(playerUUID));
	}
	
	public boolean isRegistered(Horse horse) {
		return horse != null && getPlayerUUID(horse) != null;
	}
	
	public boolean isRegistered(UUID playerUUID, String userID) {
		return playerUUID != null && userID != null && zh.getUsers().contains(getHorsePath(playerUUID, userID));
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
		return playerUUID != null && userID != null && getHorse(playerUUID, userID) != null;
	}
	
	public void lock(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			String userId = getUserID(playerUUID, horse);
	        setHorseData(playerUUID, userId, KeyWordEnum.modeLocked.getValue(), true);
			zh.saveUsers();
		}
	}
	
	public void unLock(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			String userId = getUserID(playerUUID, horse);
			setHorseData(playerUUID, userId, KeyWordEnum.modeLocked.getValue(), false);
			zh.saveUsers();
		}
	}
	
	public void protect(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			String userID = getUserID(playerUUID, horse);
			setHorseData(playerUUID, userID, KeyWordEnum.modeProtected.getValue(), true);
			zh.saveUsers();
		}
	}
	
	public void unProtect(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			String userID = getUserID(playerUUID, horse);
			setHorseData(playerUUID, userID, KeyWordEnum.modeProtected.getValue(), false);
			zh.saveUsers();
		}
	}
	
	public void registerHorse(UUID playerUUID, Horse horse, String horseName, boolean lock, boolean protect, boolean share) {
		if (playerUUID != null && horse != null && horseName != null) {
			if (!isRegistered(playerUUID)) {
				registerPlayer(playerUUID);
			}
			if (isRegistered(horse)) { // retire l'enregistrement précédent en cas de give
				remove(horse);
			}
			String userID = getNextUserID(playerUUID);
			UUID horseUUID = horse.getUniqueId();
			setHorseData(playerUUID, userID, KeyWordEnum.name.getValue(), horseName);
			setHorseData(playerUUID, userID, KeyWordEnum.modeLocked.getValue(), lock);
			setHorseData(playerUUID, userID, KeyWordEnum.modeProtected.getValue(), protect);
			setHorseData(playerUUID, userID, KeyWordEnum.modeShared.getValue(), share);
			setHorseData(playerUUID, userID, KeyWordEnum.uuid.getValue(), horseUUID.toString());
			saveLocation(playerUUID, horse, userID);
			zh.saveUsers();
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
		if (playerUUID != null && horse != null) {
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
		if (playerUUID != null && userID != null) {
			int claimsAmount = getClaimsAmount(playerUUID);
			if (claimsAmount <= 1) {
				setPlayerData(playerUUID, KeyWordEnum.horses.getValue(), null);
				zh.saveUsers();
			}
			else {
				if (userID.equals(getFavoriteUserID(playerUUID))) {
					saveFavorite(playerUUID, getDefaultFavoriteUserID());
				}
				for (int i=Integer.valueOf(userID); i<claimsAmount; i++) { // i == ID != iterator
					ConfigurationSection cs = zh.getUsers().getConfigurationSection(getHorsePath(playerUUID, Integer.toString(i+1)));
					setHorseData(playerUUID, Integer.toString(i), cs);
				}
				setHorseData(playerUUID, Integer.toString(claimsAmount), null);
				zh.saveUsers();
			}
			return true;
		}
		return false;
	}
	
	public void rename(UUID playerUUID, Horse horse, String horseName) {
		if (playerUUID != null && horse != null && horseName != null) {
			String userID = getUserID(playerUUID, horse);
			setHorseData(playerUUID, userID, KeyWordEnum.name.getValue(), horseName);
			zh.saveUsers();
		}
	}
	
	public void saveFavorite(UUID playerUUID, String userID) {
		if (playerUUID != null && userID != null) {
			setPlayerData(playerUUID, KeyWordEnum.favorite.getValue(), userID);
			zh.saveUsers();
		}
	}
	
	public void saveLanguage(UUID playerUUID, String language) {
		if (playerUUID != null && language != null) {
			setPlayerData(playerUUID, KeyWordEnum.language.getValue(), language);
			zh.saveUsers();
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
			zh.saveUsers();
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
			zh.getUsers().set(getPlayerPath(playerUUID, path), value);
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
			zh.getUsers().set(path, value);
		}
	}
	
	private void setConfigurationSectionData(String path, ConfigurationSection value) {
		if (path != null) {
			zh.getUsers().set(path, value);
		}
	}
	
	private void setStringData(String path, String value) {
		if (path != null) {
			zh.getUsers().set(path, value);
		}
	}
	
	public void share(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			String userID = getUserID(playerUUID, horse);
			setHorseData(playerUUID, userID, KeyWordEnum.modeShared.getValue(), true);
			zh.saveUsers();
		}
	}
	
	public void unShare(UUID playerUUID, Horse horse) {
		if (playerUUID != null && horse != null) {
			String userID = getUserID(playerUUID, horse);
			setHorseData(playerUUID, userID, KeyWordEnum.modeShared.getValue(), false);
			zh.saveUsers();
		}
	}
	
	public void updatePlayer(Player p) {
		if (p != null) {
			setPlayerData(p.getUniqueId(), KeyWordEnum.name.getValue(), p.getName());
			zh.saveUsers();
		}
	}
	
}
