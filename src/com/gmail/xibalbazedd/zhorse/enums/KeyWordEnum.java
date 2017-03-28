package com.gmail.xibalbazedd.zhorse.enums;

public enum KeyWordEnum {
	
	/* general keywords */
	adminSuffix(".admin"),
	dot("."),
	freeSuffix(".free"),
	zhPrefix("zh."),
	
	/* config.yml keywords */
	allowLeashOnDeadHorse("Settings.allow-leash-on-dead-horse"),
	availableLanguages("Languages.available"),
	autoAdminSuffix(".auto-admin"),
	bannedNames("HorseNames.banned-names"),
	blockLeashedTeleport("Settings.block-leashed-teleport"),
	blockMountedTeleport("Settings.block-mounted-teleport"),
	claimsLimitSuffix(".claims-limit"),
	claimOnTame("Settings.claim-on-tame"),
	colorBypassSuffix(".color-bypass"),
	colorSuffix(".color"),
	commandsPrefix("Commands."),
	costSuffix(".cost"),
	crossableSuffix(".crossable"),
	database("Databases.mysql-config.database"),
	defaultLanguage("Languages.default"),
	defaultName("HorseNames.default-name"),
	enabledSuffix(".enabled"),
	filename("Databases.sqlite-config.filename"),
	giveRandomNames("HorseNames.give-random-names"),
	groups("Groups"),
	groupsPrefix("Groups."),
	hereMaxRange("Settings.here-max-range"),
	host("Databases.mysql-config.host"),
	lockOnClaim("Settings.lock-on-claim"),
	maximumLength("HorseNames.maximum-length"),
	minimumLength("HorseNames.minimum-length"),
	muteConsole("Settings.mute-console"),
	password("Databases.mysql-config.password"),
	permissionSuffix(".permission"),
	port("Databases.mysql-config.port"),
	protectionsPrefix("Protections."),
	protectOnClaim("Settings.protect-on-claim"),
	randomNames("HorseNames.random-names"),
	respawnMissingHorse("Settings.respawn-missing-horse"),
	shareOnClaim("Settings.share-on-claim"),
	tablePrefix("Databases.mysql-config.table-prefix"),
	tpMaxRange("Settings.tp-max-range"),
	type("Databases.type"),
	useOldTeleportMethod("Settings.use-old-teleport-method"),
	user("Databases.mysql-config.user"),
	useVanillaStats("Settings.use-vanilla-stats"),
	worldsPrefix("Worlds."),
	
	/* locale.yml keywords */
	amountFlag("<amount>"),
	description("Description"),
	horseFlag("<horse>"),
	horseIDFlag("<id>"),
	langFlag("<lang>"),
	maxFlag("<max>"),
	permFlag("<perm>"),
	playerFlag("<player>"),
	usage("Usage"),
	valueFlag("<value>"),
	
	/* users.yml keywords */
	favorite("Favorite"),
	horses("Horses"),
	language("Language"),
	location("Location"),
	modeLocked("Locked"),
	name("Name"),
	players("Players"),
	modeProtected("Protected"),
	modeShared("Shared"),
	uuid("UUID"),
	world("World"),
	x("X"),
	y("Y"),
	z("Z");
	
	private String value;
	
	KeyWordEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
