package com.github.zedd7.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.MessageConfig;

public class CommandLock extends AbstractCommand {

	public CommandLock(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled()
				&& parseArgument(ArgumentEnum.HORSE_NAME, ArgumentEnum.PLAYER_NAME)) {
			if (!idMode) {
				if (!targetMode) {
					boolean ownsHorse = ownsHorse(targetUUID, true);
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
					sendCommandUsage();
				}
			}
			else {
				execute(targetUUID, horseID);
			}
		}
	}

	private void execute(UUID ownerUUID, String horseID) {
		if (isRegistered(ownerUUID, horseID)) {
			horse = zh.getHM().getHorse(ownerUUID, Integer.parseInt(horseID));
			if (isHorseLoaded(true)) {
				execute();
			}
		}
	}

	private void execute() {
		if (isOwner(false)) {
			if (!zh.getDM().isHorseLocked(horse.getUniqueId())) {
				if (zh.getDM().isHorseShared(horse.getUniqueId())) {
					zh.getDM().updateHorseShared(horse.getUniqueId(), false);
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_UNSHARED) {{ setHorseName(horseName); }});
				}
				for (Entity passenger : horse.getPassengers()) {
					if (passenger instanceof Player) {
						adminMode = false;
						boolean passengerIsOwner = isOwner(passenger.getUniqueId(), true, false, true);
						boolean passengerHasPerm = hasPermissionAdmin(passenger.getUniqueId(), command, true);
						if (!passengerIsOwner && !passengerHasPerm) {
							horse.removePassenger(passenger);
							String ownerName = zh.getDM().getOwnerName(horse.getUniqueId());
							zh.getMM().sendMessage(passenger, new MessageConfig(LocaleEnum.HORSE_BELONGS_TO) {{ setPlayerName(ownerName); }});
						}
					}
				}
				zh.getDM().updateHorseLocked(horse.getUniqueId(), true);
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_LOCKED) {{ setHorseName(horseName); }});
			}
			else {
				zh.getDM().updateHorseLocked(horse.getUniqueId(), false);
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_UNLOCKED) {{ setHorseName(horseName); }});
			}
			zh.getCmdM().updateCommandHistory(s, command);
			zh.getEM().payCommand(p, command);
		}
	}

}