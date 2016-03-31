package eu.reborn_minecraft.zhorse.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;
import eu.reborn_minecraft.zhorse.managers.HorseManager;

public class ZSpawn extends Command {
	
	private static final String BABY = "baby";
	private static final String TAMED = "tamed";
	private static final String DOUBLE_SEPARATOR = ":";
	
	private boolean valid = true;
	
	private boolean baby = false;
	private boolean tamed = false;
	private double health = -1;
	private double jumpStrength = -1;
	private double speed = -1;
	private Color color = null;
	private Variant variant = null;
	private Style style = null;

	public ZSpawn(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			if (!(idMode || targetMode)) {
				execute();
			}
			else {
				sendCommandUsage();
			}				
		}
	}

	private void execute() {
		if (zh.getEM().canAffordCommand(p, command)) {
			Location location = p.getLocation();
			parseArguments();
			if (valid) {
				Horse horse = (Horse) location.getWorld().spawnEntity(location, EntityType.HORSE);
				customize(horse);
				if (displayConsole) {
					zh.getMM().sendMessage(s, LocaleEnum.horseSpawned);
				}
				zh.getEM().payCommand(p, command);
			}
			else {
				sendCommandUsage();
			}
		}
	}

	private void parseArguments() {
		for (int i = 1; i < a.length; ++i) {
			String argument = a[i];
			boolean parsed = false;
			if (!parsed) {
				parsed = parseBaby(argument);
			}
			if (!parsed) {
				parsed = parseTamed(argument);
			}
			if (!parsed) {
				parsed = parseDoubles(argument);
			}
			if (!parsed) {
				parseColor(argument);
			}
			if (!parsed) {
				parseVariant(argument);
			}
			if (!parsed) {
				parseStyle(argument);
			}
			if (!parsed) { // if the argument was not used
				valid = false;
				zh.getMM().sendMessageValue(s, LocaleEnum.unknownSpawnArgument, argument);
			}
		}
	}
	
	private boolean parseBaby(String argument) {
		if (argument.equalsIgnoreCase(BABY)) {
			if (!baby) {
				baby = true;
				return true;
			}
			else {
				valid = false;
			}
		}
		return false;
	}

	private boolean parseTamed(String argument) {
		if (argument.equalsIgnoreCase(TAMED)) {
			if (!tamed) {
				tamed = true;
				return true;
			}
			else {
				valid = false;
			}
		}
		return false;
	}
	
	private boolean parseDoubles(String argument) {
		if (StringUtils.countMatches(argument, DOUBLE_SEPARATOR) == 2) {
			if (health == -1 && jumpStrength == -1 && speed == -1) {
				String[] args = argument.split(DOUBLE_SEPARATOR);
				if (args.length < 3) { // if jump field was empty and thus skipped
					args = new String[] {args[0], args[1], ""};
				}
				String healthArg = args[0].replace("%", "");
				String speedArg = args[1].replace("%", "");
				String jumpArg = args[2].replace("%", "");
				if (!healthArg.isEmpty() && StringUtils.isNumeric(healthArg)) {
					double healthDouble = Double.parseDouble(healthArg);
					if (healthDouble >= HorseManager.MIN_HEALTH && healthDouble <= HorseManager.MAX_HEALTH) {
						health = healthDouble;
					}
					else if (displayConsole) {
						valid = false;
						zh.getMM().sendMessageAmountMax(s, LocaleEnum.invalidHealthArgument, (int) HorseManager.MIN_HEALTH, (int) HorseManager.MAX_HEALTH);
					}
				}
				if (!speedArg.isEmpty() && StringUtils.isNumeric(speedArg)) {
					double speedDouble = Double.parseDouble(speedArg) * HorseManager.MAX_SPEED / 100;
					if (speedDouble >= HorseManager.MIN_SPEED && speedDouble <= HorseManager.MAX_SPEED) {
						speed = speedDouble;
					}
					else if (displayConsole) {
						valid = false;
						zh.getMM().sendMessageAmountMax(s, LocaleEnum.invalidSpeedArgument, (int) HorseManager.MIN_SPEED * 100, (int) HorseManager.MAX_SPEED * 100);
					}
				}
				if (!jumpArg.isEmpty() && StringUtils.isNumeric(jumpArg)) {
					double jumpDouble = Double.parseDouble(jumpArg) * HorseManager.MAX_JUMP_STRENGTH / 100;
					if (jumpDouble >= HorseManager.MIN_JUMP_STRENGTH && jumpDouble <= HorseManager.MAX_JUMP_STRENGTH) {
						jumpStrength = jumpDouble;
					}
					else if (displayConsole) {
						valid = false;
						zh.getMM().sendMessageAmountMax(s, LocaleEnum.invalidJumpArgument, (int) HorseManager.MIN_JUMP_STRENGTH * 100, (int) HorseManager.MAX_JUMP_STRENGTH * 100);
					}
				}
				return true;
			}
			else {
				valid = false;
			}
		}
		return false;
	}
	
	private boolean parseColor(String argument) {
		for (Color existingColor : Color.values()) {
			if (argument.equalsIgnoreCase(existingColor.name())) {
				if (color == null) {
					color = existingColor;
					return true;
				}
				else {
					valid = false;
				}
			}
		}
		return false;
	}
	
	private boolean parseVariant(String argument) {
		for (Variant existingVariant : Variant.values()) {
			if (argument.equalsIgnoreCase(existingVariant.name())) {
				if (variant == null) {
					variant = existingVariant;
					return true;
				}
				else {
					valid = false;
				}
			}
		}
		return false;
	}
	
	private boolean parseStyle(String argument) {
		for (Style existingStyle : Style.values()) {
			if (argument.equalsIgnoreCase(existingStyle.name())) {
				if (style == null) {
					style = existingStyle;
					return true;
				}
				else {
					valid = false;
				}
			}
		}
		return false;
	}

	private void customize(Horse horse) {
		if (health == -1) {
			health = horse.getHealth();
		}
		if (jumpStrength == -1) {
			jumpStrength = horse.getJumpStrength();
		}
		if (speed == -1) {
			speed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		}
		if (color == null) {
			color = horse.getColor();
		}
		if (variant == null) {
			variant = horse.getVariant();
		}
		if (style == null) {
			style = horse.getStyle();
		}		
		horse.setOwner(p);
		horse.setRemoveWhenFarAway(false);
		horse.setMaxHealth(health);
		horse.setHealth(health);
		if (baby) {
			horse.setBaby();
		}
		else {
			horse.setAdult();
		}
		horse.setTamed(tamed);
		horse.setJumpStrength(jumpStrength);
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
		horse.setColor(color);
		horse.setVariant(variant);
		horse.setStyle(style);
	}

}
