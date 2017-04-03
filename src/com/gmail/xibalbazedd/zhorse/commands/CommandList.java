package com.gmail.xibalbazedd.zhorse.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;

public class CommandList extends AbstractCommand {

	public CommandList(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled() && applyArgument(false)) {
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
			List<String> horseNameList = zh.getDM().getHorseNameList(targetUUID);
			String remainingClaimsMessage = getRemainingClaimsMessage(targetUUID);
			if (displayConsole) {
				if (horseNameList.size() > 0) {
					String favorite = zh.getDM().getPlayerFavoriteHorseID(targetUUID).toString();
					String horseListHeader;
					if (samePlayer) {
						horseListHeader = zh.getMM().getMessageValue(s, LocaleEnum.HORSE_LIST_HEADER, remainingClaimsMessage, true);
					}
					else {
						horseListHeader = zh.getMM().getMessagePlayerValue(s, LocaleEnum.HORSE_LIST_OTHER_HEADER, targetName, remainingClaimsMessage, true);
					}
					zh.getMM().sendMessageValue(s, LocaleEnum.HEADER_FORMAT, horseListHeader, true);
					for (int i = 1; i <= horseNameList.size(); ++i) {
						String horseID = Integer.toString(i); // order verified with "ORDER BY" SQL instruction
						String horseName = horseNameList.get(i - 1);	
						UUID horseUUID = zh.getDM().getHorseUUID(targetUUID, Integer.parseInt(horseID));
						String status = "";
						if (zh.getDM().isHorseProtected(horseUUID)) {
							status += zh.getMM().getMessageSpacer(s, LocaleEnum.PROTECTED, 1, true);
						}
						if (zh.getDM().isHorseLocked(horseUUID)) {
							status += zh.getMM().getMessageSpacer(s, LocaleEnum.LOCKED, 1, true);
						}
						else if (zh.getDM().isHorseShared(horseUUID)) {
							status += zh.getMM().getMessageSpacer(s, LocaleEnum.SHARED, 1, true);
						}
						if (horseID.equals(favorite)) {
							zh.getMM().sendMessageHorseHorseIDSpacerValue(s, LocaleEnum.HORSE_LIST_FORMAT_FAVORITE, horseName, horseID, 1, status, true);
						}
						else {
							zh.getMM().sendMessageHorseHorseIDSpacerValue(s, LocaleEnum.HORSE_LIST_FORMAT, horseName, horseID, 1, status, true);
						}
					}
				}
				else {
					if (samePlayer) {
						zh.getMM().sendMessageValue(s, LocaleEnum.NO_HORSE_OWNED, remainingClaimsMessage);
					}
					else {
						zh.getMM().sendMessagePlayerValue(s, LocaleEnum.NO_HORSE_OWNED_OTHER, targetName, remainingClaimsMessage);
					}
				}
			}
			zh.getEM().payCommand(p, command);
		}
	}
	
}