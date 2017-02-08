package eu.reborn_minecraft.zhorse.utils;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.database.HorseInventoryRecord;
import eu.reborn_minecraft.zhorse.database.HorseStatsRecord;

public class DelayedChunckUnload {
	
	public DelayedChunckUnload(ZHorse zh, Chunk chunk) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(zh, new Runnable() {
			
			@Override
			public void run() {
				for (Entity entity : chunk.getEntities()) {
					if (entity instanceof AbstractHorse) {
						AbstractHorse horse = (AbstractHorse) entity;
						if (zh.getDM().isHorseRegistered(horse.getUniqueId())) { // TODO use isHorseTracked when new spawn method is used
							zh.getHM().untrackHorse(horse);
							zh.getDM().updateHorseLocation(horse.getUniqueId(), horse.getLocation(), true);
							zh.getDM().registerHorseStats(new HorseStatsRecord(horse));
							zh.getDM().registerHorseInventory(new HorseInventoryRecord(horse.getUniqueId(), horse.getInventory()));
							// TODO kill horse
						}
					}
				}
			}
			
		});
	}

}