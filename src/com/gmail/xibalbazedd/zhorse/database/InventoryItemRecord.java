package com.gmail.xibalbazedd.zhorse.database;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public class InventoryItemRecord {
	
	private static final String KEY_VALUE_SEPARATOR = "->";
	private static final String ENTRY_SEPARATOR = "§";
	
	private String uuid;
	private Integer slot;
	private Map<String, Object> serial;

	public InventoryItemRecord(String uuid, Integer slot, String data) {
		this.uuid = uuid;
		this.slot = slot;
		
		serial = new HashMap<>();
		for (String entry : data.split(ENTRY_SEPARATOR)) {
			String key = entry.split(KEY_VALUE_SEPARATOR)[0];
			Object value = entry.split(KEY_VALUE_SEPARATOR)[1];
			serial.put(key, value);
		}
	}
	
	public InventoryItemRecord(String uuid, Integer slot, ItemStack item) {
		this.uuid = uuid;
		this.slot = slot;
		this.serial = item.serialize();
	}

	public String getUUID() {
		return uuid;
	}
	
	public Integer getSlot() {
		return slot;
	}
	
	public String getData() {
		String data = "";
		for (String key : serial.keySet()) {
			data += key + KEY_VALUE_SEPARATOR + serial.get(key) + ENTRY_SEPARATOR;
		}
		return data;
	}
	
	public Map<String, Object> getSerial() {
		return serial;
	}
	
	public ItemStack getItem() {
		return ItemStack.deserialize(serial);
	}

}
