package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZClaim extends Command {
	
	public ZClaim(ZHorse zh, CommandSender s, String[] a) {
		super(zh, a, s);
		idAllow = false;
		targetAllow = false;
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (!hasReachedMaxClaims(p.getUniqueId())) {
							if (!(idMode || targetMode)) {
								if (isOnHorse()) {
									horse = (Horse)p.getVehicle();
									execute();
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
	}
	
	private void execute() {
		if (craftHorseName()) {
			if (isClaimable()) {
				if (zh.getEM().isReadyToPay(p, command)) {
					if (zh.getUM().registerHorse(p.getUniqueId(), horseName, horse)) {
						ChatColor cc = zh.getCM().getChatColor(zh.getPerms().getPrimaryGroup(p));
						horse.setCustomName(cc + horseName);
						horse.setCustomNameVisible(true);
						horse.setTamed(true);
						if (displayConsole) {
							s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseClaimed), horseName));
						}
						zh.getEM().payCommand(p, command);
					}
					else {
						zh.getLogger().severe(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseNotRegistered, true), horseName, horse.getUniqueId().toString()));
					}
				}
			}
		}
	}	
}
