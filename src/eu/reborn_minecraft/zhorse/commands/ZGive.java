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
			if (zh.getEM().canAffordCommand(p, command)) {
				horseName = zh.getUM().getHorseName(horse);
				if (!samePlayer || adminMode) {
					boolean locked = zh.getUM().isLocked(p.getUniqueId(), horse);
					boolean protect = zh.getUM().isProtected(p.getUniqueId(), horse);
					boolean shared = zh.getUM().isShared(p.getUniqueId(), horse);
					if (zh.getUM().registerHorse(targetUUID, horse, horseName, locked, protect, shared)) {
						ChatColor cc = zh.getCM().getGroupColor(targetUUID);
						horse.setCustomName(cc + horseName + ChatColor.RESET);
						zh.getEM().payCommand(p, command);
						if (displayConsole) {
							s.sendMessage(zh.getMM().getMessagePlayerHorse(language, zh.getLM().horseGiven, targetName, horseName));
							if (isPlayerOnline(targetUUID, true)) {
								zh.getServer().getPlayer(targetUUID).sendMessage(zh.getMM().getMessagePlayerHorse(language, zh.getLM().horseReceived, p.getName(), horseName));
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
