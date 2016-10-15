package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.CommandEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class CommandHelp extends AbstractCommand {

	public CommandHelp(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			if (!idMode) { // on 2 lines to avoid calling sendCommandUsage if horse is lost
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
						sendHorseVariantList();
						sendHorseStyleList();
						sendHorseColorList();
					}
					zh.getEM().payCommand(p, command);
				}
				else if (displayConsole) {
					zh.getMM().sendMessageValue(s, LocaleEnum.unknownCommand, command);
				}
			}
		}
	}

}