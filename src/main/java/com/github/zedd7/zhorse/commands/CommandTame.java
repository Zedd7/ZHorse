package com.github.zedd7.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.MessageConfig;

public class CommandTame extends AbstractCommand {

	public CommandTame(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled()
				&& parseArgument(ArgumentEnum.HORSE_NAME, ArgumentEnum.PLAYER_NAME)) {
			if (!idMode) {
				if (!targetMode) {
					boolean ownsHorse = ownsHorse(targetUUID, true);
					if (isOnHorse(ownsHorse)) {
						horse = (AbstractHorse) p.getVehicle();
						if (adminMode || isRegistered(horse)) {
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
		if (isOwner()) {
			boolean tamed = horse.isTamed();
			if (!tamed) {
				horse.setTamed(true);
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_TAMED));
			}
			else {
				horse.setTamed(false);
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_UNTAMED));
			}
			zh.getCmdM().updateCommandHistory(s, command);
			zh.getEM().payCommand(p, command);
		}
	}
		

}