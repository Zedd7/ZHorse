package com.github.xibalba.zhorse.database;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class InventoryItemRecord {

	private static final String SERIAL_KEY = "item";

	private String uuid;
	private Integer slot;
	private String serial;

	public InventoryItemRecord(String uuid, Integer slot, String serial) {
		this.uuid = uuid;
		this.slot = slot;
		this.serial = serial;
	}

	public InventoryItemRecord(String uuid, Integer slot, ItemStack item) {
		this.uuid = uuid;
		this.slot = slot;

		YamlConfiguration serialConfig = new YamlConfiguration();
		serialConfig.set(SERIAL_KEY, item);
		serial = serialConfig.saveToString();
	}

	public String getUUID() {
		return uuid;
	}

	public Integer getSlot() {
		return slot;
	}

	public String getSerial() {
		return serial;
	}

	public ItemStack getItem() {
		YamlConfiguration serialConfig = new YamlConfiguration();
		try {
			serialConfig.loadFromString(serial);
		} catch (InvalidConfigurationException e) {}
		return (ItemStack) serialConfig.get(SERIAL_KEY);
	}

}
