package com.github.xibalba.zhorse.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;

import com.github.xibalba.zhorse.ZHorse;
import com.github.xibalba.zhorse.managers.HorseManager;

public class ChunkLoad {
	
	public ChunkLoad(ZHorse zh, Chunk chunk) {
		for (Entity entity : chunk.getEntities()) {
			if (entity instanceof AbstractHorse) {
				AbstractHorse horse = (AbstractHorse) entity;
				if (!horse.getMetadata(HorseManager.DUPLICATE_METADATA).isEmpty()) {
					horse.remove();
				}
				else if (zh.getDM().isHorseRegistered(horse.getUniqueId())) {
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