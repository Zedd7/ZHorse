package eu.reborn_minecraft.zhorse;

import java.io.IOException;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import eu.reborn_minecraft.zhorse.managers.CommandManager;
import eu.reborn_minecraft.zhorse.managers.ConfigManager;
import eu.reborn_minecraft.zhorse.managers.DataManager;
import eu.reborn_minecraft.zhorse.managers.EconomyManager;
import eu.reborn_minecraft.zhorse.managers.EventManager;
import eu.reborn_minecraft.zhorse.managers.HorseManager;
import eu.reborn_minecraft.zhorse.managers.LocaleManager;
import eu.reborn_minecraft.zhorse.managers.MessageManager;
import eu.reborn_minecraft.zhorse.managers.PermissionManager;
import eu.reborn_minecraft.zhorse.utils.Metrics;

public class ZHorse extends JavaPlugin {
	
	private CommandManager commandManager;
	private ConfigManager configManager;
	private DataManager dataManager;
	private EconomyManager economyManager;
	private EventManager eventManager;
	private HorseManager horseManager;
	private LocaleManager localeManager;
	private MessageManager messageManager;
	private PermissionManager permissionManager;
	
	@Override
	public void onEnable() {
		initDependencies();
		initMetrics();
		initManagers();
	}
	
	@Override
    public void onDisable() {
		horseManager.unloadHorses();
		dataManager.closeDatabase();
    }
	
	private void initDependencies() {
		Plugin vault = getServer().getPluginManager().getPlugin("Vault");
		if (vault != null && !vault.isEnabled()) {
			getServer().getPluginManager().enablePlugin(vault);
		}
		else if (vault == null) {
			getLogger().severe(String.format("Vault is missing ! Disabling %s...", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
		}
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
    	commandManager = new CommandManager(this);
		configManager = new ConfigManager(this);
		dataManager = new DataManager(this);
		economyManager = new EconomyManager(this);
		eventManager = new EventManager(this);
		horseManager = new HorseManager(this);
		localeManager = new LocaleManager(this);
		messageManager = new MessageManager(this);
		permissionManager = new PermissionManager(this);
		
		dataManager.openDatabase();
		horseManager.loadHorses();
		
		boolean conformConfig = configManager.checkConformity();
		boolean conformLocale = localeManager.checkConformity();
		return conformConfig && conformLocale;
	}
    
	public boolean reload() {
		horseManager.unloadHorses();
		dataManager.closeDatabase();
		return initManagers();
	}
	
	public ConfigManager getCM() {
    	return configManager;
    }
	
	public CommandManager getCmdM() {
		return commandManager;
	}
    
    public DataManager getDM() {
    	return dataManager;
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
	
	public PermissionManager getPM() {
		return permissionManager;
	}

}
