package eu.reborn_minecraft.zhorse.utils;

import org.bukkit.Chunk;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ChunkUnload {
	
	public ChunkUnload(ZHorse zh, Chunk chunk) {
		for (Entity entity : chunk.getEntities()) {
			if (entity instanceof AbstractHorse) {
				AbstractHorse horse = (AbstractHorse) entity;
				if (zh.getHM().isHorseTracked(horse.getUniqueId())) {
					zh.getHM().untrackHorse(horse.getUniqueId());
					zh.getHM().updateHorse(horse, false);
				}
			}
		}
	}

}