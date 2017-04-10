package com.gmail.xibalbazedd.zhorse.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

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
			if (horseNameList.size() > 0) {
				String favorite = zh.getDM().getPlayerFavoriteHorseID(targetUUID).toString();
				String horseListHeader;
				if (samePlayer) {
					horseListHeader = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.HORSE_LIST_HEADER) {{ setValue(remainingClaimsMessage); }}, true);
				}
				else {
					horseListHeader = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.HORSE_LIST_OTHER_HEADER) {{ setPlayerName(targetName); setValue(remainingClaimsMessage); }}, true);
				}
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HEADER_FORMAT) {{ setValue(horseListHeader); }}, true);
				for (int i = 1; i <= horseNameList.size(); ++i) {
					String horseID = Integer.toString(i); // order verified with "ORDER BY" SQL instruction
					String horseName = horseNameList.get(i - 1);	
					UUID horseUUID = zh.getDM().getHorseUUID(targetUUID, Integer.parseInt(horseID));
					String status = "";					
					if (zh.getDM().isHorseLocked(horseUUID)) {
						status += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.LOCKED) {{ setSpaceCount(1); }}, true);
					}
					else if (zh.getDM().isHorseShared(horseUUID)) {
						status += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.SHARED) {{ setSpaceCount(1); }}, true);
					}
					else {
						status += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.RESTRICTED) {{ setSpaceCount(1); }}, true);
					}
					if (zh.getDM().isHorseProtected(horseUUID)) {
						status += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.PROTECTED) {{ setSpaceCount(1); }}, true);
					}
					final String message = status;
					if (horseID.equals(favorite)) {
						zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_LIST_FORMAT_FAVORITE) {{ setHorseName(horseName); setHorseID(horseID); setSpaceCount(1); setValue(message); }}, true);
					}
					else {
						zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_LIST_FORMAT) {{ setHorseName(horseName); setHorseID(horseID); setSpaceCount(1); setValue(message); }}, true);
					}
				}
			}
			else {
				if (samePlayer) {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.NO_HORSE_OWNED) {{ setValue(remainingClaimsMessage); }});
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.NO_HORSE_OWNED_OTHER) {{ setPlayerName(targetName); setValue(remainingClaimsMessage); }});
				}
			}
			zh.getEM().payCommand(p, command);
		}
	}
	
}