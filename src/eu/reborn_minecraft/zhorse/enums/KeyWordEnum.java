package eu.reborn_minecraft.zhorse.enums;

public enum KeyWordEnum {
	
	/* general keywords */
	adminSuffix(".admin"),
	dot("."),
	freeSuffix(".free"),
	zhPrefix("zh."),
	
	/* config.yml keywords */
	// TODO drop pre/suffix and include full path in value
	allowLeashOnDeadHorse("allow-leash-on-dead-horse"),
	availableSuffix(".available"),
	autoAdminSuffix(".auto-admin"),
	bannedNames("banned-names"),
	blockLeashedTeleport("block-leashed-teleport"),
	blockMountedTeleport("block-mounted-teleport"),
	claimsLimitSuffix(".claims-limit"),
	claimOnTame("claim-on-tame"),
	colorBypassSuffix(".color-bypass"),
	colorSuffix(".color"),
	commands("Commands"),
	commandsPrefix("Commands."),
	costSuffix(".cost"),
	crossableSuffix(".crossable"),
	databaseSuffix(".database"),
	databasesPrefix("Databases."),
	defaultSuffix(".default"),
	defaultNameSuffix(".default-name"),
	defaultName("default-name"),
	enabledSuffix(".enabled"),
	filenameSuffix(".filename"),
	giveRandomNames("give-random-names"),
	groups("Groups"),
	groupsPrefix("Groups."),
	hereMaxRange("here-max-range"),
	horsenames("HorseNames"),
	horsenamesPrefix("HorseNames."),
	hostSuffix(".host"),
	languages("Languages"),
	languagesPrefix("Languages."),
	lockOnClaim("lock-on-claim"),
	maximumLength("maximum-length"),
	minimumLength("minimum-length"),
	muteConsole("mute-console"),
	mysqlConfig("mysql-config"),
	passwordSuffix(".password"),
	permissionSuffix(".permission"),
	portSuffix(".port"),
	protectionsPrefix("Protections."),
	protectOnClaim("protect-on-claim"),
	randomNames("random-names"),
	respawnMissingHorse("respawn-missing-horse"),
	settingsPrefix("Settings."),
	shareOnClaim("share-on-claim"),
	sqliteConfig("sqlite-config"),
	tablePrefixSuffix(".table-prefix"),
	tpMaxRange("tp-max-range"),
	typeSuffix(".type"),
	useOldTeleportMethod("use-old-teleport-method"),
	userSuffix(".user"),
	useVanillaStats("use-vanilla-stats"),
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
