package eu.reborn_minecraft.zhorse;

import java.io.File;
import java.io.IOException;

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
import eu.reborn_minecraft.zhorse.managers.UsersManager;
import eu.reborn_minecraft.zhorse.metrics.Metrics;

public class ZHorse extends JavaPlugin {
	private static String configSuffix = "config.yml";
	private static String usersSuffix = "users.yml";
	private static String localeSuffix = "locale.yml";
	public static String mainPath;
	public static String configPath;
	public static String usersPath;
	public static String localePath;
	private Permission perms;
	private Economy econ;
	private FileConfiguration config;
	private FileConfiguration users;
	private FileConfiguration locale;
	private CommandManager commandManager;
	private ConfigManager configManager;
	private UsersManager usersManager;
	private LocaleManager localeManager;
	private EconomyManager economyManager;
	
	public void onEnable() {
		mainPath = getDataFolder().getAbsolutePath() + "\\";
		configPath = mainPath + configSuffix;
		usersPath = mainPath + usersSuffix;
		localePath = mainPath + localeSuffix;
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
    	saveLocale();
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
    	if (!new File(configPath).exists()) {
			getLogger().info(configSuffix + " is missing... Creating it.");
			configExist = false;
			saveResource("config.yml", false);
		}
		if (!new File(usersPath).exists()) {
			getLogger().info(usersSuffix + " is missing... Creating it.");
			usersExist = false;
			saveResource("users.yml", false);
		}
		if (!new File(localePath).exists()) {
			getLogger().info(localeSuffix + " is missing... Creating it.");
			localeExist = false;
			saveResource("locale.yml", false);
		}
		updateConfig();
		updateUsers();
		updateLocale();
		localeManager = new LocaleManager(this, localeExist);
		commandManager = new CommandManager(this);
		configManager = new ConfigManager(this, configExist);
		usersManager = new UsersManager(this, usersExist);
		economyManager = new EconomyManager(this);
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
	
    public FileConfiguration getConfig() {
    	return config;
    }
    
    public FileConfiguration getUsers() {
    	return users;
    }
    
    public FileConfiguration getLocale() {
    	return locale;
    }
    
	public void updateConfig() {
		config = YamlConfiguration.loadConfiguration(new File(configPath));
	}
	
	public void updateUsers() {
		users = YamlConfiguration.loadConfiguration(new File(usersPath));
	}
	
	public void updateLocale() {
		locale = YamlConfiguration.loadConfiguration(new File(localePath));
	}
	
	public void saveConfig () {
        saveConfig(getConfig());
	}
	
	public void saveConfig (FileConfiguration config) {
        try {
			config.save(configPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveUsers () {
        saveUsers(getUsers());
	}
	
	public void saveUsers(FileConfiguration users) {
        try {
			users.save(usersPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveLocale () {
        saveLocale(getLocale());
	}
	
	public void saveLocale(FileConfiguration locale) {
        try {
			locale.save(localePath);
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
    
    public UsersManager getUM() {
    	return usersManager;
    }
    
    public LocaleManager getLM() {
    	return localeManager;
    }
    
    public EconomyManager getEM() {
    	return economyManager;
    }

}