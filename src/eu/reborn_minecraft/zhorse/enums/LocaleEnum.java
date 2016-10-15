package eu.reborn_minecraft.zhorse.enums;

public enum LocaleEnum {
	/* headers */
	adminCommandListHeader("Headers.adminCommandListHeader"),
	availableOptionFormat("Headers.availableOptionFormat"),
	commandListHeader("Headers.commandListHeader"),
	commandUsageFormat("Headers.commandUsageFormat"),
	commandUsageHeader("Headers.commandUsageHeader"),
	friendCommandListHeader("Headers.friendCommandListHeader"),
	friendListFormat("Headers.friendListFormat"),
	friendListHeader("Headers.friendListHeader"),
	friendListOtherHeader("Headers.friendListOtherHeader"),
	headerFormat("Headers.headerFormat"),
	horseInfoHeader("Headers.horseInfoHeader"),
	horseListFormat("Headers.horseListFormat"),
	horseListFormatFavorite("Headers.horseListFormatFavorite"),
	horseListHeader("Headers.horseListHeader"),
	horseListOtherHeader("Headers.horseListOtherHeader"),
	horseOptionFormat("Headers.horseOptionFormat"),
	pluginHeader("Headers.pluginHeader"),
	pluginPrefix("Headers.pluginPrefix"),
	remainingClaimsFormat("Headers.remainingClaimsFormat"),
	settingsCommandListHeader("Headers.settingsCommandListHeader"),
	
	/* messages */
	claimsLimitReached("Messages.claimsLimitReached"),
	claimsLimitReachedOther("Messages.claimsLimitReachedOther"),
	databaseImportFailure("Messages.databaseImportFailure"),
	databaseImportStarted("Messages.databaseImportStarted"),
	databaseImportSuccess("Messages.databaseImportSuccess"),
	favoriteAlreadySet("Messages.favoriteAlreadySet"),
	favoriteAlreadySetOther("Messages.favoriteAlreadySetOther"),
	favoriteEdited("Messages.favoriteEdited"),
	favoriteEditedOther("Messages.favoriteEditedOther"),
	horseAlreadyClaimed("Messages.horseAlreadyClaimed"),
	horseBelongsTo("Messages.horseBelongsTo"),
	horseClaimed("Messages.horseClaimed"),
	horseCleared("Messages.horseCleared"),
	horseClearedOther("Messages.horseClearedOther"),
	horseDied("Messages.horseDied"),
	horseFreed("Messages.horseFreed"),
	horseGiven("Messages.horseGiven"),
	horseHealed("Messages.horseHealed"),
	horseIsProtected("Messages.horseIsProtected"),
	horseLeashed("Messages.horseLeashed"),
	horseLeashedBy("Messages.horseLeashedBy"),
	horseLocked("Messages.horseLocked"),
	horseManuallyTamed("Messages.horseManuallyTamed"),
	horseMounted("Messages.horseMounted"),
	horseMountedBy("Messages.horseMountedBy"),
	horseNameBanned("Messages.horseNameBanned"),
	horseNameForbidden("Messages.horseNameForbidden"),
	horseNameMandatory("Messages.horseNameMandatory"),
	horseNameTooLong("Messages.horseNameTooLong"),
	horseNameTooShort("Messages.horseNameTooShort"),
	horseNotClaimed("Messages.horseNotClaimed"),
	horseNotFound("Messages.horseNotFound"),
	horseNotTamed("Messages.horseNotTamed"),
	horseProtected("Messages.horseProtected"),
	horseReceived("Messages.horseReceived"),
	horseRenamed("Messages.horseRenamed"),
	horseShared("Messages.horseShared"),
	horseSpawned("Messages.horseSpawned"),
	horseTamed("Messages.horseTamed"),
	horseTeleported("Messages.horseTeleported"),
	horseUnLocked("Messages.horseUnLocked"),
	horseUnProtected("Messages.horseUnProtected"),
	horseUnShared("Messages.horseUnShared"),
	horseUnTamed("Messages.horseUnTamed"),
	invalidHealthArgument("Messages.invalidHealthArgument"),
	invalidJumpArgument("Messages.invalidJumpArgument"),
	invalidSpeedArgument("Messages.invalidSpeedArgument"),
	languageAlreadyUsed("Messages.languageAlreadyUsed"),
	languageAlreadyUsedOther("Messages.languageAlreadyUsedOther"),
	languageEdited("Messages.languageEdited"),
	languageEditedOther("Messages.languageEditedOther"),
	listHorseColor("Messages.listHorseColor"),
	listHorseStyle("Messages.listHorseStyle"),
	listHorseVariant("Messages.listHorseVariant"),
	missingArguments("Messages.missingArguments"),
	missingDatabase("Messages.missingDatabase"),
	missingHorseId("Messages.missingHorseId"),
	missingLanguage("Messages.missingLanguage"),
	missingPermission("Messages.missingPermission"),
	missingPermissionOther("Messages.missingPermissionOther"),
	missingTarget("Messages.missingTarget"),
	noFriend("Messages.noFriend"),
	noFriendOther("Messages.noFriendOther"),
	noHorseOwned("Messages.noHorseOwned"),
	noHorseOwnedOther("Messages.noHorseOwnedOther"),
	notOnHorse("Messages.notOnHorse"),
	playerCleared("Messages.playerCleared"),
	playerClearedOther("Messages.playerClearedOther"),
	playerCommand("Messages.playerCommand"),
	playerOffline("Messages.playerOffline"),
	pluginReloaded("Messages.pluginReloaded"),
	pluginReloadedWithErrors("Messages.pluginReloadedWithErrors"),
	samePlayer("Messages.samePlayer"),
	teleportedToHorse("Messages.teleportedToHorse"),
	unknownAdminCommand("Messages.unknownAdminCommand"),
	unknownCommand("Messages.unknownCommand"),
	unknownDatabase("Messages.unknownDatabase"),
	unknownFriendCommand("Messages.unknownFriendCommand"),
	unknownHorseId("Messages.unknownHorseId"),
	unknownHorseIdOther("Messages.unknownHorseIdOther"),
	unknownHorseName("Messages.unknownHorseName"),
	unknownHorseNameOther("Messages.unknownHorseNameOther"),
	unknownLanguage("Messages.unknownLanguage"),
	unknownPlayer("Messages.unknownPlayer"),
	unknownSettingsCommand("Messages.unknownSettingsCommand"),
	unknownSpawnArgument("Messages.unknownSpawnArgument"),
	worldDisabled("Messages.worldDisabled"),
	worldUncrossable("Messages.worldUncrossable"),
	
	/* economy messages */
	commandCost("Economy.commandCost"),
	commandPaid("Economy.commandPaid"),
	currencySymbol("Economy.currencySymbol"),
	notEnoughMoney("Economy.notEnoughMoney"),
	
	/* horse informations */
	health("Horse informations.health"),
	id("Horse informations.id"),
	jump("Horse informations.jump"),
	location("Horse informations.location"),
	modeLocked("Horse informations.modeLocked"),
	modeProtected("Horse informations.modeProtected"),
	modeShared("Horse informations.modeShared"),
	name("Horse informations.name"),
	owner("Horse informations.owner"),
	speed("Horse informations.speed"),
	status("Horse informations.status"),
	
	/* command descriptions */
	adminDescription("Command descriptions.admin"),
	claimDescription("Command descriptions.claim"),
	freeDescription("Command descriptions.free"),
	friendDescription("Command descriptions.friend"),
	giveDescription("Command descriptions.give"),
	healDescription("Command descriptions.heal"),
	helpDescription("Command descriptions.help"),
	hereDescription("Command descriptions.here"),
	infoDescription("Command descriptions.info"),
	killDescription("Command descriptions.kill"),
	listDescription("Command descriptions.list"),
	lockDescription("Command descriptions.lock"),
	renameDescription("Command descriptions.rename"),
	protectDescription("Command descriptions.protect"),
	reloadDescription("Command descriptions.reload"),
	settingsDescription("Command descriptions.settings"),
	shareDescription("Command descriptions.share"),
	spawnDescription("Command descriptions.spawn"),
	tameDescription("Command descriptions.tame"),
	tpDescription("Command descriptions.tp"),
	
	/* admin command descriptions */
	adminClearDescription("Admin command descriptions.clear"),
	adminImportDescription("Admin command descriptions.import"),
	
	/* friend command descriptions */
	friendAddDescription("Friend command descriptions.add"),
	friendListDescription("Friend command descriptions.list"),
	friendRemoveDescription("Friend command descriptions.remove"),
	
	/* settings command descriptions */
	settingsFavoriteDescription("Settings command descriptions.favorite"),
	settingsLanguageDescription("Settings command descriptions.language"),
	
	/* command usages */
	adminUsage("Command usages.admin"),
	claimUsage("Command usages.claim"),
	freeUsage("Command usages.free"),
	friendUsage("Command usages.friend"),
	giveUsage("Command usages.give"),
	healUsage("Command usages.heal"),
	helpUsage("Command usages.help"),
	hereUsage("Command usages.here"),
	infoUsage("Command usages.info"),
	killUsage("Command usages.kill"),
	listUsage("Command usages.list"),
	lockUsage("Command usages.lock"),
	renameUsage("Command usages.rename"),
	protectUsage("Command usages.protect"),
	reloadUsage("Command usages.reload"),
	settingsUsage("Command usages.settings"),
	shareUsage("Command usages.share"),
	spawnUsage("Command usages.spawn"),
	tameUsage("Command usages.tame"),
	tpUsage("Command usages.tp"),
	
	/* admin command usages */
	adminClearUsage("Admin command usages.clear"),
	adminImportUsage("Admin command usages.import"),
	
	/* friend command usages */
	friendAddUsage("Friend command usages.add"),
	friendListUsage("Friend command usages.list"),
	friendRemoveUsage("Friend command usages.remove"),
	
	/* settings command usages */
	settingsFavoriteUsage("Settings command usages.favorite"),
	settingsLanguageUsage("Settings command usages.language");
	
	private String index;
	
	LocaleEnum(String index) {
		this.index = index;
	}
	
	public String getName() {
		return index.substring(index.indexOf(".")+1);
	}
	
	public String getIndex() {
		return index;
	}
	  
}