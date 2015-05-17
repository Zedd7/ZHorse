package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZClaim extends Command {
	
	public ZClaim(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
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
						ChatColor cc = zh.getCM().getChatColor(p.getUniqueId());
						horse.setCustomName(cc + horseName + ChatColor.RESET);
						horse.setCustomNameVisible(true);
						horse.setTamed(true);
						if (displayConsole) {
							s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().horseClaimed, horseName));
						}
						zh.getEM().payCommand(p, command);
						if (zh.getCM().shouldLockOnClaim()) {
							String[] a = {"lock"};
							new ZLock(zh, s, a);
						}
						if (zh.getCM().shouldProtectOnClaim()) {
							String[] a = {"protect"};
							new ZProtect(zh, s, a);
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
