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
						if (zh.getHM().isHorseTracked(horse.getUniqueId())) {
							zh.getHM().untrackHorse(horse.getUniqueId());
							zh.getDM().updateHorseLocation(horse.getUniqueId(), horse.getLocation(), true);
							zh.getDM().updateHorseStats(new HorseStatsRecord(horse));
							zh.getDM().updateHorseInventory(new HorseInventoryRecord(horse));
						}
					}
				}
			}
			
		});
	}

}