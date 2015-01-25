package eu.reborn_minecraft.zhorse.managers;

import eu.reborn_minecraft.zhorse.ZHorse;

public class LocaleManager {
	// d�placer liste ailleurs
	public String adminSuffix = ".admin";
	public String freeSuffix = ".free";
	public String zhPrefix = "zh.";
	
	public String pluginPrefix = "pluginPrefix";
	public String headerFormat = "headerFormat";
	public String commandListHeader = "commandListHeader";
	public String commandUsageHeader = "commandUsageHeader";
	public String settingsCommandListHeader = "settingsCommandListHeader";
	public String horseInfoHeader = "horseInfoHeader";
	public String horseListFormat = "horseListFormat";
	public String horseListHeader = "horseListHeader";
	public String horseListOtherHeader = "horseListOtherHeader";
	public String remainingClaimsFormat = "remainingClaimsFormat";
	
	public String commandIncorrect = "commandIncorrect";
	public String commandUsage = "commandUsage";
	public String differentWorld = "differentWorld";
	public String horseAlreadyClaimed = "horseAlreadyClaimed";
	public String horseBelongsTo = "horseBelongsTo";
	public String horseClaimed = "horseClaimed";
	public String horseDied = "horseDied";
	public String horseFreed = "horseFreed";
	public String horseGiven = "horseGiven";
	public String horseHealed = "horseHealed";
	public String horseIsProtected = "horseIsProtected";
	public String horseLocked = "horseLocked";
	public String horseManuallyTamed = "horseManuallyTamed";
	public String horseMounted = "horseMounted";
	public String horseMountedBy = "horseMountedBy";
	public String horseNameForbidden = "horseNameForbidden";
	public String horseNameMandatory = "horseNameMandatory";
	public String horseNameTooLong = "horseNameTooLong";
	public String horseNameTooShort = "horseNameTooShort";
	public String horseNotClaimed = "horseNotClaimed";
	public String horseNotFound = "horseNotFound";
	public String horseNotRegistered = "horseNotRegistered";
	public String horseNotTamed = "horseNotTamed";
	public String horseProtected = "horseProtected";
	public String horseReceived = "horseReceived";
	public String horseRenamed = "horseRenamed";
	public String horseShared = "horseShared";
	public String horseTamed = "horseTamed";
	public String horseTeleported = "horseTeleported";
	public String horseUnLocked = "horseUnLocked";
	public String horseUnProtected = "horseUnProtected";
	public String horseUnShared = "horseUnShared";
	public String horseUnTamed = "horseUnTamed";
	public String languageEdited = "languageEdited";
	public String languageEditedOther = "languageEditedOther";
	public String maximumClaimsReached = "maximumClaimsReached";
	public String maximumClaimsReachedOther = "maximumClaimsReachedOther";
	public String missingLanguage = "missingLanguage";
	public String missingPermission = "missingPermission";
	public String missingPermissionOther = "missingPermissionOther";
	public String noHorseOwned = "noHorseOwned";
	public String noHorseOwnedOther = "noHorseOwnedOther";
	public String notOnHorse = "notOnHorse";
	public String playerCommand = "playerCommand";
	public String playerNotRegistered = "playerNotRegistered";
	public String playerOffline = "playerOffline";
	public String pluginReloaded = "pluginReloaded";
	public String teleportedToHorse = "teleportedToHorse";
	public String unknownCommand = "unknownCommand";
	public String unknownHorseId = "unknownHorseId";
	public String unknownHorseIdOther = "unknownHorseIdOther";
	public String unknownLanguage = "unknownLanguage";
	public String unknownPlayer = "unknownPlayer";
	public String unknownSettingsCommand = "unknownSettingsCommand";
	public String worldDisabled = "worldDisabled";
	
	public String commandCost = "commandCost";
	public String commandPaid = "commandPaid";
	public String notEnoughMoney = "notEnoughMoney";
	
	public String health = "health";
	public String id = "id";
	public String jump = "jump";
	public String modeLocked = "modeLocked";
	public String modeNone = "modeNone";
	public String modeProtected = "modeProtected";
	public String modeShared = "modeShared";
	public String name = "name";
	public String owner = "owner";
	public String speed = "speed";
	public String status = "status";
	
	public String help = "help";
	public String claim = "claim";
	public String free = "free";
	public String give = "give";
	public String heal = "heal";
	public String here = "here";
	public String info = "info";
	public String kill = "kill";
	public String list = "list";
	public String lock = "lock";
	public String rename = "rename";
	public String protect = "protect";
	public String reload = "reload";
	public String settings = "settings";
	public String share = "share";
	public String tame = "tame";
	public String tp = "tp";
	
	public String language = "language";
	
	private ZHorse zh;
	
	public LocaleManager(ZHorse zh, boolean localeExist) {
		this.zh = zh;
	}
	
	public String getCommandAnswer(String language, String index) {
		return getCommandAnswer(language, index, false);
	}
	
	public String getCommandAnswer(String language, String index, boolean hidePrefix) {
		return getLocaleData(language, "Messages." + index, hidePrefix);
	}
	
	public String getCommandDescription(String language, String index) {
		return getCommandDescription(language, index, true);
	}
	
	public String getCommandDescription(String language, String index, boolean hidePrefix) {
		return getLocaleData(language, "Command descriptions." + index, hidePrefix);
	}
	
	public String getCommandUsage(String language, String index) {
		return getCommandUsage(language, index, true);
	}
	
	public String getCommandUsage(String language, String index, boolean hidePrefix) {
		return getLocaleData(language, "Command usages." + index, hidePrefix);
	}
	
	public String getEconomyAnswer(String language, String index) {
		return getEconomyAnswer(language, index, false);
	}
	
	public String getEconomyAnswer(String language, String index, boolean hidePrefix) {
		return getLocaleData(language, "Economy." + index, hidePrefix);
	}
	
	public String getHeaderMessage(String language, String index) {
		return getHeaderMessage(language, index, true);
	}
	
	public String getHeaderMessage(String language, String index, boolean hidePrefix) {
		return getLocaleData(language, "Headers." + index, hidePrefix);
	}
	
	public String getInformationMessage(String language, String index) {
		return getInformationMessage(language, index, true);
	}
	
	public String getInformationMessage(String language, String index, boolean hidePrefix) {
		return getLocaleData(language, "Horse informations." + index, hidePrefix);
	}
	
	public String getSettingsCommandDescription(String language, String index) {
		return getSettingsCommandDescription(language, index, true);
	}
	
	public String getSettingsCommandDescription(String language, String index, boolean hidePrefix) {
		return getLocaleData(language, "Settings command descriptions." + index, hidePrefix);
	}
	
	public String getLocaleData(String language, String fullIndex, boolean hidePrefix) {
		if (language == null) {
			return ("Unknown language, please contact an administrator");
		}
        String text = zh.getLocale(language).getString(fullIndex);
        if (text == null) {
        	zh.getLogger().severe("No value found in \"locale_" + language + ".yml\" at index \"" + fullIndex + "\" !");
        	if (!language.equals(zh.getDebugLanguage())) {
        		return getLocaleData(zh.getDebugLanguage(), fullIndex, hidePrefix);
        	}
        	return ("No text found at : " + fullIndex);
        }
        if (hidePrefix) {
        	return text;
        }
        return getHeaderMessage(language, pluginPrefix) + " " + text;
	}

}
