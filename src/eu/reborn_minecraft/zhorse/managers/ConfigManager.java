package eu.reborn_minecraft.zhorse.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.CommandEnum;
import eu.reborn_minecraft.zhorse.enums.DatabaseEnum;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.utils.Utf8YamlConfiguration;

public class ConfigManager {
	
	private static final String CONFIG_PATH = "config.yml";
	private static final int HORSE_NAME_LENGTH_LIMIT = 36; // Limited by DB
	
	private ZHorse zh;
	private FileConfiguration config;
	
	public ConfigManager(ZHorse zh) {
		this.zh = zh;
		File configFile = new File(zh.getDataFolder(), CONFIG_PATH);
    	if (!configFile.exists()) {
			zh.getLogger().info(CONFIG_PATH + " is missing... Creating it.");
			zh.saveResource(CONFIG_PATH, false);
		}
    	config = Utf8YamlConfiguration.loadConfiguration(configFile);
	}

	public List<String> getAvailableLanguages() {
		List<String> availableLanguages = config.getStringList(KeyWordEnum.languagesPrefix.getValue() + KeyWordEnum.availableSuffix.getValue());
		if (availableLanguages == null || availableLanguages.isEmpty()) {
			availableLanguages = new ArrayList<String>();
			availableLanguages.add(getDefaultLanguage());
		}
		return availableLanguages;
	}
	
	public int getClaimsLimit(UUID playerUUID) {
		int claimsLimit = 0;
		if (playerUUID != null) {
			String groupName = getGroupName(playerUUID);
			if (groupName != null) {
				claimsLimit = config.getInt(KeyWordEnum.groupsPrefix.getValue() + groupName + KeyWordEnum.claimsLimitSuffix.getValue(), 0);
				if (claimsLimit < 0 && claimsLimit != -1) {
					claimsLimit = 0;
				}
			}
		}
		return claimsLimit;
	}
	
	public int getCommandCost(String command) {
		int commandCost = 0;
		if (command != null) {
			commandCost = config.getInt(KeyWordEnum.commandsPrefix.getValue() + command + KeyWordEnum.costSuffix.getValue(), 0);
		}
		return Math.max(commandCost, 0);
	}
	
	public String getDatabaseFileName() {
		return config.getString(KeyWordEnum.databasesPrefix.getValue() + KeyWordEnum.sqliteConfig.getValue() + KeyWordEnum.filenameSuffix.getValue());
	}
	
	public String getDatabaseHost() {
		return config.getString(KeyWordEnum.databasesPrefix.getValue() + KeyWordEnum.mysqlConfig.getValue() + KeyWordEnum.hostSuffix.getValue());
	}
	
	public String getDatabaseName() {
		return config.getString(KeyWordEnum.databasesPrefix.getValue() + KeyWordEnum.mysqlConfig.getValue() + KeyWordEnum.databaseSuffix.getValue());
	}
	
	public String getDatabasePassword() {
		return config.getString(KeyWordEnum.databasesPrefix.getValue() + KeyWordEnum.mysqlConfig.getValue() + KeyWordEnum.passwordSuffix.getValue());
	}
	
	public int getDatabasePort() {
		return config.getInt(KeyWordEnum.databasesPrefix.getValue() + KeyWordEnum.mysqlConfig.getValue() + KeyWordEnum.portSuffix.getValue());
	}
	
	public String getDatabaseTablePrefix() {
		return config.getString(KeyWordEnum.databasesPrefix.getValue() + KeyWordEnum.mysqlConfig.getValue() + KeyWordEnum.tablePrefixSuffix.getValue());
	}
	
	public DatabaseEnum getDatabaseType() {
		String databaseType = config.getString(KeyWordEnum.databasesPrefix.getValue() + KeyWordEnum.typeSuffix.getValue());
		if (databaseType.equalsIgnoreCase(DatabaseEnum.MYSQL.getName())) {
			return DatabaseEnum.MYSQL;
		}
		else if (databaseType.equalsIgnoreCase(DatabaseEnum.SQLITE.getName())) {
			return DatabaseEnum.SQLITE;
		}
		else if (databaseType.equalsIgnoreCase(DatabaseEnum.YAML.getName())) {
			return DatabaseEnum.YAML;
		}
		return null;
	}
	
	public String getDatabaseUser() {
		return config.getString(KeyWordEnum.databasesPrefix.getValue() + KeyWordEnum.mysqlConfig.getValue() + KeyWordEnum.userSuffix.getValue());
	}
	
	public String getDefaultHorseName() {
		return config.getString(KeyWordEnum.horsenames.getValue() + KeyWordEnum.defaultNameSuffix.getValue(), "NotSet");
	}
	
	public String getDefaultLanguage() {
		return config.getString(KeyWordEnum.languages.getValue() + KeyWordEnum.defaultSuffix.getValue(), null);
	}
	
	private String getExactGroupName(String groupName) {
		if (groupName != null) {
			ConfigurationSection cs = config.getConfigurationSection(KeyWordEnum.groups.getValue());
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
	
	public String getGroupColorCode(UUID playerUUID) {
		String colorCode = "<WHITE>";
		if (playerUUID != null) {
			String groupName = getGroupName(playerUUID);
			if (groupName != null) {
				colorCode = config.getString(KeyWordEnum.groupsPrefix.getValue() + groupName + KeyWordEnum.colorSuffix.getValue(), colorCode);
		    }
		}
		return colorCode;
	}
	
	private String getGroupName(UUID playerUUID) {
		String groupName = null;
		if (playerUUID != null) {
			Player p = zh.getServer().getPlayer(playerUUID);
			if (p != null && p.hasPlayedBefore()) {
				groupName = zh.getPM().getPrimaryGroup(p);
			}
			else {
				OfflinePlayer op = zh.getServer().getOfflinePlayer(playerUUID);
				if (op != null && op.hasPlayedBefore()) {
					String world = zh.getServer().getWorlds().get(0).getName();
					groupName = zh.getPM().getPrimaryGroup(world, op);
				}
			}
			groupName = getExactGroupName(groupName);
			if (p != null && p.hasPlayedBefore() && (groupName == null || !config.contains(KeyWordEnum.groupsPrefix.getValue() + groupName))) {
				groupName = getSurrogateGroupName(p);
			}
		}
		return groupName;
	}
	
	public int getMaximumHorseNameLength() {
		return config.getInt(KeyWordEnum.horsenamesPrefix.getValue() + KeyWordEnum.maximumLength.getValue(), 0);
	}
	
	public int getMinimumHorseNameLength() {
		return Math.max(config.getInt(KeyWordEnum.horsenamesPrefix.getValue() + KeyWordEnum.minimumLength.getValue(), 0), 0);
	}
	
	public int getMaximumRangeHere() {
		return config.getInt(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.hereMaxRange.getValue(), -1);
	}
	
	public int getMaximumRangeTp() {
		return config.getInt(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.tpMaxRange.getValue(), -1);
	}
	
	public String getRandomHorseName() {
		String randomHorseName = null;
		Random random = new Random();
		List<String> randomHorseNameList = config.getStringList(KeyWordEnum.horsenamesPrefix.getValue() + KeyWordEnum.randomNames.getValue());
		if (!(randomHorseNameList == null || randomHorseNameList.size() == 0)) {
			randomHorseName = randomHorseNameList.get(random.nextInt(randomHorseNameList.size()));
		}
		return randomHorseName;
	}
	
	private String getSurrogateGroupName(Player p) {
		if (p != null) {
			ConfigurationSection cs = config.getConfigurationSection(KeyWordEnum.groups.getValue());
			if (cs != null) {
				for (String groupName : cs.getKeys(false)) {
					String permission = config.getString(KeyWordEnum.groupsPrefix.getValue() + groupName + KeyWordEnum.permissionSuffix.getValue(), null);
					if (permission != null && zh.getPM().has(p, permission)) {
						return groupName;
					}
				}
			}
		}
		return null;
	}
	
	public boolean isAutoAdminModeEnabled(String command) {
		return command != null && config.getBoolean(KeyWordEnum.commandsPrefix.getValue() + command + KeyWordEnum.autoAdminSuffix.getValue(), false);
	}
	
	public boolean isColorBypassEnabled(UUID playerUUID) {
		if (playerUUID != null) {
			String groupName = getGroupName(playerUUID);
			if (groupName != null) {
				return config.getBoolean(KeyWordEnum.groupsPrefix.getValue() + groupName + KeyWordEnum.colorBypassSuffix.getValue(), false);
			}
		}
		return false;
	}
	
	public boolean isConsoleMuted() {
		return config.getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.muteConsole.getValue(), false);
	}
	
	public boolean isHorseNameAllowed() {
		return getMaximumHorseNameLength() != 0;
	}
	
	public boolean isHorseNameBanned(String horseName) {
		if (horseName != null) {
			List<String> bannedNameList = config.getStringList(KeyWordEnum.horsenamesPrefix + KeyWordEnum.bannedNames.getValue());
			for (String bannedName : bannedNameList) {
				if (horseName.toLowerCase().contains(bannedName.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isHorseNameRequired() {
		return getMinimumHorseNameLength() > 0;
	}
	
	public boolean isLanguageAvailable(String language) {
		return language != null && getAvailableLanguages().contains(language);
	}
	
	public boolean isLeashOnDeadHorseAllowed() {
		return config.getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.allowLeashOnDeadHorse.getValue(), true);
	}
	
	public boolean isProtectionEnabled(String protection) {
		return protection != null && config.getBoolean(KeyWordEnum.protectionsPrefix.getValue() + protection + KeyWordEnum.enabledSuffix.getValue(), false);
	}
	
	public boolean isRandomHorseNameEnabled() {
		return config.getBoolean(KeyWordEnum.horsenamesPrefix.getValue() + KeyWordEnum.giveRandomNames.getValue(), false);
	}
	
	public boolean isWorldCrossable(World world) {
		return world != null && config.getBoolean(KeyWordEnum.worldsPrefix.getValue() + world.getName() + KeyWordEnum.crossableSuffix.getValue(), false);
	}
	
	public boolean isWorldEnabled(World world) {
		return world != null && config.getBoolean(KeyWordEnum.worldsPrefix.getValue() + world.getName() + KeyWordEnum.enabledSuffix.getValue(), false);
	}
	
	public boolean shouldBlockLeashedTeleport() {
		return config.getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.blockLeashedTeleport.getValue(), false);
	}
	
	public boolean shouldBlockMountedTeleport() {
		return config.getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.blockMountedTeleport.getValue(), false);
	}
	
	public boolean shouldClaimOnTame() {
		return config.getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.claimOnTame.getValue(), false);
	}
	
	public boolean shouldLockOnClaim() {
		return config.getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.lockOnClaim.getValue(), false);
	}
	
	public boolean shouldProtectOnClaim() {
		return config.getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.protectOnClaim.getValue(), false);
	}
	
	public boolean shouldShareOnClaim() {
		return config.getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.shareOnClaim.getValue(), false);
	}
	
	public boolean shouldUseOldTeleportMethod() {
		return config.getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.useOldTeleportMethod.getValue(), false);
	}
	
	public boolean shouldUseOldTrackingMethod() {
		return config.getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.useOldTrackingMethod.getValue(), false);
	}
	
	public boolean shouldUseVanillaStats() {
		return config.getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.useVanillaStats.getValue(), true);
	}
	
	public boolean checkConformity() {
		if (!(checkCommandsConformity()
				&& checkDatabaseConformity()
				&& checkGroupsConformity()
				&& checkHorseNamesConformity()
				&& checkLanguagesConformity()
				&& checkProtectionsConformity()
				&& checkSettingsConformity()
				&& checkWorldsConformity())) {
			zh.getLogger().severe("Fix that or delete config.yml and reload ZHorse.");
			return false;
		}
		return true;
	}
	
	private boolean checkCommandsConformity() {
		boolean conform = true;
		ConfigurationSection cs = config.getConfigurationSection("Commands");
		if (cs != null) {
			List<String> exactCommandList = CommandEnum.getCommandNameList();
			for (String command : cs.getKeys(false)) {
				if (exactCommandList.contains(command)) {
					if (!config.isSet("Commands." + command + ".auto-admin")) {
						zh.getLogger().severe("The \"Commands." + command + ".auto-admin\" option is missing from the config !");
			        	conform = false;
			        }
					if (!config.isSet("Commands." + command + ".cost")) {
						zh.getLogger().severe("The \"Commands." + command + ".cost\" option is missing from the config !");
						conform = false;
					}
					else {
						int commandCost = config.getInt("Commands." + command + ".cost", 0);
						if (commandCost < 0) {
							zh.getLogger().severe("The cost of the command \"" + command + "\" must be positive !");
							conform = false;
						}
					}
				}
				else {
					zh.getLogger().severe("The command \"Commands." + command + "\" is not a ZHorse command !");
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
	
	private boolean checkDatabaseConformity() {
		boolean conform = true;
		ConfigurationSection cs = config.getConfigurationSection("Databases");
		if (cs != null) {
			if (!config.isSet("Databases.type")) {
				zh.getLogger().severe("The \"Databases.type\" option is missing from the config !");
	        	conform = false;
	        }
			else if (getDatabaseType().equals(DatabaseEnum.MYSQL)) {
				if (!config.isSet("Databases.mysql-config.host")) {
					zh.getLogger().severe("The \"Databases.mysql-config.host\" option is missing from the config !");
					conform = false;
				}
				if (!config.isSet("Databases.mysql-config.port")) {
					zh.getLogger().severe("The \"Databases.mysql-config.port\" option is missing from the config !");
					conform = false;
				}
				if (!config.isSet("Databases.mysql-config.user")) {
					zh.getLogger().severe("The \"Databases.mysql-config.user\" option is missing from the config !");
					conform = false;
				}
				if (!config.isSet("Databases.mysql-config.password")) {
					zh.getLogger().severe("The \"Databases.mysql-config.password\" option is missing from the config !");
					conform = false;
				}
				if (!config.isSet("Databases.mysql-config.database")) {
					zh.getLogger().severe("The \"Databases.mysql-config.database\" option is missing from the config !");
					conform = false;
				}
				if (!config.isSet("Databases.mysql-config.table-prefix")) {
					zh.getLogger().severe("The \"Databases.mysql-config.table-prefix\" option is missing from the config !");
					conform = false;
				}
	        }
			else if (getDatabaseType().equals(DatabaseEnum.SQLITE)) {
				if (!config.isSet("Databases.sqlite-config.filename")) {
					zh.getLogger().severe("The \"Databases.sqlite-config.filename\" option is missing from the config !");
					conform = false;
				}
			}
			else {
				zh.getLogger().severe("The database type must be MySQL or SQLite !");
				conform = false;
			}
		}
		else {
			zh.getLogger().severe("The \"Databases\" section is missing from the config !");
			conform = false;
		}
		return conform;
	}
	
	private boolean checkGroupsConformity() {
		boolean conform = true;
		ConfigurationSection cs = config.getConfigurationSection("Groups");
		if (cs != null) {
			for (String group : cs.getKeys(false)) {
				String color = config.getString("Groups." + group + ".color");
		        if (color != null) {
					if (!MessageManager.isColor(color)) {
		        		zh.getLogger().severe("The color \"" + color + "\" used for the group \"" + group + "\" is not a color !");
		        		conform = false;
		        	}
		        }
		        int claimsLimit = config.getInt("Groups." + group + ".claims-limit", 0);
		        if (claimsLimit < 0 && claimsLimit != -1) {
					zh.getLogger().severe("The claims-limit of the group \"" + group + "\" must be positive or -1 !");
					conform = false;
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
		if (!config.isSet("HorseNames.default-name")) {
			zh.getLogger().severe("The \"HorseNames.default-name\" option is missing from the config !");
			conform = false;
		}
		if (!config.isSet("HorseNames.give-random-names")) {
			zh.getLogger().severe("The \"HorseNames.give-random-names\" option is missing from the config !");
			conform = false;
		}
		int maximumHorseNameLength = 0;
		if (!config.isSet("HorseNames.maximum-length")) {
			zh.getLogger().severe("The \"HorseNames.maximum-length\" option is missing from the config !");
			conform = false;
		}
		else {
			maximumHorseNameLength = config.getInt("HorseNames.maximum-length");
			if (maximumHorseNameLength < 0) {
				zh.getLogger().severe("The \"HorseNames.maximum-length\" value must be positive !");
				conform = false;
			}
			else if (maximumHorseNameLength > HORSE_NAME_LENGTH_LIMIT) {
				zh.getLogger().severe(String.format("The \"HorseNames.maximum-length\" value must be less than %d !", HORSE_NAME_LENGTH_LIMIT));
				conform = false;
			}
		}
		int minimumHorseNameLength = 0;
		if (!config.isSet("HorseNames.minimum-length")) {
			zh.getLogger().severe("The \"HorseNames.minimum-length\" option is missing from the config !");
			conform = false;
		}
		else {
			minimumHorseNameLength = config.getInt("HorseNames.minimum-length");
			if (minimumHorseNameLength < 0) {
				zh.getLogger().severe("The \"minimum-horseName-length\" value must be positive !");
				conform = false;
			}
		}
		if (conform && maximumHorseNameLength < minimumHorseNameLength) {
			zh.getLogger().severe("The \"HorseNames.maximum-length\" must be greater than the \"HorseNames.minimum-length\" !");
			conform = false;
		}
		List<String> bannedNameList = config.getStringList("HorseNames.banned-names");
		if (bannedNameList == null) {
			zh.getLogger().severe("The \"HorseNames.banned-names\" list is missing from the config !");
			conform = false;
		}
		List<String> randomNameList = config.getStringList("HorseNames.random-names");
		if (randomNameList == null || (randomNameList.size() == 0 && isRandomHorseNameEnabled())) {
			zh.getLogger().severe("The \"HorseNames.random-names\" list is missing from the config !");
			conform = false;
		}
		if (conform) {
			for (String bannedName : bannedNameList) {
				if (randomNameList.contains(bannedName)) {
					zh.getLogger().severe("The banned name \"" + bannedName + "\" can't be part of the random-names list !");
					conform = false;
				}
			}
		}
		return conform;
	}
	
	private boolean checkLanguagesConformity() {
		boolean conform = true;
		if (!config.isSet("Languages.default")) {
			zh.getLogger().severe("The \"Languages.default\" option is missing from the config !");
			conform = false;
		}
		List<String> availableLanguageList = config.getStringList("Languages.available");
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
		ConfigurationSection cs = config.getConfigurationSection("Protections");
		if (cs != null) {
			Set<String> registeredDamageCauseList = cs.getKeys(false);
			List<String> existingDamageCauseList = new ArrayList<String>();
			for (DamageCause existingDamageCause : DamageCause.values()) {
				existingDamageCauseList.add(existingDamageCause.name());
			}
			existingDamageCauseList.add("OWNER_ATTACK");
			existingDamageCauseList.add("PLAYER_ATTACK");
			for (String registeredDamageCause : registeredDamageCauseList) {
				if (existingDamageCauseList.contains(registeredDamageCause)) {
					if (!config.isSet("Protections." + registeredDamageCause + ".enabled")) {
						zh.getLogger().severe("The \"Protections." + registeredDamageCause + ".enabled\" option is missing from the config !");
			        	conform = false;
			        }
				}
				else {
					zh.getLogger().severe("The damage cause \"Protections." + registeredDamageCause + "\" is not a valid damage cause !");
					conform = false;
				}
			}
		}
		else {
			zh.getLogger().severe("The \"Protections\" section is missing from the config !");
			conform = false;
		}
		return conform;
	}
	
	private boolean checkSettingsConformity() {
		boolean conform = true;
		if (!config.isSet("Settings.allow-leash-on-dead-horse")) {
			zh.getLogger().severe("The \"Settings.allow-leash-on-dead-horse\" option is missing from the config !");
			conform = false;
		}
		if (!config.isSet("Settings.block-leashed-teleport")) {
			zh.getLogger().severe("The \"Settings.block-leashed-teleport\" option is missing from the config !");
			conform = false;
		}
		if (!config.isSet("Settings.block-mounted-teleport")) {
			zh.getLogger().severe("The \"Settings.block-mounted-teleport\" option is missing from the config !");
			conform = false;
		}
		if (!config.isSet("Settings.claim-on-tame")) {
			zh.getLogger().severe("The \"Settings.claim-on-tame\" option is missing from the config !");
			conform = false;
		}
		if (!config.isSet("Settings.lock-on-claim")) {
			zh.getLogger().severe("The \"Settings.lock-on-claim\" option is missing from the config !");
			conform = false;
		}
		if (!config.isSet("Settings.protect-on-claim")) {
			zh.getLogger().severe("The \"Settings.protect-on-claim\" option is missing from the config !");
			conform = false;
		}
		if (!config.isSet("Settings.share-on-claim")) {
			zh.getLogger().severe("The \"Settings.share-on-claim\" option is missing from the config !");
			conform = false;
		}
		if (!config.isSet("Settings.mute-console")) {
			zh.getLogger().severe("The \"Settings.mute-console\" option is missing from the config !");
			conform = false;
		}
		if (!config.isSet("Settings.use-old-teleport-method")) {
			zh.getLogger().severe("The \"Settings.use-old-teleport-method\" option is missing from the config !");
			conform = false;
		}
		if (!config.isSet("Settings.use-old-tracking-method")) {
			zh.getLogger().severe("The \"Settings.use-old-tracking-method\" option is missing from the config !");
			conform = false;
		}
		if (!config.isSet("Settings.use-vanilla-stats")) {
			zh.getLogger().severe("The \"Settings.use-vanilla-stats\" option is missing from the config !");
			conform = false;
		}
		boolean lockOnClaim = config.getBoolean("Settings.lock-on-claim");
		boolean shareOnClaim = config.getBoolean("Settings.share-on-claim");
		if (conform && (lockOnClaim && shareOnClaim)) {
			zh.getLogger().severe("The values of \"Settings.lock-on-claim\" and \"share-on-claim\" can't be both \"true\" !");
			conform = false;
		}
		return conform;
	}
	
	private boolean checkWorldsConformity() {
		boolean conform = true;
		ConfigurationSection cs = config.getConfigurationSection("Worlds");
		if (cs != null) {
			for (String world : cs.getKeys(false)) {
				if (!config.isSet("Worlds." + world + ".enabled")) {
					zh.getLogger().severe("The \"Worlds." + world + ".enabled\" option is missing from the config !");
		        	conform = false;
		        }
				if (!config.isSet("Worlds." + world + ".crossable")) {
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
