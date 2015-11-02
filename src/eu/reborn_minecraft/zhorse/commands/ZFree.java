package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class ZFree extends Command {

	public ZFree(ZHorse zh, CommandSender s, String[] a) {
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
							else {
								zh.getUM().unRegisterHorse(targetUUID, userID);
								if (samePlayer) {
									zh.getMM().sendMessageHorse(s, LocaleEnum.horseCleared, horseName);
								}
								else {
									zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.horseClearedOther, horseName, targetName);
								}
								zh.getEM().payCommand(p, command);
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
					else {
						zh.getUM().unRegisterHorse(targetUUID, userID);
						if (samePlayer) {
							zh.getMM().sendMessageHorse(s, LocaleEnum.horseCleared, horseName);
						}
						else {
							zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.horseClearedOther, horseName, targetName);
						}
						zh.getEM().payCommand(p, command);
					}
				}
			}
		}
	}

	private void execute() {
		if (isOwner() && zh.getEM().canAffordCommand(p, command)) {
			if (zh.getUM().unRegisterHorse(horse)) {
				horse.setCustomName(null);
				horse.setCustomNameVisible(false);
				if (displayConsole) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.horseFreed, horseName);
				}
				zh.getEM().payCommand(p, command);
			}
		}	
	}

}
