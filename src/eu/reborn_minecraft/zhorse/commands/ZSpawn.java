package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class ZSpawn extends Command {

	public ZSpawn(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			if (!(idMode || targetMode)) {
				execute();
			}
			else {
				sendCommandUsage();
			}				
		}
	}

	private void execute() {
		if (zh.getEM().canAffordCommand(p, command)) {
			Location location = p.getLocation();
			Horse horse = (Horse) location.getWorld().spawnEntity(location, EntityType.HORSE);
			customize(horse);
			if (displayConsole) {
				zh.getMM().sendMessage(s, LocaleEnum.horseSpawned);
			}
			zh.getEM().payCommand(p, command);
		}
	}

	private void customize(Horse horse) {
		horse.setTamed(true);
	}

}
