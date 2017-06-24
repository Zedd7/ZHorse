package com.gmail.xibalbazedd.zhorse.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.HorseStableRecord;
import com.gmail.xibalbazedd.zhorse.enums.StableSubCommandEnum;
import com.gmail.xibalbazedd.zhorse.enums.KeyWordEnum;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

public class CommandStable extends AbstractCommand {
	
	private String fullCommand;
	private String subCommand;

	public CommandStable(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			if (!idMode) {
				if (!targetMode) {
					boolean ownsHorse = ownsHorse(targetUUID, true);
					if (isOnHorse(ownsHorse)) {
						horse = (AbstractHorse) p.getVehicle();
						if (isRegistered(horse)) {
							execute();
						}
					}
					else if (ownsHorse) {
						horseID = zh.getDM().getPlayerFavoriteHorseID(p.getUniqueId()).toString();
						execute(p.getUniqueId(), horseID);
					}
				}
				else {
					sendCommandUsage();
				}
			}
			else {
				execute(targetUUID, horseID);
			}
		}
	}
	
	private void execute(UUID ownerUUID, String horseID) {
		if (isRegistered(ownerUUID, horseID)) {
			horse = zh.getHM().getHorse(ownerUUID, Integer.parseInt(horseID));
			if (isHorseLoaded(true)) {
				execute();
			}
		}
	}
	
	private void execute() {
		if (zh.getEM().canAffordCommand(p, command)) {
			if (!args.isEmpty()) {
				subCommand = args.get(0);
				if (subCommand.equalsIgnoreCase(StableSubCommandEnum.GO.name())) {
					fullCommand = command + KeyWordEnum.DOT.getValue() + StableSubCommandEnum.GO.name().toLowerCase();
					teleportToStable();
				}
				else if (subCommand.equalsIgnoreCase(StableSubCommandEnum.SET.name())) {
					fullCommand = command + KeyWordEnum.DOT.getValue() + StableSubCommandEnum.SET.name().toLowerCase();
					setStableLocation();
				}
				else if (subCommand.equalsIgnoreCase(StableSubCommandEnum.UNSET.name())) {
					fullCommand = command + KeyWordEnum.DOT.getValue() + StableSubCommandEnum.UNSET.name().toLowerCase();
					unsetStableLocation();
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_SUB_COMMAND) {{ setValue(subCommand); setValue(command); }});
					sendSubCommandDescriptionList(StableSubCommandEnum.class);
				}
			}
			else {
				sendSubCommandDescriptionList(StableSubCommandEnum.class);
			}
		}
	}

	private void teleportToStable() {
		if (hasPermission(s, fullCommand , true, false)) {
			if (isOwner(true) && isWorldCrossable(p.getWorld()) && isWorldCrossable(horse.getWorld()) && !isHorseMounted() && !isHorseLeashed()) {
				if (zh.getDM().isHorseStableRegistered(horse.getUniqueId())) {
					Location stableLocation = zh.getDM().getHorseStableLocation(horse.getUniqueId());
					if (isHorseInRangeStable(stableLocation)) {
						horse = zh.getHM().teleportHorse(horse, stableLocation);
						if (horse != null) {
							zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_TELEPORTED_TO_STABLE) {{ setHorseName(horseName); }});
							zh.getEM().payCommand(p, command);
						}
						else {
							zh.getMM().sendMessage(s, ChatColor.RED + "It seems that horses cannot spawn here, please report this to the developer. (https://github.com/Xibalba/ZHorse/issues/new)");
						}
					}
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.STABLE_NOT_SET) {{ setHorseName(horseName); }});
				}
			}
		}
	}

	private void setStableLocation() {
		if (hasPermission(s, fullCommand , true, false)) {
			if (isOwner(true)) {
				if (zh.getDM().isHorseStableRegistered(horse.getUniqueId())) {
					zh.getDM().removeHorseStable(horse.getUniqueId());
				}
				Location playerLocation = getGroundedLocation(p.getLocation());
				HorseStableRecord stableRecord = new HorseStableRecord(horse.getUniqueId().toString(), playerLocation);
				zh.getDM().registerHorseStable(stableRecord);
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.STABLE_SET) {{ setHorseName(horseName); }});
				zh.getEM().payCommand(p, command);
			}
		}
	}

	private void unsetStableLocation() {
		if (hasPermission(s, fullCommand , true, false)) {
			if (isOwner(true)) {
				if (zh.getDM().isHorseStableRegistered(horse.getUniqueId())) {
					zh.getDM().removeHorseStable(horse.getUniqueId());
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.STABLE_UNSET) {{ setHorseName(horseName); }});
					zh.getEM().payCommand(p, command);
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.STABLE_NOT_SET) {{ setHorseName(horseName); }});
				}
			}
		}
	}

}