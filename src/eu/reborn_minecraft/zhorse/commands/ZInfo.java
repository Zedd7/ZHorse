package eu.reborn_minecraft.zhorse.commands;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;
import eu.reborn_minecraft.zhorse.managers.HorseManager;

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
						horse = (Horse) p.getVehicle();
						if (isRegistered(horse)) {
							execute();
						}
					}
					else if (ownsHorse) {
						userID = zh.getUM().getFavoriteUserID(p.getUniqueId());
						if (isRegistered(p.getUniqueId(), userID)) {
							horse = zh.getHM().getFavoriteHorse(p.getUniqueId());
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
					horse = zh.getHM().getHorse(targetUUID, userID);
					if (isHorseLoaded()) {
						execute();
					}
				}
			}
		}
	}

	private void execute() {
		if (zh.getEM().canAffordCommand(p, command)) {
			UUID ownerUUID = zh.getUM().getPlayerUUID(horse);
			String userID = zh.getUM().getUserID(ownerUUID, horse);
			displayHeader();
			displayID(ownerUUID, userID);
			displayNames(ownerUUID, userID);
			displayHealth();
			displayJumpStrength();
			displaySpeed();
			displayLocation(ownerUUID, userID);
			displayStatus(ownerUUID, userID);
			zh.getEM().payCommand(p, command);
		}
	}
	
	private void displayHeader() {
		zh.getMM().sendMessageValue(s, LocaleEnum.headerFormat, zh.getMM().getMessage(s, LocaleEnum.horseInfoHeader, true), true);
	}
	
	private void displayID(UUID ownerUUID, String userID) {
		if (isOwner(false, true)) {
			zh.getMM().sendMessageSpacerUserID(s, LocaleEnum.id, 1, userID, true);
		}
	}
	
	private void displayNames(UUID ownerUUID, String userID) {
		String ownerName = zh.getUM().getPlayerName(ownerUUID);
		String horseName = zh.getUM().getHorseName(ownerUUID, userID);
		zh.getMM().sendMessagePlayerSpacer(s, LocaleEnum.owner, ownerName, 1, true);
		zh.getMM().sendMessageHorseSpacer(s, LocaleEnum.name, horseName, 1, true);
	}
	
	private void displayHealth() {
		Damageable d = horse;
		int health = (int) d.getHealth();
		int maxHealth = (int) d.getMaxHealth();
		zh.getMM().sendMessageAmountMaxSpacer(s, LocaleEnum.health, health, maxHealth, 1, true);
	}
	
	private void displayJumpStrength() {
		double jumpStrength = horse.getJumpStrength();
		int jumpRatio = (int) ((jumpStrength / HorseManager.MAX_JUMP_STRENGTH) * 100);
		zh.getMM().sendMessageAmountSpacer(s, LocaleEnum.jump, jumpRatio, 1, true);
	}
	
	private void displaySpeed() {
		double speed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		int speedRatio = (int) ((speed / HorseManager.MAX_SPEED) * 100);
		zh.getMM().sendMessageAmountSpacer(s, LocaleEnum.speed, speedRatio, 1, true);
	}
	
	private void displayLocation(UUID ownerUUID, String userID) {
		if (isNotOnHorse(true)) {
			Location loc = horse.getLocation();
			int x = (int) Math.round(loc.getX());
			int y = (int) Math.round(loc.getY());
			int z = (int) Math.round(loc.getZ());
			String world = loc.getWorld().getName();
			String location = String.format("%d/%d/%d : %s", x, y, z, world);
			zh.getMM().sendMessageSpacerValue(s, LocaleEnum.location, 1, location, true);
		}
	}
	
	private void displayStatus(UUID ownerUUID, String userID) {
		String status = "";
		if (zh.getUM().isProtected(ownerUUID, userID)) {
			status += zh.getMM().getMessageSpacer(s, LocaleEnum.modeProtected, 0, true);
		}
		int spacer = status.isEmpty() ? 0 : 1;
		if (zh.getUM().isLocked(ownerUUID, userID)) {
			status += zh.getMM().getMessageSpacer(s, LocaleEnum.modeLocked, spacer, true);
		}
		else if (zh.getUM().isShared(ownerUUID, userID)) {
			status += zh.getMM().getMessageSpacer(s, LocaleEnum.modeShared, spacer, true);
		}
		if (!status.isEmpty()) {
			zh.getMM().sendMessageSpacerValue(s, LocaleEnum.status, 1, status, true);
		}
	}
	
}