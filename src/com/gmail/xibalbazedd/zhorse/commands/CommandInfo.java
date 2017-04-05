package com.gmail.xibalbazedd.zhorse.commands;

import java.util.Locale;
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
		zh.getMM().sendMessageValue(s, LocaleEnum.HEADER_FORMAT, zh.getMM().getMessage(s, LocaleEnum.HORSE_INFO_HEADER, true), true);
	}
	
	private void displayHorseID(ZHorse zh, CommandSender s, HorseRecord horseRecord) {
		if (isOwner(false, true)) {
			String horseID = horseRecord.getId().toString();
			zh.getMM().sendMessageHorseIDSpacer(s, LocaleEnum.ID, horseID, 1, true);
		}
	}
	
	private void displayNames(ZHorse zh, CommandSender s, HorseRecord horseRecord, PlayerRecord ownerRecord) {
		String ownerName = ownerRecord.getName();
		String horseName = horseRecord.getName();
		zh.getMM().sendMessagePlayerSpacer(s, LocaleEnum.OWNER, ownerName, 1, true);
		zh.getMM().sendMessageHorseSpacer(s, LocaleEnum.NAME, horseName, 1, true);
	}
	
	public static void displayHealth(ZHorse zh, CommandSender s, HorseStatsRecord statsRecord) {
		int health = statsRecord.getHealth().intValue();
		int maxHealth = statsRecord.getMaxHealth().intValue();
		zh.getMM().sendMessageAmountMaxSpacer(s, LocaleEnum.HEALTH, health, maxHealth, 1, true);
	}
	
	public static void displaySpeed(ZHorse zh, CommandSender s, HorseStatsRecord statsRecord, boolean useExactStats, boolean useVanillaStats) {
		double speed = statsRecord.getSpeed();
		if (!useExactStats) {
			double maxSpeed = HorseStatisticEnum.MAX_SPEED.getValue(useVanillaStats);
			int speedRatio = (int) ((speed / maxSpeed) * 100);
			zh.getMM().sendMessageAmountSpacer(s, LocaleEnum.SPEED, speedRatio, 1, true);
		}
		else {
			String speedInfo = String.format(Locale.US, "%.3f", speed);
			zh.getMM().sendMessageSpacerValue(s, LocaleEnum.SPEED_EXACT, 1, speedInfo, true);
		}
	}
	
	public static void displayJumpStrength(ZHorse zh, CommandSender s, HorseStatsRecord statsRecord, boolean useExactStats, boolean useVanillaStats) {
		double jumpStrength = statsRecord.getJumpStrength();
		if (!useExactStats) {
			double maxJumpStrength = HorseStatisticEnum.MAX_JUMP_STRENGTH.getValue(useVanillaStats);
			int jumpRatio = (int) ((jumpStrength / maxJumpStrength) * 100);
			zh.getMM().sendMessageAmountSpacer(s, LocaleEnum.JUMP, jumpRatio, 1, true);
		}
		else {
			String jumpInfo = String.format(Locale.US, "%.3f", jumpStrength);
			zh.getMM().sendMessageSpacerValue(s, LocaleEnum.JUMP_EXACT, 1, jumpInfo, true);
		}
	}
	
	public static void displayChestSize(ZHorse zh, CommandSender s, AbstractHorse horse, HorseStatsRecord statsRecord) {
		if (horse instanceof ChestedHorse && statsRecord.isCarryingChest()) {
			int strength = horse instanceof Llama ? statsRecord.getStrength() : (int) HorseStatisticEnum.MAX_LLAMA_STRENGTH.getValue();
			int chestSize = strength * CHEST_SIZE_MULTIPLICATOR;
			zh.getMM().sendMessageAmountSpacer(s, LocaleEnum.STRENGTH, chestSize, 1, true);
		}
	}
	
	private void displayLocation(ZHorse zh, CommandSender s, HorseRecord horseRecord) {
		if (isNotOnHorse(true)) {
			int x = (int) Math.floor(horseRecord.getLocationX());
			int y = (int) Math.floor(horseRecord.getLocationY());
			int z = (int) Math.floor(horseRecord.getLocationZ());
			String world = horseRecord.getLocationWorld();
			String location = String.format("%d/%d/%d : %s", x, y, z, world);
			zh.getMM().sendMessageSpacerValue(s, LocaleEnum.LOCATION, 1, location, true);
		}
	}
	
	private void displayStatus(ZHorse zh, CommandSender s, HorseRecord horseRecord) {
		String status = "";
		if (horseRecord.isProtected()) {
			status += zh.getMM().getMessageSpacer(s, LocaleEnum.PROTECTED, 0, true);
		}
		int spacer = status.isEmpty() ? 0 : 1;
		if (horseRecord.isLocked()) {
			status += zh.getMM().getMessageSpacer(s, LocaleEnum.LOCKED, spacer, true);
		}
		else if (horseRecord.isShared()) {
			status += zh.getMM().getMessageSpacer(s, LocaleEnum.SHARED, spacer, true);
		}
		if (!status.isEmpty()) {
			zh.getMM().sendMessageSpacerValue(s, LocaleEnum.STATUS, 1, status, true);
		}
	}
	
	public static void displayPrice(ZHorse zh, CommandSender s, AbstractHorse horse) {
		if (zh.getDM().isHorseForSale(horse.getUniqueId())) {
			int price = zh.getDM().getSalePrice(horse.getUniqueId());
			String currencySymbol = zh.getMM().getMessage(s, LocaleEnum.CURRENCY_SYMBOL, true);
			zh.getMM().sendMessageAmountCurrencySpacer(s, LocaleEnum.PRICE, price, currencySymbol, 1, true);
		}
	}
	
}