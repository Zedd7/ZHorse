package eu.reborn_minecraft.zhorse.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryItemRecord {
	
	public static String LINE_SEPARATOR = "§";
	public static String DATA_SEPARATOR = ":";
	
	private String uuid;
	private Integer position;
	private Integer amount;
	private String displayName;
	private Integer durability;
	private Map<Enchantment, Integer> enchantments;
	private String localizedName;
	private List<String> lore;
	private String type;
	private Boolean unbreakable;

	public InventoryItemRecord(String uuid,
			Integer position,
			Integer amount,
			String displayName,
			Integer durability,
			String enchantmentsFormatted,
			String localizedName,
			String loreFormatted,
			String type,
			Boolean unbreakable)
	{
		this.uuid = uuid;
		this.position = position;
		this.amount = amount;
		this.displayName = displayName;
		this.durability = durability;
		this.localizedName = localizedName;
		this.type = type;
		this.unbreakable = unbreakable;
		
		if (enchantmentsFormatted != null) {
			enchantments = new HashMap<>();
			for (String enchantmentLine : enchantmentsFormatted.split(LINE_SEPARATOR)) {
				String[] enchantmentDescription = enchantmentLine.split(DATA_SEPARATOR);
				Enchantment enchantment = Enchantment.getByName(enchantmentDescription[0]);
				int level = Integer.valueOf(enchantmentDescription[1]);
				enchantments.put(enchantment, level);
			}
		}
		
		if (loreFormatted != null) {
			lore = new ArrayList<>();
			for (String loreLine : loreFormatted.split(LINE_SEPARATOR)) {
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
			ItemMeta itemMeta = item.getItemMeta();
			this.displayName = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : null;
			this.enchantments = itemMeta.hasEnchants() ? itemMeta.getEnchants() : null;
			this.localizedName = itemMeta.hasLocalizedName() ? itemMeta.getLocalizedName() : null;
			this.lore = itemMeta.hasLore() ? itemMeta.getLore() : null;
			this.unbreakable = itemMeta.isUnbreakable();
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
	
	public Map<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}
	
	public String getEnchantmentsFormatted() {
		if (enchantments != null) {
			String enchantmentsFormatted = "";
			Iterator<Entry<Enchantment, Integer>> itr = enchantments.entrySet().iterator();
			while (itr.hasNext()) {
				Entry<Enchantment, Integer> entry = itr.next();
				Enchantment enchantment = entry.getKey();
				Integer level = entry.getValue();
				String enchantmentDescription = enchantment.getName() + DATA_SEPARATOR + level.toString();
				enchantmentsFormatted += enchantmentDescription;
				if (itr.hasNext()) {
					enchantmentsFormatted += LINE_SEPARATOR;
				}
			}
			return enchantmentsFormatted;
		}
		return null;
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
			Iterator<String> itr = lore.iterator();
			while (itr.hasNext()) {
				loreFormatted += itr.next();
				if (itr.hasNext()) {
					loreFormatted += LINE_SEPARATOR;
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
		if (enchantments != null) {
			for (Enchantment enchantment : enchantments.keySet()) {
				itemMeta.addEnchant(enchantment, enchantments.get(enchantment), false);
			}
		}
		itemMeta.setLocalizedName(localizedName);
		itemMeta.setLore(lore);
		itemMeta.setUnbreakable(unbreakable != null && unbreakable);
		item.setItemMeta(itemMeta);
		return item;
	}

}
