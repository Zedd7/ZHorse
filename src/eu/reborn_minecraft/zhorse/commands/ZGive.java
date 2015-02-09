package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZGive extends Command {

	public ZGive(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
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
						horse.setCustomName(cc + horseName + ChatColor.RESET);
						zh.getEM().payCommand(p, command);
						if (displayConsole) {
							s.sendMessage(zh.getMM().getMessagePlayerHorse(language, zh.getLM().horseGiven,targetName, horseName));
							if (isPlayerOnline(targetUUID, true)) {
								zh.getServer().getPlayer(targetUUID).sendMessage(zh.getMM().getMessagePlayerHorse(language, zh.getLM().horseReceived, targetName, horseName));
							}
						}
					}
					else {
						zh.getLogger().severe(zh.getMM().getMessageHorseValue(language, zh.getLM().horseNotRegistered, horseName, horse.getUniqueId().toString()));
					}
				}
			}
		}
	}

}
