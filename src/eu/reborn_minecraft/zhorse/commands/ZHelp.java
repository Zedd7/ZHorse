package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class ZHelp extends Command {

	public ZHelp(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			if (!idMode) { // sur deux lignes pour ne pas apeller sendCommandUsage si cible introuvable
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
				displayCommandList(zh.getCmdM().getCommandList(), zh.getMM().getMessage(s, LocaleEnum.commandListHeader, true));
				zh.getEM().payCommand(p, command);
			}
			else {
				String subCommand = argument.toLowerCase();
				if (zh.getCmdM().getCommandList().contains(subCommand)) {
					sendCommandUsage(subCommand, true);
					zh.getEM().payCommand(p, command);
				}
				else if (displayConsole) {
					zh.getMM().sendMessageValue(s, zh.getLM().unknownCommand, subCommand);
				}
			}
		}
	}
}