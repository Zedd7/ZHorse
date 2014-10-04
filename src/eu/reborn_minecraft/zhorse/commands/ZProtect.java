package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZProtect extends Command {

	public ZProtect(ZHorse zh, CommandSender s, String[] a) {
		super(zh, a, s);
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
			if (zh.getEM().isReadyToPay(p, command)) {
				if (!zh.getUM().isProtected(horse)) {
					zh.getUM().protect(targetUUID, horse);
					if (displayConsole) {
						s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseProtected), horseName));
					}
				}
				else {
					zh.getUM().unProtect(targetUUID, horse);
					if (displayConsole) {
						s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseUnProtected), horseName));
					}
				}
				zh.getEM().payCommand(p, command);
			}
		}
	}

}
