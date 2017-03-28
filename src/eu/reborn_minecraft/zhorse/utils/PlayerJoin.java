package eu.reborn_minecraft.zhorse.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.database.PlayerRecord;

public class PlayerJoin {
	
	public PlayerJoin(ZHorse zh, Player player) {
		UUID playerUUID = player.getUniqueId();
		String playerName = player.getName();
		if (!zh.getDM().isPlayerRegistered(player.getUniqueId())) {
			PlayerRecord playerRecord = new PlayerRecord(playerUUID.toString(), playerName, zh.getCM().getDefaultLanguage(), zh.getDM().getDefaultFavoriteHorseID());
			Bukkit.getScheduler().runTaskAsynchronously(zh, new Runnable() {

				@Override
				public void run() {
					zh.getDM().registerPlayer(playerRecord);
				}
				
			});
		}
		else {
			if (!playerName.equalsIgnoreCase(zh.getDM().getPlayerName(playerUUID))) {
				
				Bukkit.getScheduler().runTaskAsynchronously(zh, new Runnable() {

					@Override
					public void run() {
						zh.getDM().updatePlayerName(playerUUID, playerName);
					}
					
				});
			}
		}
	}
}
