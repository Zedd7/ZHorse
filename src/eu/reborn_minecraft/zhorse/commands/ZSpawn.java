package eu.reborn_minecraft.zhorse.commands;

import java.util.HashMap;
import java.util.Map;

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
	private static final String[] SKELETON_ALIASES = {"skeleton", "skel"};
	private static final String[] UNDEAD_ALIASES = {"undead", "zombie", "zomb"};
	
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
		if (!argument.isEmpty()) {
			String[] argumentArray = argument.split(" "); // not using super.a to skip flags
			for (int i = 0; i < argumentArray.length; ++i) {
				String argument = argumentArray[i];
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
					parsed = parseVariant(argument);
				}
				if (!parsed) {
					parsed = parseStyle(argument);
				}
				if (!parsed) {
					parsed = parseColor(argument);
				}
				if (!parsed) {
					valid = false;
					zh.getMM().sendMessageValue(s, LocaleEnum.unknownSpawnArgument, argument);
				}
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
				Double[] doubles = buildDoubles(argument);
				if (doubles != null) {
					Double healthDouble = doubles[0];
					Double speedDouble = doubles[1];
					Double jumpDouble = doubles[2];
					System.out.println(healthDouble + " " + speedDouble + " " + jumpDouble);
					if (healthDouble != null) {
						if (healthDouble >= HorseManager.MIN_HEALTH && healthDouble <= HorseManager.MAX_HEALTH) {
							health = healthDouble;
						}
						else if (displayConsole) {
							valid = false;
							zh.getMM().sendMessageAmountMax(s, LocaleEnum.invalidHealthArgument, (int) HorseManager.MIN_HEALTH, (int) HorseManager.MAX_HEALTH);
						}
					}
					if (speedDouble != null) {
						speedDouble *= HorseManager.MAX_SPEED / 100;
						if (speedDouble >= HorseManager.MIN_SPEED && speedDouble <= HorseManager.MAX_SPEED) {
							speed = speedDouble;
						}
						else if (displayConsole) {
							valid = false;
							zh.getMM().sendMessageAmountMax(s, LocaleEnum.invalidSpeedArgument, (int) HorseManager.MIN_SPEED * 100, (int) HorseManager.MAX_SPEED * 100);
						}
					}
					if (jumpDouble != null) {
						jumpDouble *= HorseManager.MAX_JUMP_STRENGTH / 100;
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
			else {
				valid = false;
			}
		}
		return false;
	}
	
	private Double[] buildDoubles(String argument) {
		int firstSeparatorIndex = argument.indexOf(DOUBLE_SEPARATOR);
		int secondSeparatorIndex = argument.indexOf(DOUBLE_SEPARATOR, firstSeparatorIndex + 1);
		String healthArg = argument.substring(0, firstSeparatorIndex);
		String speedArg = argument.substring(firstSeparatorIndex + 1, secondSeparatorIndex);
		String jumpArg = argument.substring(secondSeparatorIndex + 1);		
		healthArg.replace("%", "");
		speedArg.replace("%", "");
		jumpArg.replace("%", "");
		Double healthDouble = null;
		Double speedDouble = null;
		Double jumpDouble = null;
		try {
			if (!healthArg.isEmpty()) {
				healthDouble = Double.parseDouble(healthArg);
			}
			if (!speedArg.isEmpty()) {
				speedDouble = Double.parseDouble(speedArg);
			}
			if (!jumpArg.isEmpty()) {
				jumpDouble = Double.parseDouble(jumpArg);
			}
		} catch (NumberFormatException e) {
			return null;
		}
		return new Double[] {healthDouble, speedDouble, jumpDouble};
	}

	private boolean parseVariant(String argument) {
		Map<String, Variant> variantMap = buildVariant();
		for (String existingVariant : variantMap.keySet()) {
			if (argument.equalsIgnoreCase(existingVariant)) {
				if (variant == null) {
					variant = variantMap.get(existingVariant);
					return true;
				}
				else {
					valid = false;
				}
			}
		}
		return false;
	}
	
	private Map<String, Variant> buildVariant() {
		Map<String, Variant> variantMap = new HashMap<String, Variant>();
		for (Variant variant : Variant.values()) {
			variantMap.put(variant.name(), variant);
		}
		for (String skeletonAliase : SKELETON_ALIASES) {
			variantMap.put(skeletonAliase, Variant.SKELETON_HORSE);
		}
		for (String undeadAliase : UNDEAD_ALIASES) {
			variantMap.put(undeadAliase, Variant.UNDEAD_HORSE);
		}
		return variantMap;
	}
	
	private boolean parseStyle(String argument) {
		for (Style existingStyle : Style.values()) {
			if (argument.equalsIgnoreCase(existingStyle.name())) {
				if (style == null) {
					style = existingStyle;
					return true;
				}
				else {
					boolean matchColor = false;
					for (Color existingColor : Color.values()) {
						if (argument.equalsIgnoreCase(existingColor.name())) {
							matchColor = true;
							break;
						}
					}
					if (!matchColor) {
						valid = false;
					}
				}
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
					boolean matchStyle = false;
					for (Style existingStyle : Style.values()) {
						if (argument.equalsIgnoreCase(existingStyle.name())) {
							matchStyle = true;
							break;
						}
					}
					if (!matchStyle) {
						valid = false;
					}
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
		if (variant == null) {
			variant = (style == null && color == null) ? horse.getVariant() : Variant.HORSE;
		}
		if (style == null) {
			style = horse.getStyle();
		}
		if (color == null) {
			color = horse.getColor();
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
		horse.setVariant(variant);
		horse.setStyle(style);
		horse.setColor(color);
	}

}
