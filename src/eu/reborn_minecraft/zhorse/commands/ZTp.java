package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZTp extends Command {

	public ZTp(ZHorse zh, CommandSender s, String[] a) {
		super(zh, a, s);
		idAllow = true;
		targetAllow = false;
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (idMode) {
							if (zh.getUM().isRegistered(targetUUID, userID)) {
								horse = zh.getUM().getHorse(targetUUID, userID);
								if (horse != null) {
									execute();
								}
								else if (displayConsole) {
									s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseNotFound), zh.getUM().getHorseName(horse)));
								}
							}
							else if (displayConsole) {
								sendUnknownHorseMessage(targetName);
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
		horseName = zh.getUM().getHorseName(horse);
		if (zh.getEM().isReadyToPay(p, command)) {
			if (isOnSameWorld()) {
				if (isNotOnHorse()) {
					p.teleport(horse);
					s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().teleportedToHorse), horseName));
					zh.getEM().payCommand(p, command);
				}
			}
		}
	}
}