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
							horse = (Horse) p.getVehicle();
							if (isRegistered(horse)) {
								execute();
							}
						}
						else if (ownsHorse) {
							horseID = zh.getDM().getPlayerFavoriteHorseID(p.getUniqueId()).toString();
							if (isRegistered(p.getUniqueId(), horseID)) {
								horse = zh.getHM().getFavoriteHorse(p.getUniqueId());
								if (isHorseLoaded()) {
									execute();
								}
							}
						}
					}
					else {
						if (isRegistered(p.getUniqueId(), horseID, true)) {
							horse = zh.getHM().getHorse(p.getUniqueId(), Integer.parseInt(horseID));
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
		if (!hasReachedClaimsLimit(targetUUID) && isOwner() && isPlayerDifferent() && zh.getEM().canAffordCommand(p, command)) {
			horseName = zh.getDM().getHorseName(horse.getUniqueId());
			boolean locked = zh.getDM().isHorseLocked(horse.getUniqueId());
			boolean protect = zh.getDM().isHorseProtected(horse.getUniqueId());
			boolean shared = zh.getDM().isHorseShared(horse.getUniqueId());
			if (zh.getDM().registerHorse(horse.getUniqueId(), targetUUID, horseName, locked, protect, shared, horse.getLocation())) {
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

}
