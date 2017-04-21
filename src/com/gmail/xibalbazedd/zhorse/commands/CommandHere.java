package com.gmail.xibalbazedd.zhorse.commands;

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
					if (isRegistered(p.getUniqueId(), horseID)) {
						horse = zh.getHM().getFavoriteHorse(p.getUniqueId());
						if (isHorseLoaded(true)) {
							execute();
						}
					}
				}
				else {
					sendCommandUsage();
				}
			}
			else {
				if (isRegistered(targetUUID, horseID)) {
					horse = zh.getHM().getHorse(targetUUID, Integer.parseInt(horseID));
					if (isHorseLoaded(true)) {
						execute();
					}
				}
			}
		}
	}
	
	private void execute() {
		if (isOwner() && isWorldCrossable(p.getWorld()) && isNotOnHorse() && !isHorseMounted() && !isHorseLeashed() && isHorseInRangeHere() && zh.getEM().canAffordCommand(p, command)) {
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