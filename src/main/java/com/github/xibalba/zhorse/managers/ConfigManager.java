package com.github.xibalba.zhorse.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.github.xibalba.zhorse.ZHorse;
import com.github.xibalba.zhorse.enums.DatabaseEnum;
import com.github.xibalba.zhorse.enums.KeyWordEnum;

public class ConfigManager {

	private ZHorse zh;
	private FileConfiguration config;

	public ConfigManager(ZHorse zh) {
		this.zh = zh;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void setConfig(FileConfiguration config) {
		this.config = config;
	}

	public List<String> getAvailableLanguages() {
		List<String> availableLanguages = config.getStringList(KeyWordEnum.AVAILABLE_LANGUAGES.getValue());
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
				claimsLimit = config.getInt(KeyWordEnum.GROUPS_PREFIX.getValue() + groupName + KeyWordEnum.CLAIMS_LIMIT_SUFFIX.getValue(), 0);
				if (claimsLimit < 0 && claimsLimit != -1) {
					claimsLimit = 0;
				}
			}
		}
		return claimsLimit;
	}

	public int getCommandCooldown(String command) {
		int commandCooldown = 0;
		if (command != null) {
			commandCooldown = config.getInt(KeyWordEnum.COMMANDS_PREFIX.getValue() + command + KeyWordEnum.COOLDOWN_SUFFIX.getValue(), 0);
		}
		return Math.max(commandCooldown, 0);
	}

	public int getCommandCost(String command) {
		int commandCost = 0;
		if (command != null) {
			commandCost = config.getInt(KeyWordEnum.COMMANDS_PREFIX.getValue() + command + KeyWordEnum.COST_SUFFIX.getValue(), 0);
		}
		return Math.max(commandCost, 0);
	}

	public String getDatabaseFileName() {
		return config.getString(KeyWordEnum.FILE_NAME.getValue());
	}

	public String getDatabaseHost() {
		return config.getString(KeyWordEnum.HOST.getValue());
	}

	public String getDatabaseName() {
		return config.getString(KeyWordEnum.DATABASE.getValue());
	}

	public String getDatabasePassword() {
		return config.getString(KeyWordEnum.PASSWORD.getValue());
	}

	public int getDatabasePort() {
		return config.getInt(KeyWordEnum.PORT.getValue(), 0);
	}

	public String getDatabaseTablePrefix() {
		return config.getString(KeyWordEnum.TABLE_PREFIX.getValue());
	}

	public DatabaseEnum getDatabaseType() {
		String databaseType = config.getString(KeyWordEnum.TYPE.getValue());
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
		return config.getString(KeyWordEnum.USER.getValue());
	}

	public String getDefaultHorseName() {
		return config.getString(KeyWordEnum.DEFAULT_NAME.getValue(), "NoDefaultHorseNameSet");
	}

	public String getDefaultLanguage() {
		return config.getString(KeyWordEnum.DEFAULT_LANGUAGE.getValue(), null);
	}

	public Location getDefaultStableLocation() {
		String worldName = config.getString(KeyWordEnum.DEFAULT_STABLE_LOCATION_WORLD.getValue(), zh.getServer().getWorlds().get(0).getName());
		World world = zh.getServer().getWorld(worldName);
		int x = config.getInt(KeyWordEnum.DEFAULT_STABLE_LOCATION_X.getValue(), 0);
		int y = config.getInt(KeyWordEnum.DEFAULT_STABLE_LOCATION_Y.getValue(), 0);
		int z = config.getInt(KeyWordEnum.DEFAULT_STABLE_LOCATION_Z.getValue(), 0);
		return new Location(world, x, y, z);
	}

	public String getGroupColorCode(UUID playerUUID) {
		String colorCode = "<WHITE>";
		if (playerUUID != null) {
			String groupName = getGroupName(playerUUID);
			if (groupName != null) {
				colorCode = config.getString(KeyWordEnum.GROUPS_PREFIX.getValue() + groupName + KeyWordEnum.COLOR_SUFFIX.getValue(), colorCode);
		    }
		}
		return colorCode;
	}

	public int getMaximumHorseNameLength() {
		return config.getInt(KeyWordEnum.MAXIMUM_LENGTH.getValue(), 0);
	}

	public int getMinimumHorseNameLength() {
		return Math.max(config.getInt(KeyWordEnum.MINIMUM_LENGTH.getValue(), 0), 0);
	}

	public int getMaximumRangeHere() {
		return config.getInt(KeyWordEnum.HERE_MAX_RANGE.getValue(), -1);
	}

	public int getMaximumRangeStable() {
		return config.getInt(KeyWordEnum.STABLE_MAX_RANGE.getValue(), -1);
	}

	public int getMaximumRangeTp() {
		return config.getInt(KeyWordEnum.TP_MAX_RANGE.getValue(), -1);
	}

	public String getRandomHorseName() {
		String randomHorseName = null;
		Random random = new Random();
		List<String> randomHorseNameList = config.getStringList(KeyWordEnum.RANDOM_NAMES.getValue());
		if (!(randomHorseNameList == null || randomHorseNameList.size() == 0)) {
			randomHorseName = randomHorseNameList.get(random.nextInt(randomHorseNameList.size()));
		}
		return randomHorseName;
	}

	public int getRezStackMaxSize() {
		return config.getInt(KeyWordEnum.REZ_STACK_SIZE.getValue(), 0);
	}

	public boolean isAutoAdminModeEnabled(String command) {
		return command != null && config.getBoolean(KeyWordEnum.COMMANDS_PREFIX.getValue() + command + KeyWordEnum.AUTO_ADMIN_SUFFIX.getValue(), false);
	}

	public boolean isColorBypassEnabled(UUID playerUUID) {
		if (playerUUID != null) {
			String groupName = getGroupName(playerUUID);
			if (groupName != null) {
				return config.getBoolean(KeyWordEnum.GROUPS_PREFIX.getValue() + groupName + KeyWordEnum.COLOR_BYPASS_SUFFIX.getValue(), false);
			}
		}
		return false;
	}

	public boolean isConsoleMuted() {
		return config.getBoolean(KeyWordEnum.MUTE_CONSOLE.getValue(), false);
	}

	public boolean isFoalRidingAllowed() {
		return config.getBoolean(KeyWordEnum.ALLOW_FOAL_RIDING.getValue(), true);
	}

	public boolean isHorseNameAllowed() {
		return getMaximumHorseNameLength() != 0;
	}

	public boolean isHorseNameBanned(String horseName) {
		if (horseName != null) {
			List<String> bannedNameList = config.getStringList(KeyWordEnum.BANNED_NAMES.getValue());
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

	public boolean isLeashOnUndeadHorseAllowed() {
		return config.getBoolean(KeyWordEnum.ALLOW_LEASH_ON_UNDEAD_HORSE.getValue(), true);
	}

	public boolean isProtectionEnabled(String protection) {
		return protection != null && config.getBoolean(KeyWordEnum.PROTECTIONS_PREFIX.getValue() + protection + KeyWordEnum.ENABLED_SUFFIX.getValue(), false);
	}

	public boolean isRandomHorseNameEnabled() {
		return config.getBoolean(KeyWordEnum.GIVE_RANDOM_NAMES.getValue(), false);
	}

	public boolean isTamingOfUndeadHorseAllowed() {
		return config.getBoolean(KeyWordEnum.ALLOW_TAMING_OF_UNDEAD_HORSE.getValue(), true);
	}

	public boolean isWorldCrossable(World world) {
		return world != null && config.getBoolean(KeyWordEnum.WORLDS_PREFIX.getValue() + world.getName() + KeyWordEnum.CROSSABLE_SUFFIX.getValue(), false);
	}

	public boolean isWorldEnabled(World world) {
		return world != null && config.getBoolean(KeyWordEnum.WORLDS_PREFIX.getValue() + world.getName() + KeyWordEnum.ENABLED_SUFFIX.getValue(), false);
	}

	public boolean shouldBlockLeashedTeleport() {
		return config.getBoolean(KeyWordEnum.BLOCK_LEASHED_TELEPORT.getValue(), false);
	}

	public boolean shouldBlockMountedTeleport() {
		return config.getBoolean(KeyWordEnum.BLOCK_MOUNTED_TELEPORT.getValue(), false);
	}

	public boolean shouldClaimOnTame() {
		return config.getBoolean(KeyWordEnum.CLAIM_ON_TAME.getValue(), false);
	}

	public boolean shouldLockOnClaim() {
		return config.getBoolean(KeyWordEnum.LOCK_ON_CLAIM.getValue(), false);
	}

	public boolean shouldProtectOnClaim() {
		return config.getBoolean(KeyWordEnum.PROTECT_ON_CLAIM.getValue(), false);
	}

	public boolean shouldRespawnMissingHorse() {
		return config.getBoolean(KeyWordEnum.RESPAWN_MISSING_HORSE.getValue(), true);
	}

	public boolean shouldRestoreInventory() {
		return config.getBoolean(KeyWordEnum.RESTORE_INVENTORY.getValue(), true);
	}

	public boolean shouldShareOnClaim() {
		return config.getBoolean(KeyWordEnum.SHARE_ON_CLAIM.getValue(), false);
	}

	public boolean shouldUseExactStats() {
		return config.getBoolean(KeyWordEnum.USE_EXACT_STATS.getValue(), false);
	}

	public boolean shouldUseVanillaStats() {
		return config.getBoolean(KeyWordEnum.USE_VANILLA_STATS.getValue(), true);
	}

	public boolean shouldUseOldTeleportMethod() {
		return config.getBoolean(KeyWordEnum.USE_OLD_TELEPORT_METHOD.getValue(), false);
	}

	public boolean shouldUseDefaultStable() {
		return config.getBoolean(KeyWordEnum.USE_DEFAULT_STABLE.getValue(), false);
	}

	private String getExactGroupName(String groupName) {
		if (groupName != null) {
			ConfigurationSection cs = config.getConfigurationSection(KeyWordEnum.GROUPS.getValue());
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
		if (playerUUID != null) {
			Player p = zh.getServer().getPlayer(playerUUID);
			if (p != null) {
				groupName = zh.getPM().getPrimaryGroup(p);
			}
			else {
				OfflinePlayer op = zh.getServer().getOfflinePlayer(playerUUID);
				if (op.hasPlayedBefore()) {
					String world = zh.getServer().getWorlds().get(0).getName();
					groupName = zh.getPM().getPrimaryGroup(world, op);
				}
			}
			groupName = getExactGroupName(groupName);
			if (p != null && (groupName == null || !config.contains(KeyWordEnum.GROUPS_PREFIX.getValue() + groupName))) {
				groupName = getSurrogateGroupName(p);
			}
		}
		return groupName;
	}

	private String getSurrogateGroupName(Player p) {
		if (p != null) {
			ConfigurationSection cs = config.getConfigurationSection(KeyWordEnum.GROUPS.getValue());
			if (cs != null) {
				for (String groupName : cs.getKeys(false)) {
					String permission = config.getString(KeyWordEnum.GROUPS_PREFIX.getValue() + groupName + KeyWordEnum.PERMISSION_SUFFIX.getValue(), null);
					if (permission != null && zh.getPM().has(p, permission)) {
						return groupName;
					}
				}
			}
		}
		return null;
	}

}
