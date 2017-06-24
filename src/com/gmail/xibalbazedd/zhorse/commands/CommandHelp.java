package com.gmail.xibalbazedd.zhorse.commands;

import org.bukkit.command.CommandSender;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.enums.CommandEnum;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

public class CommandHelp extends AbstractCommand {

	public CommandHelp(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
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
		if (zh.getEM().canAffordCommand(p, command)) {
			if (parsePageNumber(true)) {
				sendCommandDescriptionList();
				zh.getCmdM().updateCommandHistory(s, command);
				zh.getEM().payCommand(p, command);
			}
			else {
				String command = args.get(0).toLowerCase();
				if (CommandEnum.getCommandNameList().contains(command)) {
					sendCommandUsage(command, false, true);
					if (command.equals(CommandEnum.SPAWN.getName())) {
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

}