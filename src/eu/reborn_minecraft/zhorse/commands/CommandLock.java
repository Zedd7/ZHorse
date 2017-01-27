package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class CommandLock extends AbstractCommand {

	public CommandLock(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled() && applyArgument(true)) {
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
						if (isRegistered(p.getUniqueId(), horseID)) {
							horse = zh.getHM().getFavoriteHorse(p.getUniqueId());
							if (isHorseLoaded(true)) {
								execute();
							}
						}
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
		if (isOwner() && zh.getEM().canAffordCommand(p, command)) {
			if (!zh.getDM().isHorseLocked(horse.getUniqueId())) {
				if (zh.getDM().isHorseShared(horse.getUniqueId())) {
					zh.getDM().updateHorseShared(horse.getUniqueId(), false);
					if (displayConsole) {
						zh.getMM().sendMessageHorse(s, LocaleEnum.horseUnShared, horseName);
					}
				}
				for (Entity passenger : horse.getPassengers()) {
					if (passenger instanceof Player) {
						adminMode = false;
						boolean passengerIsOwner = isOwner(passenger.getUniqueId(), true);
						boolean passengerHasPerm = hasPermissionAdmin(passenger.getUniqueId(), command, true);
						if (!passengerIsOwner && !passengerHasPerm) {
							horse.eject();
							String ownerName = zh.getDM().getOwnerName(horse.getUniqueId());
							zh.getMM().sendMessagePlayer((CommandSender) passenger, LocaleEnum.horseBelongsTo, ownerName);
						}
					}
				}
				zh.getDM().updateHorseLocked(horse.getUniqueId(), true);
				if (displayConsole) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.horseLocked, horseName);
				}
			}
			else {
				zh.getDM().updateHorseLocked(horse.getUniqueId(), false);
				if (displayConsole) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.horseUnLocked, horseName);
				}
			}
			zh.getEM().payCommand(p, command);
		}
	}

}