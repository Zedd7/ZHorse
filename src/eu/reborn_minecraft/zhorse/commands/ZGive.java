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
										execute();
									}
								}
								else {
									if (zh.getUM().isRegistered(p.getUniqueId(), userID)) {
										horse = zh.getUM().getHorse(p.getUniqueId(), userID);
										if (horse != null) {
											execute();
										}
										else if (displayConsole) {
											s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseNotFound), zh.getUM().getHorseName(horse)));
										}
									}
									else if (displayConsole) {
										sendUnknownHorseMessage(p.getName(), true);
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
		if (isRegistered()) {
			if (isOwner()) {
				if (zh.getEM().isReadyToPay(p, command)) {
					horseName = zh.getUM().getHorseName(horse);
					if (!samePlayer || adminMode) {
						if (zh.getUM().registerHorse(targetUUID, horseName, horse)) {
							ChatColor cc = zh.getCM().getChatColor(targetUUID);
							horse.setCustomName(cc + horseName);
							zh.getEM().payCommand(p, command);
							if (displayConsole) {
								s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseGiven), horseName, targetName));
								if (isPlayerOnline(targetUUID)) {
									zh.getServer().getPlayer(targetUUID).sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseReceived), targetName, horseName));
								}
							}
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
