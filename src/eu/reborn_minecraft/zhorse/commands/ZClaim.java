package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZClaim extends Command {
	
	public ZClaim(ZHorse zh, String[] a, CommandSender s) {
		super(zh, a, s);
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (!hasReachedMaxClaims()) {
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
	}
	
	private void execute() {
		if (craftHorseName()) {
			if (isClaimable()) {
				if (zh.getEM().isReadyToPay(p, command)) {
					if (zh.getUM().registerHorse(p.getUniqueId(), targetUUID, horseName, horse)) {
						ChatColor cc = zh.getCM().getChatColor(zh.getPerms().getPrimaryGroup(p));
						horse.setCustomName(cc + horseName);
						horse.setCustomNameVisible(true);
						horse.setTamed(true);
						zh.getEM().payCommand(p, command);
						s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseClaimed), horseName));
					}
					else {
						zh.getLogger().severe(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseNotRegistered, true), horseName, horse.getUniqueId().toString()));
					}
				}
			}
		}
	}
}
