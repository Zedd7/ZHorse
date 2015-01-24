package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZHelp extends Command {

	public ZHelp(ZHorse zh, CommandSender s, String[] a) {
		super(zh, a, s);
		idAllow = false;
		targetAllow = false;
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (!idMode) {
							if (!targetMode || isPlayerOnline(targetUUID, false)) {
								execute();
							}
						}
						else if (displayConsole) {
							sendCommandUsage(true);
						}
					}
				}
			}
		}
	}

	private void execute() {
		if (zh.getEM().isReadyToPay(p, command)) {
			if (a.length == 0) {
				displayCommandList();
				zh.getEM().payCommand(p, command);
			}
			else {
				boolean sendErrorMessage = true;
				if (a.length == 1) {
					command = a[0];
					sendErrorMessage = false;
				}
				if (zh.getCmdM().getCommandList().contains(command)) {
					if (displayConsole) {
						sendCommandUsage(sendErrorMessage);
					}
					zh.getEM().payCommand(p, command);
				}
				else if (displayConsole) {
					s.sendMessage(zh.getLM().getCommandAnswer(language, zh.getLM().unknownCommand));
				}
			}
		}
	}

	private void displayCommandList() {
		if (displayConsole) {
			s.sendMessage(String.format(zh.getLM().getHeaderMessage(language, zh.getLM().headerFormat), zh.getLM().getHeaderMessage(language, zh.getLM().commandListHeader)));
			for (String command : zh.getCmdM().getCommandList()) {
				displayConsole = false;
				if (hasPermission(targetUUID, command, true)) {
					String message = " " + zh.getLM().getCommandDescription(language, command);
					String cost = "";
					if (!zh.getEM().isCommandFree(targetUUID, command)) {
						cost = " " + String.format(zh.getLM().getEconomyAnswer(language, zh.getLM().commandCost, true), zh.getCM().getCommandCost(command));
					}
					s.sendMessage(message + cost);
				}
			}
		}
	}
}