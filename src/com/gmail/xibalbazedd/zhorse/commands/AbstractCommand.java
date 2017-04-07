package com.gmail.xibalbazedd.zhorse.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.PlayerRecord;
import com.gmail.xibalbazedd.zhorse.enums.CommandAdminEnum;
import com.gmail.xibalbazedd.zhorse.enums.CommandEnum;
import com.gmail.xibalbazedd.zhorse.enums.CommandFriendEnum;
import com.gmail.xibalbazedd.zhorse.enums.CommandSettingsEnum;
import com.gmail.xibalbazedd.zhorse.enums.HorseStatisticEnum;
import com.gmail.xibalbazedd.zhorse.enums.HorseVariantEnum;
import com.gmail.xibalbazedd.zhorse.enums.KeyWordEnum;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.managers.MessageManager;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

import net.md_5.bungee.api.ChatColor;

public abstract class AbstractCommand {
	
	protected ZHorse zh;
	protected CommandSender s;
	protected CommandSender console;
	protected Player p;
	protected AbstractHorse horse;
	protected UUID targetUUID;
	protected String[] a;
	protected String argument;
	protected String command;
	protected String horseID;
	protected String horseName;
	protected String targetName;
	protected boolean useExactStats;
	protected boolean useVanillaStats;
	protected boolean playerCommand;
	
	protected boolean adminMode = false;
	protected boolean idMode = false;
	protected boolean targetMode = false;
	protected boolean samePlayer = false;
	protected boolean targetIsOwner = true;
	
	public AbstractCommand(ZHorse zh, CommandSender s, String[] a) {
		this.zh = zh;
		this.a = a;
		this.s = s;
		console = zh.getServer().getConsoleSender();
		command = a[0].toLowerCase();
		useExactStats = zh.getCM().shouldUseExactStats();
		useVanillaStats = zh.getCM().shouldUseVanillaStats();
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
				valid = !idMode && i != a.length - 1 && !a[i + 1].startsWith("-");
				if (valid) { // avoid to exit the loop
					idMode = true;
					horseID = a[i + 1];
					i++; // skip the ID
				}
			}
			else if (a[i].equalsIgnoreCase("-p") || a[i].equalsIgnoreCase("-t")) {
				valid = !targetMode && i != a.length - 1 && !a[i + 1].startsWith("-");
				if (valid) { // avoid to exit the loop
					targetMode = true;
					targetName = a[i + 1];
					i++; // skip the target
				}
			}
			else { // add to argument if not a flag
				if (!argument.isEmpty()) {
					argument += " ";
				}
				argument += a[i];
			}
			if (!valid) {
				sendCommandUsage();
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
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_PLAYER) {{ setPlayerName(targetName); }});
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
		if (idMode || argument.isEmpty()) return true;
		
		idMode = true;
		UUID ownerUUID = targetIsOwner ? targetUUID : p.getUniqueId();
		horseName = zh.getDM().getHorseName(ownerUUID, argument); // fix potential case errors
		Integer horseIDInt = zh.getDM().getHorseID(ownerUUID, horseName);
		if (horseIDInt != null) {
			horseID = horseIDInt.toString();
			return true;
		}
		else {
			if (samePlayer || !targetIsOwner) {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_HORSE_NAME) {{ setHorseName(horseName); }});
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_HORSE_NAME_OTHER) {{ setHorseName(horseName); setPlayerName(targetName); }});
			}
			return false;
		}
		
	}
	
	protected boolean applyArgumentToTarget() {
		if (targetMode) return true;
		
		targetMode = !argument.isEmpty();
		targetName = argument;
		targetUUID = null;
		return analyseModes();
	}
	
	protected void applyHorseName(UUID ownerUUID) {
		String customHorseName;
		if (MessageManager.isColorized(argument) && (adminMode || zh.getCM().isColorBypassEnabled(p.getUniqueId()))) {
			customHorseName = MessageManager.applyColors(argument);
		}
		else {
			String groupColorCode = zh.getCM().getGroupColorCode(ownerUUID);
			zh.getMM();
			customHorseName = MessageManager.applyColors(horseName, groupColorCode);
		}
		horse.setCustomName(customHorseName);
	}
	
	protected void applyHorsePrice(int price) {
		String currencySymbol = zh.getMM().getMessage(console, new MessageConfig(LocaleEnum.CURRENCY_SYMBOL), true);
		String horsePrice = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.HORSE_PRICE) {{ setAmount(price); setCurrencySymbol(currencySymbol); }}, true);
		horse.setCustomName(horse.getCustomName() + ChatColor.RESET + horsePrice);
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
		if (adminMode || zh.getCM().isHorseNameAllowed()) {
			zh.getMM();
			horseName = MessageManager.removeColors(argument);
			int maximumLength = zh.getCM().getMaximumHorseNameLength();
			int minimumLength = zh.getCM().getMinimumHorseNameLength();
			int length = horseName.length();
			if ((adminMode || length >= minimumLength) && length <= maximumLength) { // adminMode cannot override maxL because of DB limitations
				if (adminMode || !zh.getCM().isHorseNameBanned(horseName)) {
					return true;
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_NAME_BANNED) {{ setHorseName(horseName); }});
				}
			}
			else {
				if (length < minimumLength) {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_NAME_TOO_SHORT) {{ setAmount(minimumLength); }});
				}
				else if (length > maximumLength) {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_NAME_TOO_LONG) {{ setAmount(maximumLength); }});
				}
			}
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_NAME_FORBIDDEN));
		}
		return false;
	}
	
	private boolean craftPreviousHorseName(boolean keepPreviousName) {
		if (adminMode || !zh.getCM().isHorseNameRequired()) {
			if (keepPreviousName && zh.getDM().isHorseRegistered(horse.getUniqueId())) {
				horseName = zh.getDM().getHorseName(horse.getUniqueId());
			}
			else {
				if (zh.getCM().isRandomHorseNameEnabled()) {
					horseName = zh.getCM().getRandomHorseName();
				}
				else {
					horseName = zh.getCM().getDefaultHorseName();
				}
			}
			return true;
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_NAME_MANDATORY));
			return false;
		}
	}
	
	protected UUID getPlayerUUID(String playerName) {
		if (zh.getDM().isPlayerRegistered(playerName)) {
			return zh.getDM().getPlayerUUID(playerName);
		}
		else {
			return null;
		}
	}
	
	protected String getRemainingClaimsMessage(UUID playerUUID) {
		int horseCount = zh.getDM().getHorseCount(playerUUID);
		int maxClaims = zh.getCM().getClaimsLimit(playerUUID);
		return zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.REMAINING_CLAIMS_FORMAT) {{ setAmount(horseCount); setMax(maxClaims); }}, true);
	}
	
	protected boolean hasReachedClaimsLimit(boolean useTargetList) {
		if (adminMode) return false;
		
		UUID playerUUID = useTargetList ? targetUUID : p.getUniqueId();
		int horseCount = zh.getDM().getHorseCount(playerUUID);
		int claimsLimit = zh.getCM().getClaimsLimit(playerUUID);
		if (horseCount < claimsLimit || claimsLimit == -1) {
			return false;
		}
		else {
			if (samePlayer || !useTargetList) {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.CLAIMS_LIMIT_REACHED));
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.CLAIMS_LIMIT_REACHED_OTHER) {{ setPlayerName(targetName); }});
			}
			return true;
		}
	}
	
	protected boolean hasPermission() {
    	return (hasPermission(s, command, false, false));
	}
	
	protected boolean hasPermission(UUID playerUUID, String command, boolean ignoreAdminMode, boolean hideConsole) {
		if (isPlayerOnline(playerUUID, hideConsole)) {
    		CommandSender target = zh.getServer().getPlayer(playerUUID);
    		return hasPermission(target, command, ignoreAdminMode, hideConsole);
    	}
		else {
			return false;
		}
	}
	
	protected boolean hasPermission(CommandSender s, String command, boolean ignoreAdminMode, boolean hideConsole) {
		String permission = KeyWordEnum.ZH_PREFIX.getValue() + command;
    	if (adminMode && !ignoreAdminMode) {
    		return hasPermissionAdmin(s, command, hideConsole);
    	}
    	if (zh.getPM().has(s, permission)) {
    		return true;
    	}
    	else {
    		if (!hideConsole) {
    			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.MISSING_PERMISSION) {{ setPermission(permission); }});
    		}
    		return false;
    	}
	}
	
	protected boolean hasPermissionAdmin(boolean hideConsole) {
    	return hasPermissionAdmin(s, command, hideConsole);
	}
	
	protected boolean hasPermissionAdmin(UUID playerUUID, String command, boolean hideConsole) {
		if (isPlayerOnline(playerUUID, hideConsole)) {
    		CommandSender target = zh.getServer().getPlayer(playerUUID);
    		return hasPermissionAdmin(target, command, hideConsole);
    	}
		else {
			return false;
		}
	}
	
	protected boolean hasPermissionAdmin(CommandSender s, String command, boolean hideConsole) {
		String permission = KeyWordEnum.ZH_PREFIX.getValue() + command + KeyWordEnum.ADMIN_SUFFIX.getValue();
		if (zh.getPM().has(s, permission)) {
        	return true;
		}
        else {
        	if (!hideConsole) {
        		zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.MISSING_PERMISSION) {{ setPermission(permission); }});
        	}
        	return false;
        }
	}
	
	protected boolean isClaimable() {
		if (horse != null) {
			if (adminMode) return true;
				
			if (!zh.getDM().isHorseRegistered(horse.getUniqueId())) {
				if (horse.isTamed()) {
					return true;
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_NOT_TAMED));
				}
			}
			else {
				if (zh.getDM().isHorseOwnedBy(p.getUniqueId(), horse.getUniqueId())) {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_ALREADY_CLAIMED));
				}
				else {
					if (!targetMode) {
						targetName = zh.getDM().getOwnerName(horse.getUniqueId());
					}
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_BELONGS_TO) {{ setPlayerName(targetName); }});
				}
			}
		}
		else {
			if (idMode && !targetMode) {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_HORSE_ID) {{ setHorseID(horseID); }});
			}
			else if (idMode && targetMode) {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_HORSE_ID_OTHER) {{ setHorseID(horseID); setPlayerName(targetName); }});
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
		if (adminMode) return true;
		
		Location playerLocation = p.getLocation();
		Location horseLocation = horse.getLocation();
		int xDistance = Math.abs(Math.abs(playerLocation.getBlockX()) - Math.abs(horseLocation.getBlockX()));
		int zDistance = Math.abs(Math.abs(playerLocation.getBlockZ()) - Math.abs(horseLocation.getBlockZ()));
		double distance = Math.sqrt(Math.pow(xDistance, 2) + Math.pow(zDistance, 2));
		if (distance <= maxRadius || maxRadius == -1 || !playerLocation.getWorld().equals(horseLocation.getWorld())) {
			return true;
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_OUT_OF_RANGE) {{ setHorseName(horseName); setMax(maxRadius); }});
			return false;
		}
	}
	
	protected boolean isHorseLeashed() {
		boolean blockLeashedTeleport = zh.getCM().shouldBlockLeashedTeleport();
		if (!horse.isLeashed()) {
			return false;
		}
		else if (adminMode || !blockLeashedTeleport) {
			return false;
		}
		else {
			Entity leashHolder = horse.getLeashHolder();
			if (leashHolder instanceof Player) {
				String leashHolderName = ((Player) leashHolder).getName();
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_LEASHED_BY) {{ setHorseName(horseName); setPlayerName(leashHolderName); }});
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_LEASHED) {{ setHorseName(horseName); }});
			}
			return true;
		}		
	}
	
	protected boolean isHorseLoaded(boolean useTargetUUID) {
		if (horse != null) {
			return true;
		}
		else {
			UUID ownerUUID = useTargetUUID ? targetUUID : p.getUniqueId();
			String horseName = zh.getDM().getHorseName(ownerUUID, Integer.parseInt(horseID));
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_NOT_FOUND) {{ setHorseName(horseName); }});
			return false;
		}
	}
	
	protected boolean isHorseMounted() {
		boolean blockMountedTeleport = zh.getCM().shouldBlockMountedTeleport();
		List<Entity> passengerList = horse.getPassengers();
		if (passengerList.isEmpty()) {
			return false;
		}
		else if (adminMode || !blockMountedTeleport) {
			horse.eject();
			return false;
		}		
		else {
			Entity mainPassenger = passengerList.get(0);
			String passengerName = mainPassenger instanceof Player ? ((Player) mainPassenger).getName() : "creature";
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_MOUNTED_BY) {{ setHorseName(horseName); setPlayerName(passengerName); }});
			return true;
		}
	}
	
	protected boolean isNotOnHorse() {
		return isNotOnHorse(false);
	}
	
	protected boolean isNotOnHorse(boolean hideConsole) {
		if (adminMode || p.getVehicle() != horse) {
			return true;
		}
		else {
			if (!hideConsole) {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_MOUNTED) {{ setHorseName(horseName); }});
			}
			return false;
		}
	}
	
	protected boolean isOnHorse(boolean hideConsole) {
		if (p.isInsideVehicle() && p.getVehicle() instanceof AbstractHorse) {
			return true;
		}
		else {
			if (!hideConsole) {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.NOT_ON_HORSE));
			}
			return false;
		}
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
		if ((adminMode && !ignoreModes) || zh.getDM().isHorseOwnedBy(playerUUID, horse.getUniqueId())) {
			return true;
		}
		else {
			if (!hideConsole) {
				String ownerName = zh.getDM().getOwnerName(horse.getUniqueId());
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_BELONGS_TO) {{ setPlayerName(ownerName); }});
			}
			return false;
		}
	}
	
	protected boolean isPlayer() {
		return isPlayer(false);
	}
	
	protected boolean isPlayer(boolean hideConsole) {
		if (s instanceof Player) {
			p = (Player) s;
			playerCommand = true;
			if (!zh.getDM().isPlayerRegistered(p.getUniqueId())) {
				PlayerRecord playerRecord = new PlayerRecord(p.getUniqueId().toString(), p.getName(), zh.getCM().getDefaultLanguage(), zh.getDM().getDefaultFavoriteHorseID());
				zh.getDM().registerPlayer(playerRecord);
			}
		}
		else if (!hideConsole) {
			playerCommand = false;
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.PLAYER_COMMAND));
		}
		return playerCommand;
	}
	
	protected boolean isPlayerDifferent() {
		if (adminMode || !samePlayer) {
			return true;
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.SAME_PLAYER));
			return false;
		}
	}
	
	protected boolean isPlayerOnline(UUID playerUUID, boolean hideConsole) {
		if (playerUUID != null && zh.getServer().getOfflinePlayer(playerUUID).isOnline()) {
			return true;
		}
		else {
			if (!hideConsole) {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.PLAYER_OFFLINE) {{ setPlayerName(targetName); }});
			}
			return false;
		}
	}
	
	protected boolean isRegistered(AbstractHorse horse) {
		if (zh.getDM().isHorseRegistered(horse.getUniqueId())) {
			horseName = zh.getDM().getHorseName(horse.getUniqueId());
			return true;
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_NOT_CLAIMED));
			return false;
		}
	}
	
	protected boolean isRegistered(UUID targetUUID) {
		if (targetUUID != null && zh.getDM().isPlayerRegistered(targetUUID)) {
			return true;
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_PLAYER) {{ setPlayerName(targetName); }});
			return false;
		}
	}
	
	protected boolean isRegistered(UUID targetUUID, String horseID) {
		return isRegistered(targetUUID, horseID, false);
	}
	
	protected boolean isRegistered(UUID targetUUID, String horseID, boolean isOwner) {
		if (horseID != null) {
			try {
				if (zh.getDM().isHorseRegistered(targetUUID, Integer.parseInt(horseID))) {
					horseName = zh.getDM().getHorseName(targetUUID, Integer.parseInt(horseID));
					return true;
				}
			} catch (NumberFormatException e) {}
		}
		
		if (targetUUID == null) {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_PLAYER) {{ setPlayerName(targetName); }});
		}
		else {
			if (horseID == null) {
				if (samePlayer || isOwner) {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_HORSE_NAME) {{ setHorseName(horseName); }});
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_HORSE_NAME_OTHER) {{ setHorseName(horseName); setPlayerName(targetName); }});
				}
			}
			else {
				if (samePlayer || isOwner) {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_HORSE_ID) {{ setHorseID(horseID); }});
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_HORSE_ID_OTHER) {{ setHorseID(horseID); setPlayerName(targetName); }});
				}
			}
		}
		return false;
	}
	
	protected boolean isStatHealthValid(double health) {
		double minHealth = HorseStatisticEnum.MIN_HEALTH.getValue(useVanillaStats);
		double maxHealth = HorseStatisticEnum.MAX_HEALTH.getValue(useVanillaStats);
		if (adminMode || (health >= minHealth && health <= maxHealth)) {
			return true;
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_HEALTH_ARGUMENT) {{ setAmount((int) minHealth); setMax((int) maxHealth); }});
			return false;
		}
	}
	
	protected boolean isStatSpeedValid(double speed) {
		if (adminMode) return true;
		
		double minSpeed = HorseStatisticEnum.MIN_SPEED.getValue(useVanillaStats);
		double maxSpeed = HorseStatisticEnum.MAX_SPEED.getValue(useVanillaStats);
		if (useExactStats) {
			if (speed >= minSpeed && speed <= maxSpeed) {
				return true;
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_SPEED_ARGUMENT) {{ setAmount(minSpeed); setMax(maxSpeed); setArithmeticPrecision(3); }});
				return false;
			}
		}
		else {
			if (speed >= (minSpeed / maxSpeed) * 100 && speed <= 100) {
				return true;
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_SPEED_ARGUMENT) {{ setAmount(minSpeed / maxSpeed); setMax(1); setUsePercentage(true); }});
				return false;
			}		
		}
	}
	
	protected boolean isStatJumpStrengthValid(double jumpStrength) {
		if (adminMode) return true;
		
		double minJumpStrength = HorseStatisticEnum.MIN_JUMP_STRENGTH.getValue(useVanillaStats);
		double maxJumpStrength = HorseStatisticEnum.MAX_JUMP_STRENGTH.getValue(useVanillaStats);
		if (useExactStats) {
			if (jumpStrength >= minJumpStrength && jumpStrength <= maxJumpStrength) {
				return true;
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_JUMP_ARGUMENT) {{ setAmount(minJumpStrength); setMax(maxJumpStrength); setArithmeticPrecision(3); }});
				return false;
			}
		}
		else {
			if (jumpStrength >= (minJumpStrength / maxJumpStrength) * 100 && jumpStrength <= 100) {
				return true;
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_JUMP_ARGUMENT) {{ setAmount(minJumpStrength / maxJumpStrength); setMax(1); setUsePercentage(true); }});
				return false;
			}
		}
	}
	
	protected boolean isStatLlamaStrengthValid(int llamaStrenth) {
		int minLlamaStrength = (int) HorseStatisticEnum.MIN_LLAMA_STRENGTH.getValue(useVanillaStats);
		int maxLlamaStrength = (int) HorseStatisticEnum.MAX_LLAMA_STRENGTH.getValue(useVanillaStats);
		if (adminMode || (llamaStrenth >= minLlamaStrength && llamaStrenth <= maxLlamaStrength)) {
			return true;
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_STRENGTH_ARGUMENT) {{ setAmount(minLlamaStrength); setMax(maxLlamaStrength); }});
			return false;
		}
	}
	
	protected boolean isWorldCrossable(World world) {
		if (adminMode || zh.getCM().isWorldCrossable(world)) {
			return true;
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.WORLD_UNCROSSABLE) {{ setHorseName(horseName); }});
			return false;
		}
	}

	protected boolean isWorldEnabled() {
		if (adminMode || zh.getCM().isWorldEnabled(p.getWorld())) {
			return true;
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.WORLD_DISABLED));
			return false;
		}
	}
	
	protected boolean ownsHorse(UUID playerUUID, boolean hideConsole) {
		if (zh.getDM().getHorseCount(playerUUID) > 0) {
			return true;
		}
		else {
			if (!hideConsole) {
				String remainingClaimsMessage = getRemainingClaimsMessage(playerUUID);
				if (samePlayer) {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.NO_HORSE_OWNED) {{ setValue(remainingClaimsMessage); }});
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.NO_HORSE_OWNED_OTHER) {{ setPlayerName(targetName); setValue(remainingClaimsMessage); }});
				}
			}
			return false;
		}
	}
	
	protected void sendCommandDescription(String command, String permission, boolean subCommand) {
		boolean displayCommand = hasPermission(targetUUID, permission, true, true); // Display commands that target can use
		displayCommand &= samePlayer ? true : hasPermission(p.getUniqueId(), permission, true, true); // But hide command that player shouldn't see
		if (displayCommand) {
			LocaleEnum commandDescription;
			if (!subCommand) {
				commandDescription = LocaleEnum.valueOf(command.toUpperCase() + KeyWordEnum.SEPARATOR.getValue() + KeyWordEnum.DESCRIPTION.getValue());
			}
			else {
				String commandLocaleIndex = this.command.toUpperCase()
						+ KeyWordEnum.SEPARATOR.getValue()
						+ command.toUpperCase()
						+ KeyWordEnum.SEPARATOR.getValue()
						+ KeyWordEnum.DESCRIPTION.getValue();
				commandDescription = LocaleEnum.valueOf(commandLocaleIndex);
				command = this.command;
			}
			if (zh.getEM().isCommandFree(p.getUniqueId(), command)) {
				zh.getMM().sendMessage(s, new MessageConfig(commandDescription) {{ setSpaceCount(1); }}, true);
			}
			else {
				String message = zh.getMM().getMessage(s, new MessageConfig(commandDescription) {{ setSpaceCount(1); }}, true);
				
				int cost = zh.getCM().getCommandCost(command);
				LocaleEnum costColorCodeIndex = zh.getEM().canAffordCommand(p, command, true) ? LocaleEnum.AFFORDABLE_COLOR : LocaleEnum.UNAFFORDABLE_COLOR;
				String costColorCode = zh.getMM().getMessage(s, new MessageConfig(costColorCodeIndex), true);
				
				String currencySymbol = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.CURRENCY_SYMBOL), true);
				String costMessage = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.COMMAND_COST) {{ setAmount(cost); setCurrencySymbol(currencySymbol); }}, true);
				
				zh.getMM().sendMessage(s, message + costColorCode + costMessage);
			}
		}
	}

	protected void sendCommandDescriptionList() {
		String header = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.COMMAND_LIST_HEADER), true);
		zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HEADER_FORMAT) {{ setValue(header); }}, true);
		for (CommandEnum command : CommandEnum.values()) {
			String commandName = command.getName();
			String permission = commandName;
			sendCommandDescription(commandName, permission, false);
		}
	}
	
	protected void sendCommandAdminDescriptionList() {
		String header = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.ADMIN_COMMAND_LIST_HEADER), true);
		zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HEADER_FORMAT) {{ setValue(header); }}, true);
		for (CommandAdminEnum command : CommandAdminEnum.values()) {
			String commandName = command.getName();
			String permission = this.command + KeyWordEnum.DOT.getValue() + command.getName();				
			sendCommandDescription(commandName, permission, true);
		}
	}
	
	protected void sendCommandFriendDescriptionList() {
		String header = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.FRIEND_COMMAND_LIST_HEADER), true);
		zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HEADER_FORMAT) {{ setValue(header); }}, true);
		for (CommandFriendEnum command : CommandFriendEnum.values()) {
			String commandName = command.getName();
			String permission = this.command + KeyWordEnum.DOT.getValue() + command.getName();
			sendCommandDescription(commandName, permission, true);
		}
	}
	
	protected void sendCommandSettingsDescriptionList() {
		String header = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.SETTINGS_COMMAND_LIST_HEADER), true);
		zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HEADER_FORMAT) {{ setValue(header); }}, true);
		for (CommandSettingsEnum command : CommandSettingsEnum.values()) {
			String commandName = command.getName();
			String permission = this.command + KeyWordEnum.DOT.getValue() + command.getName();
			sendCommandDescription(commandName, permission, true);
		}
	}
	
	protected void sendCommandUsage() {
		sendCommandUsage(command, false, false);
	}
	
	protected void sendCommandUsage(String command, boolean subCommand, boolean hideError) {
		if (!hideError) {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.MISSING_ARGUMENTS));
		}
		
		LocaleEnum commandUsage;
		if (!subCommand) {
			commandUsage = LocaleEnum.valueOf(command.toUpperCase() + KeyWordEnum.SEPARATOR.getValue() + KeyWordEnum.USAGE.getValue());
		}
		else {
			String commandUsageIndex = this.command.toUpperCase()
				+ KeyWordEnum.SEPARATOR.getValue()
				+ command.toUpperCase()
				+ KeyWordEnum.SEPARATOR.getValue()
				+ KeyWordEnum.USAGE.getValue();
			commandUsage = LocaleEnum.valueOf(commandUsageIndex);
		}			
		zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.COMMAND_USAGE_HEADER) {{ setSpaceCount(1); }}, true);
		String commandUsageMessage = zh.getMM().getMessage(s, new MessageConfig(commandUsage), true);
		zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.COMMAND_USAGE_FORMAT) {{ setSpaceCount(1); setValue(commandUsageMessage); }}, true);
	}
	
	protected void sendHorseColorList() {
		sendHorseOptionList(Horse.Color.values(), LocaleEnum.LIST_HORSE_COLOR);
	}
	
	protected void sendLlamaColorList() {
		sendHorseOptionList(Llama.Color.values(), LocaleEnum.LIST_LLAMA_COLOR);
	}

	protected void sendHorseStyleList() {
		sendHorseOptionList(Horse.Style.values(), LocaleEnum.LIST_HORSE_STYLE);
	}

	protected void sendAbstractHorseVariantList() {
		sendHorseOptionList(HorseVariantEnum.getAllCodeArray(), LocaleEnum.LIST_HORSE_VARIANT);
	}
	
	protected <T> void sendHorseOptionList(T[] horseOptionArray, LocaleEnum index) {
		String horseOptionArrayMessage = "";
		for (int i = 0; i < horseOptionArray.length; i++) {
			final String horseOption = horseOptionArray[i].toString().toLowerCase();
			horseOptionArrayMessage += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.HORSE_OPTION_FORMAT) {{ setValue(horseOption); }}, true);
			if (i < horseOptionArray.length - 1) {
				horseOptionArrayMessage += ", ";
			}
		}
		horseOptionArrayMessage += ChatColor.RESET;
		final String message = horseOptionArrayMessage;
		zh.getMM().sendMessage(s, new MessageConfig(index) {{ setSpaceCount(1); setValue(message); }}, true);
	}

}
