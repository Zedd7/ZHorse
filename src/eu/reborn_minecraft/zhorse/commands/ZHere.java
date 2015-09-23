package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;

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
					horse = zh.getUM().getHorse(targetUUID, userID);
					if (isHorseLoaded()) {
						execute();
					}
				}
			}
		}
	}
	
	private void execute() {
		if (isOwner() && isHorseReachable() && isNotOnHorse() && !isHorseMounted() && zh.getEM().canAffordCommand(p, command)) {
			Location location;
			if (!p.isFlying()) {
				location = p.getLocation();
			}
			else {
				Block block = p.getWorld().getHighestBlockAt(p.getLocation());
				location = new Location(p.getWorld(), block.getX(), block.getY(), block.getZ());
			}
			horse.teleport(location);
			if (displayConsole) {
				zh.getMM().sendMessageHorse(s, zh.getLM().horseTeleported, horseName);
			}
			zh.getEM().payCommand(p, command);
		}
	}

}