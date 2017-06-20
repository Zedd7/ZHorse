package com.gmail.xibalbazedd.zhorse.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.HorseDeathRecord;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.managers.MessageManager;
import com.gmail.xibalbazedd.zhorse.utils.CompoundMessage;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

public class CommandList extends AbstractCommand {

	public CommandList(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled() && parseArgument(ArgumentEnum.PAGE_NUMBER)) {
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
			CompoundMessage compoundMessage = new CompoundMessage(true);
			
			List<String> aliveHorseNameList = zh.getDM().getAliveHorseNameList(targetUUID);
			String remainingClaimsMessage = getRemainingClaimsMessage(targetUUID);
			buildAliveHorseNameList(compoundMessage, aliveHorseNameList, remainingClaimsMessage);
			
			List<HorseDeathRecord> horseDeathRecordList = zh.getDM().getHorseDeathRecordList(targetUUID);
			String remainingDeathsMessage = getRemainingDeathsMessage(targetUUID);
			if (!aliveHorseNameList.isEmpty() && !horseDeathRecordList.isEmpty()) compoundMessage.addPage();
			int deadHorseListStartingPageNumber = compoundMessage.getPageCount();
			buildDeadHorseNameList(compoundMessage, horseDeathRecordList, remainingDeathsMessage);
			
			int maxPageNumber = compoundMessage.getPageCount();
			String pageNumberMessage = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.PAGE_NUMBER_FORMAT) {{ setAmount(pageNumber); setMax(maxPageNumber); }}, true);
			if (!aliveHorseNameList.isEmpty()) buildAliveHorseListHeader(compoundMessage, CompoundMessage.FIRST_PAGE_NUMBER, remainingClaimsMessage, pageNumberMessage);
			if (!horseDeathRecordList.isEmpty()) buildDeadHorseListHeader(compoundMessage, deadHorseListStartingPageNumber, remainingDeathsMessage, pageNumberMessage);
			
			String message = zh.getMM().getMessage(compoundMessage, pageNumber);
			if (!message.isEmpty()) {
				zh.getMM().sendMessage(s, message);
			}
			zh.getEM().payCommand(p, command);
		}
	}

	private void buildAliveHorseListHeader(CompoundMessage compoundMessage, int startingPageNumber, String remainingClaimsMessage, String pageNumberMessage) {
		String aliveHorseListHeader;
		if (samePlayer) {
			aliveHorseListHeader = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.ALIVE_HORSE_LIST_HEADER) {{
				setValue(remainingClaimsMessage); setValue(pageNumberMessage);
			}}, true);
		}
		else {
			aliveHorseListHeader = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.ALIVE_HORSE_LIST_OTHER_HEADER) {{
				setPlayerName(targetName); setValue(remainingClaimsMessage); setValue(pageNumberMessage);
			}}, true);
		}
		String headerMessage = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.HEADER_FORMAT) {{ setValue(aliveHorseListHeader); }}, true);
		compoundMessage.addHeader(headerMessage, startingPageNumber);
	}

	private void buildDeadHorseListHeader(CompoundMessage compoundMessage, int startingPageNumber, String remainingDeathsMessage, String pageNumberMessage) {
		String deadHorseListHeader;
		if (samePlayer) {
			deadHorseListHeader = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.DEAD_HORSE_LIST_HEADER) {{
				setValue(remainingDeathsMessage); setValue(pageNumberMessage);
			}}, true);
		}
		else {
			deadHorseListHeader = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.DEAD_HORSE_LIST_OTHER_HEADER) {{
				setPlayerName(targetName); setValue(remainingDeathsMessage); setValue(pageNumberMessage);
			}}, true);
		}
		String headerMessage = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.HEADER_FORMAT) {{ setValue(deadHorseListHeader); }}, true);
		compoundMessage.addHeader(headerMessage, startingPageNumber);
	}

	private void buildAliveHorseNameList(CompoundMessage compoundMessage, List<String> aliveHorseNameList, String remainingClaimsMessage) {
		if (!aliveHorseNameList.isEmpty()) {
			String favorite = zh.getDM().getPlayerFavoriteHorseID(targetUUID).toString();
			for (int horseIDInt = 1; horseIDInt <= aliveHorseNameList.size(); ++horseIDInt) {
				String horseID = Integer.toString(horseIDInt); // Order assured by DataManager
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
				String statusMessage = status;
				String message;
				if (horseID.equals(favorite)) {
					message = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.ALIVE_HORSE_LIST_FORMAT_FAVORITE) {{
						setHorseName(horseName); setHorseID(horseID); setSpaceCount(1); setValue(statusMessage);
					}}, true);
				}
				else {
					message = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.ALIVE_HORSE_LIST_FORMAT) {{
						setHorseName(horseName); setHorseID(horseID); setSpaceCount(1); setValue(statusMessage);
					}}, true);
				}
				compoundMessage.addLine(message);
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
	
	private void buildDeadHorseNameList(CompoundMessage compoundMessage, List<HorseDeathRecord> horseDeathRecordList, String remainingDeathsMessage) {
		if (!horseDeathRecordList.isEmpty()) {
			for (HorseDeathRecord deathRecord : horseDeathRecordList) {
				String horseName = zh.getDM().getHorseName(UUID.fromString(deathRecord.getUUID()));
				String deathDate = MessageManager.DATE_FORMAT_SHORT.format(deathRecord.getDate());
				String message = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.DEAD_HORSE_LIST_FORMAT) {{ setHorseName(horseName); setSpaceCount(1); setValue(deathDate); }}, true);
				compoundMessage.addLine(message);
			}
		}
	}
	
}