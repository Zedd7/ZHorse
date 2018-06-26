package com.github.zedd7.zhorse.utils;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.zedd7.zhorse.ZHorse;

public abstract class YamlResourceValidator {
	
	protected ZHorse zh;
	protected FileConfiguration resource;
	protected FileConfiguration model;
	protected File resourceFile;
	protected String fileName;
	protected boolean valid = true;
	
	public YamlResourceValidator(ZHorse zh, FileConfiguration resource, FileConfiguration model, File resourceFile, String fileName) {
		this.zh = zh;
		this.resource = resource;
		this.model = model;
		this.resourceFile = resourceFile;
		this.fileName = fileName;
	}
	
	public abstract boolean validate();
	
	protected boolean validateListSet(String index) {
		if (!resource.isList(index)) {
			zh.getLogger().severe(String.format("The %s list is missing from %s !", index, fileName));
        	valid = false;
        	return false;
		}
		return true;
	}
	
	protected boolean validateOptionSet(String index) {
		if (!resource.isSet(index)) {
			zh.getLogger().severe(String.format("The %s option is missing from %s !", index, fileName));
        	valid = false;
        	return false;
        }
		return true;
	}
	
	protected boolean validateSectionSet(String index) {
		if (!resource.isConfigurationSection(index)) {
			zh.getLogger().severe(String.format("The %s section is missing from %s !", index, fileName));
        	valid = false;
        	return false;
		}
		return true;
	}
	
	protected boolean validateGreaterOrEqual(String index, int boundary) {
		int value = resource.getInt(index);
		if (value < boundary) {
			zh.getLogger().severe(String.format("The value of %s must be greater or equal to %d in %s !", index, boundary, fileName));
			valid = false;
			return false;
		}
		return true;
	}
	
	protected boolean validateLessOrEqual(String index, int boundary) {
		int value = resource.getInt(index);
		if (value > boundary) {
			zh.getLogger().severe(String.format("The value of %s must be less or equal to %d in %s !", index, boundary, fileName));
			valid = false;
			return false;
		}
		return true;
	}
	
	protected boolean validatePositive(String index) {
		int value = resource.getInt(index);
		if (value < 0) {
			zh.getLogger().severe(String.format("The value of %s must be positive in %s !", index, fileName));
			valid = false;
			return false;
		}
		return true;
	}
	
	protected boolean validatePositiveOrMinus1(String index) {
		int value = resource.getInt(index);
		if (value < 0 && value != -1) {
			zh.getLogger().severe(String.format("The value of %s must be positive or -1 in %s !", index, fileName));
			valid = false;
			return false;
		}
		return true;
	}
	
	protected boolean validateListNotEmpty(String index) {
		List<String> list = resource.getStringList(index);
		if (list.isEmpty()) {
			zh.getLogger().severe(String.format("The %s list cannot be empty in %s !", index, fileName));
			valid = false;
			return false;
		}
		return true;
	}
	
	protected void invalidate(String message, boolean addFileName) {
		if (addFileName) {
			message += String.format(" (%s)", fileName);
		}
		zh.getLogger().severe(message);
		valid = false;
	}

}
