package com.gmail.xibalbazedd.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Llama;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.HorseRecord;
import com.gmail.xibalbazedd.zhorse.database.HorseStatsRecord;
import com.gmail.xibalbazedd.zhorse.database.PlayerRecord;
import com.gmail.xibalbazedd.zhorse.enums.HorseStatisticEnum;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

public class CommandInfo extends AbstractCommand {
	
	private static final int CHEST_SIZE_MULTIPLICATOR = 3;

	public CommandInfo(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
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
						execute(p.getUniqueId(), horseID);
					}
				}
				else {
					sendCommandUsage();
				}
			}
			else {
				execute(targetUUID, horseID);
			}
		}
	}
	
	private void execute(UUID ownerUUID, String horseID) {
		if (isRegistered(ownerUUID, horseID)) {
			horse = zh.getHM().getHorse(ownerUUID, Integer.parseInt(horseID));
			if (isHorseLoaded(true)) {
				execute();
			}
		}
	}

	private void execute() {
		if (zh.getEM().canAffordCommand(p, command)) {
			HorseRecord horseRecord = zh.getDM().getHorseRecord(horse.getUniqueId());
			HorseStatsRecord statsRecord = new HorseStatsRecord(horse);
			PlayerRecord ownerRecord = zh.getDM().getPlayerRecord(UUID.fromString(horseRecord.getOwner()));
			
			displayInfoHeader(zh, s);
			displayHorseID(zh, s, horseRecord);
			displayNames(zh, s, horseRecord, ownerRecord);
			displayHealth(zh, s, statsRecord);
			displaySpeed(zh, s, statsRecord, useExactStats, useVanillaStats);
			displayJumpStrength(zh, s, statsRecord, useExactStats, useVanillaStats);
			displayChestSize(zh, s, horse, statsRecord);
			displayLocation(zh, s, horseRecord);
			displayStatus(zh, s, horseRecord);
			displayPrice(zh, s, horse);
			
			zh.getEM().payCommand(p, command);
		}
	}
	
	public static void displayInfoHeader(ZHorse zh, CommandSender s) {
		String rawHeader = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.HORSE_INFO_HEADER), true);
		zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HEADER_FORMAT) {{ setValue(rawHeader); }}, true);
	}
	
	private void displayHorseID(ZHorse zh, CommandSender s, HorseRecord horseRecord) {
		if (isOwner(false, true)) {
			String horseID = horseRecord.getId().toString();
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.ID) {{ setHorseID(horseID); setSpaceCount(1); }}, true);
		}
	}
	
	private void displayNames(ZHorse zh, CommandSender s, HorseRecord horseRecord, PlayerRecord ownerRecord) {
		String ownerName = ownerRecord.getName();
		String horseName = horseRecord.getName();
		zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.OWNER) {{ setPlayerName(ownerName); setSpaceCount(1); }}, true);
		zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.NAME) {{ setHorseName(horseName); setSpaceCount(1); }}, true);
	}
	
	public static void displayHealth(ZHorse zh, CommandSender s, HorseStatsRecord statsRecord) {
		int health = statsRecord.getHealth().intValue();
		int maxHealth = statsRecord.getMaxHealth().intValue();
		zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HEALTH) {{ setAmount(health); setMax(maxHealth); setSpaceCount(1); }}, true);
	}
	
	public static void displaySpeed(ZHorse zh, CommandSender s, HorseStatsRecord statsRecord, boolean useExactStats, boolean useVanillaStats) {
		Double speed = statsRecord.getSpeed();
		if (!useExactStats) {
			double maxSpeed = HorseStatisticEnum.MAX_SPEED.getValue(useVanillaStats);
			double speedRatio = speed / maxSpeed;
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.SPEED) {{ setAmount(speedRatio); setSpaceCount(1); setUsePercentage(true); }}, true);
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.SPEED) {{ setAmount(speed); setSpaceCount(1); setArithmeticPrecision(3); }}, true);
		}
	}
	
	public static void displayJumpStrength(ZHorse zh, CommandSender s, HorseStatsRecord statsRecord, boolean useExactStats, boolean useVanillaStats) {
		Double jumpStrength = statsRecord.getJumpStrength();
		if (!useExactStats) {
			double maxJumpStrength = HorseStatisticEnum.MAX_JUMP_STRENGTH.getValue(useVanillaStats);
			double jumpRatio = jumpStrength / maxJumpStrength;
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.JUMP) {{ setAmount(jumpRatio); setSpaceCount(1); setUsePercentage(true); }}, true);
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.JUMP) {{ setAmount(jumpStrength); setSpaceCount(1); setArithmeticPrecision(3); }}, true);
		}
	}
	
	public static void displayChestSize(ZHorse zh, CommandSender s, AbstractHorse horse, HorseStatsRecord statsRecord) {
		if (horse instanceof ChestedHorse && statsRecord.isCarryingChest()) {
			int strength = horse instanceof Llama ? statsRecord.getStrength() : (int) HorseStatisticEnum.MAX_LLAMA_STRENGTH.getValue();
			int chestSize = strength * CHEST_SIZE_MULTIPLICATOR;
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.STRENGTH) {{ setAmount(chestSize); setSpaceCount(1); }}, true);
		}
	}
	
	private void displayLocation(ZHorse zh, CommandSender s, HorseRecord horseRecord) {
		if (isNotOnHorse(true)) {
			int x = (int) Math.floor(horseRecord.getLocationX());
			int y = (int) Math.floor(horseRecord.getLocationY());
			int z = (int) Math.floor(horseRecord.getLocationZ());
			String world = horseRecord.getLocationWorld();
			String location = String.format("%d/%d/%d : %s", x, y, z, world);
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.LOCATION) {{ setSpaceCount(1); setValue(location); }}, true);
		}
	}
	
	private void displayStatus(ZHorse zh, CommandSender s, HorseRecord horseRecord) {
		String status = "";		
		if (horseRecord.isLocked()) {
			status += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.LOCKED) {{ setSpaceCount(0); }}, true);
		}
		else if (horseRecord.isShared()) {
			status += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.SHARED) {{ setSpaceCount(0); }}, true);
		}
		else {
			status += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.RESTRICTED) {{ setSpaceCount(0); }}, true);
		}
		int spaceCount = status.isEmpty() ? 0 : 1;
		if (horseRecord.isProtected()) {
			status += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.PROTECTED) {{ setSpaceCount(spaceCount); }}, true);
		}
		final String message = status;
		zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.STATUS) {{ setSpaceCount(1); setValue(message); }}, true);
	}
	
	public static void displayPrice(ZHorse zh, CommandSender s, AbstractHorse horse) {
		if (zh.getDM().isHorseForSale(horse.getUniqueId())) {
			int price = zh.getDM().getSalePrice(horse.getUniqueId());
			String currencySymbol = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.CURRENCY_SYMBOL), true);
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.PRICE) {{ setAmount(price); setCurrencySymbol(currencySymbol); setSpaceCount(1); }}, true);
		}
	}
	
}