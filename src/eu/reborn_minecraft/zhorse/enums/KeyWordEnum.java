package eu.reborn_minecraft.zhorse.enums;

public enum KeyWordEnum {
	/* general keywords */
	adminSuffix(".admin"),
	dot("."),
	freeSuffix(".free"),
	zhPrefix("zh."),
	
	/* config.yml keywords */
	available(".available"),
	autoAdminSuffix(".auto-admin"),
	bannedNames("banned-names"),
	claimsLimitSuffix(".claims-limit"),
	claimOnTame("claim-on-tame"),
	colorSuffix(".color"),
	commands("Commands"),
	commandsPrefix("Commands."),
	costSuffix(".cost"),
	crossableSuffix(".crossable"),
	defaultSuffix(".default"),
	defaultNameSuffix(".default-name"),
	defaultName("default-name"),
	enabledSuffix(".enabled"),
	giveRandomNames("give-random-names"),
	groups("Groups"),
	groupsPrefix("Groups."),
	horsenames("HorseNames"),
	horsenamesPrefix("HorseNames."),
	languages("Languages"),
	languagesPrefix("Languages."),
	lockOnClaim("lock-on-claim"),
	maximumLength("maximum-length"),
	minimumLength("minimum-length"),
	muteConsole("mute-console"),
	permissionSuffix(".permission"),
	protectionsPrefix("Protections."),
	protectOnClaim("protect-on-claim"),
	randomNames("random-names"),
	settingsPrefix("Settings."),
	shareOnClaim("share-on-claim"),
	worldsPrefix("Worlds."),
	
	/* locale.yml keywords */
	amountFlag("<amount>"),
	description("Description"),
	horseFlag("<horse>"),
	langFlag("<lang>"),
	maxFlag("<max>"),
	permFlag("<perm>"),
	playerFlag("<player>"),
	usage("Usage"),
	userIDFlag("<id>"),
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
