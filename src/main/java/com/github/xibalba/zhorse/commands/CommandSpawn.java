package com.github.xibalba.zhorse.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import com.github.xibalba.zhorse.ZHorse;
import com.github.xibalba.zhorse.database.HorseInventoryRecord;
import com.github.xibalba.zhorse.database.HorseStatsRecord;
import com.github.xibalba.zhorse.enums.HorseStatisticEnum;
import com.github.xibalba.zhorse.enums.HorseVariantEnum;
import com.github.xibalba.zhorse.enums.LocaleEnum;
import com.github.xibalba.zhorse.utils.MessageConfig;

public class CommandSpawn extends AbstractCommand {
	
	private static final String DOUBLE_SEPARATOR = ":";
	
	private String style = null;
	private String color = null;
	private Boolean tamed = null;	
	private Boolean adult = null;
	private Boolean baby = null;	
	private Double health = null;
	private Double jumpStrength = null;
	private Double speed = null;
	private Integer strength = null;

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
			spawnHorse();
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_SPAWNED));
			zh.getCmdM().updateCommandHistory(s, command);
			zh.getEM().payCommand(p, command);
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
				if (!parsed) parsed = parseVariant(argument);
				if (!parsed) parsed = parseHorseStyle(argument);
				if (!parsed) parsed = parseColor(argument);
				if (!parsed) parsed = parseTamed(argument);
				if (!parsed) parsed = parseAdult(argument);
				if (!parsed) parsed = parseBaby(argument);
				if (!parsed) parsed = parseStats(argument);
				if (!parsed) parsed = parseLlamaStrength(argument);
				if (!parsed) {
					valid = false;
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_SPAWN_ARGUMENT) {{ setValue(argument); }});
				}
			}
		}
		return valid;
	}

	private boolean parseVariant(String argument) {
		if (horseVariant == null) {
			for (HorseVariantEnum horseVariantEnum : HorseVariantEnum.values()) {
				for (String horseVariantCode : horseVariantEnum.getCodeArray()) {
					if (argument.equalsIgnoreCase(horseVariantCode)) {
						horseVariant = horseVariantEnum;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean parseHorseStyle(String argument) {
		if (style == null) {
			for (Horse.Style horseStyle : Horse.Style.values()) {
				if (argument.equalsIgnoreCase(horseStyle.name())) {
					style = horseStyle.name();
					return true;
					}
				}
		}
		return false;
	}
	
	private boolean parseColor(String argument) {
		if (color == null) {
			for (Horse.Color horseColor : Horse.Color.values()) { // Llama.Color taken into account because it is a subset of Horse.Color
				if (argument.equalsIgnoreCase(horseColor.name())) {
					color = horseColor.name();
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean parseAdult(String argument) {
		if (adult == null) {
			if (argument.equalsIgnoreCase(HorseFlagEnum.ADULT.toString())) {
				adult = true;
				return true;
			}
		}
		return false;
	}
	
	private boolean parseBaby(String argument) {
		if (baby == null) {
			if (argument.equalsIgnoreCase(HorseFlagEnum.BABY.toString())) {
				baby = true;
				return true;
			}
		}
		return false;
	}

	private boolean parseTamed(String argument) {
		if (tamed == null) {
			if (argument.equalsIgnoreCase(HorseFlagEnum.TAMED.toString())) {
				tamed = true;
				return true;
			}
		}
		return false;
	}
	
	private boolean parseStats(String argument) {
		if (health == null && jumpStrength == null && speed == null) {
			if (StringUtils.countMatches(argument, DOUBLE_SEPARATOR) == 2) {
				Double[] stats = buildStats(argument);
				if (stats != null) {
					Double healthStat = stats[0];
					Double speedStat = stats[1];
					Double jumpStat = stats[2];
					if (healthStat != null) {
						if (!isStatHealthValid(healthStat)) return false;
						health = healthStat;
					}
					if (speedStat != null) {
						if (!isStatSpeedValid(speedStat)) return false;
						if (useExactStats) {
							speed = speedStat;
						}
						else {
							double maxSpeed = HorseStatisticEnum.MAX_SPEED.getValue(useVanillaStats);
							speed = (speedStat * maxSpeed) / 100;
						}
					}
					if (jumpStat != null) {
						if (!isStatJumpStrengthValid(jumpStat)) return false;
						if (useExactStats) {
							jumpStrength = jumpStat;
						}
						else {
							double maxJumpStrength = HorseStatisticEnum.MAX_JUMP_STRENGTH.getValue(useVanillaStats);
							jumpStrength = (jumpStat * maxJumpStrength) / 100;
						}
					}
					return true;
				}
			}
		}
		return false;
	}
	
	private Double[] buildStats(String argument) {
		argument = argument.replaceAll("%", "");
		int firstSeparatorIndex = argument.indexOf(DOUBLE_SEPARATOR);
		int secondSeparatorIndex = argument.indexOf(DOUBLE_SEPARATOR, firstSeparatorIndex + 1);
		String healthArg = argument.substring(0, firstSeparatorIndex);
		String speedArg = argument.substring(firstSeparatorIndex + 1, secondSeparatorIndex);
		String jumpArg = argument.substring(secondSeparatorIndex + 1);
		Double healthStat = null;
		Double speedStat = null;
		Double jumpStat = null;
		try {
			if (!healthArg.isEmpty()) {
				healthStat = Double.parseDouble(healthArg);
			}
			if (!speedArg.isEmpty()) {
				speedStat = Double.parseDouble(speedArg);
			}
			if (!jumpArg.isEmpty()) {
				jumpStat = Double.parseDouble(jumpArg);
			}
		} catch (NumberFormatException e) {
			return null;
		}
		return new Double[] {healthStat, speedStat, jumpStat};
	}
	
	private boolean parseLlamaStrength(String argument) {
		if (strength == null) {
			int llamaStrength;
			try {
				llamaStrength = Integer.parseInt(argument);
			} catch (NumberFormatException e) {
				return false;
			}
			if (!isStatLlamaStrengthValid(llamaStrength)) return false;
			strength = llamaStrength;
		}
		return true;
	}

	private void spawnHorse() {
		Location location = getGroundedLocation(p.getLocation());
		variant = horseVariant != null ? horseVariant.getEntityType().name() : null;
		HorseStatsRecord statsRecord = new HorseStatsRecord(
				null, null, null, null, color, null, null, null, health, null, null, null, tamed, jumpStrength, health, null, null, speed, strength, style, null, variant
		) {{ setAdult(adult); setBaby(baby); }} ;
		HorseInventoryRecord inventoryRecord = new HorseInventoryRecord();
		
		zh.getHM().spawnHorse(location, inventoryRecord, statsRecord, null, false);
	}
	
	private enum HorseFlagEnum {
		
		ADULT, BABY, TAMED	
	
	}

}
