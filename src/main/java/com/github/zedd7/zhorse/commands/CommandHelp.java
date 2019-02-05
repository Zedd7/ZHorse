package com.github.zedd7.zhorse.commands;

import org.bukkit.command.CommandSender;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.enums.CommandEnum;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.MessageConfig;

public class CommandHelp extends AbstractCommand {

	public CommandHelp(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if ((!isPlayer(true) && parseArguments()) ||
			(zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled())) {
			if (!idMode) {
				if (!targetMode || (isRegistered(targetUUID) && isPlayerOnline(targetUUID, false))) {
					execute();
				}
			}
			else {
				sendCommandUsage();
			}
		}
	}

	private void execute() {
		if (parsePageNumber(true)) {
			sendCommandDescriptionList();
			zh.getCmdM().updateCommandHistory(s, command);
			if (senderIsPlayer) {
				zh.getEM().payCommand(p, command);
			}
		}
		else {
			String command = args.get(0).toLowerCase();
			if (CommandEnum.getNameList().contains(command)) {
				sendCommandUsage(command, false, true);
				if (command.equalsIgnoreCase(CommandEnum.SPAWN.getName())) {
					sendAbstractHorseVariantList();
					sendHorseStyleList();
					sendHorseColorList();
					sendLlamaColorList();
				}
				zh.getCmdM().updateCommandHistory(s, command);
				zh.getEM().payCommand(p, command);
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_COMMAND) {{ setValue(command); }});
			}
		}
	}

}