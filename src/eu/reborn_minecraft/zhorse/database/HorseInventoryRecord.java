package eu.reborn_minecraft.zhorse.database;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.inventory.Inventory;

import eu.reborn_minecraft.zhorse.utils.InventorySerializer;

public class HorseInventoryRecord {
	
	private String uuid;
	private String inventoryData;

	public HorseInventoryRecord(String uuid, String inventoryData) {
		this.uuid = uuid;
		this.inventoryData = inventoryData;
	}
	
	public HorseInventoryRecord(UUID horseUUID, Inventory inventory) {
		uuid = horseUUID.toString();
		inventoryData = InventorySerializer.toBase64(inventory);
	}
	
	public HorseInventoryRecord(AbstractHorse horse) {
		uuid = horse.getUniqueId().toString();
		inventoryData = InventorySerializer.toBase64(horse.getInventory());
	}
	
	public String getUUID() {
		return uuid;
	}

	public Inventory getInventory() {
		Inventory inventory = null;
		try {
			inventory = InventorySerializer.fromBase64(inventoryData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inventory;
	}
	
	public String getInventoryData() {
		return inventoryData;
	}

}
