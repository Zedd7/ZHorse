package com.gmail.xibalbazedd.zhorse.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.HorseDeathRecord;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.managers.MessageManager;
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
			displayAliveHorseNameList(zh.getDM().getAliveHorseNameList(targetUUID));
			displayDeadHorseNameList(zh.getDM().getHorseDeathRecordList(targetUUID));
			zh.getEM().payCommand(p, command);
		}
	}

	private void displayAliveHorseNameList(List<String> aliveHorseNameList) {
		String remainingClaimsMessage = getRemainingClaimsMessage(targetUUID);
		if (!aliveHorseNameList.isEmpty()) {
			String favorite = zh.getDM().getPlayerFavoriteHorseID(targetUUID).toString();
			String aliveHorseListHeader;
			if (samePlayer) {
				aliveHorseListHeader = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.ALIVE_HORSE_LIST_HEADER) {{ setValue(remainingClaimsMessage); }}, true);
			}
			else {
				aliveHorseListHeader = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.ALIVE_HORSE_LIST_OTHER_HEADER) {{ setPlayerName(targetName); setValue(remainingClaimsMessage); }}, true);
			}
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HEADER_FORMAT) {{ setValue(aliveHorseListHeader); }}, true);
			
			for (int horseIDInt = 1; horseIDInt <= aliveHorseNameList.size(); ++horseIDInt) {
				String horseID = Integer.toString(horseIDInt); // Order verified with "ORDER BY" SQL instruction
				String horseName = aliveHorseNameList.get(horseIDInt - 1);
				UUID horseUUID = zh.getDM().getHorseUUID(targetUUID, horseIDInt);
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
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.ALIVE_HORSE_LIST_FORMAT_FAVORITE) {{ setHorseName(horseName); setHorseID(horseID); setSpaceCount(1); setValue(message); }}, true);
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.ALIVE_HORSE_LIST_FORMAT) {{ setHorseName(horseName); setHorseID(horseID); setSpaceCount(1); setValue(message); }}, true);
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
	}
	
	private void displayDeadHorseNameList(List<HorseDeathRecord> horseDeathRecordList) {
		if (!horseDeathRecordList.isEmpty()) {
			String remainingDeathsMessage = getRemainingDeathsMessage(targetUUID);
			String deadHorseListHeader;
			if (samePlayer) {
				deadHorseListHeader = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.DEAD_HORSE_LIST_HEADER) {{ setValue(remainingDeathsMessage); }}, true);
			}
			else {
				deadHorseListHeader = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.DEAD_HORSE_LIST_OTHER_HEADER) {{ setPlayerName(targetName); setValue(remainingDeathsMessage); }}, true);
			}
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HEADER_FORMAT) {{ setValue(deadHorseListHeader); }}, true);
			
			for (HorseDeathRecord horseDeathRecord : horseDeathRecordList) {
				String horseName = zh.getDM().getHorseName(UUID.fromString(horseDeathRecord.getUUID()));
				String deathDate = MessageManager.DATE_FORMAT_VERBOSE.format(horseDeathRecord.getDate());
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.DEAD_HORSE_LIST_FORMAT) {{ setHorseName(horseName); setSpaceCount(1); setValue(deathDate); }}, true);
			}
		}
	}
	
}