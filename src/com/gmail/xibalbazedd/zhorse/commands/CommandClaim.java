package com.gmail.xibalbazedd.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.HorseInventoryRecord;
import com.gmail.xibalbazedd.zhorse.database.HorseRecord;
import com.gmail.xibalbazedd.zhorse.database.HorseStatsRecord;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

public class CommandClaim extends AbstractCommand {
	
	public CommandClaim(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
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
		if (!hasReachedClaimsLimit(false) && isClaimable() && craftHorseName(true) && zh.getEM().canAffordCommand(p, command)) {
			boolean lock = zh.getCM().shouldLockOnClaim();
			boolean protect = zh.getCM().shouldProtectOnClaim();
			boolean share = zh.getCM().shouldShareOnClaim();
			boolean success = true;
			if (zh.getDM().isHorseRegistered(horse.getUniqueId())) {
				success &= zh.getDM().removeHorse(horse.getUniqueId());
			}
			int horseID = zh.getDM().getNextHorseID(p.getUniqueId());
			HorseRecord horseRecord = new HorseRecord(horse.getUniqueId().toString(), p.getUniqueId().toString(), horseID, horseName, lock, protect, share, horse.getLocation());
			success &= zh.getDM().registerHorse(horseRecord);
			if (success) {				
				applyHorseName(p.getUniqueId());
				horse.setCustomNameVisible(true);
				horse.setOwner(zh.getServer().getOfflinePlayer(p.getUniqueId()));
				horse.setTamed(true);
				
				HorseInventoryRecord horseInventoryRecord = new HorseInventoryRecord(horse);
				HorseStatsRecord horseStatsRecord = new HorseStatsRecord(horse);
				zh.getDM().registerHorseInventory(horseInventoryRecord);
				zh.getDM().registerHorseStats(horseStatsRecord);
				zh.getHM().trackHorse(horse);
				
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_CLAIMED) {{ setHorseName(horseName); }});
				zh.getCmdM().updateCommandHistory(s, command);
				zh.getEM().payCommand(p, command);
			}
		}
	}
}
