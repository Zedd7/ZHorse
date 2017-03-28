package eu.reborn_minecraft.zhorse.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ChunkLoad {
	
	public ChunkLoad(ZHorse zh, Chunk chunk) {
		for (Entity entity : chunk.getEntities()) {
			if (entity instanceof AbstractHorse) {
				AbstractHorse horse = (AbstractHorse) entity;
				if (zh.getDM().isHorseRegistered(horse.getUniqueId())) {
					 zh.getHM().trackHorse(horse);					 
					 UUID horseUUID = horse.getUniqueId();
					 Location horseLocation = horse.getLocation();
					 Bukkit.getScheduler().runTaskAsynchronously(zh, new Runnable() {

						@Override
						public void run() {
							zh.getDM().updateHorseLocation(horseUUID, horseLocation, true);
						}
						 
					 });
				}
			}
		}
	}

}