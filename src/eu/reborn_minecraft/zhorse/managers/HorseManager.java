package eu.reborn_minecraft.zhorse.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Llama;
import org.bukkit.inventory.ItemStack;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.database.HorseInventoryRecord;
import eu.reborn_minecraft.zhorse.database.HorseStatsRecord;
import eu.reborn_minecraft.zhorse.database.InventoryItemRecord;
import eu.reborn_minecraft.zhorse.utils.DelayedChunckLoad;

public class HorseManager {
	
	private ZHorse zh;
	private Map<UUID, AbstractHorse> trackedHorses = new HashMap<>();
	
	public HorseManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public AbstractHorse getFavoriteHorse(UUID playerUUID) {
		return getHorse(playerUUID, zh.getDM().getPlayerFavoriteHorseID(playerUUID));
	}
	
	public AbstractHorse getHorse(UUID playerUUID, Integer horseID) {
		AbstractHorse horse = null;
		if (playerUUID != null && horseID != null) {
			UUID horseUUID = zh.getDM().getHorseUUID(playerUUID, horseID);
			if (isHorseTracked(horseUUID)) {
				horse = getTrackedHorse(horseUUID);
			}
			else {
				Location location = zh.getDM().getHorseLocation(playerUUID, horseID);
				horse = getHorseFromLocation(horseUUID, location);
				if (horse != null) {
					boolean hasMoved = horse.getLocation().getBlockX() != location.getBlockX()
							|| horse.getLocation().getBlockY() != location.getBlockY()
							|| horse.getLocation().getBlockZ() != location.getBlockZ();
					System.out.println(1);
					if (hasMoved) {
						System.out.println(2);
						zh.getDM().updateHorseLocation(horseUUID, horse.getLocation(), false);
					}
				}
				else {
					
				}
			}
		}
		return horse;
	}
	
	private AbstractHorse getHorseFromLocation(UUID horseUUID, Location location) {
		AbstractHorse horse = null;
		if (location != null) {
			horse = getHorseInChunk(horseUUID, location.getChunk());
			if (horse == null) {
				List<Chunk> neighboringChunks = getChunksInRegion(location, 2, false);
				horse = getHorseInRegion(horseUUID, neighboringChunks);
			}
		}
		return horse;
	}
	
	private List<Chunk> getChunksInRegion(Location center, int chunkRange, boolean includeCentralChunk) {
		World world = center.getWorld();
		Location NWCorner = new Location(world, center.getX() - 16 * chunkRange, 0, center.getZ() - 16 * chunkRange);
		Location SECorner = new Location(world, center.getX() + 16 * chunkRange, 0, center.getZ() + 16 * chunkRange);
		List<Chunk> chunkList = new ArrayList<Chunk>();
		for (int x = NWCorner.getBlockX(); x <= SECorner.getBlockX(); x += 16) {
			for (int z = NWCorner.getBlockZ(); z <= SECorner.getBlockZ(); z += 16) {
				if (center.getBlockX() != x || center.getBlockZ() != z || includeCentralChunk) {
					Location chunkLocation = new Location(world, x, 0, z);
					Chunk chunk = world.getChunkAt(chunkLocation); // w.getChunkAt(x, z) uses chunk coordinates (loc % 16)
					chunkList.add(chunk);
				}
			}
		}
		return chunkList;
	}

	private AbstractHorse getHorseInChunk(UUID horseUUID, Chunk chunk) {
		for (Entity entity : chunk.getEntities()) {
			if (entity.getUniqueId().equals(horseUUID)) {
				return (AbstractHorse) entity;
			}
		}
		return null;
	}
	
	private AbstractHorse getHorseInRegion(UUID horseUUID, List<Chunk> region) {
		AbstractHorse horse = null;
		for (Chunk chunk : region) {
			horse = getHorseInChunk(horseUUID, chunk);
			if (horse != null) {
				return horse;
			}
		}
		return horse;
	}

	public AbstractHorse getTrackedHorse(UUID horseUUID) {
		return trackedHorses.get(horseUUID);
	}
	
	public Map<UUID, AbstractHorse> getTrackedHorses() {
		return trackedHorses;
	}
	
	public boolean isHorseTracked(UUID horseUUID) {
		return trackedHorses.containsKey(horseUUID);
	}
	
	public void trackHorse(AbstractHorse horse) {
		UUID horseUUID = horse.getUniqueId();
		if (!isHorseTracked(horseUUID)) {
			trackedHorses.put(horseUUID, horse);
		}
	}
	
	public void trackHorses() {
		for (World world : zh.getServer().getWorlds()) {
			for (Chunk chunk : world.getLoadedChunks()) {
				new DelayedChunckLoad(zh, chunk);
			}
		}
	}
	
	public void untrackHorse(UUID horseUUID) {
		if (isHorseTracked(horseUUID)) {
			trackedHorses.remove(horseUUID);
		}
	}
	
	public void untrackHorses() {
		Iterator<Entry<UUID, AbstractHorse>> trackedHorsesItr = trackedHorses.entrySet().iterator();
		while (trackedHorsesItr.hasNext()) {
			AbstractHorse horse = trackedHorsesItr.next().getValue();
			zh.getDM().updateHorseLocation(horse.getUniqueId(), horse.getLocation(), true);
			trackedHorsesItr.remove();
		}
	}
	
	public AbstractHorse teleport(AbstractHorse sourceHorse, Location destination) {
		if (zh.getCM().shouldUseOldTeleportMethod()) {
			sourceHorse.teleport(destination);
			zh.getDM().updateHorseLocation(sourceHorse.getUniqueId(), destination, false);
			return sourceHorse;
		}
		else {
			AbstractHorse copyHorse = (AbstractHorse) destination.getWorld().spawnEntity(destination, sourceHorse.getType());
			if (copyHorse != null) {
				UUID oldHorseUUID = sourceHorse.getUniqueId();
				UUID newHorseUUID = copyHorse.getUniqueId();
				String horseName = zh.getDM().getHorseName(oldHorseUUID); // call before updating horse uuid
				String ownerName = zh.getDM().getOwnerName(oldHorseUUID);
				zh.getDM().updateHorseUUID(oldHorseUUID, newHorseUUID);
				zh.getDM().updateHorseLocation(newHorseUUID, destination, true);
				zh.getDM().updateHorseStatsUUID(oldHorseUUID, newHorseUUID);
				zh.getDM().updateHorseInventoryUUID(oldHorseUUID, newHorseUUID);
				HorseInventoryRecord inventoryRecord = new HorseInventoryRecord(sourceHorse);
				HorseStatsRecord statsRecord = new HorseStatsRecord(sourceHorse);
				assignStats(copyHorse, statsRecord);
				assignInventory(copyHorse, inventoryRecord, statsRecord.isCarryingChest());
				removeLeash(sourceHorse);
				untrackHorse(sourceHorse.getUniqueId());
				trackHorse(copyHorse);
				removeHorse(sourceHorse, horseName, ownerName);
			}
			return copyHorse;
		}
	}

	private void assignStats(AbstractHorse horse, HorseStatsRecord statsRecord) {
		horse.setAge(statsRecord.getAge());
		horse.setBreed(statsRecord.canBreed());
		horse.setCanPickupItems(statsRecord.canPickupItems());
		horse.setCustomName(statsRecord.getCustomName());
		horse.setCustomNameVisible(statsRecord.isCustomNameVisible());
		horse.setDomestication(statsRecord.getDomestication());
		horse.setFireTicks(statsRecord.getFireTicks());
		horse.setGlowing(statsRecord.isGlowing());
		horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(statsRecord.getMaxHealth());
		horse.setHealth(statsRecord.getHealth()); // Define max health before current health
		horse.setJumpStrength(statsRecord.getJumpStrength());
		horse.setNoDamageTicks(statsRecord.getNoDamageTicks());
		horse.setRemainingAir(statsRecord.getRemainingAir());
		horse.setTamed(statsRecord.isTamed());
		horse.setTicksLived(statsRecord.getTicksLived());
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(statsRecord.getSpeed());
		
		switch (statsRecord.getType()) {
		case "HORSE":
			((Horse) horse).setColor(Horse.Color.valueOf(statsRecord.getColor()));
			((Horse) horse).setStyle(Horse.Style.valueOf(statsRecord.getStyle()));
			break;
		case "LLAMA":
			((Llama) horse).setColor(Llama.Color.valueOf(statsRecord.getColor()));
			((Llama) horse).setStrength(statsRecord.getStrength());
		default:
			break;
		}
	}
	
	private void assignInventory(AbstractHorse horse, HorseInventoryRecord inventoryRecord, boolean isCarryingChest) {
		if (horse instanceof ChestedHorse) {
			((ChestedHorse) horse).setCarryingChest(isCarryingChest);
		}
		for (InventoryItemRecord itemRecord : inventoryRecord.getItemRecordList()) {
			horse.getInventory().setItem(itemRecord.getPosition(), itemRecord.getItem());
		}
	}
	
	private void removeLeash(AbstractHorse horse) {
		if (horse.isLeashed()) {
			Entity leashHolder = horse.getLeashHolder();
			if (leashHolder instanceof LeashHitch) {
				leashHolder.remove();
			}
			horse.setLeashHolder(null);
			ItemStack leash = new ItemStack(Material.LEASH);
			horse.getWorld().dropItem(horse.getLocation(), leash);
		}
	}
	
	public void removeHorse(AbstractHorse horse, String horseName, String ownerName) {		
		/*UUID horseUUID = horse.getUniqueId();
		int waitTime = 60; // ticks
		horse.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, waitTime, 0));
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
		horse.setAI(false);*/
		
		boolean unloadChunk = false;
		Location horseLocation = horse.getLocation();
		int chunkXCoordinate = toChunkCoordinate(horseLocation.getBlockX());
		int chunkZCoordinate = toChunkCoordinate(horseLocation.getBlockZ());
		if (!horseLocation.getWorld().isChunkLoaded(chunkXCoordinate, chunkZCoordinate)) { // *.getChunk() loads the chunk
			horseLocation.getWorld().loadChunk(chunkXCoordinate, chunkZCoordinate); // Entity::remove fails when the chunk is unloaded
			unloadChunk = true;
		}
		horse.remove();
		if (unloadChunk) {
			horseLocation.getWorld().unloadChunk(chunkXCoordinate, chunkZCoordinate);
		}
		
		/*Bukkit.getScheduler().scheduleSyncDelayedTask(zh, new Runnable() {
			
			@Override
			public void run() {
				List<Chunk> neighboringChunks = getChunksInRegion(horseLocation, 1, true);
				AbstractHorse duplicatedHorse = getHorseInRegion(horseUUID, neighboringChunks);
				if (duplicatedHorse != null) {
					Location location = duplicatedHorse.getLocation();
					int x = location.getBlockX();
					int y = location.getBlockY();
					int z = location.getBlockZ();
					String warning = String.format("A horse named %s and owned by %s was duplicated at location %d:%d:%d in world %s, killing it.",
						horseName, ownerName, x, y, z, horseLocation.getWorld().getName());
					zh.getServer().broadcast(ChatColor.RED + warning, "zh.admin");
					zh.getLogger().severe(warning);
					duplicatedHorse.setHealth(0);
				}
			}
			
		}, waitTime);*/
	}
	
	public AbstractHorse spawnHorse(Location location, HorseInventoryRecord inventoryRecord, HorseStatsRecord statsRecord) {
		EntityType type = EntityType.valueOf(statsRecord.getType());
		AbstractHorse horse = (AbstractHorse) location.getWorld().spawnEntity(location, type);
		if (horse != null) {
			assignStats(horse, statsRecord);
			assignInventory(horse, inventoryRecord, statsRecord.isCarryingChest());
			trackHorse(horse);
		}
		return horse;
	}
	
	private int toChunkCoordinate(int coordinate) {
		if (coordinate >= 0) {
			return coordinate / 16;
		}
		else {
			return (coordinate / 16) - 1;
		}
	}

}
