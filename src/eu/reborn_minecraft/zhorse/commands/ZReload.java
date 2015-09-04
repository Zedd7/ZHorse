package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZReload extends Command {
	private static boolean playerOnly = false;

	public ZReload(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		idAllow = false;
		targetAllow = false;
		if (isPlayer(!playerOnly)) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (!(idMode || targetMode)) {
							executePlayer();
						}
						else if (displayConsole) {
							sendCommandUsage(true);
						}
					}
				}
			}
		}
		else {
			if (analyseArguments()) {
				if (!(idMode || targetMode)) {
					executeConsole();
				}
				else if (displayConsole) {
					sendCommandUsage(true);
				}
			}
		}
	}

	private void executePlayer() { // fusionner avec executeConsole
		if (zh.getEM().canAffordCommand(p, command)) {
			if (zh.reload()) {
				if (displayConsole) {
					s.sendMessage(zh.getMM().getMessageValue(language, zh.getLM().pluginReloaded, zh.getDescription().getFullName()));
				}
			}
			else {
				if (displayConsole) {
					s.sendMessage(zh.getMM().getMessageValue(language, zh.getLM().pluginReloadedWithErrors, zh.getDescription().getFullName()));
				}
			}
			zh.getEM().payCommand(p, command);
		}
	}
	
	private void executeConsole() {
		if (zh.reload()) {
			if (displayConsole) {
				s.sendMessage(zh.getMM().getMessageValue(language, zh.getLM().pluginReloaded, zh.getDescription().getFullName()));
			}
		}
		else {
			if (displayConsole) {
				s.sendMessage(zh.getMM().getMessageValue(language, zh.getLM().pluginReloadedWithErrors, zh.getDescription().getFullName()));
			}
		}
	}

}