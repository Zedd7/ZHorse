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
					if (hasMoved) {
						zh.getDM().updateHorseLocation(horseUUID, horse.getLocation(), false);
					}
				}
				else {
					HorseInventoryRecord inventoryRecord = zh.getDM().getHorseInventoryRecord(horseUUID);
					HorseStatsRecord statsRecord = zh.getDM().getHorseStatsRecord(horseUUID);
					if (inventoryRecord != null && statsRecord != null) {
						horse = spawnHorse(location, inventoryRecord, statsRecord);
						zh.getDM().updateHorseUUID(horseUUID, horse.getUniqueId());
						zh.getDM().updateHorseInventoryUUID(horseUUID, horse.getUniqueId());
						zh.getDM().updateHorseStatsUUID(horseUUID, horse.getUniqueId());
					}
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
				HorseStatsRecord statsRecord = new HorseStatsRecord(sourceHorse);
				assignStats(copyHorse, statsRecord);
				copyInventory(sourceHorse, copyHorse, statsRecord.isCarryingChest());
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
			horse.getInventory().setItem(itemRecord.getSlot(), itemRecord.getItem());
		}
	}
	
	private void copyInventory(AbstractHorse sourceHorse, AbstractHorse copyHorse, boolean isCarryingChest) {
		if (copyHorse instanceof ChestedHorse) {
			((ChestedHorse) copyHorse).setCarryingChest(isCarryingChest);
		}
		copyHorse.getInventory().setContents(sourceHorse.getInventory().getContents());
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
		boolean chunkWasLoaded = loadChunk(horse.getLocation());
		horse.remove(); // Entity::remove fails when the chunk is not loaded
		if (!chunkWasLoaded) {
			unloadChunk(horse.getLocation());
		}
	}
	
	public AbstractHorse spawnHorse(Location location, HorseInventoryRecord inventoryRecord, HorseStatsRecord statsRecord) {
		EntityType type = EntityType.valueOf(statsRecord.getType());
		boolean chunkWasLoaded = loadChunk(location);
		AbstractHorse horse = (AbstractHorse) location.getWorld().spawnEntity(location, type);
		if (horse != null) {
			assignStats(horse, statsRecord);
			assignInventory(horse, inventoryRecord, statsRecord.isCarryingChest());
			if (chunkWasLoaded) {
				trackHorse(horse);
			}
			else {
				unloadChunk(location);
			}
		}
		return horse;
	}
	
	private boolean loadChunk(Location location) {
		boolean chunkWasLoaded = true;
		int chunkXCoordinate = toChunkCoordinate(location.getBlockX());
		int chunkZCoordinate = toChunkCoordinate(location.getBlockZ());
		if (!location.getWorld().isChunkLoaded(chunkXCoordinate, chunkZCoordinate)) {
			location.getWorld().loadChunk(chunkXCoordinate, chunkZCoordinate);
			chunkWasLoaded = false;
		}
		return chunkWasLoaded;
	}
	
	private void unloadChunk(Location location) {
		int chunkXCoordinate = toChunkCoordinate(location.getBlockX());
		int chunkZCoordinate = toChunkCoordinate(location.getBlockZ());
		location.getWorld().unloadChunk(chunkXCoordinate, chunkZCoordinate);
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
