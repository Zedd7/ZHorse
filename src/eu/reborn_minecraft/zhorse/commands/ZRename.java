package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZRename extends Command {

	public ZRename(ZHorse zh, CommandSender s, String[] a) {
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
							else if (displayConsole) {
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
			if (craftHorseName()) {
				if (zh.getEM().isReadyToPay(p, command)) {
					ChatColor cc = zh.getCM().getChatColor(targetUUID);
					horse.setCustomName(cc + horseName + ChatColor.RESET);
					horse.setCustomNameVisible(true);
					zh.getUM().rename(targetUUID, horseName, horse);
					if (displayConsole) {
						s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().horseRenamed, horseName));
					}
					zh.getEM().payCommand(p, command);
				}
			}
		}
	}
}