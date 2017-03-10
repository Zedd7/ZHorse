package eu.reborn_minecraft.zhorse.database;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryItemRecord {
	
	public static String LORE_SEPARATOR = "§";
	
	private String uuid;
	private Integer position;
	private Integer amount;
	private String displayName;
	private Integer durability;
	private String localizedName;
	private List<String> lore;
	private String type;
	private Boolean unbreakable;

	public InventoryItemRecord(String uuid, Integer position, Integer amount, String displayName, Integer durability, String localizedName, String loreFormatted, String type, Boolean unbreakable) {
		this.uuid = uuid;
		this.position = position;
		this.amount = amount;
		this.displayName = displayName;
		this.durability = durability;
		this.localizedName = localizedName;
		this.type = type;
		this.unbreakable = unbreakable;
		
		if (loreFormatted != null) {
			lore = new ArrayList<>();
			for (String loreLine : loreFormatted.split(";")) {
				lore.add(loreLine);
			}
		}
	}
	
	public InventoryItemRecord(String uuid, Integer position, ItemStack item) {
		this.uuid = uuid;
		this.position = position;
		this.amount = item.getAmount();
		this.durability = (int) item.getDurability();
		this.type = item.getType().name();
		
		if (item.hasItemMeta()) {
			this.displayName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null;
			this.localizedName = item.getItemMeta().hasLocalizedName() ? item.getItemMeta().getLocalizedName() : null;
			this.lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : null;
			this.unbreakable = item.getItemMeta().isUnbreakable();
		}
	}

	public String getUUID() {
		return uuid;
	}
	
	public Integer getPosition() {
		return position;
	}
	
	public Integer getAmount() {
		return amount;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public Integer getDurability() {
		return durability;
	}
	
	public String getLocalizedName() {
		return localizedName;
	}
	
	public List<String> getLore() {
		return lore;
	}
	
	public String getLoreFormatted() {
		if (lore != null) {
			String loreFormatted = "";
			for (int i = 0; i < lore.size(); i++) {
				loreFormatted += lore.get(i);
				if (i < lore.size() - 1) {
					loreFormatted += LORE_SEPARATOR;
				}
			}
			return loreFormatted;
		}
		return null;
	}
	
	public String getType() {
		return type;
	}
	
	public Boolean isUnbreakable() {
		return unbreakable;
	}
	
	public ItemStack getItem() {
		ItemStack item = new ItemStack(Material.valueOf(type), amount, durability.shortValue());
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(displayName);
		itemMeta.setLocalizedName(localizedName);
		itemMeta.setLore(lore);
		itemMeta.setUnbreakable(unbreakable != null && unbreakable);
		item.setItemMeta(itemMeta);
		return item;
	}

}
