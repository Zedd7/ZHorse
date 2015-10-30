package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class ZLock extends Command {

	public ZLock(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			applyArgument(true);
			if (!idMode) {
				if (!targetMode) {
					boolean ownsHorse = ownsHorse(targetUUID, true);
					if (isOnHorse(ownsHorse)) {
						horse = (Horse)p.getVehicle();
						if (isRegistered(horse)) {
							execute();
						}
					}
					else if (ownsHorse) {
						userID = zh.getUM().getFavoriteUserID(p.getUniqueId());
						if (isRegistered(p.getUniqueId(), userID)) {
							horse = zh.getUM().getFavoriteHorse(p.getUniqueId());
							if (isHorseLoaded()) {
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
				if (isRegistered(targetUUID, userID)) {
					horse = zh.getUM().getHorse(targetUUID, userID);
					if (isHorseLoaded()) {
						execute();
					}
				}
			}
		}
	}

	private void execute() {
		if (isOwner() && zh.getEM().canAffordCommand(p, command)) {
			if (!zh.getUM().isLocked(horse)) {
				if (zh.getUM().isShared(horse)) {
					zh.getUM().unShare(targetUUID, horse);
					if (displayConsole) {
						zh.getMM().sendMessageHorse(s, LocaleEnum.horseUnShared, horseName);
					}
				}
				zh.getUM().lock(targetUUID, horse);
				Entity passenger = horse.getPassenger();
				if (passenger != null && passenger instanceof Player) {
					adminMode = false;
					if (!isOwner(passenger.getUniqueId(), true)) {
						horse.eject();
						zh.getMM().sendMessagePlayer((CommandSender)passenger, LocaleEnum.horseBelongsTo, zh.getUM().getPlayerName(horse));
					}
				}
				if (displayConsole) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.horseLocked, horseName);
				}
			}
			else {
				zh.getUM().unLock(targetUUID, horse);
				if (displayConsole) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.horseUnLocked, horseName);
				}
			}
			zh.getEM().payCommand(p, command);
		}
	}

}