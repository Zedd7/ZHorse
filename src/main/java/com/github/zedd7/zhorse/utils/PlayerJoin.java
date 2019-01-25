package com.github.zedd7.zhorse.utils;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.database.PendingMessageRecord;
import com.github.zedd7.zhorse.database.PlayerRecord;
import com.github.zedd7.zhorse.managers.MessageManager;

public class PlayerJoin {

	public PlayerJoin(ZHorse zh, Player player) {
		UUID playerUUID = player.getUniqueId();
		String playerName = player.getName();
		if (!zh.getDM().isPlayerRegistered(player.getUniqueId())) {
			String language = zh.getCM().getDefaultLanguage();
			int favorite = zh.getDM().getDefaultFavoriteHorseID();
			boolean displayExactStats = zh.getCM().shouldUseExactStats();
			PlayerRecord playerRecord = new PlayerRecord(playerUUID.toString(), playerName, language, favorite, displayExactStats);
			zh.getDM().registerPlayer(playerRecord, false, null);
		}
		else {
			if (!playerName.equalsIgnoreCase(zh.getDM().getPlayerName(playerUUID))) {
				zh.getDM().updatePlayerName(playerUUID, playerName, false, null);
			}

			List<PendingMessageRecord> messageRecordList = zh.getDM().getPendingMessageRecordList(player.getUniqueId());
			if (!messageRecordList.isEmpty()) {
				new BukkitRunnable() {

					@Override
					public void run() {
						for (PendingMessageRecord messageRecord : messageRecordList) {
							String message = messageRecord.getMessage();
							Date date = messageRecord.getDate();
							zh.getMM().sendMessage(player, message + " " + ChatColor.RESET + MessageManager.DATE_FORMAT_TIMESTAMP.format(date));
						}
					}

				}.runTaskLater(zh, 5 * 20L);
				zh.getDM().removePendingMessages(player.getUniqueId(), false, null);
			}
		}
	}
}
