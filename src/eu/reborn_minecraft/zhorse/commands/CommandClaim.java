package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.database.HorseRecord;
import eu.reborn_minecraft.zhorse.database.HorseStatsRecord;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

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
			HorseStatsRecord horseStatsRecord = new HorseStatsRecord(horse);
			if (zh.getDM().registerHorse(horseRecord) && zh.getDM().registerHorseStats(horseStatsRecord)) {
				targetUUID = p.getUniqueId(); // uses the player group for the horse name color
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
