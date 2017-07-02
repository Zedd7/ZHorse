package com.github.xibalba.zhorse.database;

import java.sql.Date;

import org.bukkit.entity.AbstractHorse;

public class HorseDeathRecord {
	
	private String uuid;
	private Date date;
	
	public HorseDeathRecord (String uuid, Date date) {
		this.uuid = uuid;
		this.date = date;
	}
	
	public HorseDeathRecord (AbstractHorse horse, Date date) {
		this(horse.getUniqueId().toString(), date);
	}
	
	public HorseDeathRecord (String uuid) {
		this(uuid, new Date(System.currentTimeMillis()));
	}
	
	public String getUUID() {
		return uuid;
	}
	
	public Date getDate() {
		return date;
	}

}
