package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZHelp extends Command {

	public ZHelp(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
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
		if (zh.getEM().canAffordCommand(p, command)) {
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
					s.sendMessage(zh.getMM().getMessage(language, zh.getLM().unknownCommand));
				}
			}
		}
	}

	private void displayCommandList() {
		if (displayConsole) {
			s.sendMessage(zh.getMM().getHeaderContent(language, zh.getLM().headerFormat, zh.getLM().commandListHeader, true));
			for (String command : zh.getCmdM().getCommandList()) {
				if (hasPermission(targetUUID, command, true, true)) {
					if (zh.getEM().isCommandFree(targetUUID, command)) {
						s.sendMessage(zh.getMM().getCommandDescription(language, " ", command, true));
					}
					else {
						String cost = Integer.toString(zh.getCM().getCommandCost(command));
						s.sendMessage(zh.getMM().getCommandDescriptionCostValue(language, " ", command, zh.getLM().commandCost, cost, zh.getLM().getEconomyAnswer(language, zh.getLM().currencySymbol, true), true));
					}
				}
			}
		}
	}
}