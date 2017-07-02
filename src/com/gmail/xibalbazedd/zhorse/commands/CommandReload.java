package com.gmail.xibalbazedd.zhorse.commands;

import org.bukkit.command.CommandSender;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

public class CommandReload extends AbstractCommand {

	public CommandReload(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (!isPlayer(true) || (zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled())) {
			if (!idMode && !targetMode) {
				execute();
			}
			else {
				sendCommandUsage();
			}
		}
	}

	private void execute() {
		if (zh.reload()) {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.PLUGIN_RELOADED) {{ setValue(zh.getDescription().getFullName()); }});
		}
		else {
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.PLUGIN_RELOADED_WITH_ERRORS) {{ setValue(zh.getDescription().getFullName()); }});
		}
		zh.getCmdM().updateCommandHistory(s, command);
		if (playerCommand) {
			zh.getEM().payCommand(p, command);
		}
	}

}