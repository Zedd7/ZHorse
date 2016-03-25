package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class ZTp extends Command {

	public ZTp(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			applyArgument(true);
			if (!idMode) {
				if (!targetMode) {
					userID = zh.getUM().getFavoriteUserID(p.getUniqueId());
					if (isRegistered(p.getUniqueId(), userID)) {
						horse = zh.getHM().getFavoriteHorse(p.getUniqueId());
						if (isHorseLoaded()) {
							execute();
						}
					}
				}
				else {
					sendCommandUsage();
				}
			}
			else {
				if (isRegistered(targetUUID, userID)) {
					horse = zh.getHM().getHorse(targetUUID, userID);
					if (isHorseLoaded()) {
						execute();
					}
				}
			}
		}
	}
	
	private void execute() {
		if (isOwner() && isHorseReachable() && isNotOnHorse() && zh.getEM().canAffordCommand(p, command)) {
			p.teleport(horse);
			if (displayConsole) {
				zh.getMM().sendMessageHorse(s, LocaleEnum.teleportedToHorse, horseName);
			}
			zh.getEM().payCommand(p, command);
		}
	}
}