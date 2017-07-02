package com.github.xibalba.zhorse.enums;

public enum HorseStatisticEnum {
	
	MIN_HEALTH(15.0, 1.0),
	MIN_SPEED(0.1125, 0.0),
	MIN_JUMP_STRENGTH(0.4, 0.0),
	MIN_LLAMA_STRENGTH(1.0, 1.0),
	
	MAX_HEALTH(30.0, 30.0),
	MAX_SPEED(0.3375, 1.0),
	MAX_JUMP_STRENGTH(1.0, 2.0),
	MAX_LLAMA_STRENGTH(5.0, 5.0);
	
	private double vanillaValue;
	private double bukkitValue;
	
	private HorseStatisticEnum(double vanillaValue, double bukkitValue) {
		this.vanillaValue = vanillaValue;
		this.bukkitValue = bukkitValue;
	}
	
	public double getVanillaValue() {
		return vanillaValue;
	}
	
	public double getBukkitValue() {
		return bukkitValue;
	}
	
	public double getValue() { // Assumes identical stats for vanilla and Bukkit
		return vanillaValue;
	}
	
	public double getValue(boolean vanilla) {
		return vanilla ? vanillaValue : bukkitValue;
	}

}
