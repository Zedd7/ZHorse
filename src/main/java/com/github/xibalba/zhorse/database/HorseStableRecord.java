package com.github.xibalba.zhorse.database;

import org.bukkit.Location;

public class HorseStableRecord {
	
	private String uuid;
	private String locationWorld;
	private Integer locationX;
	private Integer locationY;
	private Integer locationZ;
	
	public HorseStableRecord(
		String uuid,
		String locationWorld,
		Integer locationX,
		Integer locationY,
		Integer locationZ)
	{
		this.uuid = uuid;
		this.locationWorld = locationWorld;
		this.locationX = locationX;
		this.locationY = locationY;
		this.locationZ = locationZ;
	}
	
	public HorseStableRecord(
		String uuid,
		Location location)
	{
		this(uuid, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public String getUUID() {
		return uuid;
	}
	
	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public String getLocationWorld() {
		return locationWorld;
	}

	public Integer getLocationX() {
		return locationX;
	}

	public Integer getLocationY() {
		return locationY;
	}

	public Integer getLocationZ() {
		return locationZ;
	}

}
