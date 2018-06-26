package com.github.zedd7.zhorse.database;

import java.sql.Date;

import org.bukkit.entity.Player;

public class PendingMessageRecord {
	
	private String uuid;
	private Date date;
	private String message;
	
	public PendingMessageRecord (String uuid, Date date, String message) {
		this.uuid = uuid;
		this.date = date;
		this.message = message;
	}
	
	public PendingMessageRecord (Player player, Date date, String message) {
		this(player.getUniqueId().toString(), date, message);
	}
	
	public PendingMessageRecord (String uuid, String message) {
		this(uuid, new Date(System.currentTimeMillis()), message);
	}
	
	public PendingMessageRecord (Player player, String message) {
		this(player.getUniqueId().toString(), new Date(System.currentTimeMillis()), message);
	}

	public String getUUID() {
		return uuid;
	}
	
	public Date getDate() {
		return date;
	}

	public String getMessage() {
		return message;
	}

}
