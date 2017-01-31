package eu.reborn_minecraft.zhorse.utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;

public class HorseStats {
	
	private String uuid;
	private int age;
	private boolean canBreed;
	private boolean canPickupItems;
	private String color;
	private int domestication;
	private int fireTicks;
	private double health;
	private boolean isCustomNameVisible;
	private boolean isGlowing;
	private boolean isTamed;
	private double jumpStrength;
	private double maxHealth;
	private int noDamageTicks;
	private int remainingAir;
	private double speed;
	private int strength;
	private String style;
	private int ticksLived;
	private String type;

	public HorseStats(
			String uuid,
			int age,
			boolean canBreed,
			boolean canPickupItems,
			String color,
			int domestication,
			int fireTicks,
			double health,
			boolean isCustomNameVisible,
			boolean isGlowing,
			boolean isTamed,
			double jumpStrength,
			double maxHealth,
			int noDamageTicks,
			int remainingAir,
			double speed,
			int strength,
			String style,
			int ticksLived,
			String type)
	{
		this.uuid = uuid;
		this.age = age;
		this.canBreed = canBreed;
		this.canPickupItems = canPickupItems;
		this.color = color;
		this.domestication = domestication;
		this.fireTicks = fireTicks;
		this.health = health;
		this.isCustomNameVisible = isCustomNameVisible;
		this.isGlowing = isGlowing;
		this.isTamed = isTamed;
		this.jumpStrength = jumpStrength;
		this.maxHealth = maxHealth;
		this.noDamageTicks = noDamageTicks;
		this.remainingAir = remainingAir;
		this.speed = speed;
		this.strength = strength;
		this.style = style;
		this.ticksLived = ticksLived;
		this.type = type;
	}
	
	public HorseStats(AbstractHorse horse) {
		this.uuid = horse.getUniqueId().toString();
		this.age = horse.getAge();
		this.canBreed = horse.canBreed();
		this.canPickupItems = horse.getCanPickupItems();
		this.domestication = horse.getDomestication();
		this.fireTicks = horse.getFireTicks();
		this.health = horse.getHealth();
		this.isCustomNameVisible = horse.isCustomNameVisible();
		this.isGlowing = horse.isGlowing();
		this.isTamed = horse.isTamed();
		this.jumpStrength = horse.getJumpStrength();
		this.maxHealth = horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		this.noDamageTicks = horse.getNoDamageTicks();
		this.remainingAir = horse.getRemainingAir();
		this.speed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		this.ticksLived = horse.getTicksLived();
		this.type = horse.getType().name();
		
		switch (horse.getType()) {
		case HORSE:
			this.color = ((Horse) horse).getColor().name();
			this.style = ((Horse) horse).getStyle().name();
			break;
		case LLAMA:
			this.color = ((Llama) horse).getColor().name();
			this.strength = ((Llama) horse).getStrength();
		default:
			break;
		}
	}
	
	public String getUUID() {
		return uuid;
	}

	public int getAge() {
		return age;
	}

	public boolean canBreed() {
		return canBreed;
	}

	public boolean canPickupItems() {
		return canPickupItems;
	}

	public String getColor() {
		return color;
	}

	public int getDomestication() {
		return domestication;
	}

	public int getFireTicks() {
		return fireTicks;
	}

	public double getHealth() {
		return health;
	}

	public boolean isCustomNameVisible() {
		return isCustomNameVisible;
	}

	public boolean isGlowing() {
		return isGlowing;
	}

	public boolean isTamed() {
		return isTamed;
	}

	public double getJumpStrength() {
		return jumpStrength;
	}

	public double getMaxHealth() {
		return maxHealth;
	}

	public int getNoDamageTicks() {
		return noDamageTicks;
	}

	public int getRemainingAir() {
		return remainingAir;
	}

	public double getSpeed() {
		return speed;
	}

	public int getStrength() {
		return strength;
	}

	public String getStyle() {
		return style;
	}

	public int getTicksLived() {
		return ticksLived;
	}

	public String getType() {
		return type;
	}

}
