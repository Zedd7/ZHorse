package eu.reborn_minecraft.zhorse.utils;

import org.bukkit.Chunk;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ChunkLoad {
	
	public ChunkLoad(ZHorse zh, Chunk chunk) {
		for (Entity entity : chunk.getEntities()) {
			if (entity instanceof AbstractHorse) {
				AbstractHorse horse = (AbstractHorse) entity;
				if (zh.getDM().isHorseRegistered(horse.getUniqueId())) {
					 zh.getDM().updateHorseLocation(horse.getUniqueId(), horse.getLocation(), true);
					 zh.getHM().trackHorse(horse);
				}
			}
		}
	}

}