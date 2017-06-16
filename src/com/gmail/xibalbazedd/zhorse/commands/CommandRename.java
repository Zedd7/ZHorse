package com.gmail.xibalbazedd.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

public class CommandRename extends AbstractCommand {

	public CommandRename(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
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
		if (isOwner() && craftHorseName(false) && zh.getEM().canAffordCommand(p, command)) {
			UUID ownerUUID = zh.getDM().getOwnerUUID(horse.getUniqueId());
			applyHorseName(ownerUUID);
			if (zh.getDM().isHorseForSale(horse.getUniqueId())) {
				int price = zh.getDM().getSalePrice(horse.getUniqueId());
				applyHorsePrice(price);
			}
			horse.setCustomNameVisible(true);
			zh.getDM().updateHorseName(horse.getUniqueId(), horseName);
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_RENAMED) {{ setHorseName(horseName); }});
			zh.getEM().payCommand(p, command);
		}
	}
}