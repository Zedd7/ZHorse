package eu.reborn_minecraft.zhorse.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;

public class ConfigManager {
	private ZHorse zh;
	
	public ConfigManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public List<String> getAvailableLanguages() {
		List<String> availableLanguages = zh.getConfig().getStringList(KeyWordEnum.languagesPrefix.getValue() + KeyWordEnum.available.getValue());
		if (availableLanguages == null || availableLanguages.isEmpty()) {
			availableLanguages = new ArrayList<String>();
			availableLanguages.add(getDefaultLanguage());
		}
		return availableLanguages;
	}
	
	public int getClaimsLimit(UUID playerUUID) {
		int value = 0;
		if (playerUUID != null) {
			String groupName = getGroupName(playerUUID);
			if (groupName != null) {
				String claimsLimit = zh.getConfig().getString(KeyWordEnum.groupsPrefix.getValue() + groupName + KeyWordEnum.claimsLimitSuffix.getValue());
				if (claimsLimit != null) {
					value = Integer.parseInt(claimsLimit);
					if (value < 0 && value != -1) {
						value = 0;
					}
				}
			}
		}
		return value;
	}
	
	public int getCommandCost(String command) {
		int value = 0;
		if (command != null) {
			String commandCost = zh.getConfig().getString(KeyWordEnum.commandsPrefix.getValue() + command + KeyWordEnum.costSuffix.getValue());
			if (commandCost != null) {
				value = Integer.parseInt(commandCost);
				if (value < 0) {
					value = 0;
				}
			}
		}
		return value;
	}
	
	public String getDefaultHorseName() {
		return zh.getConfig().getString(KeyWordEnum.horsenames.getValue() + KeyWordEnum.defaultNameSuffix.getValue(), "NotSet");
	}
	
	public String getDefaultLanguage() {
		return zh.getConfig().getString(KeyWordEnum.languages.getValue() + KeyWordEnum.defaultSuffix.getValue(), null);
	}
	
	private String getExactGroupName(String groupName) {
		if (groupName != null) {
			ConfigurationSection cs = zh.getConfig().getConfigurationSection(KeyWordEnum.groups.getValue());
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
				colorCode = zh.getConfig().getString(KeyWordEnum.groupsPrefix.getValue() + groupName + KeyWordEnum.colorSuffix.getValue(), colorCode);
		    }
		}
		return colorCode;
	}
	
	private String getGroupName(UUID playerUUID) {
		String groupName = null;
		if (playerUUID != null) {
			Player p = zh.getServer().getPlayer(playerUUID);
			if (p != null && p.hasPlayedBefore()) {
				groupName = zh.getPerms().getPrimaryGroup(p);
			}
			else {
				OfflinePlayer op = zh.getServer().getOfflinePlayer(playerUUID);
				if (op != null && op.hasPlayedBefore()) {
					String world = zh.getServer().getWorlds().get(0).getName();
					groupName = zh.getPerms().getPrimaryGroup(world, op);
				}
			}
			if (p != null && p.hasPlayedBefore() && (groupName == null || !zh.getConfig().contains(KeyWordEnum.groupsPrefix.getValue() + groupName))) {
				groupName = getSurrogateGroupName(p);
			}
			else {
				groupName = getExactGroupName(groupName);
			}
		}
		return groupName;
	}
	
	public int getMaximumHorseNameLength() {
		int value = -1;
		String maximumHorseNameLength = zh.getConfig().getString(KeyWordEnum.horsenamesPrefix.getValue() + KeyWordEnum.maximumLength.getValue());
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
		String minimumHorseNameLength = zh.getConfig().getString(KeyWordEnum.horsenamesPrefix.getValue() + KeyWordEnum.minimumLength.getValue());
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
		List<String> randomHorseNameList = zh.getConfig().getStringList(KeyWordEnum.horsenamesPrefix.getValue() + KeyWordEnum.randomNames.getValue());
		if (!(randomHorseNameList == null || randomHorseNameList.size() == 0)) {
			randomHorseName = randomHorseNameList.get(random.nextInt(randomHorseNameList.size()));
		}
		return randomHorseName;
	}
	
	private String getSurrogateGroupName(Player p) {
		if (p != null) {
			ConfigurationSection cs = zh.getConfig().getConfigurationSection(KeyWordEnum.groups.getValue());
			if (cs != null) {
				for (String groupName : cs.getKeys(false)) {
					String permission = zh.getConfig().getString(KeyWordEnum.groupsPrefix.getValue() + groupName + KeyWordEnum.permissionSuffix.getValue(), null);
					if (permission != null && zh.getPerms().has(p, permission)) {
						return groupName;
					}
				}
			}
		}
		return null;
	}
	
	public boolean isAutoAdminModeEnabled(String command) {
		return command != null && zh.getConfig().getBoolean(KeyWordEnum.commandsPrefix.getValue() + command + KeyWordEnum.autoAdminSuffix.getValue(), false);
	}
	
	public boolean isColorBypassEnabled(UUID playerUUID) {
		if (playerUUID != null) {
			String groupName = getGroupName(playerUUID);
			if (groupName != null) {
				return zh.getConfig().getBoolean(KeyWordEnum.groupsPrefix.getValue() + groupName + KeyWordEnum.colorBypassSuffix.getValue(), false);
			}
		}
		return false;
	}
	
	public boolean isConsoleMuted() {
		return zh.getConfig().getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.muteConsole.getValue(), false);
	}
	
	public boolean isHorseNameAllowed() {
		return getMaximumHorseNameLength() != 0;
	}
	
	public boolean isHorseNameBanned(String horseName) {
		if (horseName != null) {
			List<String> bannedNameList = zh.getConfig().getStringList(KeyWordEnum.horsenamesPrefix + KeyWordEnum.bannedNames.getValue());
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
	
	public boolean isProtectionEnabled(String protection) {
		return protection != null && zh.getConfig().getBoolean(KeyWordEnum.protectionsPrefix.getValue() + protection + KeyWordEnum.enabledSuffix.getValue(), false);
	}
	
	public boolean isRandomHorseNameEnabled() {
		return zh.getConfig().getBoolean(KeyWordEnum.horsenamesPrefix.getValue() + KeyWordEnum.giveRandomNames.getValue(), false);
	}
	
	public boolean isWorldCrossable(World world) {
		return world != null && zh.getConfig().getBoolean(KeyWordEnum.worldsPrefix.getValue() + world.getName() + KeyWordEnum.crossableSuffix.getValue(), false);
	}
	
	public boolean isWorldEnabled(World world) {
		return world != null && zh.getConfig().getBoolean(KeyWordEnum.worldsPrefix.getValue() + world.getName() + KeyWordEnum.enabledSuffix.getValue(), false);
	}
	
	public boolean shouldBlockLeashedTeleport() {
		return zh.getConfig().getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.blockLeashedTeleport.getValue(), false);
	}
	
	public boolean shouldBlockMountedTeleport() {
		return zh.getConfig().getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.blockMountedTeleport.getValue(), false);
	}
	
	public boolean shouldClaimOnTame() {
		return zh.getConfig().getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.claimOnTame.getValue(), false);
	}
	
	public boolean shouldLockOnClaim() {
		return zh.getConfig().getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.lockOnClaim.getValue(), false);
	}
	
	public boolean shouldProtectOnClaim() {
		return zh.getConfig().getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.protectOnClaim.getValue(), false);
	}
	
	public boolean shouldShareOnClaim() {
		return zh.getConfig().getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.shareOnClaim.getValue(), false);
	}
	
	public boolean shouldUseOldTeleportMethod() {
		return zh.getConfig().getBoolean(KeyWordEnum.settingsPrefix.getValue() + KeyWordEnum.useOldTeleportMethod.getValue(), false);
	}
	
	public boolean checkConformity() {
		if (!(checkCommandsConformity()
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
		ConfigurationSection cs = zh.getConfig().getConfigurationSection("Commands");
		if (cs != null) {
			List<String> exactCommandList = zh.getCmdM().getCommandNameList();
			for (String command : cs.getKeys(false)) {
				if (exactCommandList.contains(command)) {
					if (!zh.getConfig().isSet("Commands." + command + ".auto-admin")) {
						zh.getLogger().severe("The \"Commands." + command + ".auto-admin\" option is missing from the config !");
			        	conform = false;
			        }
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
	
	private boolean checkGroupsConformity() {
		boolean conform = true;
		ConfigurationSection cs = zh.getConfig().getConfigurationSection("Groups");
		if (cs != null) {
			for (String group : cs.getKeys(false)) {
				String color = zh.getConfig().getString("Groups." + group + ".color");
		        if (color != null) {
					if (!zh.getMM().isColor(color)) {
		        		zh.getLogger().severe("The color \"" + color + "\" used for the group \"" + group + "\" is not a color !");
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
		ConfigurationSection cs = zh.getConfig().getConfigurationSection("Protections");
		if (cs != null) {
			Set<String> registeredDamageCauseList = cs.getKeys(false);
			List<String> existingDamageCauseList = new ArrayList<String>();
			DamageCause[] damageCauseEnum = DamageCause.values();
			for (DamageCause existingDamageCause : damageCauseEnum) {
				existingDamageCauseList.add(existingDamageCause.name());
			}
			existingDamageCauseList.add("OWNER_ATTACK");
			existingDamageCauseList.add("PLAYER_ATTACK");
			for (String damageCause : existingDamageCauseList) {
				if (registeredDamageCauseList.contains(damageCause)) {
					if (!zh.getConfig().isSet("Protections." + damageCause + ".enabled")) {
						zh.getLogger().severe("The \"Protections." + damageCause + ".enabled\" option is missing from the config !");
			        	conform = false;
			        }
				}
				else {
					zh.getLogger().severe("The damage cause \"Protections." + damageCause + "\" is not a valid damage cause !");
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
		if (!zh.getConfig().isSet("Settings.block-leashed-teleport")) {
			zh.getLogger().severe("The \"Settings.block-leashed-teleport\" option is missing from the config !");
			conform = false;
		}
		if (!zh.getConfig().isSet("Settings.block-mounted-teleport")) {
			zh.getLogger().severe("The \"Settings.block-mounted-teleport\" option is missing from the config !");
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
		if (!zh.getConfig().isSet("Settings.mute-console")) {
			zh.getLogger().severe("The \"Settings.mute-console\" option is missing from the config !");
			conform = false;
		}
		if (!zh.getConfig().isSet("Settings.use-old-teleport-method")) {
			zh.getLogger().severe("The \"Settings.use-old-teleport-method\" option is missing from the config !");
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
