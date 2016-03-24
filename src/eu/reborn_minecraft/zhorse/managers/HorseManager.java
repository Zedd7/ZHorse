package eu.reborn_minecraft.zhorse.managers;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class HorseManager {
	
	private ZHorse zh;
	
	public HorseManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public Horse teleport(Horse sourceHorse, Location destination) {
		Horse copyHorse = (Horse) destination.getWorld().spawnEntity(destination, EntityType.HORSE);
		UUID playerUUID = zh.getUM().getPlayerUUID(sourceHorse);
		String userID = zh.getUM().getUserID(playerUUID, sourceHorse);
		zh.getUM().updateHorse(playerUUID, userID, copyHorse);
		copyAttributes(sourceHorse, copyHorse);
		sourceHorse.remove();
		return copyHorse;
	}
	
	private void copyAttributes(Horse sourceHorse, Horse copyHorse) {		
		// Define maximums before actual values
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
