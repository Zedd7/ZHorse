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
				if (isRegistered(targetUUID, userID)) {
					horse = zh.getHM().getHorse(targetUUID, userID);
					if (isHorseLoaded()) {
						execute();
					}
				}
			}				
		}
	}
	
	private void execute() {
		if (!hasReachedMaxClaims(p.getUniqueId()) && isClaimable() && craftHorseName(true) && zh.getEM().canAffordCommand(p, command)) {
			boolean lock = zh.getCM().shouldLockOnClaim();
			boolean protect = zh.getCM().shouldProtectOnClaim();
			boolean share = zh.getCM().shouldShareOnClaim();
			zh.getUM().registerHorse(p.getUniqueId(), horse, horseName, lock, protect, share);
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
