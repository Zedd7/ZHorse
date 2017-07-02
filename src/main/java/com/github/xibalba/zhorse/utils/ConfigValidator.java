package com.github.xibalba.zhorse.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.xibalba.zhorse.ZHorse;
import com.github.xibalba.zhorse.enums.CommandEnum;
import com.github.xibalba.zhorse.enums.DatabaseEnum;
import com.github.xibalba.zhorse.enums.KeyWordEnum;
import com.github.xibalba.zhorse.managers.EventManager;
import com.github.xibalba.zhorse.managers.MessageManager;

public class ConfigValidator extends YamlResourceValidator {
	
	private static final int HORSE_NAME_LENGTH_LIMIT = 36; // Limited by DB
	
	public ConfigValidator(ZHorse zh, FileConfiguration config, FileConfiguration model, File configFile, String fileName) {
		super(zh, config, model, configFile, fileName);
	}
	
	public boolean validate() {
		valid = true;
		validateCommandsSection();
		validateDatabaseSection();
		validateGroupsSection();
		validateHorseNamesSection();
		validateLanguagesSection();
		validateProtectionsSection();
		validateSettingsSection();
		validateWorldsSection();
		if (!valid) {
			invalidate(String.format("Fix the above issues or delete %s and reload ZHorse.", fileName), false);
			return false;
		}
		return true;
	}
	
	private void validateCommandsSection() {
		if (validateSectionSet(KeyWordEnum.COMMANDS.getValue())) {
			ConfigurationSection cs = resource.getConfigurationSection(KeyWordEnum.COMMANDS.getValue());
			List<String> exactCommandList = CommandEnum.getCommandNameList();
			for (String command : cs.getKeys(false)) {
				if (exactCommandList.contains(command)) {
					validateOptionSet(KeyWordEnum.COMMANDS_PREFIX.getValue() + command + KeyWordEnum.AUTO_ADMIN_SUFFIX.getValue());
					if (validateOptionSet(KeyWordEnum.COMMANDS_PREFIX.getValue() + command + KeyWordEnum.COOLDOWN_SUFFIX.getValue())) {
						validatePositive(KeyWordEnum.COMMANDS_PREFIX.getValue() + command + KeyWordEnum.COOLDOWN_SUFFIX.getValue());
					}
					if (validateOptionSet(KeyWordEnum.COMMANDS_PREFIX.getValue() + command + KeyWordEnum.COST_SUFFIX.getValue())) {
						validatePositive(KeyWordEnum.COMMANDS_PREFIX.getValue() + command + KeyWordEnum.COST_SUFFIX.getValue());
					}
				}
				else {
					invalidate(String.format("The command %s is not a ZHorse command !", KeyWordEnum.COMMANDS_PREFIX.getValue() + command), true);
				}
			}
		}
	}

	private void validateDatabaseSection() {
		if (validateSectionSet(KeyWordEnum.DATABASES.getValue())) {
			if (validateOptionSet(KeyWordEnum.TYPE.getValue())) {
				String databaseType = resource.getString(KeyWordEnum.TYPE.getValue());
				if (databaseType.equalsIgnoreCase(DatabaseEnum.MYSQL.getName())) {
					validateOptionSet(KeyWordEnum.HOST.getValue());
					validateOptionSet(KeyWordEnum.PORT.getValue());
					validateOptionSet(KeyWordEnum.USER.getValue());
					validateOptionSet(KeyWordEnum.PASSWORD.getValue());
					validateOptionSet(KeyWordEnum.DATABASE.getValue());
					validateOptionSet(KeyWordEnum.TABLE_PREFIX.getValue());
				}
				else if (databaseType.equalsIgnoreCase(DatabaseEnum.SQLITE.getName())) {
					validateOptionSet(KeyWordEnum.FILE_NAME.getValue());
				}
				else {
					invalidate("The database type must be MySQL or SQLite !", true);
				}
	        }
		}
	}
	
	private void validateGroupsSection() {
		if (validateSectionSet(KeyWordEnum.GROUPS.getValue())) {
			ConfigurationSection cs = resource.getConfigurationSection(KeyWordEnum.GROUPS.getValue());
			for (String group : cs.getKeys(false)) {
				if (validateOptionSet(KeyWordEnum.GROUPS_PREFIX.getValue() + group + KeyWordEnum.COLOR_SUFFIX.getValue())) {
		        	String color = resource.getString(KeyWordEnum.GROUPS_PREFIX.getValue() + group + KeyWordEnum.COLOR_SUFFIX.getValue());
					if (!MessageManager.isColor(color)) {
			        	invalidate(String.format("The color %s used for the group %s is not a color !", color, group), true);
			        }
				}
				validateOptionSet(KeyWordEnum.GROUPS_PREFIX.getValue() + group + KeyWordEnum.COLOR_BYPASS_SUFFIX.getValue());
				if (validateOptionSet(KeyWordEnum.GROUPS_PREFIX.getValue() + group + KeyWordEnum.CLAIMS_LIMIT_SUFFIX.getValue())) {
					validatePositiveOrMinus1(KeyWordEnum.GROUPS_PREFIX.getValue() + group + KeyWordEnum.CLAIMS_LIMIT_SUFFIX.getValue());
				}
			}
		}
	}
	
	private void validateHorseNamesSection() {
		if (validateSectionSet(KeyWordEnum.HORSENAMES.getValue())) {
			int maximumHorseNameLength = 0;
			if (validateOptionSet(KeyWordEnum.MAXIMUM_LENGTH.getValue())) {
				maximumHorseNameLength = resource.getInt(KeyWordEnum.MAXIMUM_LENGTH.getValue());
				if (validatePositive(KeyWordEnum.MAXIMUM_LENGTH.getValue())) {
					validateLessOrEqual(KeyWordEnum.MAXIMUM_LENGTH.getValue(), HORSE_NAME_LENGTH_LIMIT);
				}
			}
			int minimumHorseNameLength = 0;
			if (validateOptionSet(KeyWordEnum.MINIMUM_LENGTH.getValue())) {
				minimumHorseNameLength = resource.getInt(KeyWordEnum.MINIMUM_LENGTH.getValue());
				validatePositive(KeyWordEnum.MINIMUM_LENGTH.getValue());
			}
			if (valid && maximumHorseNameLength < minimumHorseNameLength) {
				invalidate(String.format("The %s must be greater than the %s !", KeyWordEnum.MAXIMUM_LENGTH.getValue(), KeyWordEnum.MINIMUM_LENGTH.getValue()), true);
			}
			validateOptionSet(KeyWordEnum.DEFAULT_NAME.getValue());
			boolean randomHorseNameEnabled = false;
			if (validateOptionSet(KeyWordEnum.GIVE_RANDOM_NAMES.getValue())) {
				randomHorseNameEnabled = resource.getBoolean(KeyWordEnum.GIVE_RANDOM_NAMES.getValue());
			}
			List<String> randomNameList = new ArrayList<>();
			if (validateListSet(KeyWordEnum.RANDOM_NAMES.getValue())) {
				randomNameList = resource.getStringList(KeyWordEnum.RANDOM_NAMES.getValue());
				if (randomHorseNameEnabled) {
					validateListNotEmpty(KeyWordEnum.RANDOM_NAMES.getValue());
				}
			}
			if (validateListSet(KeyWordEnum.BANNED_NAMES.getValue())) {
				for (String bannedName : resource.getStringList(KeyWordEnum.BANNED_NAMES.getValue())) {
					if (randomNameList.contains(bannedName)) {
						invalidate(String.format("The banned name %s cannot be part of the %s list !", bannedName, KeyWordEnum.RANDOM_NAMES.getValue()), true);
					}
				}
			}
		}
	}
	
	private void validateLanguagesSection() {
		if (validateSectionSet(KeyWordEnum.LANGUAGES.getValue())) {
			String defaultLanguage = null;
			if (validateOptionSet(KeyWordEnum.DEFAULT_LANGUAGE.getValue())) {
				defaultLanguage = resource.getString(KeyWordEnum.DEFAULT_LANGUAGE.getValue());
			}
			if (validateListSet(KeyWordEnum.AVAILABLE_LANGUAGES.getValue())) {
				List<String> availableLanguageList = resource.getStringList(KeyWordEnum.AVAILABLE_LANGUAGES.getValue());
				if (validateListNotEmpty(KeyWordEnum.AVAILABLE_LANGUAGES.getValue())) {
					if (defaultLanguage != null && !availableLanguageList.contains(defaultLanguage)) {
						invalidate(String.format("The %s list must contain the default language !", KeyWordEnum.AVAILABLE_LANGUAGES.getValue()), true);
					}
				}
			}
		}
	}
	
	private void validateProtectionsSection() {
		if (validateSectionSet(KeyWordEnum.PROTECTIONS.getValue())) {
			ConfigurationSection cs = resource.getConfigurationSection(KeyWordEnum.PROTECTIONS.getValue());
			Set<String> registeredDamageCauseList = cs.getKeys(false);
			List<String> existingDamageCauseList = new ArrayList<String>();
			for (DamageCause existingDamageCause : DamageCause.values()) {
				existingDamageCauseList.add(existingDamageCause.name());
			}
			existingDamageCauseList.add(EventManager.CustomAttackType.OWNER.getCode());
			existingDamageCauseList.add(EventManager.CustomAttackType.PLAYER.getCode());
			for (String registeredDamageCause : registeredDamageCauseList) {
				if (existingDamageCauseList.contains(registeredDamageCause)) {
					validateOptionSet(KeyWordEnum.PROTECTIONS_PREFIX.getValue() + registeredDamageCause + KeyWordEnum.ENABLED_SUFFIX.getValue());
				}
				else {
					invalidate(String.format("The damage cause %s is not a valid damage cause !", registeredDamageCause), true);
				}
			}
		}
	}
	
	private void validateSettingsSection() {
		if (validateSectionSet(KeyWordEnum.SETTINGS.getValue())) {
			validateOptionSet(KeyWordEnum.ALLOW_FOAL_RIDING.getValue());
			validateOptionSet(KeyWordEnum.ALLOW_LEASH_ON_UNDEAD_HORSE.getValue());
			validateOptionSet(KeyWordEnum.BLOCK_LEASHED_TELEPORT.getValue());
			validateOptionSet(KeyWordEnum.BLOCK_MOUNTED_TELEPORT.getValue());
			validateOptionSet(KeyWordEnum.CLAIM_ON_TAME.getValue());
			boolean lockOnClaim = false;
			if (validateOptionSet(KeyWordEnum.LOCK_ON_CLAIM.getValue())) {
				lockOnClaim = resource.getBoolean(KeyWordEnum.LOCK_ON_CLAIM.getValue());
			}
			validateOptionSet(KeyWordEnum.PROTECT_ON_CLAIM.getValue());
			boolean shareOnClaim = false;
			if (validateOptionSet(KeyWordEnum.SHARE_ON_CLAIM.getValue())) {
				shareOnClaim = resource.getBoolean(KeyWordEnum.SHARE_ON_CLAIM.getValue());
			}
			if (validateOptionSet(KeyWordEnum.HERE_MAX_RANGE.getValue())) {
				validatePositiveOrMinus1(KeyWordEnum.HERE_MAX_RANGE.getValue());
			}
			if (validateOptionSet(KeyWordEnum.STABLE_MAX_RANGE.getValue())) {
				validatePositiveOrMinus1(KeyWordEnum.STABLE_MAX_RANGE.getValue());
			}
			if (validateOptionSet(KeyWordEnum.TP_MAX_RANGE.getValue())) {
				validatePositiveOrMinus1(KeyWordEnum.TP_MAX_RANGE.getValue());
			}
			validateOptionSet(KeyWordEnum.RESPAWN_MISSING_HORSE.getValue());
			validateOptionSet(KeyWordEnum.RESTORE_INVENTORY.getValue());
			if (validateOptionSet(KeyWordEnum.REZ_STACK_SIZE.getValue())) {
				validatePositive(KeyWordEnum.REZ_STACK_SIZE.getValue());
			}
			validateOptionSet(KeyWordEnum.USE_EXACT_STATS.getValue());
			validateOptionSet(KeyWordEnum.USE_VANILLA_STATS.getValue());
			validateOptionSet(KeyWordEnum.USE_OLD_TELEPORT_METHOD.getValue());
			validateOptionSet(KeyWordEnum.USE_DEFAULT_STABLE.getValue());
			validateOptionSet(KeyWordEnum.DEFAULT_STABLE_LOCATION_WORLD.getValue());
			validateOptionSet(KeyWordEnum.DEFAULT_STABLE_LOCATION_X.getValue());
			validateOptionSet(KeyWordEnum.DEFAULT_STABLE_LOCATION_Y.getValue());
			validateOptionSet(KeyWordEnum.DEFAULT_STABLE_LOCATION_Z.getValue());
			validateOptionSet(KeyWordEnum.MUTE_CONSOLE.getValue());
			if (lockOnClaim && shareOnClaim) {
				invalidate(String.format("The values of %s and %s cannot be both true !", KeyWordEnum.LOCK_ON_CLAIM.getValue(), KeyWordEnum.SHARE_ON_CLAIM.getValue()), true);
			}
		}
	}
	
	private void validateWorldsSection() {
		if (validateSectionSet(KeyWordEnum.WORLDS.getValue())) {
			ConfigurationSection cs = resource.getConfigurationSection(KeyWordEnum.WORLDS.getValue());
			for (String world : cs.getKeys(false)) {
				validateOptionSet(KeyWordEnum.WORLDS_PREFIX.getValue() + world + KeyWordEnum.ENABLED_SUFFIX.getValue());
				validateOptionSet(KeyWordEnum.WORLDS_PREFIX.getValue() + world + KeyWordEnum.CROSSABLE_SUFFIX.getValue());
			}
		}
	}

}
