package eu.reborn_minecraft.zhorse.commands;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class ZInfo extends Command {

	public ZInfo(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			applyArgument(true);
			if (!idMode) {
				if (!targetMode) {
					boolean ownsHorse = ownsHorse(targetUUID, true);
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
					sendCommandUsage();
				}
			}
			else {
				if (isRegistered(targetUUID, userID)) {
					horse = zh.getUM().getHorse(targetUUID, userID);
					if (isHorseLoaded()) {
						execute();
					}
				}
			}
		}
	}

	private void execute() {
		if (zh.getEM().canAffordCommand(p, command)) {
			Damageable d = horse;
			UUID ownerUUID = zh.getUM().getPlayerUUID(horse);
			String ownerName = zh.getUM().getPlayerName(ownerUUID);
			String userID = zh.getUM().getUserID(ownerUUID, horse);
			String horseName = zh.getUM().getHorseName(ownerUUID, userID);
			int health = ((Number) d.getHealth()).intValue();
			int maxHealth = ((Number) d.getMaxHealth()).intValue();
			Location loc = zh.getUM().getLocation(ownerUUID, userID);
			String x = Double.toString(loc.getX());
			String y = Double.toString(loc.getY());
			String z = Double.toString(loc.getZ());
			String world = loc.getWorld().getName();
			String location = x.substring(0, x.indexOf(".")) + "/" + y.substring(0, y.indexOf(".")) + "/" + z.substring(0, z.indexOf(".")) + " : " + world;
			String status = "";
			if (zh.getUM().isProtected(ownerUUID, userID)) {
				status += zh.getMM().getMessageSpacer(s, LocaleEnum.modeProtected, 1, true);
			}
			if (zh.getUM().isLocked(ownerUUID, userID)) {
				status += zh.getMM().getMessageSpacer(s, LocaleEnum.modeLocked, 1, true);
			}
			else if (zh.getUM().isShared(ownerUUID, userID)) {
				status += zh.getMM().getMessageSpacer(s, LocaleEnum.modeShared, 1, true);
			}
			zh.getMM().sendMessageValue(s, LocaleEnum.headerFormat, zh.getMM().getMessage(s, LocaleEnum.horseInfoHeader, true), true);
			if (isOwner(false, true)) {
				zh.getMM().sendMessageSpacerUserID(s, LocaleEnum.id, 1, userID, true);
			}
			zh.getMM().sendMessagePlayerSpacer(s, LocaleEnum.owner, ownerName, 1, true);
			zh.getMM().sendMessageHorseSpacer(s, LocaleEnum.name, horseName, 1, true);
			zh.getMM().sendMessageAmountMaxSpacer(s, LocaleEnum.health, health, maxHealth, 1, true);
			if (isNotOnHorse(true)) {
				zh.getMM().sendMessageSpacerValue(s, LocaleEnum.location, 1, location, true);
			}
			if (!status.isEmpty()) {
				zh.getMM().sendMessageSpacerValue(s, LocaleEnum.status, 1, status, true);
			}
			zh.getEM().payCommand(p, command);
		}
	}

}