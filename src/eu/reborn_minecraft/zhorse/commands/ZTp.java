package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZTp extends Command {

	public ZTp(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		idAllow = true;
		targetAllow = false;
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (idMode) {
							if (isRegistered(targetUUID, userID)) {
								horse = zh.getUM().getHorse(targetUUID, userID);
								if (isHorseLoaded()) {
									execute();
								}
							}
						}
						else if (displayConsole) {
							sendCommandUsage();
						}
					}
				}
			}
		}
	}
	
	private void execute() {
		if (isOwner()) {
			if (isHorseReachable()) {
				if (isNotOnHorse()) {
					if (zh.getEM().isReadyToPay(p, command)) {
						p.teleport(horse);
						if (displayConsole) {
							s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().teleportedToHorse, horseName));
						}
						zh.getEM().payCommand(p, command);
					}
				}
			}
		}
	}
}