package eu.reborn_minecraft.zhorse.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZHelp {

	public ZHelp(CommandSender s, Command c, String[] a, ZHorse zh, List<String> cmds) {
		String message = String.format(zh.getLM().getHeaderMessage(zh.getLM().headerFormat), zh.getLM().getHeaderMessage(zh.getLM().commandListHeader));
		if (s instanceof Player) {
			Player p = (Player)s;
			String perm = "zh." + zh.getLM().help;
			if(zh.getPerms().has(p, perm)) {
				if (zh.getEM().isReadyToPay(p, zh.getLM().help)) {
					for (String cmd : cmds) {
						if (zh.getPerms().has(p, "zh." + cmd)) {
							message += "\n " + zh.getLM().getCommandDescription(cmd);
							if (!zh.getEM().isCommandFree(p, cmd)) {
								message += String.format(zh.getLM().getEconomyAnswer(zh.getLM().commandCost, true), zh.getCM().getCommandCost(cmd));
							}
						}
					}
					p.sendMessage(message);
					zh.getEM().payCommand(p, zh.getLM().help);
				}
			}
			else {
				p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().missingPermission), perm));
			}
		}
		else {
			s.sendMessage(message);
			for (String cmd : cmds) {
				s.sendMessage("" + zh.getLM().getCommandDescription(cmd));
			}
		}
	}
}
