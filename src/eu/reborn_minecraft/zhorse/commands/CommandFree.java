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
								zh.getDM().removeHorse(horse.getUniqueId(), targetUUID, Integer.parseInt(horseID));
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
				if (isRegistered(targetUUID, horseID)) {
					horse = zh.getHM().getHorse(targetUUID, Integer.parseInt(horseID));
					if (isHorseLoaded(true)) {
						execute();
					}
					else {
						UUID horseUUID = zh.getDM().getHorseUUID(targetUUID, Integer.parseInt(horseID));
						zh.getDM().removeHorse(horseUUID, targetUUID, Integer.parseInt(horseID));
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
			if (zh.getDM().removeHorse(horse.getUniqueId(), targetUUID)) { // horseID null if called from horse back
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
