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
		if (craftHorseName(true)) {
			if (isClaimable()) {
				if (zh.getEM().canAffordCommand(p, command)) {
					boolean lock = zh.getCM().shouldLockOnClaim();
					boolean protect = zh.getCM().shouldProtectOnClaim();
					boolean share = zh.getCM().shouldShareOnClaim();
					if (zh.getUM().registerHorse(p.getUniqueId(), horse, horseName, lock, protect, share)) {
						ChatColor cc = zh.getCM().getGroupColor(p.getUniqueId());
						horse.setCustomName(cc + horseName + ChatColor.RESET);
						horse.setCustomNameVisible(true);
						horse.setTamed(true);
						if (displayConsole) {
							s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().horseClaimed, horseName));
						}
						zh.getEM().payCommand(p, command);
					}
					else {
						zh.getLogger().severe(zh.getMM().getMessageHorseValue(language, zh.getLM().horseNotRegistered, horseName, horse.getUniqueId().toString()));
					}
				}
			}
		}
	}	
}
