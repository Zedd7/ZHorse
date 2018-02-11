package com.github.xibalba.zhorse.database;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.xibalba.zhorse.enums.KeyWordEnum;

public class HorseInventoryRecord {

	private static final String SIZE_KEY = "size";
	private static final String ITEMS_KEY = "items";

	private String uuid;
	private String serial;

	public HorseInventoryRecord(String uuid, String serial) {
		this.uuid = uuid;
		this.serial = serial;
	}

	public HorseInventoryRecord(String uuid) {
		this(uuid, null);
	}

	public HorseInventoryRecord() {
		this(null, null);
	}

	public HorseInventoryRecord(AbstractHorse horse) {
		uuid = horse.getUniqueId().toString();

		YamlConfiguration serialConfig = new YamlConfiguration();
		Inventory horseInventory = horse.getInventory();
		serialConfig.set(SIZE_KEY, horseInventory.getSize());
		for (int slot = 0; slot < horseInventory.getSize(); slot++) {
			ItemStack item = horseInventory.getItem(slot);
			if (item != null) {
				serialConfig.set(ITEMS_KEY + KeyWordEnum.DOT.getValue() + Integer.toString(slot), item);
			}
		}
		serial = serialConfig.saveToString();
	}

	public String getUUID() {
		return uuid;
	}

	public String getSerial() {
		return serial;
	}

	public ItemStack[] getItems() {
		try {
			YamlConfiguration serialConfig = new YamlConfiguration();
			serialConfig.loadFromString(serial);
			int inventorySize = serialConfig.getInt(SIZE_KEY);
			ItemStack[] items = new ItemStack[inventorySize];
			for (int slot = 0; slot < inventorySize; slot++) {
				ItemStack item = serialConfig.getItemStack(ITEMS_KEY + KeyWordEnum.DOT.getValue() + Integer.toString(slot), null);
				items[slot] = item;
			}
			return items;
		} catch (InvalidConfigurationException e) {
			return null;
		}
	}

}
