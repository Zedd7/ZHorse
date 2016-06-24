package eu.reborn_minecraft.zhorse.utils;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class AsyncChunckLoad {
	
	public AsyncChunckLoad(ZHorse zh, Chunk chunk) {
		final Entity[] entityArray = chunk.getEntities();
		Bukkit.getScheduler().runTaskAsynchronously(zh, new Runnable() {	
			
			@Override
			public void run() {
				for (Entity entity : entityArray) {
					if (entity instanceof Horse) {
						Horse horse = (Horse) entity;
						if (zh.getUM().isRegistered(horse)) {
							zh.getHM().loadHorse(horse);
							zh.getUM().saveLocation(horse);
						}
					}
				}
			}
			
		});
	}

}