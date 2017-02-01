package eu.reborn_minecraft.zhorse.database;

import org.bukkit.Location;

public class HorseRecord {
	
	private String uuid;
	private String owner;
	private Integer id;
	private String name;
	private Boolean modeLocked;
	private Boolean modeProtected;
	private Boolean modeShared;
	private String locationWorld;
	private Integer locationX;
	private Integer locationY;
	private Integer locationZ;
	
	public HorseRecord(
		String uuid,
		String owner,
		Integer id,
		String name,
		Boolean modeLocked,
		Boolean modeProtected,
		Boolean modeShared,
		String locationWorld,
		Integer locationX,
		Integer locationY,
		Integer locationZ)
	{
		this.uuid = uuid;
		this.owner = owner;
		this.id = id;
		this.name = name;
		this.modeLocked = modeLocked;
		this.modeProtected = modeProtected;
		this.modeShared = modeShared;
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
		Boolean modeLocked,
		Boolean modeProtected,
		Boolean modeShared,
		Location location)
	{
		this(uuid, owner, id, name, modeLocked, modeProtected, modeShared, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public String getUUID() {
		return uuid;
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

	public Boolean getModeLocked() {
		return modeLocked;
	}

	public Boolean getModeProtected() {
		return modeProtected;
	}

	public Boolean getModeShared() {
		return modeShared;
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
