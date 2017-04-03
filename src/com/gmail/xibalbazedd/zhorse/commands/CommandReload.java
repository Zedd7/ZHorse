package com.gmail.xibalbazedd.zhorse.commands;

import org.bukkit.command.CommandSender;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;

public class CommandReload extends AbstractCommand {

	public CommandReload(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer(true)) {
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
					zh.getMM().sendMessageValue(s, LocaleEnum.PLUGIN_RELOADED, zh.getDescription().getFullName());
				}
			}
			else if (displayConsole) {
				zh.getMM().sendMessageValue(s, LocaleEnum.PLUGIN_RELOADED_WITH_ERRORS, zh.getDescription().getFullName());
			}
			if (playerCommand) {
				zh.getEM().payCommand(p, command);
			}
		}
	}

}