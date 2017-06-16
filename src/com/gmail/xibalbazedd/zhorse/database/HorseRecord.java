package com.gmail.xibalbazedd.zhorse.database;

import org.bukkit.Location;

public class HorseRecord {
	
	private String uuid;
	private String owner;
	private Integer id;
	private String name;
	private Boolean locked;
	private Boolean protected_;
	private Boolean shared;
	private String locationWorld;
	private Integer locationX;
	private Integer locationY;
	private Integer locationZ;
	
	public HorseRecord(
		String uuid,
		String owner,
		Integer id,
		String name,
		Boolean locked,
		Boolean protected_,
		Boolean shared,
		String locationWorld,
		Integer locationX,
		Integer locationY,
		Integer locationZ)
	{
		this.uuid = uuid;
		this.owner = owner;
		this.id = id;
		this.name = name;
		this.locked = locked;
		this.protected_ = protected_;
		this.shared = shared;
		this.locationWorld = locationWorld;
		this.locationX = locationX;
		this.locationY = locationY;
		this.locationZ = locationZ;
	}
	
	public HorseRecord(
		String uuid,
		String owner,
		Integer id,
		String name,
		Boolean locked,
		Boolean protected_,
		Boolean shared,
		Location location)
	{
		this(uuid, owner, id, name, locked, protected_, shared, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public String getUUID() {
		return uuid;
	}
	
	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public String getOwner() {
		return owner;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Boolean isLocked() {
		return locked;
	}

	public Boolean isProtected() {
		return protected_;
	}

	public Boolean isShared() {
		return shared;
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
