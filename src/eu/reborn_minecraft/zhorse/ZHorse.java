package eu.reborn_minecraft.zhorse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import eu.reborn_minecraft.zhorse.managers.CommandManager;
import eu.reborn_minecraft.zhorse.managers.ConfigManager;
import eu.reborn_minecraft.zhorse.managers.EconomyManager;
import eu.reborn_minecraft.zhorse.managers.EventManager;
import eu.reborn_minecraft.zhorse.managers.LocaleManager;
import eu.reborn_minecraft.zhorse.managers.UserManager;
import eu.reborn_minecraft.zhorse.metrics.Metrics;

public class ZHorse extends JavaPlugin {
	private static String configPath = "config.yml";
	private static String usersPath = "users.yml";
	private static String localePath = "locale_%s.yml";
	private static String debugLanguage = "EN"; // s'en passer
	private static String[] providedLanguages = {"EN", "FR"};
	private static File configFile;
	private static File usersFile;
	private Permission perms;
	private Economy econ;
	private FileConfiguration config;
	private FileConfiguration users;
	private Map<String,FileConfiguration> locales;
	private CommandManager commandManager;
	private ConfigManager configManager;
	private UserManager userManager;
	private LocaleManager localeManager;
	private EconomyManager economyManager;
	
	public void onEnable() {
		configFile = new File(getDataFolder(), configPath);
		usersFile = new File(getDataFolder(), usersPath);
		initDependencies();
		initPermissions();
		initEconomy();
		initManagers();
		initMetrics();
		getCommand("zhorse").setExecutor(commandManager);
		getServer().getPluginManager().registerEvents(new EventManager(this), this);
	}

	@Override
    public void onDisable() {
    	saveConfig();
    	saveUsers();
    	saveLocales();
    }
	
	private void initDependencies() {
		Plugin vault = getServer().getPluginManager().getPlugin("Vault");
		if (!(vault == null || vault.isEnabled())) {
			getServer().getPluginManager().enablePlugin(vault);
		}
		else if (vault == null) {
			getLogger().severe(String.format("Vault is missing ! Disabling %s...", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	private boolean initPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    private boolean initEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private void initManagers() {
    	boolean configExist = true;
    	boolean usersExist = true;
    	boolean localeExist = true;
    	if (!configFile.exists()) {
			getLogger().info(configPath + " is missing... Creating it.");
			configExist = false;
			saveResource(configPath, false);
		}
		if (!usersFile.exists()) {
			getLogger().info(usersPath + " is missing... Creating it.");
			usersExist = false;
			saveResource(usersPath, false);
		}
		for (String language : providedLanguages) {
			String exactLocalePath = String.format(localePath, language);
			if (!new File(getDataFolder(), exactLocalePath).exists()) {
				getLogger().info(exactLocalePath + " is missing... Creating it.");
				if (getDebugLanguage().equals(language)) {
					localeExist = false;
				}
				saveResource(exactLocalePath, false);
			}
		}
		updateConfig();
		updateUsers();
		localeManager = new LocaleManager(this, localeExist);
		commandManager = new CommandManager(this);
		configManager = new ConfigManager(this, configExist);
		userManager = new UserManager(this, usersExist);
		economyManager = new EconomyManager(this);
		updateLocales();
	}
    
    private void initMetrics() {
    	try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
        	getLogger().severe("Failed to start Metrics !");
        }
    }
    
	public void reload() {
		initManagers();
	}
	
	public String getDebugLanguage() {
		return debugLanguage;
	}
	
	public String[] getProvidedLanguages() {
		return providedLanguages;
	}
	
    public FileConfiguration getConfig() {
    	return config;
    }
    
    public FileConfiguration getUsers() {
    	return users;
    }
    
    public Map<String,FileConfiguration> getLocales() {
    	return locales;
    }
    
    public FileConfiguration getLocale(String language) {
    	if (locales.containsKey(language)) {
    		return locales.get(language);
    	}
    	getLogger().severe("A player is using an unknow language : \"" + language + "\" !");
    	getLogger().severe("Please fix or delete \"users.yml\"");
    	return locales.get(getDebugLanguage());
    }
    
	public void updateConfig() {
		config = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public void updateUsers() {
		users = YamlConfiguration.loadConfiguration(usersFile);
	}
	
	public void updateLocales() {
		locales = new HashMap<String, FileConfiguration>();
		for (String language : getCM().getAvailableLanguages()) {
			String exactLocalePath = String.format(localePath, language);
			File localeFile = new File(getDataFolder(), exactLocalePath);
			if (localeFile.exists()) {
				FileConfiguration locale = YamlConfiguration.loadConfiguration(localeFile);
				locales.put(language, locale);
			}
			else {
				getLogger().info(exactLocalePath + " is missing... Creating it.");
				exactLocalePath = String.format(localePath, getDebugLanguage());
				localeFile = new File(getDataFolder(), exactLocalePath);
				FileConfiguration locale = YamlConfiguration.loadConfiguration(localeFile);
				saveLocale(locale, language);
				locales.put(language, locale);
			}
		}
	}
	
	public void saveConfig () {
        saveConfig(getConfig());
	}
	
	public void saveConfig (FileConfiguration config) {
        try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveUsers () {
        saveUsers(getUsers());
	}
	
	public void saveUsers(FileConfiguration users) {
        try {
			users.save(usersFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveLocales () {
        for (String language : locales.keySet()) {
        	saveLocale(locales.get(language), language);
        }
	}
	
	public void saveLocale(FileConfiguration locale, String language) {
		String exactLocalePath = String.format(localePath, language);
		File localeFile = new File(getDataFolder(), exactLocalePath);
        try {
			locale.save(localeFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public CommandManager getCmdM() {
		return commandManager;
	}
	
    public Permission getPerms() {
    	return perms;
    }
    
    public Economy getEcon() {
    	return econ;
    }
    
    public ConfigManager getCM() {
    	return configManager;
    }
    
    public UserManager getUM() {
    	return userManager;
    }
    
    public LocaleManager getLM() {
    	return localeManager;
    }
    
    public EconomyManager getEM() {
    	return economyManager;
    }

}