package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZReload extends Command {
	private static boolean playerOnly = false;

	public ZReload(ZHorse zh, CommandSender s, String[] a) {
		super(zh, a, s);
		idAllow = false;
		targetAllow = false;
		if (isPlayer(playerOnly)) {
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

	private void executePlayer() {
		if (zh.getEM().isReadyToPay(p, command)) {
			zh.reload();
			if (displayConsole) {
				s.sendMessage(zh.getMM().getMessageValue(language, zh.getLM().pluginReloaded, zh.getDescription().getFullName()));
			}
			zh.getEM().payCommand(p, command);
		}
	}
	
	private void executeConsole() {
		zh.reload();
		if (displayConsole) {
			s.sendMessage(zh.getMM().getMessageValue(language, zh.getLM().pluginReloaded, zh.getDescription().getFullName()));
		}
	}

}