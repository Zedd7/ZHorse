package eu.reborn_minecraft.zhorse.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class HorseManager {
	
	private ZHorse zh;
	private Map<UUID, Horse> loadedHorses = new HashMap<UUID, Horse>();
	
	public HorseManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public Horse getHorse(UUID playerUUID, String userID) {
		Horse horse = null;
		if (playerUUID != null && userID != null) {
			UUID horseUUID = zh.getUM().getHorseUUID(playerUUID, userID);
			if (horseUUID != null) {
				horse = getLoadedHorse(horseUUID);
				if (horse == null) {
					Location location = zh.getUM().getLocation(playerUUID, userID);
					if (location != null) {
						horse = getHorseInChunk(location.getChunk(), horseUUID);
					}
				}
			}
		}
		return horse;
	}
	
	public Horse getHorseInChunk(Chunk chunk, UUID horseUUID) {
		boolean unloadChunk = false;
		if (!chunk.isLoaded()) {
			chunk.load();
			unloadChunk = true;
		}
		for (Entity entity : chunk.getEntities()) {
			if (entity.getUniqueId().equals(horseUUID)) {
				return (Horse) entity;
			}
		}
		if (unloadChunk) {
			chunk.unload(true);
		}
		return null;
	}
	
	public Horse getLoadedHorse(UUID horseUUID) {
		return loadedHorses.get(horseUUID);
	}
	
	public void loadHorse(Horse horse) {
		UUID horseUUID = horse.getUniqueId();
		if (!loadedHorses.containsKey(horseUUID)) {
			loadedHorses.put(horseUUID, horse);
		}
	}
	
	public void unloadHorse(Horse horse) {
		unloadHorse(horse.getUniqueId());
	}
	
	public void unloadHorse(UUID horseUUID) {
		if (loadedHorses.containsKey(horseUUID)) {
			loadedHorses.remove(horseUUID);
		}
	}
	
	public Horse teleport(Horse sourceHorse, Location destination) {
		Horse copyHorse = (Horse) destination.getWorld().spawnEntity(destination, EntityType.HORSE);
		if (copyHorse != null) {
			UUID playerUUID = zh.getUM().getPlayerUUID(sourceHorse);
			String userID = zh.getUM().getUserID(playerUUID, sourceHorse);
			zh.getUM().updateHorse(playerUUID, userID, copyHorse);
			copyAttributes(sourceHorse, copyHorse);
			loadHorse(copyHorse);
			unloadHorse(sourceHorse);
			sourceHorse.remove();
		}
		return copyHorse;
	}
	
	private void copyAttributes(Horse sourceHorse, Horse copyHorse) {		
		// Define maximum before value to keep it in valid range
		copyHorse.setMaxDomestication(sourceHorse.getMaxDomestication());
		copyHorse.setMaxHealth(sourceHorse.getMaxHealth());
		copyHorse.setMaximumAir(sourceHorse.getMaximumAir());
		copyHorse.setMaximumNoDamageTicks(sourceHorse.getMaximumNoDamageTicks());
		
		copyHorse.addPotionEffects(sourceHorse.getActivePotionEffects());
		copyHorse.getInventory().setContents(sourceHorse.getInventory().getContents());
		copyHorse.setAge(sourceHorse.getAge());
		copyHorse.setAgeLock(sourceHorse.getAgeLock());
		copyHorse.setBreed(sourceHorse.canBreed());
		copyHorse.setCanPickupItems(sourceHorse.getCanPickupItems());
		copyHorse.setCarryingChest(sourceHorse.isCarryingChest());
		copyHorse.setColor(sourceHorse.getColor());
		copyHorse.setCustomName(sourceHorse.getCustomName());
		copyHorse.setCustomNameVisible(sourceHorse.isCustomNameVisible());
		copyHorse.setDomestication(sourceHorse.getDomestication());
		copyHorse.setFallDistance(sourceHorse.getFallDistance());
		copyHorse.setFireTicks(sourceHorse.getFireTicks());
		copyHorse.setGlowing(sourceHorse.isGlowing());
		copyHorse.setHealth(sourceHorse.getHealth());
		copyHorse.setJumpStrength(sourceHorse.getJumpStrength());
		copyHorse.setLastDamage(sourceHorse.getLastDamage());
		copyHorse.setLastDamageCause(sourceHorse.getLastDamageCause());
		copyHorse.setNoDamageTicks(sourceHorse.getNoDamageTicks());
		copyHorse.setOwner(sourceHorse.getOwner());
		copyHorse.setRemainingAir(sourceHorse.getRemainingAir());
		copyHorse.setRemoveWhenFarAway(sourceHorse.getRemoveWhenFarAway());
		copyHorse.setStyle(sourceHorse.getStyle());
		copyHorse.setTamed(sourceHorse.isTamed());
		copyHorse.setTicksLived(sourceHorse.getTicksLived());
		copyHorse.setVariant(sourceHorse.getVariant());
		
//		copyHorse.setMetadata(metadataKey);
	}

}
