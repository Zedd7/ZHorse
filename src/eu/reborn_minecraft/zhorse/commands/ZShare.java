package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class ZShare extends Command {

	public ZShare(ZHorse zh, CommandSender s, String[] a) {
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
			if (!zh.getUM().isShared(horse)) {
				if (zh.getUM().isLocked(horse)) {
					zh.getUM().unLock(targetUUID, horse);
					if (displayConsole) {
						zh.getMM().sendMessageHorse(s, LocaleEnum.horseUnLocked, horseName);
					}
				}
				zh.getUM().share(targetUUID, horse);
				if (displayConsole) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.horseShared, horseName);
				}
			}
			else {
				zh.getUM().unShare(targetUUID, horse);
				if (displayConsole) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.horseUnShared, horseName);
				}
			}
			zh.getEM().payCommand(p, command);
		}
	}

}