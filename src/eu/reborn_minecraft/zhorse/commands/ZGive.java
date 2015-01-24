package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZGive extends Command {

	public ZGive(ZHorse zh, CommandSender s, String[] a) {
		super(zh, a, s);
		idAllow = true;
		targetAllow = true;
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (!hasReachedMaxClaims(targetUUID)) {
							if (targetMode) {
								if (!idMode) {
									if (isOnHorse()) {
										horse = (Horse)p.getVehicle();
										if (isRegistered()) {
											execute();
										}
									}
								}
								else {
									if (isRegistered(p.getUniqueId(), userID, true)) {
										horse = zh.getUM().getHorse(p.getUniqueId(), userID);
										if (isHorseLoaded()) {
											execute();
										}
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
			if (zh.getEM().isReadyToPay(p, command)) {
				horseName = zh.getUM().getHorseName(horse);
				if (!samePlayer || adminMode) {
					if (zh.getUM().registerHorse(targetUUID, horseName, horse)) {
						ChatColor cc = zh.getCM().getChatColor(targetUUID);
						horse.setCustomName(cc + horseName);
						zh.getEM().payCommand(p, command);
						if (displayConsole) {
							s.sendMessage(String.format(zh.getLM().getCommandAnswer(language, zh.getLM().horseGiven), horseName, targetName));
							if (isPlayerOnline(targetUUID, true)) {
								zh.getServer().getPlayer(targetUUID).sendMessage(String.format(zh.getLM().getCommandAnswer(language, zh.getLM().horseReceived), targetName, horseName));
							}
						}
					}
					else {
						zh.getLogger().severe(String.format(zh.getLM().getCommandAnswer(language, zh.getLM().horseNotRegistered, true), horseName, horse.getUniqueId().toString()));
					}
				}
			}
		}
	}

}
