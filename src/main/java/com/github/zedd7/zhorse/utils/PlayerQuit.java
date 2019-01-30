package com.github.zedd7.zhorse.utils;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.database.HorseRecord;

public class PlayerQuit {

	public PlayerQuit(ZHorse zh, Player player) {
		if (player.getVehicle() != null && player.getVehicle() instanceof AbstractHorse) {
			player.getVehicle().eject();
		}
		if (zh.getCM().shouldSendToStableOnOwnerLogout()) {
			UUID playerUUID = player.getUniqueId();
			zh.getDM().getHorseRecordList(playerUUID, false, false, new CallbackListener<List<HorseRecord>>() {

				@Override
				public void callback(CallbackResponse<List<HorseRecord>> response) {
					boolean blockLeashedTeleport = zh.getCM().shouldBlockLeashedTeleport();
					for (HorseRecord horseRecord : response.getResult()) {
						UUID horseUUID = UUID.fromString(horseRecord.getUUID());
						zh.getDM().isHorseStableRegistered(horseUUID, false, new CallbackListener<Boolean>() {

							@Override
							public void callback(CallbackResponse<Boolean> response) {
								CallbackListener<Location> getHorseStableLocationListener = new CallbackListener<Location>() {

									@Override
									public void callback(CallbackResponse<Location> response) {
										AbstractHorse horse = zh.getHM().getHorse(horseRecord);
										if (horse != null && (!horse.isLeashed() || !blockLeashedTeleport)) {
											Location stableLocation = response.getResult();
											if (stableLocation != null && zh.getCM().isWorldCrossable(stableLocation.getWorld())) {
												zh.getHM().teleportHorse(horse, stableLocation, false);
											}
										}
									}

								};
								if (response.getResult() != null && response.getResult()) { // Check if response null because of async call
									zh.getDM().getHorseStableLocation(horseUUID, false, getHorseStableLocationListener);
								}
								else if (zh.getCM().shouldUseDefaultStable()) {
									getHorseStableLocationListener.callback(new CallbackResponse<>(zh.getCM().getDefaultStableLocation()));
								}
								else {
									/* Keep horse where it is */
								}
							}

						});
					}
				}

			});

		}

	}
}
