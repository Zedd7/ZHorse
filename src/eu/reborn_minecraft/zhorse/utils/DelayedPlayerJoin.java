package eu.reborn_minecraft.zhorse.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.database.PlayerRecord;

public class DelayedPlayerJoin {
	
	public DelayedPlayerJoin(ZHorse zh, PlayerJoinEvent e) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(zh, new Runnable() {
			
			@Override
			public void run() {
				Player p = e.getPlayer();
				if (!zh.getDM().isPlayerRegistered(p.getUniqueId())) {
					PlayerRecord playerRecord = new PlayerRecord(p.getUniqueId().toString(), p.getName(), zh.getCM().getDefaultLanguage(), zh.getDM().getDefaultFavoriteHorseID());
					zh.getDM().registerPlayer(playerRecord);
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
