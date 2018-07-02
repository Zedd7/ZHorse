package com.github.zedd7.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.database.HorseStatsRecord;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.MessageConfig;

public class CommandEdit extends AttributeParsingCommand {

	public CommandEdit(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled()) {
			if (!idMode) {
				if (!targetMode) {
					boolean ownsHorse = ownsHorse(targetUUID, true);
					if (isOnHorse(ownsHorse)) {
						horse = (AbstractHorse) p.getVehicle();
						if (isRegistered(horse)) {
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
		if (isOwner(false)) {
			if (parseEditArguments()) {
				editHorse();
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_EDITED) {{ setHorseName(horseName); }});
				zh.getCmdM().updateCommandHistory(s, command);
				zh.getEM().payCommand(p, command);
			}
			else {
				sendCommandUsage();
			}
		}
	}

	private boolean parseEditArguments() {
		boolean valid = true;
		if (!args.isEmpty()) {
			for (String argument : args) { // Check for each token if it is some type of attribute
				boolean parsed = false;
				if (!parsed && tamed == null) parsed = parseTamed(argument);
				if (!parsed && adult == null) parsed = parseAdult(argument);
				if (!parsed && baby == null) parsed = parseBaby(argument);
				if (!parsed && health == null && speed == null && jumpStrength == null) parsed = parseStats(argument);
				if (!parsed) {
					valid = false;
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.INVALID_EDIT_ARGUMENT) {{ setValue(argument); }});
				}
			}
		}
		else {
			valid = false;
		}
		return valid;
	}

	private void editHorse() {
		HorseStatsRecord statsRecord = new HorseStatsRecord(
			null, null, null, null, null, null, null, null, health, null, null, null, tamed, jumpStrength, health, null, null, speed, null, null, null, null
		) {{ setAdult(adult); setBaby(baby); }} ;

		zh.getHM().assignStats(horse, statsRecord, null);
	}

}