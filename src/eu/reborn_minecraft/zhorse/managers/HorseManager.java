package eu.reborn_minecraft.zhorse.managers;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class HorseManager {
	
	@SuppressWarnings("unused")
	private ZHorse zh;
	
	public HorseManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public Horse teleport(Horse horse, Location destination) {
		Horse newHorse = (Horse) destination.getWorld().spawnEntity(destination, EntityType.HORSE);
		copyAttributes(horse, newHorse);
		horse.remove();
		return newHorse;
	}
	
	private void copyAttributes(Horse original, Horse copy) {
		copy.setAge(original.getAge());
	}

}
