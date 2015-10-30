package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class ZReload extends Command {

	public ZReload(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = false;
		needTarget = false;
		if (isPlayer(!playerOnly)) {
			if (analyseArguments() && hasPermission() && isWorldEnabled()) {
				if (!idMode && !targetMode) {
					execute();
				}
				else {
					sendCommandUsage();
				}
			}
		}
		else {
			if (analyseArguments()) {
				if (!idMode && !targetMode) {
					execute();
				}
				else {
					sendCommandUsage();
				}
			}
		}
	}

	private void execute() {
		if (!playerCommand || (playerCommand && zh.getEM().canAffordCommand(p, command))) {
			if (zh.reload()) {
				if (displayConsole) {
					zh.getMM().sendMessageValue(s, LocaleEnum.pluginReloaded, zh.getDescription().getFullName());
				}
			}
			else if (displayConsole) {
				zh.getMM().sendMessageValue(s, LocaleEnum.pluginReloadedWithErrors, zh.getDescription().getFullName());
			}
			if (playerCommand) {
				zh.getEM().payCommand(p, command);
			}
		}
	}

}