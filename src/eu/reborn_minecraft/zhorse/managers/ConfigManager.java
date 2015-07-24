package eu.reborn_minecraft.zhorse.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ConfigManager {
	private ZHorse zh;
	
	public ConfigManager(ZHorse zh) {
		this.zh = zh;
		if (!checkConformity()) {
			zh.getLogger().severe("This can be fixed by deleting \"config.yml\" and reloading ZHorse.");
		}
	}
	
	public List<String> getAvailableLanguages() {
		List<String> availableLanguages = zh.getConfig().getStringList("Languages.available");
		if (availableLanguages == null || availableLanguages.isEmpty()) {
			availableLanguages = new ArrayList<String>();
			availableLanguages.add(getDefaultLanguage());
		}
		return availableLanguages;
	}
	
	public int getClaimsLimit(UUID playerUUID) {
		int value = 0;
		String groupName = getGroupName(playerUUID);
		if (groupName != null) {
			String claimsLimit = zh.getConfig().getString("Groups." + groupName + ".claims-limit");
			if (claimsLimit != null) {
				value = Integer.parseInt(claimsLimit);
				if (value < 0 && value != -1) {
					value = 0;
				}
			}
		}
		return value;
	}
	
	public int getCommandCost(String command) {
		int value = 0;
		String commandCost = zh.getConfig().getString("Commands." + command + ".cost");
		if (commandCost != null) {
			value = Integer.parseInt(commandCost);
			if (value < 0) {
				value = 0;
			}
		}
		return value;
	}
	
	public String getDefaultHorseName() {
		String defaultHorseName = zh.getConfig().getString("HorseNames.default-name");
		if (defaultHorseName == null) {
			defaultHorseName = "NotSet";
		}
		return defaultHorseName;
	}
	
	public String getDefaultLanguage() {
		String defaultLanguage = zh.getConfig().getString("Languages.default");
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
	
	public ChatColor getGroupColor(UUID playerUUID) {
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
	
	public int getMaximumHorseNameLength() {
		int value = -1;
		String maximumHorseNameLength = zh.getConfig().getString("HorseNames.maximum-length");
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
		String minimumHorseNameLength = zh.getConfig().getString("HorseNames.minimum-length");
		if (minimumHorseNameLength != null) {
			value = Integer.parseInt(minimumHorseNameLength);
			if (value < 0) {
				value = 0;
			}
		}
		return value;
	}
	
	public String getRandomHorseName() {
		String randomHorseName = null;
		Random random = new Random();
		List<String> randomHorseNameList = zh.getConfig().getStringList("HorseNames.random-names");
		if (!(randomHorseNameList == null || randomHorseNameList.size() == 0)) {
			randomHorseName = randomHorseNameList.get(random.nextInt(randomHorseNameList.size()));
		}
		return randomHorseName;
	}
	
	private String getSurrogateGroupName(Player p) {
		ConfigurationSection cs = zh.getConfig().getConfigurationSection("Groups");
		if (cs != null) {
			for (String groupName : cs.getKeys(false)) {
				System.out.println("group : " + groupName);
				String permission = zh.getConfig().getString("Groups." + groupName + ".permission", null);
				System.out.println("perm : " + permission);
				if (permission != null && zh.getPerms().has(p, permission)) {
					System.out.println("return : " + groupName);
					return groupName;
				}
			}
			System.out.println("not found");
		}
		return null;
	}
	
	public boolean isConsoleMuted() {
		return zh.getConfig().getBoolean("Settings.mute-console", false);
	}
	
	public boolean isHorseNameAllowed() {
		return getMaximumHorseNameLength() != 0;
	}
	
	public boolean isHorseNameBanned(String horseName) {
		List<String> bannedNameList = zh.getConfig().getStringList("HorseNames.banned-names");
		for (String bannedName : bannedNameList) {
			if (horseName.toLowerCase().contains(bannedName.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isHorseNameRequired() {
		return getMinimumHorseNameLength() > 0;
	}
	
	public boolean isLanguageAvailable(String language) {
		return getAvailableLanguages().contains(language);
	}
	
	public boolean isRandomHorseNameEnabled() {
		return zh.getConfig().getBoolean("HorseNames.give-random-names", false);
	}
	
	public boolean isWorldCrossable(World world) {
		return zh.getConfig().getBoolean("Worlds." + world.getName() + ".crossable", false);
	}
	
	public boolean isWorldEnabled(World world) {
		return zh.getConfig().getBoolean("Worlds." + world.getName() + ".enabled", false);
	}
	
	public boolean shouldClaimOnTame() {
		return zh.getConfig().getBoolean("Settings.claim-on-tame", false);
	}
	
	public boolean shouldLockOnClaim() {
		return zh.getConfig().getBoolean("Settings.lock-on-claim", false);
	}
	
	public boolean shouldProtectOnClaim() {
		return zh.getConfig().getBoolean("Settings.protect-on-claim", false);
	}
	
	public boolean shouldShareOnClaim() {
		return zh.getConfig().getBoolean("Settings.share-onclaim", false);
	}
	
	private boolean checkConformity() {
		boolean conform = true;
		if (!checkCommandsConformity()) {
			conform = false;
		}
		if (!checkGroupsConformity()) {
			conform = false;
		}
		if (!checkHorseNamesConformity()) {
			conform = false;
		}
		if (!checkLanguagesConformity()) {
			conform = false;
		}
		if (!checkProtectionsConformity()) {
			conform = false;
		}
		if (!checkSettingsConformity()) {
			conform = false;
		}
		if (!checkWorldsConformity()) {
			conform = false;
		}
		return conform;
	}
	
	private boolean checkCommandsConformity() {
		boolean conform = true;
		ConfigurationSection cs = zh.getConfig().getConfigurationSection("Commands");
		if (cs != null) {
			List<String> exactCommandList = zh.getCmdM().getCommandList();
			Set<String> configCommandList = cs.getKeys(false);
			for (int i=0; i<exactCommandList.size(); i++) {
				String command = exactCommandList.get(i);
				if (configCommandList.contains(command)) {
					String commandCost = zh.getConfig().getString("Commands." + command + ".cost");
					if (commandCost != null) {
						int value = Integer.parseInt(commandCost);
						if (value < 0) {
							zh.getLogger().severe("The cost of the command \"" + command + "\" must be positive !");
							conform = false;
						}
					}
					else {
						zh.getLogger().severe("The \"Commands." + command + ".cost\" option is missing from the config !");
						conform = false;
					}
				}
				else {
					zh.getLogger().severe("The \"Commands." + command + "\" option is missing from the config !");
					conform = false;
				}
			}
		}
		else {
			zh.getLogger().severe("The \"Commands\" section is missing from the config !");
			conform = false;
		}
		return conform;
	}
	
	private boolean checkGroupsConformity() {
		boolean conform = true;
		ConfigurationSection cs = zh.getConfig().getConfigurationSection("Groups");
		if (cs != null) {
			for (String group : cs.getKeys(false)) {
				String color = zh.getConfig().getString("Groups." + group + ".color");
		        if (color != null) {
					if (!zh.getMM().isColor(color)) {
		        		zh.getLogger().severe("The color \"" + color + "\" of the group \"" + group + "\" is not a color !");
		        		conform = false;
		        	}
		        }
		        String claimsLimit = zh.getConfig().getString("Groups." + group + ".claims-limit");
		        if (claimsLimit != null) {
		        	int value = Integer.parseInt(claimsLimit);
		        	if (value < 0 && value != -1) {
						zh.getLogger().severe("The claims-limit of the group \"" + group + "\" must be positive or -1 !");
						conform = false;
					}
		        }
			}
		}
		else {
			zh.getLogger().severe("The \"Groups\" section is missing from the config !");
			conform = false;
		}
		return conform;
	}
	
	private boolean checkHorseNamesConformity() {
		boolean conform = true;
		if (!zh.getConfig().isSet("HorseNames.default-name")) {
			zh.getLogger().severe("The \"HorseNames.default-name\" option is missing from the config !");
			conform = false;
		}
		if (!zh.getConfig().isSet("HorseNames.give-random-names")) {
			zh.getLogger().severe("The \"HorseNames.give-random-names\" option is missing from the config !");
			conform = false;
		}
		String maximumHorseNameLength = zh.getConfig().getString("HorseNames.maximum-length");
		if (maximumHorseNameLength != null) {
			int value = Integer.parseInt(maximumHorseNameLength);
			if (value < 0 && value != -1) {
				zh.getLogger().severe("The \"HorseNames.maximum-length\" value must be positive or -1 !");
				conform = false;
			}
		}
		else {
			zh.getLogger().severe("The \"HorseNames.maximum-length\" option is missing from the config !");
			conform = false;
		}
		String minimumHorseNameLength = zh.getConfig().getString("HorseNames.minimum-length");
		if (minimumHorseNameLength != null) {
			int value = Integer.parseInt(minimumHorseNameLength);
			if (value < 0) {
				zh.getLogger().severe("The \"minimum-horseName-length\" value must be positive !");
				conform = false;
			}
		}
		else {
			zh.getLogger().severe("The \"HorseNames.minimum-length\" option is missing from the config !");
			conform = false;
		}
		if (conform) {
			if (Integer.parseInt(maximumHorseNameLength) < Integer.parseInt(minimumHorseNameLength) && Integer.parseInt(maximumHorseNameLength) != -1) {
				zh.getLogger().severe("The \"HorseNames.maximum-length\" must be greater than the \"HorseNames.minimum-length\" !");
				conform = false;
			}
		}
		List<String> bannedNameList = zh.getConfig().getStringList("HorseNames.banned-names");
		if (bannedNameList == null) {
			zh.getLogger().severe("The \"HorseNames.banned-names\" list is missing from the config !");
			conform = false;
		}
		List<String> randomNameList = zh.getConfig().getStringList("HorseNames.random-names");
		if (randomNameList == null || (randomNameList.size() == 0 && isRandomHorseNameEnabled())) {
			zh.getLogger().severe("The \"HorseNames.random-names\" list is missing from the config !");
			conform = false;
		}
		if (conform) {
			for (String bannedName : bannedNameList) {
				if (randomNameList.contains(bannedName)) {
					zh.getLogger().severe("The banned name \"" + bannedName + "\" can't exist in the random-names list !");
					conform = false;
				}
			}
		}
		return conform;
	}
	
	private boolean checkLanguagesConformity() {
		boolean conform = true;
		if (!zh.getConfig().isSet("Languages.default")) {
			zh.getLogger().severe("The \"Languages.default\" option is missing from the config !");
			conform = false;
		}
		List<String> availableLanguageList = zh.getConfig().getStringList("Languages.available");
		if (availableLanguageList == null || availableLanguageList.size() == 0) {
			zh.getLogger().severe("The \"Languages.available\" list is missing from the config !");
			conform = false;
		}
		else if (!availableLanguageList.contains(getDefaultLanguage())) {
			zh.getLogger().severe("The \"Languages.available\" list must contain the default language !");
			conform = false;
		}
		return conform;
	}
	
	private boolean checkProtectionsConformity() {
		boolean conform = true;
		// TODO
		return conform;
	}
	
	private boolean checkSettingsConformity() {
		boolean conform = true;
		if (!zh.getConfig().isSet("Settings.mute-console")) {
			zh.getLogger().severe("The \"Settings.mute-console\" option is missing from the config !");
			conform = false;
		}
		if (!zh.getConfig().isSet("Settings.claim-on-tame")) {
			zh.getLogger().severe("The \"Settings.claim-on-tame\" option is missing from the config !");
			conform = false;
		}
		if (!zh.getConfig().isSet("Settings.lock-on-claim")) {
			zh.getLogger().severe("The \"Settings.lock-on-claim\" option is missing from the config !");
			conform = false;
		}
		if (!zh.getConfig().isSet("Settings.protect-on-claim")) {
			zh.getLogger().severe("The \"Settings.protect-on-claim\" option is missing from the config !");
			conform = false;
		}
		if (!zh.getConfig().isSet("Settings.share-on-claim")) {
			zh.getLogger().severe("The \"Settings.share-on-claim\" option is missing from the config !");
			conform = false;
		}
		boolean lockOnClaim = zh.getConfig().getBoolean("Settings.lock-on-claim");
		boolean shareOnClaim = zh.getConfig().getBoolean("Settings.share-on-claim");
		if (conform && (lockOnClaim && shareOnClaim)) {
			zh.getLogger().severe("The values of \"Settings.lock-on-claim\" and \"share-on-claim\" can't be both \"true\" !");
			conform = false;
		}
		return conform;
	}
	
	private boolean checkWorldsConformity() {
		boolean conform = true;
		ConfigurationSection cs = zh.getConfig().getConfigurationSection("Worlds");
		if (cs != null) {
			for (String world : cs.getKeys(false)) {
				if (!zh.getConfig().isSet("Worlds." + world + ".enabled")) {
					zh.getLogger().severe("The \"Worlds." + world + ".enabled\" option is missing from the config !");
		        	conform = false;
		        }
				if (!zh.getConfig().isSet("Worlds." + world + ".crossable")) {
					zh.getLogger().severe("The \"Worlds." + world + ".crossable\" option is missing from the config !");
		        	conform = false;
		        }
			}
		}
		else {
			zh.getLogger().severe("The \"Worlds\" section is missing from the config !");
			conform = false;
		}
		return conform;
	}

}
