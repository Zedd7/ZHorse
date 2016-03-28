package eu.reborn_minecraft.zhorse.managers;

import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LeashHitch;
import org.bukkit.inventory.ItemStack;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.utils.AsyncChunckLoad;

public class HorseManager {
	
	private ZHorse zh;
	
	public HorseManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public Horse getFavoriteHorse(UUID playerUUID) {
		return getHorse(playerUUID, zh.getUM().getFavoriteUserID(playerUUID));
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
						Entity[] entities = location.getChunk().getEntities();
						for (int i=0; i < entities.length && horse == null; ++i) {
							if (entities[i].getUniqueId().equals(horseUUID)) {
								horse = (Horse) entities[i];
							}
						}
					}
				}
			}
		}
		return horse;
	}
	
	public Horse getLoadedHorse(UUID horseUUID) {
		return zh.getLoadedHorses().get(horseUUID);
	}
	
	public void loadHorse(Horse horse) {
		UUID horseUUID = horse.getUniqueId();
		if (!zh.getLoadedHorses().containsKey(horseUUID)) {
			zh.getLoadedHorses().put(horseUUID, horse);
		}
	}
	
	public void loadHorses() {
		for (World world : zh.getServer().getWorlds()) {
			for (Chunk chunk : world.getLoadedChunks()) {
				AsyncChunckLoad.asyncChunkLoadScheduler(zh, chunk);
			}
		}
	}
	
	public void unloadHorse(Horse horse) {
		unloadHorse(horse.getUniqueId());
	}
	
	public void unloadHorse(UUID horseUUID) {
		if (zh.getLoadedHorses().containsKey(horseUUID)) {
			zh.getLoadedHorses().remove(horseUUID);
		}
	}
	
	public void unloadHorses() {
		for (UUID horseUUID : zh.getLoadedHorses().keySet()) {
			Horse horse = zh.getLoadedHorses().get(horseUUID);
			zh.getUM().saveLocation(horse);
			zh.getLoadedHorses().remove(horseUUID);
		}
	}
	
	public Horse teleport(Horse sourceHorse, Location destination) {
		Horse copyHorse = (Horse) destination.getWorld().spawnEntity(destination, EntityType.HORSE);
		if (copyHorse != null) {
			UUID playerUUID = zh.getUM().getPlayerUUID(sourceHorse);
			String userID = zh.getUM().getUserID(playerUUID, sourceHorse);
			copyAttributes(sourceHorse, copyHorse);
			copyInventory(sourceHorse, copyHorse);
			removeLeash(sourceHorse);
			unloadHorse(sourceHorse);
			loadHorse(copyHorse);
			sourceHorse.remove();
			zh.getUM().updateHorse(playerUUID, userID, copyHorse);
		}
		return copyHorse;
	}

	private void removeLeash(Horse horse) {
		if (horse.isLeashed()) {
			Entity leashHolder = horse.getLeashHolder();
			if (leashHolder instanceof LeashHitch) {
				leashHolder.remove();
			}
			ItemStack leash = new ItemStack(Material.LEASH);
			horse.getWorld().dropItem(horse.getLocation(), leash);
		}
	}

	private void copyAttributes(Horse sourceHorse, Horse copyHorse) {	
		// Define maximumof value before actual value to keep it in valid range
		copyHorse.setMaxDomestication(sourceHorse.getMaxDomestication());
		copyHorse.setMaxHealth(sourceHorse.getMaxHealth());
		copyHorse.setMaximumAir(sourceHorse.getMaximumAir());
		copyHorse.setMaximumNoDamageTicks(sourceHorse.getMaximumNoDamageTicks());
		
		copyHorse.addPotionEffects(sourceHorse.getActivePotionEffects());
		copyHorse.setAge(sourceHorse.getAge());
		copyHorse.setAgeLock(sourceHorse.getAgeLock());
		copyHorse.setBreed(sourceHorse.canBreed());
		copyHorse.setCanPickupItems(sourceHorse.getCanPickupItems());
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
	}
	
	private void copyInventory(Horse sourceHorse, Horse copyHorse) {
		copyHorse.setCarryingChest(sourceHorse.isCarryingChest());
		copyHorse.getInventory().setContents(sourceHorse.getInventory().getContents());
	}

}
