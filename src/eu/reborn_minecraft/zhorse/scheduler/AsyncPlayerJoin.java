package eu.reborn_minecraft.zhorse.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.reborn_minecraft.zhorse.ZHorse;

public class AsyncPlayerJoin {
	
	private PlayerJoinEvent e;
	private ZHorse zh;
	
	public AsyncPlayerJoin(PlayerJoinEvent e, ZHorse zh) {
		this.e = e;
		this.zh = zh;
		
		asyncPlayerJoinSchedule();
		
	}
	
	private void asyncPlayerJoinSchedule(){
		
		Bukkit.getScheduler().runTaskAsynchronously(zh, new Runnable() {
			
			@Override
			public void run() {
				Player p = e.getPlayer();
				if (!zh.getUM().isRegistered(p.getUniqueId())) {
					zh.getUM().registerPlayer(p.getUniqueId());
				}
				else {
					if (!p.getName().equalsIgnoreCase(zh.getUM().getPlayerName(p.getUniqueId()))) {
						zh.getUM().updatePlayer(p);
					}
				}				
			}
		});
	}
}
