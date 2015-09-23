package eu.reborn_minecraft.zhorse.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZList extends Command {

	public ZList(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			applyArgument(false);
			if (!idMode) {
				if (!targetMode || isRegistered(targetUUID)) {
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
			List<String> horseList = zh.getUM().getHorseList(targetUUID);
			String remainingClaimsMessage = getRemainingClaimsMessage(targetUUID);
			if (displayConsole) {
				if (ownsHorse(targetUUID, true)) {
					String horseListHeader;
					if (samePlayer) {
						horseListHeader = zh.getMM().getHeaderAmount(s, zh.getLM().horseListHeader, 0, remainingClaimsMessage, true);
					}
					else {
						horseListHeader = zh.getMM().getHeaderPlayerAmount(s, zh.getLM().horseListOtherHeader, 0, targetName, remainingClaimsMessage, true);
					}
					zh.getMM().sendHeaderValue(s, zh.getLM().headerFormat, horseListHeader, true);
					for (int i=1; i<=horseList.size(); i++) {
						String userID = Integer.toString(i);
						String horseName = horseList.get(i-1);
						String message = zh.getMM().getHeaderHorseUserID(s, zh.getLM().horseListFormat, horseName, userID, true);
						String status = "";
						if (zh.getUM().isProtected(targetUUID, userID)) {
							status += zh.getMM().getInfo(s, zh.getLM().modeProtected, true);
						}
						if (zh.getUM().isLocked(targetUUID, userID)) {
							status += zh.getMM().getInfo(s, zh.getLM().modeLocked, true);
						}
						else if (zh.getUM().isShared(targetUUID, userID)) {
							status += zh.getMM().getInfo(s, zh.getLM().modeShared, true);
						}
						s.sendMessage(message + status);
					}
				}
				else {
					if (samePlayer) {
						zh.getMM().sendMessageAmount(s, zh.getLM().noHorseOwned, remainingClaimsMessage);
					}
					else {
						zh.getMM().sendMessagePlayerAmount(s, zh.getLM().noHorseOwnedOther, targetName, remainingClaimsMessage);
					}
				}
			}
			zh.getEM().payCommand(p, command);
		}
	}
	
}