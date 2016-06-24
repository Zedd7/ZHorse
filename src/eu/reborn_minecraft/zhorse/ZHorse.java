package eu.reborn_minecraft.zhorse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import eu.reborn_minecraft.zhorse.managers.CommandManager;
import eu.reborn_minecraft.zhorse.managers.ConfigManager;
import eu.reborn_minecraft.zhorse.managers.DataManager;
import eu.reborn_minecraft.zhorse.managers.EconomyManager;
import eu.reborn_minecraft.zhorse.managers.EventManager;
import eu.reborn_minecraft.zhorse.managers.HorseManager;
import eu.reborn_minecraft.zhorse.managers.LocaleManager;
import eu.reborn_minecraft.zhorse.managers.MessageManager;
import eu.reborn_minecraft.zhorse.managers.UserManager;
import eu.reborn_minecraft.zhorse.utils.Metrics;
import eu.reborn_minecraft.zhorse.utils.Utf8YamlConfiguration;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class ZHorse extends JavaPlugin {
	private static final String CONFIG_PATH = "config.yml";
	private static final String USERS_PATH = "users.yml";
	private static final String LOCALE_PATH = "locale_%s.yml";
	private static final String[] PROVIDED_LANGUAGES = {"EN", "FR", "NL"};
	private Permission vaultPerms;
	private Economy vaultEcon;
	private FileConfiguration config;
	private FileConfiguration users;
	private Map<String, FileConfiguration> locales;
	private CommandManager commandManager;
	private ConfigManager configManager;
	private EconomyManager economyManager;
	private EventManager eventManager;
	private HorseManager horseManager;
	private LocaleManager localeManager;
	private MessageManager messageManager;
	private UserManager userManager;
	private DataManager dataManager;
	
	@Override
	public void onEnable() {
		initDependencies();
		initPermissions();
		initEconomy();
		initMetrics();
		initManagers();
	}
	
	@Override
    public void onDisable() {
		horseManager.unloadHorses();
    }
	
	private void initDependencies() {
		Plugin vault = getServer().getPluginManager().getPlugin("Vault");
		if (!(vault == null || vault.isEnabled())) {
			getServer().getPluginManager().enablePlugin(vault);
		} else if (vault == null) {
			getLogger().severe(String.format("Vault is missing ! Disabling %s...", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	private boolean initPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        vaultPerms = rsp.getProvider();
        return vaultPerms != null;
    }
    
    private boolean initEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        vaultEcon = rsp.getProvider();
        return vaultEcon != null;
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
    	File configFile = new File(getDataFolder(), CONFIG_PATH);	
    	if (!configFile.exists()) {
			getLogger().info(CONFIG_PATH + " is missing... Creating it.");
			saveResource(CONFIG_PATH, false);
		}
    	File usersFile = new File(getDataFolder(), USERS_PATH);    
		if (!usersFile.exists()) {
			getLogger().info(USERS_PATH + " is missing... Creating it.");
			saveResource(USERS_PATH, false);			
		}
		for (String language : PROVIDED_LANGUAGES) {
			String exactLocalePath = String.format(LOCALE_PATH, language);
			File localeFile = new File(getDataFolder(), exactLocalePath);
			if (!localeFile.exists()) {
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
		dataManager = new DataManager(this);
		economyManager = new EconomyManager(this);
		eventManager = new EventManager(this);
		horseManager = new HorseManager(this);
		loadLocales();
		
		boolean success = true;
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
		File configFile = new File(getDataFolder(), CONFIG_PATH);
		config = Utf8YamlConfiguration.loadConfiguration(configFile);
	}
	
	public void loadUsers() {
		File usersFile = new File(getDataFolder(), USERS_PATH);
		users = Utf8YamlConfiguration.loadConfiguration(usersFile);
	}
	
	public void loadLocales() {
		locales = new HashMap<String, FileConfiguration>();
		for (String language : getCM().getAvailableLanguages()) {
			String exactLocalePath = String.format(LOCALE_PATH, language);
			File localeFile = new File(getDataFolder(), exactLocalePath);
			if (localeFile.exists()) {
				FileConfiguration locale = Utf8YamlConfiguration.loadConfiguration(localeFile);
				locales.put(language, locale);
			} else {
				getLogger().info(exactLocalePath + " is missing... Creating it.");
				exactLocalePath = String.format(LOCALE_PATH, getCM().getDefaultLanguage());
				localeFile = new File(getDataFolder(), exactLocalePath);
				FileConfiguration locale = Utf8YamlConfiguration.loadConfiguration(localeFile);
				saveLocale(locale, language);
				locales.put(language, locale);
			}
		}
	}
	
	public void saveConfig() {
		File configFile = new File(getDataFolder(), CONFIG_PATH);
        try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveUsers() {
		File usersFile = new File(getDataFolder(), USERS_PATH);
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
		String exactLocalePath = String.format(LOCALE_PATH, language);
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
	
    public ConfigManager getCM() {
    	return configManager;
    }
    
    public DataManager getDM() {
    	return dataManager;
    }
    
	public Economy getEcon() {
		return vaultEcon;
	}
	
	public EconomyManager getEM() {
		return economyManager;
	}
	
	public EventManager getEvM() {
		return eventManager;
	}
	
	public HorseManager getHM() {
		return horseManager;
	}
	
	public LocaleManager getLM() {
		return localeManager;
	}
	
	public MessageManager getMM() {
		return messageManager;
	}
	
	public Permission getPerms() {
		return vaultPerms;
	}
	
	public UserManager getUM() {
		return userManager;
	}

}
