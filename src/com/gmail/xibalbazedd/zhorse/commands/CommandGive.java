package com.gmail.xibalbazedd.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

public class CommandGive extends AbstractCommand {

	public CommandGive(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		targetIsOwner = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled() && applyArgument(false)) {
			if (!targetMode) {
				sendCommandUsage();
			}
			else {
				if (isRegistered(targetUUID)) {
					if (!idMode) {
						boolean ownsHorse = ownsHorse(p.getUniqueId(), true);
						if (isOnHorse(ownsHorse)) {
							horse = (AbstractHorse) p.getVehicle();
							if (isRegistered(horse)) {
								execute();
							}
						}
						else if (ownsHorse) {
							horseID = zh.getDM().getPlayerFavoriteHorseID(p.getUniqueId()).toString();
							execute(p.getUniqueId(), horseID);
						}
					}
					else {
						execute(p.getUniqueId(), horseID);
					}
				}
			}
		}
	}
	
	private void execute(UUID ownerUUID, String horseID) {
		if (isRegistered(ownerUUID, horseID, true)) {
			horse = zh.getHM().getHorse(ownerUUID, Integer.parseInt(horseID));
			if (isHorseLoaded(false)) {
				execute();
			}
		}
	}
	
	private void execute() {
		if (!hasReachedClaimsLimit(true) && isOwner() && isPlayerDifferent() && zh.getEM().canAffordCommand(p, command)) {
			int horseID = zh.getDM().getNextHorseID(targetUUID);
			horseName = zh.getDM().getHorseName(horse.getUniqueId());
			boolean lock = zh.getCM().shouldLockOnClaim();
			boolean protect = zh.getCM().shouldProtectOnClaim();
			boolean share = zh.getCM().shouldShareOnClaim();
			boolean success = true;
			success &= zh.getDM().removeSale(horse.getUniqueId());
			success &= zh.getDM().updateHorseOwner(horse.getUniqueId(), targetUUID);
			success &= zh.getDM().updateHorseID(horse.getUniqueId(), horseID);
			success &= zh.getDM().updateHorseName(horse.getUniqueId(), horseName);
			success &= zh.getDM().updateHorseLocked(horse.getUniqueId(), lock);
			success &= zh.getDM().updateHorseProtected(horse.getUniqueId(), protect);
			success &= zh.getDM().updateHorseShared(horse.getUniqueId(), share);
			if (success) {
				applyHorseName(targetUUID);
				horse.setOwner(zh.getServer().getOfflinePlayer(targetUUID));
				
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_GIVEN) {{ setHorseName(horseName); setPlayerName(targetName); }});
				zh.getMM().sendPendingMessage(targetUUID, new MessageConfig(LocaleEnum.HORSE_RECEIVED) {{ setHorseName(horseName); setPlayerName(p.getName()); }});
				zh.getEM().payCommand(p, command);
			}
		}
	}

}
