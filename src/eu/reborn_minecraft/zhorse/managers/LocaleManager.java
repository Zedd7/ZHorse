package eu.reborn_minecraft.zhorse.managers;

import eu.reborn_minecraft.zhorse.ZHorse;

public class LocaleManager {
	public String adminSuffix = ".admin";
	public String freeSuffix = ".free";
	public String zhPrefix = "zh.";
	
	public String pluginPrefix = "pluginPrefix";
	public String headerFormat = "headerFormat";
	public String commandListHeader = "commandListHeader";
	public String commandUsageHeader = "commandUsageHeader";
	public String horseInfoHeader = "horseInfoHeader";
	public String horseListHeader = "horseListHeader";
	public String horseListOtherHeader = "horseListOtherHeader";
	
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
	public String horseListFormat = "horseListFormat";
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
	public String maximumClaimsReached = "maximumClaimsReached";
	public String maximumClaimsReachedOther = "maximumClaimsReachedOther";
	public String missingPermission = "missingPermission";
	public String missingPermissionOther = "missingPermissionOther";
	public String noHorseOwned = "noHorseOwned";
	public String noHorseOwnedOther = "noHorseOwnedOther";
	public String notOnHorse = "notOnHorse";
	public String playerCommand = "playerCommand";
	public String playerNotRegistered = "playerNotRegistered";
	public String playerOffline = "playerOffline";
	public String pluginReloaded = "pluginReloaded";
	public String remainingClaims = "remainingClaims";
	public String teleportedToHorse = "teleportedToHorse";
	public String unknownCommand = "unknownCommand";
	public String unknownHorseId = "unknownHorseId";
	public String unknownHorseIdOther = "unknownHorseIdOther";
	public String unknownPlayer = "unknownPlayer";
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
	public String share = "share";
	public String tame = "tame";
	public String tp = "tp";
	
	private ZHorse zh;
	
	public LocaleManager(ZHorse zh, boolean init) {
		this.zh = zh;
	}
	
	public String getCommandAnswer(String index) {
		return getCommandAnswer(index, false);
	}
	
	public String getCommandAnswer(String index, boolean hidePrefix) {
		return getLocaleData("Messages." + index, hidePrefix);
	}
	
	public String getCommandDescription(String index) {
		return getCommandDescription(index, true);
	}
	
	public String getCommandDescription(String index, boolean hidePrefix) {
		return getLocaleData("Command descriptions." + index, hidePrefix);
	}
	
	public String getCommandUsage(String index) {
		return getCommandUsage(index, true);
	}
	
	public String getCommandUsage(String index, boolean hidePrefix) {
		return getLocaleData("Command usages." + index, hidePrefix);
	}
	
	public String getEconomyAnswer(String index) {
		return getEconomyAnswer(index, false);
	}
	
	public String getEconomyAnswer(String index, boolean hidePrefix) {
		return getLocaleData("Economy." + index, hidePrefix);
	}
	
	public String getHeaderMessage(String index) {
		return getHeaderMessage(index, true);
	}
	
	public String getHeaderMessage(String index, boolean hidePrefix) {
		return getLocaleData("Headers." + index, hidePrefix);
	}
	
	public String getInformationMessage(String index) {
		return getInformationMessage(index, true);
	}
	
	public String getInformationMessage(String index, boolean hidePrefix) {
		return getLocaleData("Horse informations." + index, hidePrefix);
	}
	
	public String getLocaleData(String fullIndex, boolean hidePrefix) {
        String text = zh.getLocale().getString(fullIndex);
        if (text == null) {
        	zh.getLogger().severe("No value found in \"locale.yml\" for index \"" + fullIndex + "\" !");
        	text = fullIndex + " NULL";
        }
        if (hidePrefix) {
        	return text;
        }
        return getHeaderMessage(pluginPrefix) + " " + text;
	}

}
