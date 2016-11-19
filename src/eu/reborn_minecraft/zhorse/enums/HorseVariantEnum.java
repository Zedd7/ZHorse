package eu.reborn_minecraft.zhorse.enums;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;

public enum HorseVariantEnum {
	
	DONKEY(EntityType.DONKEY, "donkey"),
	HORSE(EntityType.HORSE, "horse"),
	LLAMA(EntityType.LLAMA, "llama", "lama"),
	MULE(EntityType.MULE, "mule"),
	SKELETON(EntityType.SKELETON_HORSE, "skeleton", "skel"),
	ZOMBIE(EntityType.ZOMBIE_HORSE, "zombie", "zomb", "undead");
	
	private EntityType entityType;
	private List<String> codeList = new ArrayList<>();
	
	HorseVariantEnum(final EntityType entityType, final String... codeArray) {
		this.entityType = entityType;
		for (String code : codeArray) {
			codeList.add(code);
		}
	}
	
	public EntityType getEntityType() {
		return entityType;
	}
	
	public List<String> getCodeList() {
		return codeList;
	}
	
	public static String[] getCodeArray() {		
		int arrayLength = 0;
		for (HorseVariantEnum variant : values()) {
			arrayLength += variant.getCodeList().size();
		}
				
		String[] codeArray = new String[arrayLength];
		int index = 0;
		for (HorseVariantEnum variant : values()) {
			for (String code : variant.getCodeList()) {
				codeArray[index] = code;
				index++;
			}
		}
		return codeArray;
	}

}