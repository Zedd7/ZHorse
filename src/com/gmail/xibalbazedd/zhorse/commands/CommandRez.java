package com.gmail.xibalbazedd.zhorse.commands;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.HorseInventoryRecord;
import com.gmail.xibalbazedd.zhorse.database.HorseStatsRecord;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

public class CommandRez extends AbstractCommand {

	public CommandRez(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled()) {
			if (!idMode) {
				if (!targetMode || isRegistered(targetUUID)) {
					execute();
				}
			}
			else {
				sendCommandUsage();
			}
		}
	}
	
	private void execute() {
		if (ownsDeadHorse(targetUUID) && !hasReachedClaimsLimit(true)) {
			UUID horseUUID = zh.getDM().getNewestHorseDeathUUID(targetUUID);
			int horseID = zh.getDM().getNextHorseID(targetUUID);
			boolean success = true;
			HorseInventoryRecord inventoryRecord = zh.getDM().getHorseInventoryRecord(horseUUID);
			HorseStatsRecord statsRecord = zh.getDM().getHorseStatsRecord(horseUUID);
			success &= zh.getDM().removeHorseDeath(horseUUID);
			success &= zh.getDM().updateHorseID(horseUUID, horseID);
			success &= craftHorseName(true, horseUUID);
			if (success) {
				Location destination = p.getLocation();
				if (p.isFlying()) {
					Block block = destination.getWorld().getHighestBlockAt(destination);
					destination = new Location(destination.getWorld(), block.getX(), block.getY(), block.getZ());
				}
				horse = zh.getHM().spawnHorse(destination, inventoryRecord, statsRecord, horseUUID, true);
				if (horse != null) {
					applyHorseName(targetUUID);
					horse.setHealth(horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_RESURRECTED) {{ setHorseName(horseName); }});
					zh.getCmdM().updateCommandHistory(s, command);
					zh.getEM().payCommand(p, command);
				}
			}
		}
	}

}