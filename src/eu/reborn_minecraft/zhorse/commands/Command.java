package eu.reborn_minecraft.zhorse.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class Command {
	protected ZHorse zh;
	protected String[] a;
	protected CommandSender s;
	protected String command;
	protected boolean displayConsole;
	protected boolean adminMode;
	protected boolean idMode;
	protected boolean targetMode;
	protected boolean idAllow;
	protected boolean targetAllow;
	protected boolean playerCommand;
	protected String userID;
	protected String targetName;
	protected UUID targetUUID;
	protected Player p;
	protected String language;
	protected Horse horse;
	protected String horseName;
	protected boolean samePlayer;
	
	public Command(ZHorse zh, CommandSender s, String[] a) {
		this.zh = zh;
		this.a = a;
		this.s = s;
		if (a.length != 0) {
			this.command = a[0];
		}
		else {
			this.command = zh.getLM().help;
		}
		this.language = zh.getCM().getDefaultLanguage();
		this.displayConsole = !(zh.getCM().isConsoleMuted());
		this.idAllow = false;
		this.targetAllow = false;
	}
	
	protected boolean analyseArguments() {
		int adminModeCount = 0;
		int idModeCount = 0;
		int targetModeCount = 0;
		for (int i=0; i<a.length; i++) {
			boolean checkSuccess = true;
			if (a[i].equals("-a")) {
				checkSuccess = (adminModeCount == 0);
				if (checkSuccess) {
					adminMode = true;
					adminModeCount += 1;
				}
			}
			else if (a[i].equals("-i")) {
				checkSuccess = (idModeCount == 0 && i != a.length-1 && !a[i+1].startsWith("-"));
				if (checkSuccess) {
					idMode = true;
					userID = a[i+1];
					idModeCount += 1;
				}
			}
			else if (a[i].equals("-t")) {
				checkSuccess = (targetModeCount == 0 && i != a.length-1 && !a[i+1].startsWith("-"));
				if (checkSuccess) {
					targetMode = true;
					targetName = a[i+1];
					targetModeCount += 1;
				}
			}
			if (!checkSuccess) {
				if (displayConsole) {
					sendCommandUsage();
				}
				return false;
			}
		}
		if (targetName == null) {
			targetName = s.getName();
			if (playerCommand) {
				targetUUID = p.getUniqueId();
			}
			samePlayer = true;
		}
		else {
			targetName = zh.getUM().getPlayerName(targetName);
			targetUUID = getPlayerUUID(targetName);
			samePlayer = playerCommand && p.getUniqueId().equals(targetUUID);
		}
		if (targetUUID == null && playerCommand) {
			s.sendMessage(zh.getMM().getMessagePlayer(language, zh.getLM().unknownPlayer, targetName));
			return false;
		}
		cleanArgs();
		return true;
	}

	protected void cleanArgs() {
		List<String> a2 = new ArrayList<String>();
		for (int i=1; i<a.length; i++) {
			if (!(a[i].equals("-a") || a[i].equals("-i") || a[i].equals("-t"))) {
				a2.add(a[i]);
			}
			else if (a[i].equals("-i") || a[i].equals("-t")) {
				i++;
			}
		}
		String[] a3 = new String[a2.size()];
		for (int i=0; i<a2.size(); i++) {
			a3[i] = a2.get(i);
		}
		a = a3;
	}
	
	protected boolean craftHorseName() {
		if (a.length != 0) {
			horseName = "";
			if (zh.getCM().isHorseNameAllowed() || adminMode) {
				for (int i=0; i<a.length; i++) {
					horseName += a[i];
					if (i+1 < a.length) {
						horseName += " ";
					}
				}
				int maxLength = zh.getCM().getMaximumHorseNameLength();
				int minLength = zh.getCM().getMinimumHorseNameLength();
				int l = horseName.length();
				if ((l >= minLength && (l <= maxLength || maxLength == -1)) || adminMode) {
					return true;
				}
				else if (displayConsole) {
					if (l < minLength) {
						s.sendMessage(zh.getMM().getMessageAmount(language, zh.getLM().horseNameTooShort, Integer.toString(minLength)));
					}
					else if (l > maxLength) {
						s.sendMessage(zh.getMM().getMessageAmount(language, zh.getLM().horseNameTooLong, Integer.toString(maxLength)));
					}
				}
			}
			else if (displayConsole) {
				s.sendMessage(zh.getMM().getMessage(language, zh.getLM().horseNameForbidden));
			}
		}
		else {
			if (!zh.getCM().isHorseNameRequired() || adminMode) {
				if (zh.getUM().isRegistered(horse)) {
					horseName = zh.getUM().getHorseName(horse);
					return true;
				}
				else {
					horseName = zh.getCM().getRandomName();
					if (horseName != null) {
						return true;
					}
					String errorMessage = "\"horsenames\" list in config is empty ! Please add one name in it or increment \"minimum-horsename-length\".";
					zh.getLogger().severe(errorMessage);
				}
			}
			else if (displayConsole) {
				s.sendMessage(zh.getMM().getMessage(language, zh.getLM().horseNameMandatory));
			}
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")	
	protected UUID getPlayerUUID(String playerName) {
		if (zh.getUM().isRegistered(playerName)) {
			return zh.getUM().getPlayerUUID(playerName);
		}
		else {
			if (zh.getServer().getOfflinePlayer(playerName).hasPlayedBefore()) {
				return zh.getServer().getOfflinePlayer(playerName).getUniqueId();
			}
			return null;
		}
	}
	
	protected boolean hasReachedMaxClaims(UUID playerUUID) {
		if (adminMode) {
			return false;
		}
		int claimsAmount;
		int maxClaims;
		claimsAmount = zh.getUM().getClaimsAmount(playerUUID);
		maxClaims = zh.getCM().getMaximumClaims(playerUUID);
		if (claimsAmount < maxClaims || maxClaims == -1) {
			return false;
		}
		else if (displayConsole) {
			if (samePlayer) {
				s.sendMessage(zh.getMM().getMessage(language, zh.getLM().maximumClaimsReached));
			}
			else {
				s.sendMessage(zh.getMM().getMessagePlayer(language, zh.getLM().maximumClaimsReachedOther, targetName));
			}
		}
		return true;
	}
	
	protected boolean hasPermission() {
    	return (hasPermissionSender(s, command, false, false));
	}
	
	protected boolean hasPermissionAdmin(UUID playerUUID, boolean hideConsole) {
    	return hasPermissionAdmin(playerUUID, command, hideConsole);
	}
	
	protected boolean hasPermissionAdmin(UUID playerUUID, String command, boolean hideConsole) {
		String perm = zh.getLM().zhPrefix + command + zh.getLM().adminSuffix;
		if (isPlayerOnline(playerUUID, false)) {
    		Player target = zh.getServer().getPlayer(playerUUID);
        	if (zh.getPerms().has(target, perm)) {
        		return true;
        	}
		}
        else if (displayConsole && !hideConsole) {
        	s.sendMessage(zh.getMM().getMessagePerm(language, zh.getLM().missingPermission, perm));
        }
    	return false;
	}
	
	protected boolean hasPermission(UUID playerUUID, String command, boolean ignoreModes, boolean hideConsole) {
		if (isPlayerOnline(playerUUID, false)) {
    		Player target = zh.getServer().getPlayer(playerUUID);
    		return hasPermissionPlayer(target, command, ignoreModes, hideConsole);
    	}
    	return false;
	}
	
	protected boolean hasPermissionPlayer(Player target, String command, boolean ignoreModes, boolean hideConsole) {
		String perm = zh.getLM().zhPrefix + command;
    	if ((adminMode || (idMode && !idAllow) || (targetMode && !targetAllow)) && !ignoreModes) {
    		perm += zh.getLM().adminSuffix;
    	}
    	if (zh.getPerms().has(target, perm)) {
    		return true;
    	}
    	else if (displayConsole && !hideConsole) {
    		s.sendMessage(zh.getMM().getMessagePlayerPerm(language, zh.getLM().missingPermissionOther, target.getName(), perm));
    	}
    	return false;
	}
	
	protected boolean hasPermissionSender(CommandSender s, String command, boolean ignoreModes, boolean hideConsole) {
		String perm = zh.getLM().zhPrefix + command;
    	if (!ignoreModes && (adminMode || (idMode && !idAllow) || (targetMode && !targetAllow)) ) {
    		perm += zh.getLM().adminSuffix;
    	}
    	if (zh.getPerms().has(s, perm)) {
    		return true;
    	}
    	else if (displayConsole && !hideConsole) {
    		s.sendMessage(zh.getMM().getMessagePerm(language, zh.getLM().missingPermission, perm));
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
						s.sendMessage(zh.getMM().getMessage(language, zh.getLM().horseAlreadyClaimed));
					}
					else {
						if (!targetMode) {
							targetName = zh.getUM().getPlayerName(horse);
						}
						s.sendMessage(zh.getMM().getMessagePlayer(language, zh.getLM().horseBelongsTo, targetName));
					}
				}
			}
			else if (displayConsole) {
				s.sendMessage(zh.getMM().getMessage(language, zh.getLM().horseNotTamed));
			}
		}
		else if (displayConsole) {
			if (idMode && !targetMode) {
				s.sendMessage(zh.getMM().getMessageUserID(language, zh.getLM().unknownHorseId, userID));
			}
			else if (idMode && targetMode) {
				s.sendMessage(zh.getMM().getMessagePlayerUserID(language, zh.getLM().unknownHorseIdOther, targetName, userID));
			}
		}
		return false;
	}
	
	protected boolean isHorseEmpty(boolean eject) {
		if (adminMode && eject) {
			horse.eject();
			return true;
		}
		Entity passenger = horse.getPassenger();
		if (passenger == null) {
			return true;
		}
		else if (displayConsole) {
			String passengerName = ((Player)passenger).getName();
			s.sendMessage(zh.getMM().getMessagePlayerHorse(language, zh.getLM().horseMountedBy, passengerName, horseName));
		}
		return false;
	}
	
	protected boolean isHorseLoaded() {
		if (horse != null) {
			return true;
		}
		else if (displayConsole) {
			s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().horseNotFound, zh.getUM().getHorseName(targetUUID, userID)));
		}
		return false;
	}
	
	protected boolean isNotOnHorse() {
		if (adminMode) {
			return true;
		}
		if (p.getVehicle() != horse) {
			return true;
		}
		else if (displayConsole) {
			s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().horseMounted, horseName));
		}
		return false;
	}
	
	protected boolean isOnHorse() {
		if (p.isInsideVehicle() && p.getVehicle() instanceof Horse) {
			return true;
		}
		else if (displayConsole) {
			s.sendMessage(zh.getMM().getMessage(language, zh.getLM().notOnHorse));
		}
		return false;
	}
	
	protected boolean isOwner() {
		return isOwner(false);
	}
	
	protected boolean isOwner(boolean hideConsole) {
		if (adminMode) {
			return true;
		}
		if (zh.getUM().isClaimedBy(p.getUniqueId(), horse)) {
			return true;
		}
		else if (displayConsole && !hideConsole) {
			String ownerName = zh.getUM().getPlayerName(horse);
			s.sendMessage(zh.getMM().getMessagePlayer(language, zh.getLM().horseBelongsTo, ownerName));
		}
		return false;
	}
	
	protected boolean isPlayer() {
		return isPlayer(true);
	}
	
	protected boolean isPlayer(boolean sendErrorMessage) {
		if (s instanceof Player) {
			p = (Player)s;
			language = zh.getUM().getPlayerLanguage(p.getUniqueId());
			playerCommand = true;
			return true;
		}
		else if (displayConsole && sendErrorMessage) {
			s.sendMessage(zh.getMM().getMessage(language, zh.getLM().playerCommand));
		}
		playerCommand = false;
		return playerCommand;
	}
	
	protected boolean isPlayerOnline(UUID playerUUID, boolean hideConsole) {
		if (zh.getServer().getPlayer(playerUUID) != null) {
			return true;
		}
    	if (displayConsole && !hideConsole) {
    		s.sendMessage(zh.getMM().getMessagePlayer(language, zh.getLM().playerOffline, zh.getUM().getPlayerName(playerUUID)));
    	}
		return false;
	}
	
	protected boolean isRegistered() {
		if (zh.getUM().isRegistered(horse)) {
			horseName = zh.getUM().getHorseName(horse);
			return true;
		}
		else if (displayConsole) {
			s.sendMessage(zh.getMM().getMessage(language, zh.getLM().horseNotClaimed));
		}
		return false;
	}
	
	protected boolean isRegistered(UUID targetUUID, String userID) {
		return isRegistered(targetUUID, userID, false);
	}
	
	protected boolean isRegistered(UUID targetUUID, String userID, boolean playerHorse) {
		if (zh.getUM().isRegistered(targetUUID, userID)) {
			horseName = zh.getUM().getHorseName(targetUUID, userID);
			return true;
		}
		else if (displayConsole) {
			sendUnknownHorseMessage(targetName, playerHorse);
		}
		return false;
	}
	
	protected boolean isWorldCompatible() {
		if (adminMode) {
			return true;
		}
		if (zh.getCM().isWorldCrossingAllowed()) {
			return true;
		}
		if (p.getWorld().equals(horse.getLocation().getWorld())) {
			return true;
		}
		else if (displayConsole) {
			s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().differentWorld, horseName));
		}
		return false;
	}
	
	protected boolean isWorldEnabled() {
		if (adminMode) {
			return true;
		}
		if (zh.getCM().isWorldEnabled(p.getWorld())) {
			return true;
		}
		else if (displayConsole) {
			s.sendMessage(zh.getMM().getMessage(language, zh.getLM().worldDisabled));
		}
		return false;
	}
	
	protected void sendCommandUsage() {
		sendCommandUsage(true);
	}
	
	protected void sendCommandUsage(boolean sendErrorMessage) {
		if (sendErrorMessage) {
			s.sendMessage(zh.getMM().getMessage(language, zh.getLM().commandIncorrect));
		}
		s.sendMessage(zh.getMM().getHeader(language, zh.getLM().commandUsageHeader, " ", true));
		s.sendMessage(zh.getMM().getCommandUsage(language, zh.getLM().commandUsageFormat, " ", command, true));
	}
	
	protected void sendUnknownHorseMessage(String playerName) {
		sendUnknownHorseMessage(playerName, false);
	}
	
	protected void sendUnknownHorseMessage(String playerName, boolean playerHorse) {
		if (displayConsole) {
			if (samePlayer || playerHorse) {
				s.sendMessage(zh.getMM().getMessageUserID(language, zh.getLM().unknownHorseId, userID));
			}
			else {
				s.sendMessage(zh.getMM().getMessagePlayerUserID(language, zh.getLM().unknownHorseIdOther, playerName, userID));
			}
		}
	}
}
