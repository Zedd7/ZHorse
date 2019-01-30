package com.github.zedd7.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.database.HorseInventoryRecord;
import com.github.zedd7.zhorse.database.HorseRecord;
import com.github.zedd7.zhorse.database.HorseStatsRecord;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.CallbackListener;
import com.github.zedd7.zhorse.utils.CallbackResponse;
import com.github.zedd7.zhorse.utils.MessageConfig;

public class CommandClaim extends AbstractCommand {

	public CommandClaim(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled()) {
			if (!idMode) {
				if (!targetMode) {
					if (isOnHorse(false)) {
						horse = (AbstractHorse) p.getVehicle();
						execute();
					}
				}
				else {
					sendCommandUsage();
				}
			}
			else {
				if (isRegistered(targetUUID, horseID)) {
					horse = zh.getHM().getHorse(targetUUID, Integer.parseInt(horseID));
					if (isHorseLoaded(true)) {
						execute();
					}
				}
			}
		}
	}

	private void execute() {
		if (!hasReachedClaimsLimit(false) && isClaimable() && craftHorseName(true)) {
			CallbackListener<Boolean> listener = new CallbackListener<Boolean>() {

				@Override
				public void callback(CallbackResponse<Boolean> response) {
					if (response.getResult()) {
						int horseID = zh.getDM().getNextHorseID(p.getUniqueId());
						boolean lock = zh.getCM().shouldLockOnClaim();
						boolean protect = zh.getCM().shouldProtectOnClaim();
						boolean share = zh.getCM().shouldShareOnClaim();
						HorseRecord horseRecord = new HorseRecord(horse.getUniqueId().toString(), p.getUniqueId().toString(), horseID, horseName, lock, protect, share, horse.getLocation());
						zh.getDM().registerHorse(horseRecord, false, new CallbackListener<Boolean>() {

							@Override
							public void callback(CallbackResponse<Boolean> response) {
								if (response.getResult()) {
									applyHorseName(p.getUniqueId());
									horse.setCustomNameVisible(true);
									horse.setOwner(zh.getServer().getOfflinePlayer(p.getUniqueId()));
									horse.setTamed(true);

									HorseInventoryRecord horseInventoryRecord = new HorseInventoryRecord(horse);
									HorseStatsRecord horseStatsRecord = new HorseStatsRecord(horse);
									zh.getDM().registerHorseInventory(horseInventoryRecord, false, null);
									zh.getDM().registerHorseStats(horseStatsRecord, false, null);
									zh.getHM().trackHorse(horse);

									zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_CLAIMED) {{ setHorseName(horseName); }});
									zh.getCmdM().updateCommandHistory(s, command);
									zh.getEM().payCommand(p, command);
								}
							}

						});

					}
				}

			};

			if (zh.getDM().isHorseRegistered(horse.getUniqueId(), true, null)) {
				zh.getDM().removeHorse(horse.getUniqueId(), false, listener);
			}
			else {
				listener.callback(new CallbackResponse<Boolean>(true));
			}
		}
	}
}
