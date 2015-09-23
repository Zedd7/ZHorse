package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZReload extends Command {

	public ZReload(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = false;
		needTarget = false;
		if (isPlayer(!playerOnly)) {
			if (analyseArguments() && hasPermission() && isWorldEnabled()) {
				if (!idMode && !targetMode) {
					executePlayer();
				}
				else {
					sendCommandUsage();
				}
			}
		}
		else {
			if (analyseArguments()) {
				if (!idMode && !targetMode) {
					executeConsole();
				}
				else {
					sendCommandUsage();
				}
			}
		}
	}

	private void executePlayer() { // TODO fusionner avec executeConsole
		if (zh.getEM().canAffordCommand(p, command)) {
			if (zh.reload()) {
				if (displayConsole) {
					zh.getMM().sendMessageValue(s, zh.getLM().pluginReloaded, zh.getDescription().getFullName());
				}
			}
			else if (displayConsole) {
				zh.getMM().sendMessageValue(s, zh.getLM().pluginReloadedWithErrors, zh.getDescription().getFullName());
			}
			zh.getEM().payCommand(p, command);
		}
	}
	
	private void executeConsole() {
		if (zh.reload()) {
			if (displayConsole) {
				zh.getMM().sendMessageValue(s, zh.getLM().pluginReloaded, zh.getDescription().getFullName());
			}
		}
		else if (displayConsole) {
			zh.getMM().sendMessageValue(s, zh.getLM().pluginReloadedWithErrors, zh.getDescription().getFullName());
		}
	}

}