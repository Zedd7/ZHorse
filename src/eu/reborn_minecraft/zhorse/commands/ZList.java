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
				String horseListHeader;
				if (samePlayer) {
					horseListHeader = zh.getLM().getHeaderMessage(zh.getLM().horseListHeader);
				}
				else {
					horseListHeader = String.format(zh.getLM().getHeaderMessage(zh.getLM().horseListOtherHeader), targetName);
				}
				if (displayConsole) {
					s.sendMessage(String.format(zh.getLM().getHeaderMessage(zh.getLM().headerFormat), horseListHeader + remainingClaimsMessage));
					for (int i=1; i<=horseList.size(); i++) {
						String userID = Integer.toString(i);
						String horseName = horseList.get(i-1);
						String message = " " + String.format(zh.getLM().getCommandAnswer(zh.getLM().horseListFormat, true), userID, horseName);
						String status = "";
						if (zh.getUM().isProtected(targetUUID, userID)) {
							status += " " + zh.getLM().getInformationMessage(zh.getLM().modeProtected);
						}
						if (zh.getUM().isLocked(targetUUID, userID)) {
							status += " " + zh.getLM().getInformationMessage(zh.getLM().modeLocked);
						}
						else if (zh.getUM().isShared(targetUUID, userID)) {
							status += " " + zh.getLM().getInformationMessage(zh.getLM().modeShared);
						}
						s.sendMessage(message + status);
					}
				}
			}
			else if (displayConsole) {
				if (samePlayer) {
					s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().noHorseOwned) + remainingClaimsMessage);
				}
				else {
					s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().noHorseOwnedOther), targetName) + remainingClaimsMessage);
				}
			}
			zh.getEM().payCommand(p, command);
		}
	}
	
	private String getRemainingClaimsMessage(List<String> horseList) {
		String message = "";
		if (samePlayer || isPlayerOnline(targetUUID, true)) {
			int maxClaims = zh.getCM().getMaximumClaims(zh.getServer().getPlayer(targetUUID));
			message = " " + String.format(zh.getLM().getCommandAnswer(zh.getLM().remainingClaims, true), horseList.size(), maxClaims);
		}
		return message;
	}
	
}