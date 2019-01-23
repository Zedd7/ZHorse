package com.github.zedd7.zhorse.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;

import com.github.zedd7.zhorse.ZHorse;

public class PlayerQuit {

	public PlayerQuit(ZHorse zh, Player player) {
		if (player.getVehicle() != null) {
			player.getVehicle().eject();
		}

		if (zh.getCM().shouldSendToStableOnOwnerLogout()) {
			UUID playerUUID = player.getUniqueId();
			boolean blockLeashedTeleport = zh.getCM().shouldBlockLeashedTeleport();

			Bukkit.getScheduler().scheduleSyncDelayedTask(zh, new Runnable() {

				@Override
				public void run() {
					for (UUID horseUUID : zh.getDM().getHorseUUIDList(playerUUID, false)) {
						int horseID = zh.getDM().getHorseID(horseUUID);
						AbstractHorse horse = zh.getHM().getHorse(playerUUID, horseID);
						if (!horse.isLeashed() || !blockLeashedTeleport) {
							Location stableLocation = null;
							if (zh.getDM().isHorseStableRegistered(horseUUID)) {
								stableLocation = zh.getDM().getHorseStableLocation(horseUUID);
							}
							else if (zh.getCM().shouldUseDefaultStable()) {
								stableLocation = zh.getCM().getDefaultStableLocation();
							}
							if (stableLocation != null && zh.getCM().isWorldCrossable(stableLocation.getWorld())) {
								zh.getHM().teleportHorse(horse, stableLocation);
							}
						}
					}
				}

			});
		}

	}
}
