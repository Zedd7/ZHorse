package eu.reborn_minecraft.zhorse.commands;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

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
			String health = Integer.toString(((Number) d.getHealth()).intValue());
			String maxHealth = Integer.toString(((Number) d.getMaxHealth()).intValue());
			Location loc = zh.getUM().getLocation(ownerUUID, userID);
			String x = Double.toString(loc.getX());
			String y = Double.toString(loc.getY());
			String z = Double.toString(loc.getZ());
			String world = loc.getWorld().getName();
			String location = x.substring(0, x.indexOf(".")) + "/" + y.substring(0, y.indexOf(".")) + "/" + z.substring(0, z.indexOf(".")) + " : " + world;
			String status = "";
			boolean normal = true;
			if (zh.getUM().isProtected(ownerUUID, userID)) {
				status += zh.getMM().getInfo(s, zh.getLM().modeProtected, true);
				normal = false;
			}
			if (zh.getUM().isLocked(ownerUUID, userID)) {
				status += zh.getMM().getInfo(s, zh.getLM().modeLocked, true);
				normal = false;
			}
			else if (zh.getUM().isShared(ownerUUID, userID)) {
				status += zh.getMM().getInfo(s, zh.getLM().modeShared, true);
				normal = false;
			}
			if (normal) {
				status += zh.getMM().getInfo(s, zh.getLM().modeNone, true);
			}
			zh.getMM().sendHeaderContent(s, zh.getLM().headerFormat, zh.getLM().horseInfoHeader, true);
			if (isOwner(true)) {
				zh.getMM().sendInfoUserID(s, zh.getLM().id, userID, true);
			}
			zh.getMM().sendInfoPlayer(s, zh.getLM().owner, ownerName, true);
			zh.getMM().sendInfoHorse(s, zh.getLM().name, horseName, true);
			zh.getMM().sendInfoAmountMax(s, zh.getLM().health, health, maxHealth, true);
			if (isNotOnHorse(true)) {
				zh.getMM().sendInfoValue(s, zh.getLM().location, location, true);
			}
			zh.getMM().sendInfoValue(s, zh.getLM().status, status, true);
			zh.getEM().payCommand(p, command);
		}
	}

}