package eu.reborn_minecraft.zhorse.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

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
					String favorite = zh.getUM().getFavoriteUserID(targetUUID);
					String horseListHeader;
					if (samePlayer) {
						horseListHeader = zh.getMM().getMessageValue(s, LocaleEnum.horseListHeader, remainingClaimsMessage, true);
					}
					else {
						horseListHeader = zh.getMM().getMessagePlayerValue(s, LocaleEnum.horseListOtherHeader, targetName, remainingClaimsMessage, true);
					}
					zh.getMM().sendMessageValue(s, LocaleEnum.headerFormat, horseListHeader, true);
					for (int i=1; i<=horseList.size(); i++) {
						String userID = Integer.toString(i);
						String horseName = horseList.get(i-1);						
						String status = "";
						if (zh.getUM().isProtected(targetUUID, userID)) {
							status += zh.getMM().getMessageSpacer(s, LocaleEnum.modeProtected, 1, true);
						}
						if (zh.getUM().isLocked(targetUUID, userID)) {
							status += zh.getMM().getMessageSpacer(s, LocaleEnum.modeLocked, 1, true);
						}
						else if (zh.getUM().isShared(targetUUID, userID)) {
							status += zh.getMM().getMessageSpacer(s, LocaleEnum.modeShared, 1, true);
						}
						if (userID.equals(favorite)) {
							zh.getMM().sendMessageHorseSpacerUserIDValue(s, LocaleEnum.horseListFormatFavorite, horseName, 1, userID, status, true);
						}
						else {
							zh.getMM().sendMessageHorseSpacerUserIDValue(s, LocaleEnum.horseListFormat, horseName, 1, userID, status, true);
						}
					}
				}
				else {
					if (samePlayer) {
						zh.getMM().sendMessageValue(s, LocaleEnum.noHorseOwned, remainingClaimsMessage);
					}
					else {
						zh.getMM().sendMessagePlayerValue(s, LocaleEnum.noHorseOwnedOther, targetName, remainingClaimsMessage);
					}
				}
			}
			zh.getEM().payCommand(p, command);
		}
	}
	
}