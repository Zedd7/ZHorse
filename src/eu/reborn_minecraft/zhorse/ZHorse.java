package eu.reborn_minecraft.zhorse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import eu.reborn_minecraft.zhorse.managers.MessageManager;
import eu.reborn_minecraft.zhorse.managers.UserManager;
import eu.reborn_minecraft.zhorse.metrics.Metrics;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class ZHorse extends JavaPlugin {
	private static String configPath = "config.yml";
	private static String usersPath = "users.yml";
	private static String localePath = "locale_%s.yml";
	private static String[] providedLanguages = {"EN", "FR", "NL"};
	private static File configFile;
	private static File usersFile;
	private Permission perms;
	private Economy econ;
	private FileConfiguration config;
	private FileConfiguration users;
	private Map<String, FileConfiguration> locales;
	private ConfigManager configManager;
	private UserManager userManager;
	private LocaleManager localeManager;
	private CommandManager commandManager;
	private MessageManager messageManager;
	private EconomyManager economyManager;
	
	public void onEnable() {
		configFile = new File(getDataFolder(), configPath);
		usersFile = new File(getDataFolder(), usersPath);
		initDependencies();
		initPermissions();
		initEconomy();
		initMetrics();
		initManagers();
		getCommand(this.getName().toLowerCase()).setExecutor(commandManager);
		getServer().getPluginManager().registerEvents(new EventManager(this), this);
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
    
    private void initMetrics() {
    	try {
    		Metrics metrics = new Metrics(this);
			metrics.start();
        } catch (IOException e) {
        	getLogger().severe("Failed to start Metrics !");
        }
    }
    
    private boolean initManagers() {
    	boolean success = true;
    	if (!configFile.exists()) {
			getLogger().info(configPath + " is missing... Creating it.");
			saveResource(configPath, false);
		}
		if (!usersFile.exists()) {
			getLogger().info(usersPath + " is missing... Creating it.");
			saveResource(usersPath, false);			
		}
		for (String language : providedLanguages) {
			String exactLocalePath = String.format(localePath, language);
			if (!new File(getDataFolder(), exactLocalePath).exists()) {
				getLogger().info(exactLocalePath + " is missing... Creating it.");
				saveResource(exactLocalePath, false);
			}
		}
		loadConfig();
		loadUsers();
		messageManager = new MessageManager(this);
		localeManager = new LocaleManager(this);
		commandManager = new CommandManager(this);
		configManager = new ConfigManager(this);
		userManager = new UserManager(this);
		economyManager = new EconomyManager(this);
		loadLocales();
		if (!configManager.checkConformity()) {
			success = false;
		}
		if (!localeManager.checkConformity()) {
			success = false;
		}
		return success;
	}
    
	public boolean reload() {
		return initManagers();
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
    	getLogger().severe("A player is using an unavailable language : \"" + language + "\" !");
    	return locales.get(getCM().getDefaultLanguage());
    }
    
	public void loadConfig() {
		config = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public void loadUsers() {
		users = YamlConfiguration.loadConfiguration(usersFile);
	}
	
	public void loadLocales() {
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
				exactLocalePath = String.format(localePath, getCM().getDefaultLanguage());
				localeFile = new File(getDataFolder(), exactLocalePath);
				FileConfiguration locale = YamlConfiguration.loadConfiguration(localeFile);
				saveLocale(locale, language);
				locales.put(language, locale);
			}
		}
	}
	
	public void saveConfig() {
        try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveUsers() {
        try {
			synchronized (users) {
				users.save(usersFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveLocale(String language) {
		saveLocale(locales.get(language), language);
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
	
	public MessageManager getMM() {
		return messageManager;
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
