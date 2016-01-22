package eu.reborn_minecraft.zhorse.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.reborn_minecraft.zhorse.ZHorse;

public class AsyncPlayerJoin {
	
	private ZHorse zh;
	private PlayerJoinEvent e;
	
	public AsyncPlayerJoin(ZHorse zh, PlayerJoinEvent e) {
		this.zh = zh;	
		this.e = e;	
		asyncPlayerJoinSchedule();
		
	}
	
	private void asyncPlayerJoinSchedule() {		
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
