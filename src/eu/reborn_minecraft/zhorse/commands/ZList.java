package eu.reborn_minecraft.zhorse.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZList extends Command {

	public ZList(ZHorse zh, CommandSender s, String[] a) {
		super(zh, a, s);
		idAllow = false;
		targetAllow = false;
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (!idMode) {
							execute();
						}
						else {
							sendCommandUsage(true);
						}
					}
				}
			}
		}
	}

	private void execute() {
		if (zh.getEM().isReadyToPay(p, command)) {
			List<String> horseList = zh.getUM().getHorseList(targetUUID);
			String remainingClaimsMessage = getRemainingClaimsMessage(horseList);
			if (!horseList.isEmpty()) {
				String horseListHeader = "";
				if (samePlayer) {
					horseListHeader = zh.getMM().getHeaderAmount(language, zh.getLM().horseListHeader, remainingClaimsMessage, true);
				}
				else {
					horseListHeader = zh.getMM().getHeaderPlayerAmount(language, zh.getLM().horseListOtherHeader, targetName, remainingClaimsMessage, true);
				}
				if (displayConsole) {
					s.sendMessage(zh.getMM().getHeaderValue(language, zh.getLM().headerFormat, horseListHeader, true));
					for (int i=1; i<=horseList.size(); i++) {
						String userID = Integer.toString(i);
						String horseName = horseList.get(i-1);
						String message = zh.getMM().getHeaderHorseUserID(language, zh.getLM().horseListFormat, " ", horseName, userID, true);
						String status = "";
						if (zh.getUM().isProtected(targetUUID, userID)) {
							status += zh.getMM().getInfo(language, zh.getLM().modeProtected, " ", true);
						}
						if (zh.getUM().isLocked(targetUUID, userID)) {
							status += zh.getMM().getInfo(language, zh.getLM().modeLocked, " ", true);
						}
						else if (zh.getUM().isShared(targetUUID, userID)) {
							status += zh.getMM().getInfo(language, zh.getLM().modeShared, " ", true);
						}
						s.sendMessage(message + status);
					}
				}
			}
			else if (displayConsole) {
				if (samePlayer) {
					s.sendMessage(zh.getMM().getMessageAmount(language, zh.getLM().noHorseOwned, remainingClaimsMessage));
				}
				else {
					s.sendMessage(zh.getMM().getMessagePlayerAmount(language, zh.getLM().noHorseOwnedOther, targetName, remainingClaimsMessage));
				}
			}
			zh.getEM().payCommand(p, command);
		}
	}
	
	private String getRemainingClaimsMessage(List<String> horseList) {
		String message = "";
		if (samePlayer || isPlayerOnline(targetUUID, true)) {
			int maxClaims = zh.getCM().getMaximumClaims(zh.getServer().getPlayer(targetUUID));
			message = zh.getMM().getHeaderAmountMax(language, zh.getLM().remainingClaimsFormat, Integer.toString(horseList.size()), Integer.toString(maxClaims), true);
		}
		return message;
	}
	
}