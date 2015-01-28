package eu.reborn_minecraft.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZInfo extends Command {

	public ZInfo(ZHorse zh, CommandSender s, String[] a) {
		super(zh, a, s);
		idAllow = true;
		targetAllow = false;
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (!(idMode || targetMode)) {
							if (isOnHorse()) {
								horse = (Horse)p.getVehicle();
								if (isRegistered()) {
									execute();
								}
							}
						}
						else {
							if (idMode) {
								if (isRegistered(targetUUID, userID)) {
									horse = zh.getUM().getHorse(targetUUID, userID);
									if (isHorseLoaded()) {
										execute();
									}
								}
							}
							else if (displayConsole){
								sendCommandUsage();
							}
						}
					}
				}
			}
		}
	}

	private void execute() {
		if (zh.getEM().isReadyToPay(p, command)) {
			Damageable d = horse;
			UUID ownerUUID = zh.getUM().getPlayerUUID(horse);
			String ownerName = zh.getUM().getPlayerName(ownerUUID);
			String userID = zh.getUM().getUserID(ownerUUID, horse);
			String horseName = zh.getUM().getHorseName(ownerUUID, userID);
			String health = Integer.toString(((Number) d.getHealth()).intValue());
			String maxHealth = Integer.toString(((Number) d.getMaxHealth()).intValue());						
			s.sendMessage(zh.getMM().getHeaderContent(language, zh.getLM().headerFormat, zh.getLM().horseInfoHeader, true));
			if (isOwner(true)) {
				s.sendMessage(zh.getMM().getInfoUserID(language, zh.getLM().id, " ", userID, true));
			}
			s.sendMessage(zh.getMM().getInfoPlayer(language, zh.getLM().owner, " ", ownerName, true));
			s.sendMessage(zh.getMM().getInfoHorse(language, zh.getLM().name, " ", horseName, true));
			s.sendMessage(zh.getMM().getInfoAmountMax(language, zh.getLM().health, " ", health, maxHealth, true));
			String status = "";
			boolean normal = true;
			if (zh.getUM().isProtected(ownerUUID, userID)) {
				status += zh.getMM().getInfo(language, zh.getLM().modeProtected, " ", true);
				normal = false;
			}
			if (zh.getUM().isLocked(ownerUUID, userID)) {
				status += zh.getMM().getInfo(language, zh.getLM().modeLocked, " ", true);
				normal = false;
			}
			else if (zh.getUM().isShared(ownerUUID, userID)) {
				status += zh.getMM().getInfo(language, zh.getLM().modeShared, " ", true);
				normal = false;
			}
			if (normal) {
				status += zh.getMM().getInfo(language, zh.getLM().modeNone, " ", true);
			}
			s.sendMessage(zh.getMM().getInfoValue(language, zh.getLM().status, " ", status, true));
			zh.getEM().payCommand(p, command);
		}
	}

}