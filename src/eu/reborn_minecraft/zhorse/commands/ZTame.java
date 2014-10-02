package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZTame {

	public ZTame(CommandSender s, String[] a, ZHorse zh) {
		if (s instanceof Player) {
			Player p = (Player) s;
			if (zh.getCM().isWorldEnabled(p.getWorld())) {
				String perm = "zh." + a[0];
				if(zh.getPerms().has(p, perm)) {
					if (p.isInsideVehicle() && p.getVehicle() instanceof Horse) {
						Horse horse = (Horse)p.getVehicle();
						if (zh.getEM().isReadyToPay(p, a[0])) {
							if (execute(zh, p, horse)) {
								zh.getEM().payCommand(p, a[0]);
							}
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
	
	private boolean execute(ZHorse zh, Player p, Horse horse) {
		boolean tamed = horse.isTamed();
		if (!tamed) {
			horse.setTamed(!tamed);
			p.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().horseTamed));
		}
		else {
			horse.setTamed(!tamed);
			p.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().horseUnTamed));
		}
		return true;
	}

}
