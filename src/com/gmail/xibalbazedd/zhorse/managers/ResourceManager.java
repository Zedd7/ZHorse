package com.gmail.xibalbazedd.zhorse.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.xibalbazedd.zhorse.ZHorse;

public class ResourceManager {
	
	private static final String SQL_TABLES_FOLDER_PATH = "res\\sql\\tables\\".replace('\\', '/');
	private static final String SQL_PATCHES_FOLDER_PATH = "res\\sql\\patches\\".replace('\\', '/');
	private static final String YAML_FOLDER_PATH = ("res\\yaml\\").replace('\\', '/');
	
	private static final String CONFIG_FILE_NAME = "config.yml";
	private static final String LOCALE_FILE_NAME_FORMAT = "locale_%s.yml";
	private static final String SQL_TABLE_FILE_NAME_FORMAT = "%s-table.sql";
	private static final String SQL_PATCH_FILE_NAME_FORMAT = "%s-patch.sql";
	
	private ZHorse zh;
	
	public ResourceManager(ZHorse zh) {
		this.zh = zh;
		loadSQLResources();
		loadYamlResources();
	}
	
	private void loadSQLResources() {
		zh.getDM().setScriptLists(loadSQLTableScriptList(), loadSQLPatchScriptList());
	}
	
	private List<String> loadSQLTableScriptList() {
		List<String> sqlTableScriptList = new ArrayList<>();
		for (String tableName : DataManager.TABLE_ARRAY) {
			String scriptFileName = String.format(SQL_TABLE_FILE_NAME_FORMAT, tableName);
			sqlTableScriptList.add(getSQLResource(SQL_TABLES_FOLDER_PATH, scriptFileName));
		}
		return sqlTableScriptList;
	}
	
	private List<String> loadSQLPatchScriptList() {
		List<String> sqlPatchScriptList = new ArrayList<>();
		for (String patchName : DataManager.PATCH_ARRAY) {
			String scriptFileName = String.format(SQL_PATCH_FILE_NAME_FORMAT, patchName);
			sqlPatchScriptList.add(getSQLResource(SQL_PATCHES_FOLDER_PATH, scriptFileName));
		}
		return sqlPatchScriptList;
	}
	
	private String getSQLResource(String folderPath, String resourceName) {
		String resourcePath = folderPath + resourceName;
		String resource = null;
		try {
			resource = IOUtils.toString(zh.getResource(resourcePath), "UTF-8");
		} catch (Exception e) {
			zh.getLogger().log(Level.SEVERE, "Could not extract resource file from jar : " + resourceName, e);
		}
		return resource;
	}
	
	private void loadYamlResources() {
		zh.getCM().setConfig(loadYamlResource(CONFIG_FILE_NAME, null));
		zh.getLM().setLocales(loadLocales());
	}
	
	private Map<String, FileConfiguration> loadLocales() {
		Map<String, FileConfiguration> locales = new HashMap<>();
		
		for (String language : LocaleManager.PROVIDED_LANGUAGES) {
			String resourceName = String.format(LOCALE_FILE_NAME_FORMAT, language);
			locales.put(language, loadYamlResource(resourceName, null));
		}
		
		for (String language : zh.getCM().getAvailableLanguages()) {
			if (!locales.containsKey(language)) {
				String resourceName = String.format(LOCALE_FILE_NAME_FORMAT, language);
				String defaultResourceName = String.format(LOCALE_FILE_NAME_FORMAT, zh.getCM().getDefaultLanguage());
				locales.put(language, loadYamlResource(resourceName, defaultResourceName));
			}
		}
		
		return locales;
	}

	private FileConfiguration loadYamlResource(String resourceName, String defaultResourceName) {
		File resourceFile = new File(zh.getDataFolder(), resourceName);
		
    	if (!resourceFile.exists()) {
    		if (defaultResourceName == null) {
    			extractYamlResource(resourceName, resourceName);
    		}
    		else {
    			extractYamlResource(defaultResourceName, resourceName);
    		}
		}
		
		YamlConfiguration resource = new YamlConfiguration();
		try {
			resource.load(resourceFile);
		} catch (Exception e) {
			zh.getLogger().log(Level.SEVERE, "Could not load resource file : " + resourceName, e);
		}
		return resource;
	}
	
	private void extractYamlResource(String resourceName, String destination) {
		String resourcePath = YAML_FOLDER_PATH + resourceName;
		try {
			zh.getLogger().info(destination + " is missing... Creating it.");
			FileUtils.copyInputStreamToFile(zh.getResource(resourcePath), new File(zh.getDataFolder(), destination));
		} catch (Exception e) {
			zh.getLogger().log(Level.SEVERE, "Could not extract resource file from jar : " + resourceName, e);
		}
	}


}
