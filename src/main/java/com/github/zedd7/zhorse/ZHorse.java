package com.github.zedd7.zhorse;

import java.util.HashMap;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.zedd7.zhorse.managers.CommandManager;
import com.github.zedd7.zhorse.managers.ConfigManager;
import com.github.zedd7.zhorse.managers.DataManager;
import com.github.zedd7.zhorse.managers.EconomyManager;
import com.github.zedd7.zhorse.managers.EventManager;
import com.github.zedd7.zhorse.managers.HorseManager;
import com.github.zedd7.zhorse.managers.LocaleManager;
import com.github.zedd7.zhorse.managers.MessageManager;
import com.github.zedd7.zhorse.managers.PermissionManager;
import com.github.zedd7.zhorse.managers.ResourceManager;
import com.github.zedd7.zhorse.utils.Metrics;

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
	private ResourceManager resourceManager;

	@Override
	public void onEnable() {
		initDependencies();
		initManagers();
		initMetrics();
	}

	@Override
    public void onDisable() {
		horseManager.untrackHorses();
		dataManager.closeDatabase();
    }

	public void disable() {
		getServer().getPluginManager().disablePlugin(this);
	}

	public boolean reload() {
		eventManager.unregisterEvents();
		horseManager.untrackHorses();
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

	public ResourceManager getRM() {
		return resourceManager;
	}

	private void initDependencies() {
		Plugin vault = getServer().getPluginManager().getPlugin("Vault");
		if (vault != null && !vault.isEnabled()) {
			getServer().getPluginManager().enablePlugin(vault);
		}
		else if (vault == null) {
			getLogger().severe(String.format("Vault is missing ! Disabling %s...", getDescription().getName()));
			disable();
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
		resourceManager = new ResourceManager(this);

		boolean resourcesValid = resourceManager.validateResources();

		commandManager.loadCommandCooldowns();
		dataManager.openDatabase();
		horseManager.trackHorses();
		messageManager.setDisplayConsole(!configManager.isConsoleMuted());

		return resourcesValid;
	}

    private void initMetrics() {
		Metrics metrics = new Metrics(this);

		metrics.addCustomChart(new Metrics.SimplePie("database_type") {

            @Override
            public String getValue() {
            	return configManager.getDatabaseType().getName();
            }

        });

		metrics.addCustomChart(new Metrics.SimplePie("default_language") {

            @Override
            public String getValue() {
                return configManager.getDefaultLanguage();
            }

        });

		metrics.addCustomChart(new Metrics.SimpleBarChart("owner_to_player_ratio_2") {

			@Override
			public HashMap<String, Integer> getValues(HashMap<String, Integer> valueMap) {
				int totalOwnersCount = dataManager.getTotalOwnersCount(true, null);
				int totalPlayersCount = dataManager.getTotalPlayersCount(true, null);
				int ownersToPlayersRatio = (int) ((totalOwnersCount / (double) totalPlayersCount) * 100);
				String binLabel = getRatioBinLabel(ownersToPlayersRatio);
				valueMap.put(binLabel, 1);
				return valueMap;
			}

		});

		metrics.addCustomChart(new Metrics.SimpleBarChart("total_horse_count_2") {

			@Override
			public HashMap<String, Integer> getValues(HashMap<String, Integer> valueMap) {
				int totalHorsesCount = dataManager.getTotalHorsesCount(true, null);
				String binLabel = getCountBinLabel(totalHorsesCount);
				valueMap.put(binLabel, 1);
				return valueMap;
			}

		});

		metrics.addCustomChart(new Metrics.SimpleBarChart("horse_per_owner_average") {

			@Override
			public HashMap<String, Integer> getValues(HashMap<String, Integer> valueMap) {
				int totalHorsesCount = dataManager.getTotalHorsesCount(true, null);
				int totalOwnersCount = dataManager.getTotalOwnersCount(true, null);
				int horsesPerOwnersAverage = totalOwnersCount != 0 ? totalHorsesCount / totalOwnersCount : 0;
				String binLabel = getCountBinLabel(horsesPerOwnersAverage);
				valueMap.put(binLabel, 1);
				return valueMap;
			}

		});

    }

    private String getRatioBinLabel(int ownersToPlayersRatio) {
    	if (ownersToPlayersRatio < 1) {
    		return "0% - 1%";
    	}
    	else if (ownersToPlayersRatio < 5) {
    		return "1% - 5%";
    	}
    	else if (ownersToPlayersRatio < 10) {
    		return "5% - 10%";
    	}
    	else if (ownersToPlayersRatio < 20) {
    		return "10% - 20%";
    	}
    	else if (ownersToPlayersRatio < 40) {
    		return "20% - 40%";
    	}
    	else if (ownersToPlayersRatio < 60) {
    		return "40% - 60%";
    	}
    	else if (ownersToPlayersRatio < 80) {
    		return "60% - 80%";
    	}
    	else {
    		return "80% - 100%";
    	}
    }

    private String getCountBinLabel(int totalHorsesCount) {
    	if (totalHorsesCount < 1) {
    		return "0 - 1";
    	}
    	else if (totalHorsesCount < 5) {
    		return "1 - 5";
    	}
    	else if (totalHorsesCount < 10) {
    		return "5 - 10";
    	}
    	else if (totalHorsesCount < 50) {
    		return "10 - 50";
    	}
    	else if (totalHorsesCount < 100) {
    		return "50 - 100";
    	}
    	else if (totalHorsesCount < 200) {
    		return "100 - 200";
    	}
    	else if (totalHorsesCount < 500) {
    		return "200 - 500";
    	}
    	else if (totalHorsesCount < 1000) {
    		return "500 - 1000";
    	}
    	else if (totalHorsesCount < 2000) {
    		return "1000 - 2000";
    	}
    	else if (totalHorsesCount < 5000) {
    		return "2000 - 5000";
    	}
    	else if (totalHorsesCount < 10000) {
    		return "5000 - 10000";
    	}
    	else {
    		return "10000+";
    	}
    }

}
