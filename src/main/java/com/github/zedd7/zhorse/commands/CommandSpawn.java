package com.github.zedd7.zhorse.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.database.HorseInventoryRecord;
import com.github.zedd7.zhorse.database.HorseStatsRecord;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.MessageConfig;

public class CommandSpawn extends AttributeParsingCommand {

	public CommandSpawn(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled()) {
			if (!idMode && !targetMode && (!variantMode || isRegistered(horseVariant))) {
				execute();
			}
			else {
				sendCommandUsage();
			}
		}
	}

	private void execute() {
		if (parseSpawnArguments()) {
			horse = spawnHorse();
			if (horse != null) {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_SPAWNED));
				zh.getCmdM().updateCommandHistory(s, command);
				zh.getEM().payCommand(p, command);
			}
			else {
				zh.getMM().sendMessage(s, ChatColor.RED + "It seems that horses cannot spawn here, please report this to the developer. (https://github.com/Zedd7/ZHorse/issues/new)");
			}
		}
		else {
			sendCommandUsage();
		}
	}

	private boolean parseSpawnArguments() {
		boolean valid = true;
		if (!args.isEmpty()) {
			for (String argument : args) { // Check for each token if it is some type of attribute
				boolean parsed = false;
				if (!parsed && horseVariant == null) parsed = parseVariant(argument);
				if (!parsed && style == null) parsed = parseHorseStyle(argument);
				if (!parsed && color == null) parsed = parseColor(argument);
				if (!parsed && tamed == null) parsed = parseTamed(argument);
				if (!parsed && adult == null) parsed = parseAdult(argument);
				if (!parsed && baby == null) parsed = parseBaby(argument);
				if (!parsed && health == null && speed == null && jumpStrength == null) parsed = parseStats(argument);
				if (!parsed && strength == null) parsed = parseLlamaStrength(argument);
				if (!parsed) {
					valid = false;
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_SPAWN_ARGUMENT) {{ setValue(argument); }});
				}
			}
		}
		return valid;
	}

	private AbstractHorse spawnHorse() {
		Location location = getGroundedLocation(p.getLocation());
		HorseInventoryRecord inventoryRecord = new HorseInventoryRecord();
		variant = horseVariant != null ? horseVariant.getEntityType().name() : null;
		HorseStatsRecord statsRecord = new HorseStatsRecord(
				null, null, null, null, color, null, null, null, health, null, null, null, tamed, jumpStrength, health, null, null, speed, strength, style, null, variant
		) {{ setAdult(adult); setBaby(baby); }} ;

		return zh.getHM().spawnHorse(location, inventoryRecord, statsRecord, null, false);
	}

}
