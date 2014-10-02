package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZRename {

	public ZRename(CommandSender s, String[] a, ZHorse zh) {
		if (s instanceof Player) {
			Player p = (Player) s;
			if (zh.getCM().isWorldEnabled(p.getWorld())) {
				String perm = "zh." + a[0];
				if(zh.getPerms().has(p, perm)) {
					if (p.isInsideVehicle() && p.getVehicle() instanceof Horse) {
						Horse horse = (Horse)p.getVehicle();
						if (zh.getUM().isRegistered(horse)) {
							if (zh.getUM().isClaimedBy(p.getUniqueId(), horse) || zh.getPerms().has(p, perm + zh.getLM().bypass)) {
								String horseName = "";
								for (int i=1; i<a.length; i++) {
									horseName += a[i];
									if (i+1 < a.length) {
										horseName += " ";
									}
								}
								ChatColor cc = zh.getCM().getChatColor(zh.getPerms().getPrimaryGroup(p));
								if (horseName.length() == 0) {
									boolean sameName = true;
									while (sameName) {
										horseName = zh.getCM().getRandomName();
										if (!(cc + horseName).equals(horse.getCustomName())) {
											sameName = false;
										}
									}
								}
								if (zh.getEM().isReadyToPay(p, a[0])) {
									if (horseName.length() < 3) {
										p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseNameTooShort), 3)); // utiliser zh.getCM().getMinimumHorseNameLength()
									}
									else if (horseName.length() > 30) {
										p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseNameTooLong), 30)); // utiliser zh.getCM().getMaximumHorseNameLength()
									}
									else {
										horse.setCustomName(cc + horseName);
										zh.getUM().rename(zh.getUM().getPlayerUUID(horse), horseName, horse);
										p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseRenamed), horseName));
										zh.getEM().payCommand(p, a[0]);
									}
								}
							}
							else {
								String ownerName = zh.getUM().getPlayerName(horse);
								p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseBelongsTo), ownerName));
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
