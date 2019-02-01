package com.github.zedd7.zhorse.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.enums.HorseStatisticEnum;
import com.github.zedd7.zhorse.enums.HorseVariantEnum;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.MessageConfig;

public class AttributeParsingCommand extends AbstractCommand {

	private static final String DOUBLE_SEPARATOR = ":";

	protected String style = null;
	protected String color = null;
	protected Boolean tamed = null;
	protected Boolean adult = null;
	protected Boolean baby = null;
	protected Double health = null;
	protected Double speed = null;
	protected Double jumpStrength = null;
	protected Integer strength = null;

	public AttributeParsingCommand(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
	}

	protected boolean parseVariant(String argument) {
		for (HorseVariantEnum horseVariantEnum : HorseVariantEnum.values()) {
			for (String horseVariantCode : horseVariantEnum.getCodeArray()) {
				if (argument.equalsIgnoreCase(horseVariantCode)) {
					horseVariant = horseVariantEnum;
					return true;
				}
			}
		}
		return false;
	}

	protected boolean parseHorseStyle(String argument) {
		for (Horse.Style horseStyle : Horse.Style.values()) {
			if (argument.equalsIgnoreCase(horseStyle.name())) {
				style = horseStyle.name();
				return true;
			}
		}
		return false;
	}

	protected boolean parseColor(String argument) {
		for (Horse.Color horseColor : Horse.Color.values()) { // Llama.Color taken into account because it is a subset of Horse.Color
			if (argument.equalsIgnoreCase(horseColor.name())) {
				color = horseColor.name();
				return true;
			}
		}
		return false;
	}

	protected boolean parseAdult(String argument) {
		if (argument.equalsIgnoreCase(HorseFlagEnum.ADULT.toString())) {
			adult = true;
			return true;
		}
		return false;
	}

	protected boolean parseBaby(String argument) {
		if (argument.equalsIgnoreCase(HorseFlagEnum.BABY.toString())) {
			baby = true;
			return true;
		}
		return false;
	}

	protected boolean parseTamed(String argument) {
		if (argument.equalsIgnoreCase(HorseFlagEnum.TAMED.toString())) {
			tamed = true;
			return true;
		}
		return false;
	}

	protected boolean parseStats(String argument) {
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
		return false;
	}

	protected boolean parseLlamaStrength(String argument) {
		int llamaStrength;
		try {
			llamaStrength = Integer.parseInt(argument);
		} catch (NumberFormatException e) {
			return false;
		}
		if (!isStatLlamaStrengthValid(llamaStrength)) return false;
		strength = llamaStrength;
		return true;
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

	private boolean isStatHealthValid(double health) {
		double minHealth = HorseStatisticEnum.MIN_HEALTH.getValue(useVanillaStats);
		double maxHealth = HorseStatisticEnum.MAX_HEALTH.getValue(useVanillaStats);
		if (adminMode || (health >= minHealth && health <= maxHealth)) {
			return true;
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_HEALTH_ARGUMENT) {{ setAmount((int) minHealth); setMax((int) maxHealth); }});
			return false;
		}
	}

	private boolean isStatSpeedValid(double speed) {
		if (adminMode) return true;

		double minSpeed = HorseStatisticEnum.MIN_SPEED.getValue(useVanillaStats);
		double maxSpeed = HorseStatisticEnum.MAX_SPEED.getValue(useVanillaStats);
		if (useExactStats) {
			if (speed >= minSpeed && speed <= maxSpeed) {
				return true;
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_SPEED_ARGUMENT) {{ setAmount(minSpeed); setMax(maxSpeed); setArithmeticPrecision(3); }});
				return false;
			}
		}
		else {
			if (speed >= (minSpeed / maxSpeed) * 100 && speed <= 100) {
				return true;
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_SPEED_ARGUMENT) {{ setAmount(minSpeed / maxSpeed); setMax(1); setUsePercentage(true); }});
				return false;
			}
		}
	}

	private boolean isStatJumpStrengthValid(double jumpStrength) {
		if (adminMode) return true;

		double minJumpStrength = HorseStatisticEnum.MIN_JUMP_STRENGTH.getValue(useVanillaStats);
		double maxJumpStrength = HorseStatisticEnum.MAX_JUMP_STRENGTH.getValue(useVanillaStats);
		if (useExactStats) {
			if (jumpStrength >= minJumpStrength && jumpStrength <= maxJumpStrength) {
				return true;
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_JUMP_ARGUMENT) {{ setAmount(minJumpStrength); setMax(maxJumpStrength); setArithmeticPrecision(3); }});
				return false;
			}
		}
		else {
			if (jumpStrength >= (minJumpStrength / maxJumpStrength) * 100 && jumpStrength <= 100) {
				return true;
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_JUMP_ARGUMENT) {{ setAmount(minJumpStrength / maxJumpStrength); setMax(1); setUsePercentage(true); }});
				return false;
			}
		}
	}

	private boolean isStatLlamaStrengthValid(int llamaStrenth) {
		int minLlamaStrength = (int) HorseStatisticEnum.MIN_LLAMA_STRENGTH.getValue(useVanillaStats);
		int maxLlamaStrength = (int) HorseStatisticEnum.MAX_LLAMA_STRENGTH.getValue(useVanillaStats);
		if (adminMode || (llamaStrenth >= minLlamaStrength && llamaStrenth <= maxLlamaStrength)) {
			return true;
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_STRENGTH_ARGUMENT) {{ setAmount(minLlamaStrength); setMax(maxLlamaStrength); }});
			return false;
		}
	}

	private enum HorseFlagEnum {

		ADULT, BABY, TAMED

	}

}
