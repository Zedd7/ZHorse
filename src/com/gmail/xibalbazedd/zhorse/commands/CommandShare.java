package com.gmail.xibalbazedd.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;

public class CommandShare extends AbstractCommand {

	public CommandShare(ZHorse zh, CommandSender s, String[] a) {
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
			if (!zh.getDM().isHorseShared(horse.getUniqueId())) {
				if (zh.getDM().isHorseLocked(horse.getUniqueId())) {
					zh.getDM().updateHorseLocked(horse.getUniqueId(), false);
					if (displayConsole) {
						zh.getMM().sendMessageHorse(s, LocaleEnum.horseUnLocked, horseName);
					}
				}
				zh.getDM().updateHorseShared(horse.getUniqueId(), true);
				if (displayConsole) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.horseShared, horseName);
				}
			}
			else {
				zh.getDM().updateHorseShared(horse.getUniqueId(), false);
				if (displayConsole) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.horseUnShared, horseName);
				}
			}
			zh.getEM().payCommand(p, command);
		}
	}

}