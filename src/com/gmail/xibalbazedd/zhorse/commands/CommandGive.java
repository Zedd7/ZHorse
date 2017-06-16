package com.gmail.xibalbazedd.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.HorseRecord;
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
			HorseRecord horseRecord = new HorseRecord(horse.getUniqueId().toString(), targetUUID.toString(), horseID, horseName, lock, protect, share, horse.getLocation());
			if (zh.getDM().removeHorse(horse.getUniqueId(), p.getUniqueId(), true, true, false, false) && zh.getDM().registerHorse(horseRecord)) {
				applyHorseName(targetUUID);
				horse.setOwner(zh.getServer().getOfflinePlayer(targetUUID));
				
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_GIVEN) {{ setHorseName(horseName); setPlayerName(targetName); }});
				zh.getMM().sendPendingMessage(targetUUID, new MessageConfig(LocaleEnum.HORSE_RECEIVED) {{ setHorseName(horseName); setPlayerName(p.getName()); }});
				zh.getEM().payCommand(p, command);
			}
		}
	}

}
