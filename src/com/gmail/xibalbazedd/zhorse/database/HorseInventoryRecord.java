package com.gmail.xibalbazedd.zhorse.database;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HorseInventoryRecord {
	
	private String uuid;
	private List<InventoryItemRecord> itemRecordList;
	
	public HorseInventoryRecord(String uuid, List<InventoryItemRecord> itemRecordList) {
		this.uuid = uuid;
		this.itemRecordList = itemRecordList;
	}
	
	public HorseInventoryRecord(List<InventoryItemRecord> itemRecordList) {
		this(!itemRecordList.isEmpty() ? itemRecordList.get(0).getUUID() : "null", itemRecordList);
	}
	
	public HorseInventoryRecord(AbstractHorse horse) {
		uuid = horse.getUniqueId().toString();
		itemRecordList = new ArrayList<>();
		Inventory horseInventory = horse.getInventory();
		for (int slot = 0; slot < horseInventory.getSize(); slot++) {
			ItemStack item = horseInventory.getItem(slot);
			if (item != null) {
				itemRecordList.add(new InventoryItemRecord(uuid, slot, item));
			}
		}
	}
	
	public String getUUID() {
		return uuid;
	}
	
	public List<InventoryItemRecord> getItemRecordList() {
		return itemRecordList;
	}

}
