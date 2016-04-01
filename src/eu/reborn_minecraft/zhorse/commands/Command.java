package eu.reborn_minecraft.zhorse.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.CommandAdminEnum;
import eu.reborn_minecraft.zhorse.enums.CommandEnum;
import eu.reborn_minecraft.zhorse.enums.CommandSettingsEnum;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;
import net.md_5.bungee.api.ChatColor;

public class Command {
	protected ZHorse zh;
	protected CommandSender s;
	protected Player p;
	protected Horse horse;
	protected UUID targetUUID;
	protected String[] a;
	protected String argument;
	protected String command;
	protected String horseName;
	protected String userID;
	protected String targetName;
	protected boolean displayConsole;
	protected boolean adminMode;
	protected boolean idMode;
	protected boolean needTarget;
	protected boolean playerCommand;
	protected boolean playerOnly;
	protected boolean samePlayer;
	protected boolean targetMode;
	
	public Command(ZHorse zh, CommandSender s, String[] a) {
		this.zh = zh;
		this.a = a;
		this.s = s;
		this.command = a[0].toLowerCase();
		this.displayConsole = !(zh.getCM().isConsoleMuted());
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
					userID = a[i+1];
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
			targetName = zh.getUM().getPlayerName(targetName); // correction de la casse
			targetUUID = getPlayerUUID(targetName);
		}
		adminMode = adminMode || (zh.getCM().isAutoAdminModeEnabled(command) && hasPermissionAdmin(true));
		samePlayer = !targetMode || (playerCommand && p.getUniqueId().equals(targetUUID));
		return true;
	}
	
	protected void applyArgument(boolean userIDFirst) {
		if (!argument.isEmpty()) {
			if (userIDFirst) {
				if (!idMode) {
					applyArgumentToUserID();
				}
				else if (!targetMode) {
					applyArgumentToTarget();
				}
			}
			else {
				if (!targetMode) {
					applyArgumentToTarget();
				}
				else if (!idMode) {
					applyArgumentToUserID();
				}
			}
		}
	}
	
	protected void applyArgumentToUserID() {
		idMode = true;
		horseName = argument;
		userID = zh.getUM().getUserID(targetUUID, horseName);
	}
	
	protected void applyArgumentToTarget() {
		targetMode = true;
		targetName = argument;
		analyseModes();
	}
	
	protected void applyHorseName() {
		String customHorseName;
		if (zh.getMM().isColorized(argument) && zh.getCM().isColorBypassEnabled(p.getUniqueId())) {
			customHorseName = zh.getMM().applyColors(argument);
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
			if ((length >= minimumLength && (length <= maximumLength || maximumLength == -1)) || adminMode) {
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
			if (keepPreviousName && zh.getUM().isRegistered(horse)) {
				horseName = zh.getUM().getHorseName(horse);
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
	
	@SuppressWarnings("deprecation")	
	protected UUID getPlayerUUID(String playerName) {
		if (zh.getUM().isRegistered(playerName)) {
			return zh.getUM().getPlayerUUID(playerName);
		}
		else if (zh.getServer().getOfflinePlayer(playerName).hasPlayedBefore()) {
			return zh.getServer().getOfflinePlayer(playerName).getUniqueId();
		}
		return null;
	}
	
	protected String getRemainingClaimsMessage(UUID playerUUID) {
		String message = "";
		if (samePlayer || isPlayerOnline(playerUUID, true)) {
			int claimsAmount = zh.getUM().getClaimsAmount(playerUUID);
			int maxClaims = zh.getCM().getClaimsLimit(playerUUID);
			message = zh.getMM().getMessageAmountMax(s, LocaleEnum.remainingClaimsFormat, claimsAmount, maxClaims, true);
		}
		return message;
	}
	
	protected boolean hasReachedMaxClaims(UUID playerUUID) {
		if (adminMode) {
			return false;
		}
		int claimsAmount;
		int maxClaims;
		claimsAmount = zh.getUM().getClaimsAmount(playerUUID);
		maxClaims = zh.getCM().getClaimsLimit(playerUUID);
		if (claimsAmount < maxClaims || maxClaims == -1) {
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
    	if (zh.getPerms().has(s, permission)) {
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
		if (zh.getPerms().has(s, permission)) {
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
				if (!zh.getUM().isRegistered(horse)) {
					return true;
				}
				else if (displayConsole) {
					if (zh.getUM().isClaimedBy(p.getUniqueId(), horse)) {
						zh.getMM().sendMessage(s, LocaleEnum.horseAlreadyClaimed);
					}
					else {
						if (!targetMode) {
							targetName = zh.getUM().getPlayerName(horse);
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
				zh.getMM().sendMessageUserID(s, LocaleEnum.unknownHorseId, userID);
			}
			else if (idMode && targetMode) {
				zh.getMM().sendMessagePlayerUserID(s, LocaleEnum.unknownHorseIdOther, targetName, userID);
			}
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
	
	protected boolean isHorseLoaded() {
		if (horse != null) {
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessageHorse(s, LocaleEnum.horseNotFound, zh.getUM().getHorseName(targetUUID, userID));
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
	
	protected boolean isHorseReachable() {
		if ((zh.getCM().isWorldCrossable(p.getWorld()) && zh.getCM().isWorldCrossable(horse.getWorld())) || adminMode) {
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessageHorse(s, LocaleEnum.worldUnreachable, horseName);
		}
		return false;
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
		if (p.isInsideVehicle() && p.getVehicle() instanceof Horse) {
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
		if (zh.getUM().isClaimedBy(playerUUID, horse) || (!ignoreModes && adminMode)) {
			return true;
		}
		else if (displayConsole && !hideConsole) {
			String ownerName = zh.getUM().getPlayerName(horse);
			zh.getMM().sendMessagePlayer(s, LocaleEnum.horseBelongsTo, ownerName);
		}
		return false;
	}
	
	protected boolean isPlayer() {
		return isPlayer(false);
	}
	
	protected boolean isPlayer(boolean hideConsole) {
		if (s instanceof Player) {
			p = (Player)s;
			playerCommand = true;
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
	
	protected boolean isRegistered(Horse horse) {
		if (zh.getUM().isRegistered(horse)) {
			horseName = zh.getUM().getHorseName(horse);
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessage(s, LocaleEnum.horseNotClaimed);
		}
		return false;
	}
	
	protected boolean isRegistered(UUID targetUUID) {
		if (zh.getUM().isRegistered(targetUUID)) {
			return true;
		}
		else if (displayConsole) {
			zh.getMM().sendMessagePlayer(s, LocaleEnum.unknownPlayer, targetName);
		}
		return false;
	}
	
	protected boolean isRegistered(UUID targetUUID, String userID) {
		return isRegistered(targetUUID, userID, false);
	}
	
	protected boolean isRegistered(UUID targetUUID, String userID, boolean isOwner) {
		if (zh.getUM().isRegistered(targetUUID, userID)) {
			horseName = zh.getUM().getHorseName(targetUUID, userID);
			return true;
		}
		else if (displayConsole) {
			if (targetUUID == null) {
				zh.getMM().sendMessagePlayer(s, LocaleEnum.unknownPlayer, targetName);
			}
			else {
				if (userID == null) {
					if (samePlayer || isOwner) {
						zh.getMM().sendMessageHorse(s, LocaleEnum.unknownHorseName, horseName);
					}
					else {
						zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.unknownHorseNameOther, horseName, targetName);
					}
				}
				else {
					if (samePlayer || isOwner) {
						zh.getMM().sendMessageUserID(s, LocaleEnum.unknownHorseId, userID);
					}
					else {
						zh.getMM().sendMessagePlayerUserID(s, LocaleEnum.unknownHorseIdOther, targetName, userID);
					}
				}
			}
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
		if (zh.getUM().getClaimsAmount(playerUUID) > 0) {
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
			LocaleEnum commandDescription = LocaleEnum.valueOf(command + KeyWordEnum.description.getValue());
			command = subCommand ? this.command : command;
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
		sendCommandUsage(command, false);
	}
	
	protected void sendCommandUsage(String command, boolean hideError) {
		if (displayConsole) {
			if (!hideError) {
				zh.getMM().sendMessage(s, LocaleEnum.missingArguments);
			}
			zh.getMM().sendMessageSpacer(s, LocaleEnum.commandUsageHeader, 1, true);
			String commandUsage = zh.getMM().getMessage(s, LocaleEnum.valueOf(command + KeyWordEnum.usage.getValue()), true);
			zh.getMM().sendMessageSpacerValue(s, LocaleEnum.commandUsageFormat, 1, commandUsage, true);
		}
	}
	
	protected void sendHorseColorList() {
		if (displayConsole) {
			Color[] horseColorArray = Horse.Color.values();
			List<String> horseColorList = new ArrayList<String>();
			for (int i = 0; i < horseColorArray.length; ++i) {
				horseColorList.add(horseColorArray[i].name().toLowerCase());
			}
			sendHorseOptionList(horseColorList, LocaleEnum.listHorseColor);
		}
	}

	protected void sendHorseStyleList() {
		if (displayConsole) {
			Style[] horseStyleArray = Horse.Style.values();
			List<String> horseStyleList = new ArrayList<String>();
			for (int i = 0; i < horseStyleArray.length; ++i) {
				horseStyleList.add(horseStyleArray[i].name().toLowerCase());
			}
			sendHorseOptionList(horseStyleList, LocaleEnum.listHorseStyle);
		}
	}

	protected void sendHorseVariantList() {
		if (displayConsole) {
			Variant[] horseVariantArray = Horse.Variant.values();
			List<String> horseVariantList = new ArrayList<String>();
			for (int i = 0; i < horseVariantArray.length; ++i) {
				horseVariantList.add(horseVariantArray[i].name().toLowerCase());
			}
			sendHorseOptionList(horseVariantList, LocaleEnum.listHorseVariant);
		}
	}
	
	protected void sendHorseOptionList(List<String> horseOptionList, LocaleEnum index) {
		String horseOptionListMessage = "";
		for (int i = 0; i < horseOptionList.size(); ++i) {
			horseOptionListMessage += zh.getMM().getMessageValue(s, LocaleEnum.horseOptionFormat, horseOptionList.get(i), true);
			if (i < horseOptionList.size() - 1) {
				horseOptionListMessage += ", ";
			}
		}
		horseOptionListMessage += ChatColor.RESET;
		zh.getMM().sendMessageSpacerValue(s, index, 1, horseOptionListMessage, true);
	}

}
