package eu.reborn_minecraft.zhorse.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ConfigManager {
	private ZHorse zh;
	
	public ConfigManager(ZHorse zh, boolean initConfig) {
		this.zh = zh;
		if (initConfig) {
			initLanguages();
			initEconomy();
			initWorlds();
			zh.saveConfig();
		}
		if (!checkIntegrity()) {
			zh.getLogger().severe("This can be fixed by deleting \"config.yml\" and reloading ZHorse.");
		}
	}
	
	public List<String> getAvailableLanguages() {
		List<String> availableLanguages = zh.getConfig().getStringList("Settings.availableLanguages");
		if (availableLanguages == null || availableLanguages.isEmpty()) {
			availableLanguages = new ArrayList<String>();
			availableLanguages.add(getDefaultLanguage());
		}
		return availableLanguages;
	}
	
	public ChatColor getChatColor(UUID playerUUID) {
		ChatColor cc = ChatColor.WHITE;
		String groupName = getGroupName(playerUUID);
		if (groupName != null) {
			String color = zh.getConfig().getString("Groups." + groupName + ".color", null);
			if (color != null) {
				cc = zh.getMM().getColor(color);
			}
	    }
		return cc;
	}
	
	public int getCommandCost(String command) {
		int value = 0;
		String commandCost = zh.getConfig().getString("Economy." + command);
		if (commandCost != null) {
			value = Integer.parseInt(commandCost);
			if (value < 0) {
				value = 0;
			}
		}
		return value;
	}
	
	public String getDefaultLanguage() {
		String defaultLanguage = zh.getConfig().getString("Settings.defaultLanguage");
		if (defaultLanguage == null) {
			defaultLanguage = "EN";
		}
		return defaultLanguage;
	}
	
	private String getExactGroupName(String groupName) {
		if (groupName != null) {
			ConfigurationSection cs = zh.getConfig().getConfigurationSection("Groups");
			if (cs != null) {
				for (String exactGroupName : cs.getKeys(false)) {
					if (groupName.equalsIgnoreCase(exactGroupName)) {
						return exactGroupName;
					}
				}
			}
		}
		return groupName;
	}
	
	private String getGroupName(UUID playerUUID) {
		String groupName = null;
		Player p = zh.getServer().getPlayer(playerUUID);
		if (p != null && p.hasPlayedBefore()) {
			groupName = zh.getPerms().getPrimaryGroup(p);
		}
		else {
			OfflinePlayer op = zh.getServer().getOfflinePlayer(playerUUID);
			if (op.hasPlayedBefore()) {
				String world = zh.getServer().getWorlds().get(0).getName();
				groupName = zh.getPerms().getPrimaryGroup(world, op);
			}
		}
		if (p != null && p.hasPlayedBefore() && (groupName == null || !zh.getConfig().contains("Groups." + groupName))) {
			groupName = getSurrogateGroupName(p);
		}
		else {
			groupName = getExactGroupName(groupName);
		}
		return groupName;
	}
	
	public int getMaximumClaims(UUID playerUUID) {
		int value = 0;
		String groupName = getGroupName(playerUUID);
		if (groupName != null) {
			String maximumClaims = zh.getConfig().getString("Groups." + groupName + ".maximumClaims");
			if (maximumClaims != null) {
				value = Integer.parseInt(maximumClaims);
				if (value < 0 && value != -1) {
					value = 0;
				}
			}
		}
		return value;
	}
	
	public int getMaximumHorseNameLength() {
		int value = -1;
		String maximumHorseNameLength = zh.getConfig().getString("Settings.maximumHorsenameLength");
		if (maximumHorseNameLength != null) {
			value = Integer.parseInt(maximumHorseNameLength);
			if (value < 0 && value != -1) {
				value = -1;
			}
		}
		return value;
	}
	
	public int getMinimumHorseNameLength() {
		int value = 0;
		String minimumHorseNameLength = zh.getConfig().getString("Settings.minimumHorsenameLength");
		if (minimumHorseNameLength != null) {
			value = Integer.parseInt(minimumHorseNameLength);
			if (value < 0) {
				value = 0;
			}
		}
		return value;
	}
	
	public String getRandomName() {
		String randomName = null;
		Random random = new Random();
		List<String> randomNamesList = zh.getConfig().getStringList("Horsenames");
		if (!(randomNamesList == null || randomNamesList.size() == 0)) {
			randomName = randomNamesList.get(random.nextInt(randomNamesList.size()));
		}
		return randomName;
	}
	
	private String getSurrogateGroupName(Player p) {
		ConfigurationSection cs = zh.getConfig().getConfigurationSection("Groups");
		if (cs != null) {
			for (String groupName : cs.getKeys(false)) {
				String permission = zh.getConfig().getString("Groups." + groupName + ".permission", null);
				if (permission != null && zh.getPerms().has(p, permission)) {
					return groupName;
				}
			}
		}
		return null;
	}
	
	private void initLanguages() {
		List<String> providedLanguages = new ArrayList<String>();
		for (String language : zh.getProvidedLanguages()) {
			providedLanguages.add(language);
		}
		zh.getConfig().set("Settings.availableLanguages", providedLanguages);
	}
	
	private void initEconomy() {
		List<String> commandList = zh.getCmdM().getCommandList();
		for (String cmd : commandList) {
			zh.getConfig().set("Economy." + cmd, 0);
		}
	}
	
	private void initWorlds() {
		List<World> worlds = zh.getServer().getWorlds();
		List<String> worldsList = new ArrayList<String>();
		for (World world : worlds) {
			worldsList.add(world.getName());
		}
		zh.getConfig().set("Settings.activeWorlds", worldsList);
	}
	
	public boolean isConsoleMuted() {
		return zh.getConfig().getBoolean("Settings.muteConsole", false);
	}
	
	public boolean isHorseNameAllowed() {
		return getMaximumHorseNameLength() != 0;
	}
	
	public boolean isHorseNameRequired() {
		return getMinimumHorseNameLength() > 0;
	}
	
	public boolean isLanguageAvailable(String language) {
		return getAvailableLanguages().contains(language);
	}
	
	public boolean isWorldCrossingAllowed() {
		return zh.getConfig().getBoolean("Settings.worldCrossing", false);
	}
	
	public boolean isWorldEnabled(World world) {
		List<String> worlds = zh.getConfig().getStringList("Settings.activeWorlds");
		if (!(worlds == null || worlds.size() == 0)) {
			return worlds.contains(world.getName());
		}
        return false;
	}
	
	public boolean shouldClaimOnTame() {
		return zh.getConfig().getBoolean("Settings.claimOnTame", false);
	}
	
	public boolean shouldLockOnClaim() {
		return zh.getConfig().getBoolean("Settings.lockOnClaim", false);
	}
	
	public boolean shouldProtectOnClaim() {
		return zh.getConfig().getBoolean("Settings.protectOnClaim", false);
	}
	
	private boolean checkIntegrity() {
		boolean integrity = true;
		if (!checkColorsIntegrity()) {
			integrity = false;
		}
		if (!checkCommandsIntegrity()) {
			integrity = false;
		}
		if (!checkLanguagesIntegrity()) {
			integrity = false;
		}
		if (!checkMaximumClaimsIntegrity()) {
			integrity = false;
		}
		if (!checkHorseNameRangeIntegrity()) {
			integrity = false;
		}
		if (!checkWorldsIntegrity()) {
			integrity = false;
		}
		return integrity;
	}
	
	private boolean checkColorsIntegrity() {
		boolean integrity = true;
		ConfigurationSection cs = zh.getConfig().getConfigurationSection("Groups");
		if (cs != null) {
			for (String groupName : cs.getKeys(false)) {
				String color = zh.getConfig().getString("Groups." + groupName + ".color");
		        if (color != null) {
					if (!zh.getMM().isColor(color)) {
		        		zh.getLogger().severe("The value \"" + color + "\" attributed to the group \"" + groupName + "\" is not a color !");
		        		integrity = false;
		        	}
		        }
			}
		}
		else {
			zh.getLogger().severe("The \"Groups\" section is missing from config !");
			integrity = false;
		}
		return integrity;
	}
	
	private boolean checkCommandsIntegrity() {
		boolean integrity = true;
		ConfigurationSection cs = zh.getConfig().getConfigurationSection("Economy");
		if (cs != null) {
			for (String command : cs.getKeys(false)) {
				String commandCost = zh.getConfig().getString("Economy." + command);
				if (commandCost != null) {
					int value = Integer.parseInt(commandCost);
					if (value < 0) {
						zh.getLogger().severe("The cost of command \"" + command + "\" must be positive !");
						integrity = false;
					}
				}
			}
		}
		else {
			zh.getLogger().severe("The \"Economy\" section is missing from config !");
			integrity = false;
		}
		return integrity;
	}
	
	private boolean checkLanguagesIntegrity() {
		boolean integrity = true;
		List<String> languages = zh.getConfig().getStringList("Settings.availableLanguages");
		if (languages == null || languages.size() == 0) {
			zh.getLogger().severe("The \"availableLanguages\" section is missing from config !");
			integrity = false;
		}
		else if (!languages.contains(getDefaultLanguage())) {
			zh.getLogger().severe("The \"availableLanguages\" section must contains the default language !");
			integrity = false;
		}
		return integrity;
	}
	
	private boolean checkMaximumClaimsIntegrity() {
		boolean integrity = true;
		ConfigurationSection cs = zh.getConfig().getConfigurationSection("Groups");
		if (cs != null) {
			for (String groupName : cs.getKeys(false)) {
				String maximumClaims = zh.getConfig().getString("Groups." + groupName + ".maximumClaims");
				if (maximumClaims != null) {
					int value = Integer.parseInt(maximumClaims);
					if (value < 0 && value != -1) {
						zh.getLogger().severe("The \"maximumClaims\" value of group \"" + groupName + "\" must be positive or -1 !");
						integrity = false;
					}
				}
			}
		}
		else {
			zh.getLogger().severe("The \"Groups\" section is missing from config !");
			integrity = false;
		}
		return integrity;
	}
	
	private boolean checkHorseNameRangeIntegrity() {
		boolean integrity = true;
		String maximumHorseNameLength = zh.getConfig().getString("Settings.maximumHorsenameLength");
		String minimumHorseNameLength = zh.getConfig().getString("Settings.minimumHorsenameLength");
		if (maximumHorseNameLength != null) {
			int value = Integer.parseInt(maximumHorseNameLength);
			if (value < 0 && value != -1) {
				zh.getLogger().severe("The \"maximumHorsenameLength\" value must be positive or -1 !");
				integrity = false;
			}
		}
		if (minimumHorseNameLength != null) {
			int value = Integer.parseInt(minimumHorseNameLength);
			if (value < 0) {
				zh.getLogger().severe("The \"minimumHorsenameLength\" value must be positive !");
				integrity = false;
			}
		}
		return integrity;
	}
	
	private boolean checkWorldsIntegrity() {
		boolean integrity = true;
		List<String> worlds = zh.getConfig().getStringList("Settings.activeWorlds");
		if (worlds == null || worlds.size() == 0) {
			zh.getLogger().severe("The \"activeWorlds\" section is missing from config !");
			integrity = false;
		}
		return integrity;
	}

}
