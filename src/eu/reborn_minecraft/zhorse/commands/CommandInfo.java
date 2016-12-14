package eu.reborn_minecraft.zhorse.commands;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Llama;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.HorseStatisticEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class CommandInfo extends AbstractCommand {
	
	private static final int CHEST_SIZE_MULTIPLICATOR = 3;

	public CommandInfo(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled() && applyArgument(true)) {
			if (!idMode) {
				if (!targetMode) {
					boolean ownsHorse = ownsHorse(targetUUID, true);
					if (isOnHorse(ownsHorse)) {
						horse = (AbstractHorse) p.getVehicle();
						if (isRegistered(horse)) {
							execute();
						}
					}
					else if (ownsHorse) {
						horseID = zh.getDM().getPlayerFavoriteHorseID(p.getUniqueId()).toString();
						if (isRegistered(p.getUniqueId(), horseID)) {
							horse = zh.getHM().getFavoriteHorse(p.getUniqueId());
							if (isHorseLoaded(true)) {
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
				if (isRegistered(targetUUID, horseID)) {
					horse = zh.getHM().getHorse(targetUUID, Integer.parseInt(horseID));
					if (isHorseLoaded(true)) {
						execute();
					}
				}
			}
		}
	}

	private void execute() {
		if (zh.getEM().canAffordCommand(p, command)) {
			displayHeader();
			displayHorseID();
			displayNames();
			displayHealth();
			displaySpeed();
			displayJumpStrength();
			displayChestSize();
			displayLocation();
			displayStatus();
			zh.getEM().payCommand(p, command);
		}
	}
	
	private void displayHeader() {
		zh.getMM().sendMessageValue(s, LocaleEnum.headerFormat, zh.getMM().getMessage(s, LocaleEnum.horseInfoHeader, true), true);
	}
	
	private void displayHorseID() {
		if (isOwner(false, true)) {
			Integer horseIDInt = zh.getDM().getHorseID(horse.getUniqueId());
			String horseID = horseIDInt != null ? horseIDInt.toString() : null;
			zh.getMM().sendMessageHorseIDSpacer(s, LocaleEnum.id, horseID, 1, true);
		}
	}
	
	private void displayNames() {
		UUID ownerUUID = zh.getDM().getOwnerUUID(horse.getUniqueId());
		String ownerName = zh.getDM().getPlayerName(ownerUUID);
		String horseName = zh.getDM().getHorseName(horse.getUniqueId());
		zh.getMM().sendMessagePlayerSpacer(s, LocaleEnum.owner, ownerName, 1, true);
		zh.getMM().sendMessageHorseSpacer(s, LocaleEnum.name, horseName, 1, true);
	}
	
	private void displayHealth() {
		int health = (int) horse.getHealth();
		int maxHealth = (int) horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		zh.getMM().sendMessageAmountMaxSpacer(s, LocaleEnum.health, health, maxHealth, 1, true);
	}
	
	private void displaySpeed() {
		double speed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		double maxSpeed = HorseStatisticEnum.MAX_SPEED.getValue(useVanillaStats);
		int speedRatio = (int) ((speed / maxSpeed) * 100);
		zh.getMM().sendMessageAmountSpacer(s, LocaleEnum.speed, speedRatio, 1, true);
	}
	
	private void displayJumpStrength() {
		double jumpStrength = horse.getJumpStrength();
		double maxJumpStrength = HorseStatisticEnum.MAX_JUMP_STRENGTH.getValue(useVanillaStats);
		int jumpRatio = (int) ((jumpStrength / maxJumpStrength) * 100);
		zh.getMM().sendMessageAmountSpacer(s, LocaleEnum.jump, jumpRatio, 1, true);
	}
	
	private void displayChestSize() {
		if (horse instanceof ChestedHorse && ((ChestedHorse) horse).isCarryingChest()) {
			int strength = horse instanceof Llama ? ((Llama) horse).getStrength() : (int) HorseStatisticEnum.MAX_LLAMA_STRENGTH.getValue(useVanillaStats);
			int chestSize = strength * CHEST_SIZE_MULTIPLICATOR;
			zh.getMM().sendMessageAmountSpacer(s, LocaleEnum.strength, chestSize, 1, true);
		}
	}
	
	private void displayLocation() {
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
	
	private void displayStatus() {
		String status = "";
		if (zh.getDM().isHorseProtected(horse.getUniqueId())) {
			status += zh.getMM().getMessageSpacer(s, LocaleEnum.modeProtected, 0, true);
		}
		int spacer = status.isEmpty() ? 0 : 1;
		if (zh.getDM().isHorseLocked(horse.getUniqueId())) {
			status += zh.getMM().getMessageSpacer(s, LocaleEnum.modeLocked, spacer, true);
		}
		else if (zh.getDM().isHorseShared(horse.getUniqueId())) {
			status += zh.getMM().getMessageSpacer(s, LocaleEnum.modeShared, spacer, true);
		}
		if (!status.isEmpty()) {
			zh.getMM().sendMessageSpacerValue(s, LocaleEnum.status, 1, status, true);
		}
	}
	
}