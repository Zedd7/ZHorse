package eu.reborn_minecraft.zhorse.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.event.world.ChunkLoadEvent;

import eu.reborn_minecraft.zhorse.ZHorse;

public class AsyncChunckLoad {
	
	private ZHorse zh;
	private ChunkLoadEvent e;
	
	public AsyncChunckLoad(ZHorse zh, ChunkLoadEvent e) {
		this.zh = zh;
		this.e = e;		
		asyncChunkLoadScheduler();
	}
	
	private void asyncChunkLoadScheduler(){
		final Entity[] entityArray = e.getChunk().getEntities();
		Bukkit.getScheduler().runTaskAsynchronously(zh, new Runnable() {			
			
			@Override
			public void run() {
				for (Entity entity : entityArray) {
					if (entity instanceof Horse) {
						Horse horse = (Horse) entity;
						if (zh.getUM().isRegistered(horse)) {
							zh.getHM().loadHorse(horse);
						}
					}
				}
			}
		});
	}

}