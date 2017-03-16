package eu.reborn_minecraft.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class CommandFree extends AbstractCommand {

	public CommandFree(ZHorse zh, CommandSender s, String[] a) {
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
							else {
								removeLostHorse();
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
					else {
						removeLostHorse();
					}
				}
			}
		}
	}

	private void execute() {
		if (isOwner() && zh.getEM().canAffordCommand(p, command)) {
			zh.getHM().untrackHorse(horse.getUniqueId());
			boolean success = zh.getDM().removeHorse(horse.getUniqueId(), targetUUID);
			success &= zh.getDM().removeHorseInventory(horse.getUniqueId());
			success &= zh.getDM().removeHorseStats(horse.getUniqueId());
			if (success) {
				horse.setCustomName(null);
				horse.setCustomNameVisible(false);
				if (displayConsole) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.horseFreed, horseName);
				}
				zh.getEM().payCommand(p, command);
			}
		}	
	}
	
	private void removeLostHorse() {
		UUID horseUUID = zh.getDM().getHorseUUID(targetUUID, Integer.parseInt(horseID));
		zh.getHM().untrackHorse(horseUUID);
		boolean success = zh.getDM().removeHorse(horseUUID, targetUUID, Integer.parseInt(horseID));
		success &= zh.getDM().removeHorseInventory(horseUUID);
		success &= zh.getDM().removeHorseStats(horseUUID);
		if (success) {
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
