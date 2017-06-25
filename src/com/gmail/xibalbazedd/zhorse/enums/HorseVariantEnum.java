package com.gmail.xibalbazedd.zhorse.enums;

import org.bukkit.entity.EntityType;

public enum HorseVariantEnum {
	
	DONKEY(EntityType.DONKEY, "donkey"),
	HORSE(EntityType.HORSE, "horse"),
	LLAMA(EntityType.LLAMA, "llama", "lama"),
	MULE(EntityType.MULE, "mule"),
	SKELETON(EntityType.SKELETON_HORSE, "skeleton", "skel"),
	ZOMBIE(EntityType.ZOMBIE_HORSE, "zombie", "zomb", "undead");
	
	private EntityType entityType;
	private String[] codeArray;
	
	HorseVariantEnum(final EntityType entityType, final String... codeArray) {
		this.entityType = entityType;
		this.codeArray = codeArray;
	}
	
	public EntityType getEntityType() {
		return entityType;
	}
	
	public String[] getCodeArray() {
		return codeArray;
	}
	
	public static String[] getAllCodeArray() {		
		int arrayLength = 0;
		for (HorseVariantEnum variant : values()) {
			arrayLength += variant.getCodeArray().length;
		}
				
		String[] allCodeArray = new String[arrayLength];
		int index = 0;
		for (HorseVariantEnum variant : values()) {
			for (String code : variant.getCodeArray()) {
				allCodeArray[index] = code;
				index++;
			}
		}
		return allCodeArray;
	}
	
	public static HorseVariantEnum from(EntityType entityType) {
		for (HorseVariantEnum horseVariantEnum : values()) {
			if (horseVariantEnum.getEntityType().equals(entityType)) {
				return horseVariantEnum;
			}
		}
		return null;
	}

}