package eu.reborn_minecraft.zhorse.utils;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;

import eu.reborn_minecraft.zhorse.ZHorse;

public class DelayedChunckLoad {
	
	public DelayedChunckLoad(ZHorse zh, Chunk chunk) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(zh, new Runnable() {	
			
			@Override
			public void run() {
				for (Entity entity : chunk.getEntities()) {
					if (entity instanceof AbstractHorse) {
						AbstractHorse horse = (AbstractHorse) entity;
						if (zh.getDM().isHorseRegistered(horse.getUniqueId())) {
							
							
							zh.getHM().trackHorse(horse);
							zh.getDM().updateHorseLocation(horse.getUniqueId(), horse.getLocation(), true);
							
						}
					}
				}
			}
			
		});
	}

}