package com.github.xibalba.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;

import com.github.xibalba.zhorse.ZHorse;
import com.github.xibalba.zhorse.enums.LocaleEnum;
import com.github.xibalba.zhorse.utils.MessageConfig;

public class CommandTp extends AbstractCommand {

	public CommandTp(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled()
				&& parseArgument(ArgumentEnum.HORSE_NAME, ArgumentEnum.PLAYER_NAME)) {
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
		if (isOwner(true) && isWorldCrossable(p.getWorld()) && isWorldCrossable(horse.getWorld()) && isNotOnHorse() && isHorseInRangeTp()) {
			p.teleport(horse);
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.TELEPORTED_TO_HORSE) {{ setHorseName(horseName); }});
			zh.getCmdM().updateCommandHistory(s, command);
			zh.getEM().payCommand(p, command);
		}
	}
}