package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZGive extends Command {

	public ZGive(ZHorse zh, String[] a, CommandSender s) {
		super(zh, a, s);
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (!hasReachedMaxClaims()) {
							if (targetMode) {
								if (!idMode) {
									if (isOnHorse()) {
										horse = (Horse)p.getVehicle();
										execute();
									}
								}
								else {
									if (zh.getUM().isRegistered(p.getUniqueId(), userID)) {
										horse = zh.getUM().getHorse(p.getUniqueId(), userID);
										execute();
									}
									else {
										sendUnknownHorseMessage(p.getName(), true);
									}
								}
							}
							else if (displayError) {
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
				if (!samePlayer || adminMode) {
					if (zh.getUM().remove(horse)) {
						if (zh.getUM().registerHorse(targetUUID, p.getUniqueId(), horseName, horse)) {
							ChatColor cc = zh.getCM().getChatColor(targetUUID);
							horse.setCustomName(cc + horseName);
							zh.getEM().payCommand(p, command);
							s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseGiven), horseName, targetName));
						}
						else {
							zh.getLogger().severe(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseNotRegistered, true), horseName, horse.getUniqueId().toString()));
						}
					}
				}
			}
		}
	}

}
