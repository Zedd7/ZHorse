package com.gmail.xibalbazedd.zhorse.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

public class CommandHere extends AbstractCommand {

	public CommandHere(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled() && applyArgument(true)) {
			if (!idMode) {
				if (!targetMode) {
					horseID = zh.getDM().getPlayerFavoriteHorseID(p.getUniqueId()).toString();
					execute(p.getUniqueId(), horseID);
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
		if (isOwner(true) && isWorldCrossable(p.getWorld()) && isNotOnHorse() && !isHorseMounted() && !isHorseLeashed() && isHorseInRangeHere() && zh.getEM().canAffordCommand(p, command)) {
			Location destination = p.getLocation();
			if (p.isFlying()) {
				Block block = destination.getWorld().getHighestBlockAt(destination);
				destination = new Location(destination.getWorld(), block.getX(), block.getY(), block.getZ());
			}
			horse = zh.getHM().teleportHorse(horse, destination);
			if (horse != null) {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_TELEPORTED) {{ setHorseName(horseName); }});
				zh.getEM().payCommand(p, command);
			}
			else {
				zh.getMM().sendMessage(s, ChatColor.RED + "It seems that horses cannot spawn here, please report this to the developer. (https://github.com/Xibalba/ZHorse/issues/new)");
			}
		}
	}

}