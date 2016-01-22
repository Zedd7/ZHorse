package eu.reborn_minecraft.zhorse.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.event.world.ChunkUnloadEvent;

import eu.reborn_minecraft.zhorse.ZHorse;

public class AsyncChunckUnload {
	
	private ZHorse zh;
	private ChunkUnloadEvent e;
	
	public AsyncChunckUnload(ZHorse zh, ChunkUnloadEvent e) {
		this.zh = zh;
		this.e = e;		
		asyncChunkUnloadScheduler();
	}
	
	private void asyncChunkUnloadScheduler(){
		final Entity[] entityArray = e.getChunk().getEntities();
		Bukkit.getScheduler().runTaskAsynchronously(zh, new Runnable() {			
			
			@Override
			public void run() {
				for(Entity entity : entityArray){
					if(entity instanceof Horse){
						Horse horse = (Horse) entity;
						if(zh.getUM().isRegistered(horse)){
							zh.getUM().saveLocation(horse);
						}
					}
				}
			}
		});
	}

}
