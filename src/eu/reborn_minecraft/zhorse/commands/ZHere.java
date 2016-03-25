package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;
import net.md_5.bungee.api.ChatColor;

public class ZHere extends Command {

	public ZHere(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			applyArgument(true);
			if (!idMode) {
				if (!targetMode) {
					userID = zh.getUM().getFavoriteUserID(p.getUniqueId());
					if (isRegistered(p.getUniqueId(), userID)) {
						horse = zh.getUM().getFavoriteHorse(p.getUniqueId());
						if (isHorseLoaded()) {
							execute();
						}
					}
				}
				else {
					sendCommandUsage();
				}
			}
			else {
				if (isRegistered(targetUUID, userID)) {
					horse = zh.getHM().getHorse(targetUUID, userID);
					if (isHorseLoaded()) {
						execute();
					}
				}
			}
		}
	}
	
	private void execute() {
		if (isOwner() && isHorseReachable() && isNotOnHorse() && !isHorseMounted() && zh.getEM().canAffordCommand(p, command)) {
			Location destination = p.getLocation();
			if (p.isFlying()) {
				Block block = destination.getWorld().getHighestBlockAt(destination);
				destination = new Location(destination.getWorld(), block.getX(), block.getY(), block.getZ());
			}
//			if old_method:
//				horse.teleport(destination);
//				zh.getUM().saveLocation(p.getUniqueId(), horse, userID);
//			else:	
			if (!horse.isCarryingChest()) { // TODO handle chests
				horse = zh.getHM().teleport(horse, destination);
				if (horse != null) {
					if (displayConsole) {
						zh.getMM().sendMessageHorse(s, LocaleEnum.horseTeleported, horseName);
					}
					zh.getEM().payCommand(p, command);
				}
				else {
					s.sendMessage(ChatColor.RED + "It seems that horses cannot spawn here, please report this to ZHorse's dev.");
				}
			}
			else {
				s.sendMessage(ChatColor.RED + "The new teleportation method has not been adapted to Donkeys or Mules yet.");
			}
		}
	}

}