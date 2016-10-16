package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class CommandTp extends AbstractCommand {

	public CommandTp(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled() && applyArgument(true)) {
			if (!idMode) {
				if (!targetMode) {
					horseID = zh.getDM().getPlayerFavoriteHorseID(p.getUniqueId()).toString();
					if (isRegistered(p.getUniqueId(), horseID)) {
						horse = zh.getHM().getFavoriteHorse(p.getUniqueId());
						if (isHorseLoaded(true)) {
							execute();
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
		if (isOwner() && isWorldCrossable(p.getWorld()) && isWorldCrossable(horse.getWorld()) && isNotOnHorse() && zh.getEM().canAffordCommand(p, command)) {
			p.teleport(horse);
			if (displayConsole) {
				zh.getMM().sendMessageHorse(s, LocaleEnum.teleportedToHorse, horseName);
			}
			zh.getEM().payCommand(p, command);
		}
	}
}