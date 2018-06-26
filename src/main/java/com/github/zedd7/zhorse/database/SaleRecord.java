package com.github.zedd7.zhorse.database;

public class SaleRecord {
	
	private String uuid;
	private Integer price;
	
	public SaleRecord(String uuid, Integer price) {
		this.uuid = uuid;
		this.price = price;
	}

	public String getUUID() {
		return uuid;
	}
	
	public Integer getPrice() {
		return price;
	}

}
