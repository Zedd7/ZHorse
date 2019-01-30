package com.github.zedd7.zhorse.utils;

import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.managers.HorseManager;

public class ChunkLoad {

	public ChunkLoad(ZHorse zh, Chunk chunk) {
		for (Entity entity : chunk.getEntities()) {
			if (entity instanceof AbstractHorse) {
				AbstractHorse horse = (AbstractHorse) entity;
				if (!horse.getMetadata(HorseManager.DUPLICATE_METADATA).isEmpty()) {
					horse.remove();
				}
				else {
					zh.getDM().isHorseRegistered(horse.getUniqueId(), false, new CallbackListener<Boolean>() {

						@Override
						public void callback(CallbackResponse<Boolean> response) {
							if (response.getResult() != null) {
								boolean horseRegistered = response.getResult();
								if (horseRegistered) {
									 zh.getHM().trackHorse(horse);
									 UUID horseUUID = horse.getUniqueId();
									 Location horseLocation = horse.getLocation();
									 zh.getDM().updateHorseLocation(horseUUID, horseLocation, false, false, null);
								}
							}
						}

					});
				}
			}
		}
	}

}