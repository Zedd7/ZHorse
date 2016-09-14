package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class ZClaim extends Command {
	
	public ZClaim(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			if (!idMode) {
				if (!targetMode) {
					if (isOnHorse(false)) {
						horse = (Horse) p.getVehicle();
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
					if (isHorseLoaded()) {
						execute();
					}
				}
			}				
		}
	}
	
	private void execute() {
		if (!hasReachedClaimsLimit(p.getUniqueId()) && isClaimable() && craftHorseName(true) && zh.getEM().canAffordCommand(p, command)) {
			boolean lock = zh.getCM().shouldLockOnClaim();
			boolean protect = zh.getCM().shouldProtectOnClaim();
			boolean share = zh.getCM().shouldShareOnClaim();
			if (zh.getDM().registerHorse(horse.getUniqueId(), p.getUniqueId(), horseName, lock, protect, share, horse.getLocation())) {
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
