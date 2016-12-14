package eu.reborn_minecraft.zhorse.commands;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.CommandAdminEnum;
import eu.reborn_minecraft.zhorse.enums.CommandEnum;
import eu.reborn_minecraft.zhorse.enums.CommandFriendEnum;
import eu.reborn_minecraft.zhorse.enums.CommandSettingsEnum;
import eu.reborn_minecraft.zhorse.enums.HorseStatisticEnum;
import eu.reborn_minecraft.zhorse.enums.HorseVariantEnum;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;
import eu.reborn_minecraft.zhorse.managers.MessageManager;
import net.md_5.bungee.api.ChatColor;

public abstract class AbstractCommand {
	
	protected ZHorse zh;
	protected CommandSender s;
	protected Player p;
	protected AbstractHorse horse;
	protected UUID targetUUID;
	protected String[] a;
	protected String argument;
	protected String command;
	protected String horseID;
	protected String horseName;
	protected String targetName;
	protected boolean displayConsole;
	protected boolean useVanillaStats;
	protected boolean adminMode;
	protected boolean idMode;
	protected boolean needTarget;
	protected boolean playerCommand;
	protected boolean playerOnly;
	protected boolean samePlayer;
	protected boolean targetMode;
	
	public AbstractCommand(ZHorse zh, CommandSender s, String[] a) {
		this.zh = zh;
		this.a = a;
		this.s = s;
		this.command = a[0].toLowerCase();
		this.displayConsole = !(zh.getCM().isConsoleMuted());
		this.useVanillaStats = zh.getCM().shouldUseVanillaStats();
	}
	
	protected boolean analyseArguments() {
		argument = "";
		adminMode = false;
		idMode = false;
		targetMode = false;
		for (int i = 1; i < a.length; ++i) { // start at 1 to skip the command
			boolean valid = true;
			if (a[i].equalsIgnoreCase("-a")) {
				valid = !adminMode;
				adminMode = true;
			}
			else if (a[i].equalsIgnoreCase("-i")) {
				valid = (!idMode) && (i != a.length-1) && (!a[i+1].startsWith("-"));
				if (valid) { // avoid to exit the loop
					idMode = true;
					horseID = a[i+1];
					i++; // skip the id
				}
			}
			else if (a[i].equalsIgnoreCase("-t")) {
				valid = (!targetMode) && (i != a.length-1) && (!a[i+1].startsWith("-"));
				if (valid) { // avoid to exit the loop
					targetMode = true;
					targetName = a[i+1];
					i++; // saut du target
				}
			}
			else { // add argument if not a flag
				if (!argument.isEmpty()) {
					argument += " ";
				}
				argument += a[i];
			}
			if (!valid) {
				if (displayConsole) {
					sendCommandUsage();
				}
				return false;
			}
		}
		return analyseModes();
	}
	
	protected boolean analyseModes() {
		if (!targetMode) {
			if (playerCommand) {
				targetUUID = p.getUniqueId();
			}
			targetName = s.getName();
		}
		else {
			String fixedTargetName = zh.getDM().getPlayerName(targetName); // fix potential case errors
			if (fixedTargetName != null) {
				targetName = fixedTargetName;
				targetUUID = getPlayerUUID(targetName);
			}
			if (targetName == null || targetUUID == null) {
				if (displayConsole) {
					zh.getMM().sendMessagePlayer(s, LocaleEnum.unknownPlayer, targetName);
				}
				return false;
			}
		}
		adminMode = adminMode || (zh.getCM().isAutoAdminModeEnabled(command) && hasPermissionAdmin(true));
		samePlayer = !targetMode || (playerCommand && p.getUniqueId().equals(targetUUID));
		return true;
	}
	
	protected boolean applyArgument(boolean horseIDFirst) {
		if (horseIDFirst) {
			if (!idMode) {
				return applyArgumentToHorseID();
			}
			else if (!targetMode) {
				return applyArgumentToTarget();
			}
		}
		else {
			if (!targetMode) {
				return applyArgumentToTarget();
			}
			else if (!idMode) {
				return applyArgumentToHorseID();
			}
		}
		return true;
	}
	
	protected boolean applyArgumentToHorseID() {
		if (idMode || argument.isEmpty()) {
			return true;
		}
		idMode = true;
		UUID ownerUUID = needTarget ? p.getUniqueId() : targetUUID;
		horseName = zh.getDM().getHorseName(ownerUUID, argument); // fix potential case errors
		Integer horseIDInt = zh.getDM().getHorseID(ownerUUID, horseName);
		if (horseIDInt == null) {
			if (displayConsole) {
				if (samePlayer || needTarget) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.unknownHorseName, horseName);
				}
				else {
					zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.unknownHorseNameOther, horseName, targetName);
				}
			}
			return false;
		}
		horseID = horseIDInt.toString();
		return true;
	}
	
	protected boolean applyArgumentToTarget() {
		if (targetMode) {
			return true;
		}
		targetMode = !argument.isEmpty();
		targetName = argument;
		targetUUID = null;
		return analyseModes();
	}
	
	protected void applyHorseName() {
		String customHorseName;
		if (MessageManager.isColorized(argument) && (zh.getCM().isColorBypassEnabled(p.getUniqueId()) || adminMode)) {
			customHorseName = MessageManager.applyColors(argument);
		}
		else {
			String groupColorCode = zh.getCM().getGroupColorCode(targetUUID);
			customHorseName = zh.getMM().applyColors(horseName, groupColorCode);
		}
		horse.setCustomName(customHorseName);
	}
	
	protected boolean craftHorseName(boolean keepPreviousName) {
		if (!argument.isEmpty()) {
			return craftCustomHorseName();
		}
		else {
			return craftPreviousHorseName(keepPreviousName);
		}
	}

	private boolean craftCustomHorseName() {
		if (zh.getCM().isHorseNameAllowed() || adminMode) {
			horseName = zh.getMM().removeColors(argument);
			int maximumLength = zh.getCM().getMaximumHorseNameLength();
			int minimumLength = zh.getCM().getMinimumHorseNameLength();
			int length = horseName.length();
			if ((length >= minimumLength || adminMode) && length <= maximumLength) {
				if (!zh.getCM().isHorseNameBanned(horseName) || adminMode) {
					return true;
				}
				else if (displayConsole) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.horseNameBanned, horseName);
				}
			}
			else if (displayConsole) {
				if (length < minimumLength) {
					zh.getMM().sendMessageAmount(s, LocaleEnum.horseNameTooShort, minimumLength);
				}
				else if (length > maximumLength) {
					zh.getMM().sendMessageAmount(s, LocaleEnum.horseNameTooLong, maximumLength);
				}
			}
		}
		else if (displayConsole) {
			zh.getMM().sendMessage(s, LocaleEnum.horseNameForbidden);
		}
		return false;
	}
	
	private boolean craftPreviousHorseName(boolean keepPreviousName) {
		if (!zh.getCM().isHorseNameRequired() || adminMode) {
			if (keepPreviousName && zh.getDM().isHorseRegistered(horse.getUniqueId())) {
				horseName = zh.getDM().getHorseName(horse.getUniqueId());
				return true;
			}
			else {
				if (zh.getCM().isRandomHorseNameEnabled()) {
					horseName = zh.getCM().getRandomHorseName();
				}
				else {
					horseName = zh.getCM().getDefaultHorseName();
				}
				return true;
			}
		}
		else if (displayConsole) {
			zh.getMM().sendMessage(s, LocaleEnum.horseNameMandatory);
		}
		return false;
	}
	
	@SuppressWarnings("deprecation") // TODO use DataManager's methods instead
	protected UUID getPlayerUUID(String playerName) {
		if (zh.getDM().isPlayerRegistered(playerName)) {
			return zh.getDM().getPlayerUUID(playerName);
		}
		else if (zh.getServer().getOfflinePlayer(playerName).hasPlayedBefore()) {
			return zh.getServer().getOfflinePlayer(playerName).getUniqueId();
		}
		return null;
	}
	
	protected String getRemainingClaimsMessage(UUID playerUUID) {
		String message = "";
		int horseCount = zh.getDM().getHorseCount(playerUUID);
		int maxClaims = zh.getCM().getClaimsLimit(playerUUID);
		message = zh.getMM().getMessageAmountMax(s, LocaleEnum.remainingClaimsFormat, horseCount, maxClaims, true);
		return message;
	}
	
	protected boolean hasReachedClaimsLimit(UUID playerUUID) {
		if (adminMode) {
			return false;
		}
		int horseCount = zh.getDM().getHorseCount(playerUUID);
		int claimsLimit = zh.getCM().getClaimsLimit(playerUUID);
		if (horseCount < claimsLimit || claimsLimit == -1) {
			return false;
		}
		else if (displayConsole) {
			if (samePlayer) {
				zh.getMM().sendMessage(s, LocaleEnum.claimsLimitReached);
			}
			else {
				zh.getMM().sendMessagePlayer(s, LocaleEnum.claimsLimitReachedOther, targetName);
			}
		}
		return true;
	}
	
	protected boolean hasPermission() {
    	return (hasPermission(s, command, false, false));
	}
	
	protected boolean hasPermission(UUID playerUUID, String command, boolean ignoreModes, boolean hideConsole) {
		if (isPlayerOnline(playerUUID, hideConsole)) {
    		CommandSender target = zh.getServer().getPlayer(playerUUID);
    		return hasPermission(target, command, ignoreModes, hideConsole);
    	}
    	return false;
	}
	
	protected boolean hasPermission(CommandSender s, String command, boolean ignoreModes, boolean hideConsole) {
		String permission = KeyWordEnum.zhPrefix.getValue() + command;
    	if (!ignoreModes && (adminMode || (targetMode && !needTarget)) ) {
    		return hasPermissionAdmin(s, command, hideConsole);
    	}
    	if (zh.getPM().has(s, permission)) {
    		return true;
    	}
    	else if (displayConsole && !hideConsole) {
    		zh.getMM().sendMessagePerm(s, LocaleEnum.missingPermission, permission);
    	}
    	return false;
	}
	
	protected boolean hasPermissionAdmin(boolean hideConsole) {
    	return hasPermissionAdmin(s, command, hideConsole);
	}
	
	protected boolean hasPermissionAdmin(UUID playerUUID, String command, boolean hideConsole) {
		if (isPlayerOnline(playerUUID, hideConsole)) {
    		CommandSender target = zh.getServer().getPlayer(playerUUID);
    		return hasPermissionAdmin(target, command, hideConsole);
    	}
    	return false;
	}
	
	protected boolean hasPermissionAdmin(CommandSender s, String command, boolean hideConsole) {
		String permission = KeyWordEnum.zhPrefix.getValue() + command + KeyWordEnum.adminSuffix.getValue();
		if (zh.getPM().has(s, permission)) {
        	return true;
		}
        else if (displayConsole && !hideConsole) {
        	zh.getMM().sendMessagePerm(s, LocaleEnum.missingPermission, permission);
        }
    	return false;
	}
	
	protected boolean isClaimable() {
		if (horse != null) {
			if (adminMode) {
				return true;
			}
			if (horse.isTamed()) {
				if (!zh.getDM().isHorseRegistered(horse.getUniqueId())) {
					return true;
				}
				else if (displayConsole) {
					if (zh.getDM().isHorseOwnedBy(p.getUniqueId(), horse.getUniqueId())) {
						zh.getMM().sendMessage(s, LocaleEnum.horseAlreadyClaimed);
					}
					else {
						if (!targetMode) {
							targetName = zh.getDM().getOwnerName(horse.getUniqueId());
						}
						zh.getMM().sendMessagePlayer(s, LocaleEnum.horseBelongsTo, targetName);
					}
				}
			}
			else if (displayConsole) {
				zh.getMM().sendMessage(s, LocaleEnum.horseNotTamed);
			}
		}
		else if (displayConsole) {
			if (idMode && !targetMode) {
				zh.getMM().sendMessageHorseID(s, LocaleEnum.unknownHorseId, horseID);
			}
			else if (idMode && targetMode) {
				zh.getMM().sendMessageHorseIDPlayer(s, LocaleEnum.unknownHorseIdOther, horseID, targetName);
			}
		}
		return false;
	}
	
	protected boolean isHorseInRangeHere() {
		int maxRadius = zh.getCM().getMaximumRangeHere();
		return isHorseInRange(maxRadius);
	}
	
	protected boolean isHorseInRangeTp() {
		int maxRadius = zh.getCM().getMaximumRangeTp();
		return isHorseInRange(maxRadius);
	}
	
	protected boolean isHorseInRange(int maxRadius) {
		if (adminMode) {
			return true;
		}
		Location playerLocation = p.getLocation();
		Location horseLocation = horse.getLocation();
		int xDistance = Math.abs(Math.abs(playerLocation.getBlockX()) - Math.abs(horseLocation.getBlockX()));
		int zDistance = Math.abs(Math.abs(playerLocation.getBlockZ()) - Math.abs(horseLocation.getBlockZ()));
		double distance = Math.sqrt(Math.pow(xDistance, 2) + Math.pow(zDistance, 2));
		if (distance <= maxRadius || maxRadius == -1 || !playerLocation.getWorld().equals(horseLocation.getWorld())) {
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessageHorseMax(s, LocaleEnum.horseOutOfRange, horseName, maxRadius);
		}
		return false;
	}
	
	protected boolean isHorseLeashed() {
		boolean blockLeashedTeleport = zh.getCM().shouldBlockLeashedTeleport();
		if (!horse.isLeashed()) {
			return false;
		}
		else if (!blockLeashedTeleport || adminMode) {
			return false;
		}
		else if (displayConsole) {
			Entity leashHolder = horse.getLeashHolder();
			if (leashHolder instanceof Player) {
				String leashHolderName = ((Player) leashHolder).getName();
				zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.horseLeashedBy, horseName, leashHolderName);
			}
			else {
				zh.getMM().sendMessageHorse(s, LocaleEnum.horseLeashed, horseName);
			}
		}
		return true;
	}
	
	protected boolean isHorseLoaded(boolean useTargetUUID) {
		if (horse != null) {
			return true;
		}
		else if (displayConsole) {
			UUID ownerUUID = useTargetUUID ? targetUUID : p.getUniqueId();
			zh.getMM().sendMessageHorse(s, LocaleEnum.horseNotFound, zh.getDM().getHorseName(ownerUUID, Integer.parseInt(horseID)));
		}
		return false;
	}
	
	protected boolean isHorseMounted() {
		boolean blockMountedTeleport = zh.getCM().shouldBlockMountedTeleport();
		Entity passenger = horse.getPassenger();
		if (passenger == null) {
			return false;
		}
		else if (!blockMountedTeleport || adminMode) {
			horse.eject();
			return false;
		}		
		else if (displayConsole) {
			String passengerName = ((Player) passenger).getName();
			zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.horseMountedBy, horseName, passengerName);
		}
		return true;
	}
	
	protected boolean isNotOnHorse() {
		return isNotOnHorse(false);
	}
	
	protected boolean isNotOnHorse(boolean hideConsole) {
		if (p.getVehicle() != horse || adminMode) {
			return true;
		}
		else if (displayConsole && !hideConsole) {
			zh.getMM().sendMessageHorse(s, LocaleEnum.horseMounted, horseName);
		}
		return false;
	}
	
	protected boolean isOnHorse(boolean hideConsole) {
		if (p.isInsideVehicle() && p.getVehicle() instanceof AbstractHorse) {
			return true;
		}
		else if (displayConsole && !hideConsole) {
			zh.getMM().sendMessage(s, LocaleEnum.notOnHorse);
		}
		return false;
	}
	
	protected boolean isOwner() {
		return isOwner(p.getUniqueId(), false, false);
	}
	
	protected boolean isOwner(boolean ignoreModes, boolean hideConsole) {
		return isOwner(p.getUniqueId(), ignoreModes, hideConsole);
	}
	
	protected boolean isOwner(UUID playerUUID, boolean hideConsole) {
		return isOwner(playerUUID, false, hideConsole);
	}
	
	protected boolean isOwner(UUID playerUUID, boolean ignoreModes, boolean hideConsole) {
		if (zh.getDM().isHorseOwnedBy(playerUUID, horse.getUniqueId()) || (!ignoreModes && adminMode)) {
			return true;
		}
		else if (displayConsole && !hideConsole) {
			String ownerName = zh.getDM().getOwnerName(horse.getUniqueId());
			zh.getMM().sendMessagePlayer(s, LocaleEnum.horseBelongsTo, ownerName);
		}
		return false;
	}
	
	protected boolean isPlayer() {
		return isPlayer(false);
	}
	
	protected boolean isPlayer(boolean hideConsole) {
		if (s instanceof Player) {
			p = (Player) s;
			playerCommand = true;
			if (!zh.getDM().isPlayerRegistered(p.getUniqueId())) {
				zh.getDM().registerPlayer(p.getUniqueId(), p.getName(), zh.getCM().getDefaultLanguage(), zh.getDM().getDefaultFavoriteHorseID());
			}
			return playerCommand;
		}
		if (displayConsole && !hideConsole) {
			zh.getMM().sendMessage(s, LocaleEnum.playerCommand);
		}
		playerCommand = false;
		return playerCommand;
	}
	
	protected boolean isPlayerDifferent() {
		if (!samePlayer || adminMode) {
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessage(s, LocaleEnum.samePlayer);
		}
		return false;
	}
	
	protected boolean isPlayerOnline(UUID playerUUID, boolean hideConsole) {
		if (playerUUID != null && zh.getServer().getOfflinePlayer(playerUUID).isOnline()) {
			return true;
		}
    	if (displayConsole && !hideConsole) {
    		zh.getMM().sendMessagePlayer(s, LocaleEnum.playerOffline, targetName);
    	}
		return false;
	}
	
	protected boolean isRegistered(AbstractHorse horse) {
		if (zh.getDM().isHorseRegistered(horse.getUniqueId())) {
			horseName = zh.getDM().getHorseName(horse.getUniqueId());
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessage(s, LocaleEnum.horseNotClaimed);
		}
		return false;
	}
	
	protected boolean isRegistered(UUID targetUUID) {
		if (zh.getDM().isPlayerRegistered(targetUUID)) {
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessagePlayer(s, LocaleEnum.unknownPlayer, targetName);
		}
		return false;
	}
	
	protected boolean isRegistered(UUID targetUUID, String horseID) {
		return isRegistered(targetUUID, horseID, false);
	}
	
	protected boolean isRegistered(UUID targetUUID, String horseID, boolean isOwner) {
		if (horseID != null && zh.getDM().isHorseRegistered(targetUUID, Integer.parseInt(horseID))) {
			horseName = zh.getDM().getHorseName(targetUUID, Integer.parseInt(horseID));
			return true;
		}
		else if (displayConsole) {
			if (targetUUID == null) {
				zh.getMM().sendMessagePlayer(s, LocaleEnum.unknownPlayer, targetName);
			}
			else {
				if (horseID == null) {
					if (samePlayer || isOwner) {
						zh.getMM().sendMessageHorse(s, LocaleEnum.unknownHorseName, horseName);
					}
					else {
						zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.unknownHorseNameOther, horseName, targetName);
					}
				}
				else {
					if (samePlayer || isOwner) {
						zh.getMM().sendMessageHorseID(s, LocaleEnum.unknownHorseId, horseID);
					}
					else {
						zh.getMM().sendMessageHorseIDPlayer(s, LocaleEnum.unknownHorseIdOther, horseID, targetName);
					}
				}
			}
		}
		return false;
	}
	
	protected boolean isStatHealthValid(double health) {
		double minHealth = HorseStatisticEnum.MIN_HEALTH.getValue(useVanillaStats);
		double maxHealth = HorseStatisticEnum.MAX_HEALTH.getValue(useVanillaStats);
		if ((health >= minHealth && health <= maxHealth) || adminMode) {
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessageAmountMax(s, LocaleEnum.invalidHealthArgument, (int) minHealth, (int) maxHealth);
		}
		return false;
	}
	
	protected boolean isStatSpeedValid(double speed) {
		double minSpeed = HorseStatisticEnum.MIN_SPEED.getValue(useVanillaStats);
		double maxSpeed = HorseStatisticEnum.MAX_SPEED.getValue(useVanillaStats);
		if ((speed >= (minSpeed / maxSpeed) * 100 && speed <= 100) || adminMode) {
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessageAmountMax(s, LocaleEnum.invalidSpeedArgument, (int) Math.ceil((minSpeed / maxSpeed) * 100), 100);
		}
		return false;
	}
	
	protected boolean isStatJumpStrengthValid(double jumpStrength) {
		double minJumpStrength = HorseStatisticEnum.MIN_JUMP_STRENGTH.getValue(useVanillaStats);
		double maxJumpStrength = HorseStatisticEnum.MAX_JUMP_STRENGTH.getValue(useVanillaStats);
		if ((jumpStrength >= (minJumpStrength / maxJumpStrength) * 100 && jumpStrength <= 100) || adminMode) {
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessageAmountMax(s, LocaleEnum.invalidJumpArgument, (int) Math.ceil((minJumpStrength / maxJumpStrength) * 100), 100);
		}
		return false;
	}
	
	protected boolean isStatLlamaStrengthValid(int llamaStrenth) {
		int minLlamaStrength = (int) HorseStatisticEnum.MIN_LLAMA_STRENGTH.getValue(useVanillaStats);
		int maxLlamaStrength = (int) HorseStatisticEnum.MAX_LLAMA_STRENGTH.getValue(useVanillaStats);
		if ((llamaStrenth >= minLlamaStrength && llamaStrenth <= maxLlamaStrength) || adminMode) {
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessageAmountMax(s, LocaleEnum.invalidStrengthArgument, minLlamaStrength, maxLlamaStrength);
		}
		return false;
	}
	
	protected boolean isWorldCrossable(World world) {
		if (zh.getCM().isWorldCrossable(world) || adminMode) {
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessageHorse(s, LocaleEnum.worldUncrossable, horseName);
		}
		return false;
	}

	protected boolean isWorldEnabled() {
		if (zh.getCM().isWorldEnabled(p.getWorld()) || adminMode) {
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessage(s, LocaleEnum.worldDisabled);
		}
		return false;
	}
	
	protected boolean ownsHorse(UUID playerUUID, boolean hideConsole) {
		if (zh.getDM().getHorseCount(playerUUID) > 0) {
			return true;
		}
		else if (displayConsole && !hideConsole) {
			if (samePlayer) {
				zh.getMM().sendMessageValue(s, LocaleEnum.noHorseOwned, getRemainingClaimsMessage(playerUUID));
			}
			else {
				zh.getMM().sendMessagePlayerValue(s, LocaleEnum.noHorseOwnedOther, targetName, getRemainingClaimsMessage(playerUUID));
			}
		}
		return false;
	}
	
	protected void sendCommandDescription(String command, String permission, boolean subCommand) {
		if (hasPermission(targetUUID, permission, true, true)) {
			LocaleEnum commandDescription;
			if (!subCommand) {
				commandDescription = LocaleEnum.valueOf(command + KeyWordEnum.description.getValue());
			}
			else {
				String commandLocaleIndex = this.command
						+ command.substring(0, 1).toUpperCase()
						+ command.substring(1)
						+ KeyWordEnum.description.getValue();
				commandDescription = LocaleEnum.valueOf(commandLocaleIndex);
				command = this.command;
			}
			if (zh.getEM().isCommandFree(targetUUID, command)) {
				zh.getMM().sendMessageSpacer(s, commandDescription, 1, true);
			}
			else {
				int cost = zh.getCM().getCommandCost(command);
				String currencySymbol = zh.getMM().getMessage(s, LocaleEnum.currencySymbol, true);
				zh.getMM().sendMessageCostSpacerValue(s, commandDescription, cost, 1, currencySymbol, true);
			}
		}
	}

	protected void sendCommandDescriptionList() {
		if (displayConsole) {
			String header = zh.getMM().getMessage(s, LocaleEnum.commandListHeader, true);
			zh.getMM().sendMessageValue(s, LocaleEnum.headerFormat, header, true);
			for (CommandEnum command : CommandEnum.values()) {
				String commandName = command.getName();
				String permission = commandName;
				sendCommandDescription(commandName, permission, false);
			}
		}
	}
	
	protected void sendCommandAdminDescriptionList() {
		if (displayConsole) {
			String header = zh.getMM().getMessage(s, LocaleEnum.adminCommandListHeader, true);
			zh.getMM().sendMessageValue(s, LocaleEnum.headerFormat, header, true);
			for (CommandAdminEnum command : CommandAdminEnum.values()) {
				String commandName = command.getName();
				String permission = this.command + KeyWordEnum.dot.getValue() + command.getName();				
				sendCommandDescription(commandName, permission, true);
			}
		}
	}
	
	protected void sendCommandFriendDescriptionList() {
		if (displayConsole) {
			String header = zh.getMM().getMessage(s, LocaleEnum.friendCommandListHeader, true);
			zh.getMM().sendMessageValue(s, LocaleEnum.headerFormat, header, true);
			for (CommandFriendEnum command : CommandFriendEnum.values()) {
				String commandName = command.getName();
				String permission = this.command + KeyWordEnum.dot.getValue() + command.getName();
				sendCommandDescription(commandName, permission, true);
			}
		}
	}
	
	protected void sendCommandSettingsDescriptionList() {
		if (displayConsole) {
			String header = zh.getMM().getMessage(s, LocaleEnum.settingsCommandListHeader, true);
			zh.getMM().sendMessageValue(s, LocaleEnum.headerFormat, header, true);
			for (CommandSettingsEnum command : CommandSettingsEnum.values()) {
				String commandName = command.getName();
				String permission = this.command + KeyWordEnum.dot.getValue() + command.getName();
				sendCommandDescription(commandName, permission, true);
			}
		}
	}
	
	protected void sendCommandUsage() {
		sendCommandUsage(command, false, false);
	}
	
	protected void sendCommandUsage(String command, boolean subCommand, boolean hideError) {
		if (displayConsole) {
			if (!hideError) {
				zh.getMM().sendMessage(s, LocaleEnum.missingArguments);
			}
			
			LocaleEnum commandUsage;
			if (!subCommand) {
				commandUsage = LocaleEnum.valueOf(command + KeyWordEnum.usage.getValue());
			}
			else {
				String commandUsageIndex = this.command
						+ command.substring(0, 1).toUpperCase()
						+ command.substring(1)
						+ KeyWordEnum.usage.getValue();
				commandUsage = LocaleEnum.valueOf(commandUsageIndex);
			}			
			zh.getMM().sendMessageSpacer(s, LocaleEnum.commandUsageHeader, 1, true);
			String commandUsageMessage = zh.getMM().getMessage(s, commandUsage, true);
			zh.getMM().sendMessageSpacerValue(s, LocaleEnum.commandUsageFormat, 1, commandUsageMessage, true);
		}
	}
	
	protected void sendHorseColorList() {
		if (displayConsole) {
			sendHorseOptionList(Horse.Color.values(), LocaleEnum.listHorseColor);
		}
	}
	
	protected void sendLlamaColorList() {
		if (displayConsole) {
			sendHorseOptionList(Llama.Color.values(), LocaleEnum.listLlamaColor);
		}
	}

	protected void sendHorseStyleList() {
		if (displayConsole) {
			sendHorseOptionList(Horse.Style.values(), LocaleEnum.listHorseStyle);
		}
	}

	protected void sendAbstractHorseVariantList() {
		if (displayConsole) {
			sendHorseOptionList(HorseVariantEnum.getCodeArray(), LocaleEnum.listHorseVariant);
		}
	}
	
	protected <T> void sendHorseOptionList (T[] horseOptionArray, LocaleEnum index) {
		String horseOptionArrayMessage = "";
		for (int i = 0; i < horseOptionArray.length; i++) {
			horseOptionArrayMessage += zh.getMM().getMessageValue(s, LocaleEnum.horseOptionFormat, horseOptionArray[i].toString().toLowerCase(), true);
			if (i < horseOptionArray.length - 1) {
				horseOptionArrayMessage += ", ";
			}
		}
		horseOptionArrayMessage += ChatColor.RESET;
		zh.getMM().sendMessageSpacerValue(s, index, 1, horseOptionArrayMessage, true);
	}

}
