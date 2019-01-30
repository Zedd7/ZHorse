package com.github.zedd7.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.MessageConfig;

public class CommandGive extends AbstractCommand {

	public CommandGive(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		targetIsOwner = false;
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled()
				&& parseArgument(ArgumentEnum.PLAYER_NAME, ArgumentEnum.HORSE_NAME)) {
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
							horseID = zh.getDM().getPlayerFavoriteHorseID(p.getUniqueId(), true, null).toString();
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
		if (isRegistered(ownerUUID, horseID)) {
			horse = zh.getHM().getHorse(ownerUUID, Integer.parseInt(horseID));
			if (isHorseLoaded(false)) {
				execute();
			}
		}
	}

	private void execute() {
		if (!hasReachedClaimsLimit(true) && isOwner(false) && isPlayerDifferent()) {
			int previousHorseID = zh.getDM().getHorseID(horse.getUniqueId(), true, null);
			int horseID = zh.getDM().getNextHorseID(targetUUID);
			horseName = zh.getDM().getHorseName(horse.getUniqueId(), true, null);
			boolean lock = zh.getCM().shouldLockOnClaim();
			boolean protect = zh.getCM().shouldProtectOnClaim();
			boolean share = zh.getCM().shouldShareOnClaim();
			boolean success = true; // Always true because of async updates
			success &= zh.getDM().removeSale(horse.getUniqueId(), false, null);
			success &= zh.getDM().updateHorseOwner(horse.getUniqueId(), targetUUID, false, null);
			success &= zh.getDM().updateHorseID(horse.getUniqueId(), horseID, false, null);
			success &= zh.getDM().updateHorseName(horse.getUniqueId(), horseName, false, null);
			success &= zh.getDM().updateHorseLocked(horse.getUniqueId(), lock, false, null);
			success &= zh.getDM().updateHorseProtected(horse.getUniqueId(), protect, false, null);
			success &= zh.getDM().updateHorseShared(horse.getUniqueId(), share, false, null);
			success &= zh.getDM().updateHorseIDMapping(p.getUniqueId(), previousHorseID, false, null);
			if (success) {
				applyHorseName(targetUUID);
				horse.setOwner(zh.getServer().getOfflinePlayer(targetUUID));

				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_GIVEN) {{ setHorseName(horseName); setPlayerName(targetName); }});
				zh.getMM().sendPendingMessage(targetUUID, new MessageConfig(LocaleEnum.HORSE_RECEIVED) {{ setHorseName(horseName); setPlayerName(p.getName()); }});
				zh.getCmdM().updateCommandHistory(s, command);
				zh.getEM().payCommand(p, command);
			}
		}
	}

}
