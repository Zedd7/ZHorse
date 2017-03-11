package eu.reborn_minecraft.zhorse.database;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;

public class HorseStatsRecord {
	
	private String uuid;
	private Integer age;
	private Boolean canBreed;
	private Boolean canPickupItems;
	private String color;
	private String customName;
	private Integer domestication;
	private Integer fireTicks;
	private Double health;
	private Boolean isCarryingChest;
	private Boolean isCustomNameVisible;
	private Boolean isGlowing;
	private Boolean isTamed;
	private Double jumpStrength;
	private Double maxHealth;
	private Integer noDamageTicks;
	private Integer remainingAir;
	private Double speed;
	private Integer strength;
	private String style;
	private Integer ticksLived;
	private String type;

	public HorseStatsRecord(
		String uuid,
		Integer age,
		Boolean canBreed,
		Boolean canPickupItems,
		String color,
		String customName,
		Integer domestication,
		Integer fireTicks,
		Double health,
		Boolean isCarryingChest,
		Boolean isCustomNameVisible,
		Boolean isGlowing,
		Boolean isTamed,
		Double jumpStrength,
		Double maxHealth,
		Integer noDamageTicks,
		Integer remainingAir,
		Double speed,
		Integer strength,
		String style,
		Integer ticksLived,
		String type)
	{
		this.uuid = uuid;
		this.age = age;
		this.canBreed = canBreed;
		this.canPickupItems = canPickupItems;
		this.color = color;
		this.customName = customName;
		this.domestication = domestication;
		this.fireTicks = fireTicks;
		this.health = health;
		this.isCarryingChest = isCarryingChest;
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
	
	public HorseStatsRecord(AbstractHorse horse) {
		this.uuid = horse.getUniqueId().toString();
		this.age = horse.getAge();
		this.canBreed = horse.canBreed();
		this.canPickupItems = horse.getCanPickupItems();
		this.customName = horse.getCustomName();
		this.domestication = horse.getDomestication();
		this.fireTicks = horse.getFireTicks();
		this.health = horse.getHealth();
		this.isCarryingChest = horse instanceof ChestedHorse ? ((ChestedHorse) horse).isCarryingChest() : false;
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

	public Integer getAge() {
		return age;
	}

	public Boolean canBreed() {
		return canBreed;
	}

	public Boolean canPickupItems() {
		return canPickupItems;
	}

	public String getColor() {
		return color;
	}
	
	public String getCustomName() {
		return customName;
	}

	public Integer getDomestication() {
		return domestication;
	}

	public Integer getFireTicks() {
		return fireTicks;
	}

	public Double getHealth() {
		return health;
	}
	
	public Boolean isCarryingChest() {
		return isCarryingChest;
	}

	public Boolean isCustomNameVisible() {
		return isCustomNameVisible;
	}

	public Boolean isGlowing() {
		return isGlowing;
	}

	public Boolean isTamed() {
		return isTamed;
	}

	public Double getJumpStrength() {
		return jumpStrength;
	}

	public Double getMaxHealth() {
		return maxHealth;
	}

	public Integer getNoDamageTicks() {
		return noDamageTicks;
	}

	public Integer getRemainingAir() {
		return remainingAir;
	}

	public Double getSpeed() {
		return speed;
	}

	public Integer getStrength() {
		return strength;
	}

	public String getStyle() {
		return style;
	}

	public Integer getTicksLived() {
		return ticksLived;
	}

	public String getType() {
		return type;
	}

}
