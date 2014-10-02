package eu.reborn_minecraft.zhorse.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class Command {
	protected ZHorse zh;
	protected String[] a;
	protected CommandSender s;
	protected String command;
	protected boolean displayError;
	protected boolean adminMode;
	protected boolean idMode;
	protected boolean targetMode;
	protected String userID;
	protected String targetName;
	protected UUID targetUUID;
	protected Player p;
	protected Horse horse;
	protected String horseName;
	protected boolean samePlayer;
	
	public Command(ZHorse zh, String[] a, CommandSender s) {
		this.zh = zh;
		this.a = a;
		this.s = s;
		this.command = a[0];
		this.displayError = !(zh.getCM().isConsoleMuted());
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
				checkSuccess = (idModeCount == 0 && i != a.length-1 && !(a[i+1].equals("-a") || a[i+1].equals("-i") || a[i+1].equals("-t")));
				if (checkSuccess) {
					idMode = true;
					userID = a[i+1];
					idModeCount += 1;
				}
			}
			else if (a[i].equals("-t")) {
				checkSuccess = (targetModeCount == 0 && i != a.length-1 && !(a[i+1].equals("-a") || a[i+1].equals("-i") || a[i+1].equals("-t")));
				if (checkSuccess) {
					targetMode = true;
					targetName = a[i+1];
					targetModeCount += 1;
				}
			}
			if (!checkSuccess) {
				if (displayError) {
					sendCommandUsage();
				}
				return false;
			}
		}
		if (targetName == null) {
			targetName = p.getName();
			targetUUID = p.getUniqueId();
			samePlayer = true;
		}
		else {
			targetName = zh.getUM().getPlayerName(targetName);
			targetUUID = getPlayerUUID(targetName);
			samePlayer = p.getUniqueId().equals(targetUUID);
		}
		if (targetUUID == null) {
			s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().unknownPlayer), targetName));
			return false;
		}
		cleanArgs();
		return true;
	}

	protected void cleanArgs() {
		List<String> b = new ArrayList<String>();
		for (int i=1; i<a.length; i++) {
			if (!(a[i].equals("-a") || a[i].equals("-i") || a[i].equals("-t"))) {
				b.add(a[i]);
			}
			else if (a[i].equals("-i") || a[i].equals("-t")) {
				i++;
			}
		}
		String[] c = new String[b.size()];
		for (int i=0; i<b.size(); i++) {
			c[i] = b.get(i);
		}
		a = c;
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
				else if (displayError) {
					if (l < minLength) {
						s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseNameTooShort), minLength));
					}
					else if (l > maxLength) {
						s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseNameTooLong), maxLength));
					}
				}
			}
			else if (displayError) {
				s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().horseNameForbidden));
			}
		}
		else {
			if (!(idMode || targetMode)) {
				if (!zh.getCM().isHorseNameRequired() || adminMode) {
					horseName = zh.getCM().getRandomName();
					if (horseName != null) {
						return true;
					}
					String errorMessage = "\"horsenames\" list in config is empty ! Please add one name in it or increment \"minimum-horsename-length\".";
					zh.getLogger().severe(errorMessage);
				}
				else if (displayError) {
					s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().horseNameMandatory));
				}
			}
			else {
				horseName = zh.getUM().getHorseName(targetUUID, horse);
				return true;
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
	
	protected boolean hasReachedMaxClaims() {
		if (adminMode) {
			return false;
		}
		int claimsAmount;
		int maxClaims;
		if (!targetMode) {
			claimsAmount = zh.getUM().getClaimsAmount(p.getUniqueId());
			maxClaims = zh.getCM().getMaximumClaims(p);
		}
		else {
			claimsAmount = zh.getUM().getClaimsAmount(targetUUID);
			maxClaims = zh.getCM().getMaximumClaims(targetUUID);
		}
		if (claimsAmount < maxClaims || maxClaims == -1) {
			return false;
		}
		else if (displayError) {
			if (!targetMode) {
				s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().maximumClaimsReached));
			}
			else {
				s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().maximumClaimsReachedOther), targetName));
			}
		}
		return true;
	}
	
	protected boolean hasPermission() {
		String perm = "zh." + command;
    	if (adminMode || idMode || targetMode) {
    		perm += zh.getLM().admin;
    	}
    	if (zh.getPerms().has(s, perm)) {
    		return true;
    	}
    	else if (displayError) {
    		s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().missingPermission), perm));
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
				else if (displayError) {
					if (zh.getUM().isClaimedBy(p.getUniqueId(), horse)) {
						s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().horseAlreadyClaimed));
					}
					else {
						if (!targetMode) {
							targetName = zh.getUM().getPlayerName(horse);
						}
						s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseBelongsTo), targetName));
					}
				}
			}
			else if (displayError) {
				s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().horseNotTamed));
			}
		}
		else if (displayError) {
			if (idMode && !targetMode) {
				s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().unknownHorseId), userID));
			}
			else if (idMode && targetMode) {
				s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().unknownHorseIdOther), targetName, userID));
			}
		}
		return false;
	}
	
	protected boolean isOnHorse() {
		if (p.isInsideVehicle() && p.getVehicle() instanceof Horse) {
			return true;
		}
		else if (displayError) {
			s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().notOnHorse));
		}
		return false;
	}
	
	protected boolean isOwner() {
		if (adminMode) {
			return true;
		}
		if (zh.getUM().isRegistered(horse)) {
			if (zh.getUM().isClaimedBy(p.getUniqueId(), horse)) {
				return true;
			}
			else if (displayError) {
				String ownerName = zh.getUM().getPlayerName(horse);
				s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseBelongsTo), ownerName));
			}
		}
		else if (displayError) {
			s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().horseNotClaimed));
		}
		return false;
	}
	
	protected boolean isPlayer() {
		if (s instanceof Player) {
			p = (Player)s;
			return true;
		}
		else if (displayError) {
			s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().playerCommand));
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
		else if (displayError) {
			s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().worldDisabled));
		}
		return false;
	}
	
	protected void sendCommandUsage() {
		s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().commandIncorrect));
		s.sendMessage(" " + zh.getLM().getHeaderMessage(zh.getLM().commandUsageHeader));
		s.sendMessage(" " + String.format(zh.getLM().getCommandAnswer(zh.getLM().commandUsage, true), zh.getLM().getCommandUsage(command)));
	}
	
	protected void sendUnknownHorseMessage(String playerName) {
		sendUnknownHorseMessage(playerName, false);
	}
	
	protected void sendUnknownHorseMessage(String playerName, boolean playerHorse) {
		if (displayError) {
			if (samePlayer || playerHorse) {
				s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().unknownHorseId), userID));
			}
			else {
				s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().unknownHorseIdOther), playerName, userID));
			}
		}
	}
}
