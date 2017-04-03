package com.gmail.xibalbazedd.zhorse.commands;

import org.bukkit.command.CommandSender;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.enums.CommandEnum;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;

public class CommandHelp extends AbstractCommand {

	public CommandHelp(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			if (!idMode) { // On 2 lines to avoid calling sendCommandUsage if horse is lost
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
			if (argument.isEmpty()) {
				sendCommandDescriptionList();
				zh.getEM().payCommand(p, command);
			}
			else {
				String command = argument.toLowerCase();
				if (CommandEnum.getCommandNameList().contains(command)) {
					sendCommandUsage(command, false, true);
					if (command.equals(CommandEnum.SPAWN.getName())) {
						sendAbstractHorseVariantList();
						sendHorseStyleList();
						sendHorseColorList();
						sendLlamaColorList();
					}
					zh.getEM().payCommand(p, command);
				}
				else if (displayConsole) {
					zh.getMM().sendMessageValue(s, LocaleEnum.UNKNOWN_COMMAND, command);
				}
			}
		}
	}

}