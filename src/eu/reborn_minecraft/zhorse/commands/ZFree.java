package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZFree extends Command {

	public ZFree(ZHorse zh, String[] a, CommandSender s) {
		super(zh, a, s);
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (!(idMode || targetMode)) {
							if (isOnHorse()) {
								horse = (Horse)p.getVehicle();
								execute();
							}
						}
						else {
							if (idMode) {
								if (zh.getUM().isRegistered(targetUUID, userID)) {
									horse = zh.getUM().getHorse(targetUUID, userID);
									execute();
								}
								else {
									sendUnknownHorseMessage(targetName);
								}
							}
							else if (displayError){
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
				horseName = zh.getUM().getHorseName(horse);
				if (zh.getUM().remove(horse)) {
					horse.setCustomName(null);
					horse.setCustomNameVisible(false);
					s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseFreed), horseName));
					zh.getEM().payCommand(p, command);
				}
			}
		}
	}

}
