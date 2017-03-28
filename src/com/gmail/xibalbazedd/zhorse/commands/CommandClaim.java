package com.gmail.xibalbazedd.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.HorseInventoryRecord;
import com.gmail.xibalbazedd.zhorse.database.HorseRecord;
import com.gmail.xibalbazedd.zhorse.database.HorseStatsRecord;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;

public class CommandClaim extends AbstractCommand {
	
	public CommandClaim(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
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
		if (!hasReachedClaimsLimit(p.getUniqueId()) && isClaimable() && craftHorseName(true) && zh.getEM().canAffordCommand(p, command)) {
			int horseID = zh.getDM().getNextHorseID(p.getUniqueId());
			boolean lock = zh.getCM().shouldLockOnClaim();
			boolean protect = zh.getCM().shouldProtectOnClaim();
			boolean share = zh.getCM().shouldShareOnClaim();
			HorseRecord horseRecord = new HorseRecord(horse.getUniqueId().toString(), p.getUniqueId().toString(), horseID, horseName, lock, protect, share, horse.getLocation());
			if (zh.getDM().registerHorse(horseRecord)) {
				HorseInventoryRecord horseInventoryRecord = new HorseInventoryRecord(horse);
				HorseStatsRecord horseStatsRecord = new HorseStatsRecord(horse);
				zh.getDM().registerHorseInventory(horseInventoryRecord);
				zh.getDM().registerHorseStats(horseStatsRecord);
				zh.getHM().trackHorse(horse);
				
				targetUUID = p.getUniqueId(); // Uses the player group for the horse name color
				applyHorseName();
				horse.setCustomNameVisible(true);
				horse.setTamed(true);
				if (displayConsole) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.horseClaimed, horseName);
				}
				zh.getEM().payCommand(p, command);
			}
		}
	}
}
