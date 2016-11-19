package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ChestedHorse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class CommandHere extends AbstractCommand {

	public CommandHere(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
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
		if (isOwner() && isWorldCrossable(p.getWorld()) && isNotOnHorse() && !isHorseMounted() && !isHorseLeashed() && zh.getEM().canAffordCommand(p, command)) {
			Location destination = p.getLocation();
			if (p.isFlying()) {
				Block block = destination.getWorld().getHighestBlockAt(destination);
				destination = new Location(destination.getWorld(), block.getX(), block.getY(), block.getZ());
			}
			if (zh.getCM().shouldUseOldTeleportMethod()) {
				horse.teleport(destination);
				zh.getDM().updateHorseLocation(horse.getUniqueId(), horse.getLocation(), false);
			}
			else {				
				if (horse instanceof ChestedHorse && ((ChestedHorse) horse).isCarryingChest()) {
					s.sendMessage(ChatColor.RED + "The new teleportation method is currently disabled for chested horses.");
					return;
				}
				else {
					horse = zh.getHM().teleport(horse, destination);
				}
			}
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
	}

}