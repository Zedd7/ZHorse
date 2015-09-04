package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZShare extends Command {

	public ZShare(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		idAllow = true;
		targetAllow = false;
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (!(idMode || targetMode)) {
							if (isOnHorse()) {
								horse = (Horse)p.getVehicle();
								if (isRegistered()) {
									execute();
								}
							}
						}
						else {
							if (idMode) {
								if (isRegistered(targetUUID, userID)) {
									horse = zh.getUM().getHorse(targetUUID, userID);
									if (isHorseLoaded()) {
										execute();
									}
								}
							}
							else if (displayConsole){
								sendCommandUsage();
							}
						}
					}
				}
			}
		}
	}

	private void execute() {
		if (isOwner()) {
			if (zh.getEM().canAffordCommand(p, command)) {
				if (!zh.getUM().isShared(horse)) {
					if (zh.getUM().isLocked(horse)) {
						zh.getUM().unLock(targetUUID, horse);
						if (displayConsole) {
							s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().horseUnLocked, horseName));
						}
					}
					zh.getUM().share(targetUUID, horse);
					if (displayConsole) {
						s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().horseShared, horseName));
					}
				}
				else {
					zh.getUM().unShare(targetUUID, horse);
					if (displayConsole) {
						s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().horseUnShared, horseName));
					}
				}
				zh.getEM().payCommand(p, command);
			}
		}
	}

}