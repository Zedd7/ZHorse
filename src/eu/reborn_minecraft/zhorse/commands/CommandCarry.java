package eu.reborn_minecraft.zhorse.commands;

import eu.reborn_minecraft.zhorse.ZHorse;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public class CommandCarry extends AbstractCommand {

	public CommandCarry(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled() && applyArgument(true)) {
			if (!idMode) {
				if (!targetMode) {
					boolean ownsHorse = ownsHorse(targetUUID, true);
					if (isOnHorse(ownsHorse)) {
						horse = (Horse) p.getVehicle();
						if (isRegistered(horse)) {
							execute();
						}
					}
					else if (ownsHorse) {
						horseID = zh.getDM().getPlayerFavoriteHorseID(p.getUniqueId()).toString();
						if (isRegistered(p.getUniqueId(), horseID)) {
							horse = zh.getHM().getFavoriteHorse(p.getUniqueId());
							if (isHorseLoaded(true)) {
								execute();
							}
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
		if (isOwner() && zh.getEM().canAffordCommand(p, command)) {
			Player player = (Player) s;
			if(player.getPassenger() == null && horse.getPassenger() == null) {
				player.setPassenger(horse);
				zh.getHM().playOutMountPacket(player);
			}
		}
	}
}