package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class ZGive extends Command {

	public ZGive(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = true;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			applyArgument(false);
			if (!targetMode) {
				sendCommandUsage();
			}
			else {
				if (isRegistered(targetUUID)) {
					if (!idMode) {
						boolean ownsHorse = ownsHorse(p.getUniqueId(), true);
						if (isOnHorse(ownsHorse)) {
							horse = (Horse)p.getVehicle();
							if (isRegistered(horse)) {
								execute();
							}
						}
						else if (ownsHorse) {
							userID = zh.getUM().getFavoriteUserID(p.getUniqueId());
							if (isRegistered(p.getUniqueId(), userID)) {
								horse = zh.getUM().getFavoriteHorse(p.getUniqueId());
								if (isHorseLoaded()) {
									execute();
								}
							}
						}
					}
					else {
						if (isRegistered(p.getUniqueId(), userID, true)) {
							horse = zh.getHM().getHorse(p.getUniqueId(), userID);
							if (isHorseLoaded()) {
								execute();
							}
						}
					}
				}
			}
		}
	}
	
	private void execute() {
		if (!hasReachedMaxClaims(targetUUID) && isOwner() && isPlayerDifferent() && zh.getEM().canAffordCommand(p, command)) {
			horseName = zh.getUM().getHorseName(horse);
			boolean locked = zh.getUM().isLocked(p.getUniqueId(), horse);
			boolean protect = zh.getUM().isProtected(p.getUniqueId(), horse);
			boolean shared = zh.getUM().isShared(p.getUniqueId(), horse);
			zh.getUM().registerHorse(targetUUID, horse, horseName, locked, protect, shared);
			applyHorseName();
			zh.getEM().payCommand(p, command);
			if (displayConsole) {
				zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.horseGiven, horseName, targetName);
				if (isPlayerOnline(targetUUID, true)) {
					zh.getMM().sendMessageHorsePlayer(((CommandSender)zh.getServer().getPlayer(targetUUID)), LocaleEnum.horseReceived, horseName, p.getName());
				}
			}
		}
	}

}
