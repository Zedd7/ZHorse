package eu.reborn_minecraft.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZInfo {

	public ZInfo(CommandSender s, String[] a, ZHorse zh) {
		if (s instanceof Player) {
			Player p = (Player) s;
			if (zh.getCM().isWorldEnabled(p.getWorld())) {
				String perm = "zh." + a[0];
				if(zh.getPerms().has(p, perm)) {
					if (p.isInsideVehicle() && p.getVehicle() instanceof Horse) {
						Horse horse = (Horse)p.getVehicle();
						if (zh.getUM().isRegistered(horse)) {
							if (zh.getEM().isReadyToPay(p, a[0])) {
								Damageable d = horse;
								UUID ownerUUID = zh.getUM().getPlayerUUID(horse);
								String ownerName = zh.getUM().getPlayerName(horse);
								String userID = zh.getUM().getUserID(ownerUUID, horse);
								String horseName = zh.getUM().getHorseName(ownerUUID, userID);
								String health = Integer.toString(((Number) d.getHealth()).intValue());
								String maxHealth = Integer.toString(((Number) d.getMaxHealth()).intValue());						
								String message = String.format(zh.getLM().getHeaderMessage(zh.getLM().headerFormat), zh.getLM().getHeaderMessage(zh.getLM().horseInfoHeader));
								if (zh.getUM().isClaimedBy(p.getUniqueId(), horse)) {
									message += "\n " + String.format(zh.getLM().getInformationMessage(zh.getLM().id, true), userID);
								}
								message += "\n " + String.format(zh.getLM().getInformationMessage(zh.getLM().owner, true), ownerName);
								message += "\n " + String.format(zh.getLM().getInformationMessage(zh.getLM().name, true), horseName);
								message += "\n " + String.format(zh.getLM().getInformationMessage(zh.getLM().health, true), health, maxHealth);
								message += "\n " + zh.getLM().getInformationMessage(zh.getLM().status, true);
								boolean normal = true;
								if (zh.getUM().isProtected(ownerUUID, userID)) {
									message += " [Pr]";
									normal = false;
								}
								if (zh.getUM().isLocked(ownerUUID, userID)) {
									message += " [Lo]";
									normal = false;
								}
								if (zh.getUM().isShared(ownerUUID, userID)) {
									message += " [Sh]";
									normal = false;
								}
								if (normal) {
									message += " None";
								}
								p.sendMessage(message);
								zh.getEM().payCommand(p, a[0]);
							}
						}
						else {
							p.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().horseNotClaimed));
						}
					}
					else {
						p.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().notOnHorse));
					}
				}
				else {
					p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().missingPermission), perm));
				}
			}
			else {
				p.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().worldDisabled));
			}
		}
		else {
			s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().playerCommand));
		}
	}

}