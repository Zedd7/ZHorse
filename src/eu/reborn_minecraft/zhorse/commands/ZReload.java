package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZReload {

	public ZReload(CommandSender s, String[] a, ZHorse zh) {
		String perm = "zh." + a[0];
		if(zh.getPerms().has(s, perm)) {
			zh.reload();
			s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().pluginReloaded), zh.getDescription().getName()));		
		}
		else {
			s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().missingPermission), perm));
		}
	}

}
