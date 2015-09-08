package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZLock extends Command {

	public ZLock(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		idAllow = true;
		targetAllow = false;
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (!(idMode || targetMode)) {
							if (isOnHorse()) {
								horse = (Horse)p.getVehicle();
								if (isRegistered()) {
									execute();
								}
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
							else if (displayConsole){
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
				if (!zh.getUM().isLocked(horse)) {
					if (zh.getUM().isShared(horse)) {
						zh.getUM().unShare(targetUUID, horse);
						if (displayConsole) {
							s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().horseUnShared, horseName));
						}
					}
					zh.getUM().lock(targetUUID, horse);
					Entity passenger = horse.getPassenger();
					if (passenger != null && passenger instanceof Player) {
						adminMode = false;
						if (!isOwner(passenger.getUniqueId(), true)) {
							horse.eject();
							passenger.sendMessage(zh.getMM().getMessagePlayer(language, zh.getLM().horseBelongsTo, zh.getUM().getPlayerName(horse)));
						}
					}
					if (displayConsole) {
						s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().horseLocked, horseName));
					}
				}
				else {
					zh.getUM().unLock(targetUUID, horse);
					if (displayConsole) {
						s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().horseUnLocked, horseName));
					}
				}
				zh.getEM().payCommand(p, command);
			}
		}
	}

}