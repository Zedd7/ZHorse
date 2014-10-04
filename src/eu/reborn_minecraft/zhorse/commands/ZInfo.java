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
								execute();
							}
						}
						else {
							if (idMode) {
								if (zh.getUM().isRegistered(targetUUID, userID)) {
									horse = zh.getUM().getHorse(targetUUID, userID);
									if (horse != null) {
										execute();
									}
									else if (displayConsole) {
										s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseNotFound), zh.getUM().getHorseName(horse)));
									}
								}
								else if (displayConsole) {
									sendUnknownHorseMessage(targetName);
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
		if (isRegistered()) {
			if (zh.getEM().isReadyToPay(p, command)) {
				Damageable d = horse;
				UUID ownerUUID = zh.getUM().getPlayerUUID(horse);
				String ownerName = zh.getUM().getPlayerName(ownerUUID);
				String userID = zh.getUM().getUserID(ownerUUID, horse);
				String horseName = zh.getUM().getHorseName(ownerUUID, userID);
				String health = Integer.toString(((Number) d.getHealth()).intValue());
				String maxHealth = Integer.toString(((Number) d.getMaxHealth()).intValue());						
				s.sendMessage(String.format(zh.getLM().getHeaderMessage(zh.getLM().headerFormat), zh.getLM().getHeaderMessage(zh.getLM().horseInfoHeader)));
				if (isOwner(true)) {
					s.sendMessage(" " + String.format(zh.getLM().getInformationMessage(zh.getLM().id, true), userID));
				}
				s.sendMessage(" " + String.format(zh.getLM().getInformationMessage(zh.getLM().owner, true), ownerName));
				s.sendMessage(" " + String.format(zh.getLM().getInformationMessage(zh.getLM().name, true), horseName));
				s.sendMessage(" " + String.format(zh.getLM().getInformationMessage(zh.getLM().health, true), health, maxHealth));
				String status = "";
				boolean normal = true;
				if (zh.getUM().isProtected(ownerUUID, userID)) {
					status += " " + zh.getLM().getInformationMessage(zh.getLM().modeProtected);
					normal = false;
				}
				if (zh.getUM().isLocked(ownerUUID, userID)) {
					status += " " + zh.getLM().getInformationMessage(zh.getLM().modeLocked);
					normal = false;
				}
				else if (zh.getUM().isShared(ownerUUID, userID)) {
					status += " " + zh.getLM().getInformationMessage(zh.getLM().modeShared);
					normal = false;
				}
				if (normal) {
					status += " " + zh.getLM().getInformationMessage(zh.getLM().modeNone);
				}
				s.sendMessage(" " + String.format(zh.getLM().getInformationMessage(zh.getLM().status), status));
				zh.getEM().payCommand(p, command);
			}
		}
	}

}