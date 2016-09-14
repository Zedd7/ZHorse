package eu.reborn_minecraft.zhorse.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.reborn_minecraft.zhorse.ZHorse;

public class DelayedPlayerJoin {
	
	public DelayedPlayerJoin(ZHorse zh, PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask(zh, new Runnable() {
			
			@Override
			public void run() {
				if (!zh.getDM().isPlayerRegistered(p.getUniqueId())) {
					zh.getDM().registerPlayer(p.getUniqueId(), p.getName(), zh.getCM().getDefaultLanguage(), zh.getDM().getDefaultFavoriteHorseID());
				}
				else {
					if (!p.getName().equalsIgnoreCase(zh.getDM().getPlayerName(p.getUniqueId()))) {
						zh.getDM().updatePlayerName(p.getUniqueId(), p.getName());
					}
				}				
			}
		});
	}
}
