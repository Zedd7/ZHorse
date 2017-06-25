package com.gmail.xibalbazedd.zhorse.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.HorseDeathRecord;
import com.gmail.xibalbazedd.zhorse.enums.HorseVariantEnum;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.managers.MessageManager;
import com.gmail.xibalbazedd.zhorse.utils.CompoundMessage;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

public class CommandList extends AbstractCommand {
	
	private int livingHorseListStartingPageNumber = CompoundMessage.FIRST_PAGE_NUMBER;
	private int deadHorseListStartingPageNumber = CompoundMessage.FIRST_PAGE_NUMBER;
	private boolean livingHorseListed = false;
	private boolean deadHorseListed = false;
	private boolean deadHorsesPageCreated = false;

	public CommandList(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && parseArguments() && hasPermission() && isWorldEnabled() && parseArgument(ArgumentEnum.PAGE_NUMBER)) {
			if (!idMode) {
				if ((!targetMode || isRegistered(targetUUID)) && (!variantMode || isRegistered(horseVariant))) {
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
			buildDeadHorseNameList(compoundMessage, horseDeathRecordList, remainingDeathsMessage);
			
			int maxPageNumber = compoundMessage.getPageCount();
			String pageNumberMessage = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.PAGE_NUMBER_FORMAT) {{ setAmount(pageNumber); setMax(maxPageNumber); }}, true);
			buildAliveHorseListHeader(compoundMessage, livingHorseListStartingPageNumber, remainingClaimsMessage, pageNumberMessage);
			buildDeadHorseListHeader(compoundMessage, deadHorseListStartingPageNumber, remainingDeathsMessage, pageNumberMessage);
			
			String message = zh.getMM().getMessage(compoundMessage, pageNumber);
			if (!message.isEmpty()) {
				zh.getMM().sendMessage(s, message);
			}
			zh.getEM().payCommand(p, command);
		}
	}

	private void buildAliveHorseNameList(CompoundMessage compoundMessage, List<String> aliveHorseNameList, String remainingClaimsMessage) {
		if (!aliveHorseNameList.isEmpty()) {
			String favorite = zh.getDM().getPlayerFavoriteHorseID(targetUUID).toString();
			for (int horseIDInt = 1; horseIDInt <= aliveHorseNameList.size(); ++horseIDInt) {
				UUID horseUUID = zh.getDM().getHorseUUID(targetUUID, horseIDInt);
				if (!variantMode || zh.getDM().isHorseOfType(horseUUID, variant)) {
					livingHorseListed = true;
					String horseID = Integer.toString(horseIDInt); // Order assured by DataManager
					String horseName = aliveHorseNameList.get(horseIDInt - 1);
					String variantMessage = buildVariantMessage(horseUUID);
					String statusMessage = buildStatusMessage(horseUUID);
					String message;
					if (horseID.equals(favorite)) {
						message = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.ALIVE_HORSE_LIST_FORMAT_FAVORITE) {{
							setHorseName(horseName); setHorseID(horseID); setSpaceCount(1); setValue(variantMessage); setValue(statusMessage);
						}}, true);
					}
					else {
						message = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.ALIVE_HORSE_LIST_FORMAT) {{
							setHorseName(horseName); setHorseID(horseID); setSpaceCount(1); setValue(variantMessage); setValue(statusMessage);
						}}, true);
					}
					compoundMessage.addLine(message);
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
	
	private String buildVariantMessage(UUID horseUUID) {
		String variantMessage = "";
		EntityType horseType = EntityType.valueOf(zh.getDM().getHorseType(horseUUID));
		HorseVariantEnum horseVariant = HorseVariantEnum.from(horseType);
		switch (horseVariant) {
		case HORSE:
			variantMessage = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.VARIANT_HORSE) {{ setSpaceCount(1); }}, true);
			break;
		case DONKEY:
			variantMessage = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.VARIANT_DONKEY) {{ setSpaceCount(1); }}, true);
			break;
		case MULE:
			variantMessage = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.VARIANT_MULE) {{ setSpaceCount(1); }}, true);
			break;
		case LLAMA:
			variantMessage = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.VARIANT_LLAMA) {{ setSpaceCount(1); }}, true);
			break;
		case SKELETON:
			variantMessage = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.VARIANT_SKELETON) {{ setSpaceCount(1); }}, true);
			break;
		case ZOMBIE:
			variantMessage = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.VARIANT_ZOMBIE) {{ setSpaceCount(1); }}, true);
			break;
		}
		return variantMessage;
	}

	private String buildStatusMessage(UUID horseUUID) {
		String statusMessage = "";
		if (zh.getDM().isHorseLocked(horseUUID)) {
			statusMessage += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.LOCKED) {{ setSpaceCount(1); }}, true);
		}
		else if (zh.getDM().isHorseShared(horseUUID)) {
			statusMessage += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.SHARED) {{ setSpaceCount(1); }}, true);
		}
		else {
			statusMessage += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.RESTRICTED) {{ setSpaceCount(1); }}, true);
		}
		if (zh.getDM().isHorseProtected(horseUUID)) {
			statusMessage += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.PROTECTED) {{ setSpaceCount(1); }}, true);
		}
		return statusMessage;
	}

	private void buildDeadHorseNameList(CompoundMessage compoundMessage, List<HorseDeathRecord> horseDeathRecordList, String remainingDeathsMessage) {
		if (!horseDeathRecordList.isEmpty()) {
			for (HorseDeathRecord deathRecord : horseDeathRecordList) {
				UUID horseUUID = UUID.fromString(deathRecord.getUUID());
				if (!variantMode || zh.getDM().isHorseOfType(horseUUID, variant)) {
					deadHorseListed = true;
					if (!deadHorsesPageCreated) {
						createDeadHorsesPage(compoundMessage);
					}
					String horseName = zh.getDM().getHorseName(horseUUID);
					String variantMessage = buildVariantMessage(horseUUID);
					String deathDate = MessageManager.DATE_FORMAT_SHORT.format(deathRecord.getDate());
					String message = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.DEAD_HORSE_LIST_FORMAT) {{
						setHorseName(horseName); setValue(variantMessage); setValue(deathDate); setSpaceCount(1);
					}}, true);
					compoundMessage.addLine(message);
				}
			}
		}
	}
	
	private void createDeadHorsesPage(CompoundMessage compoundMessage) {
		if (livingHorseListed) {
			compoundMessage.addPage();
		}
		deadHorsesPageCreated = true;
		deadHorseListStartingPageNumber = compoundMessage.getPageCount();
	}

	private void buildAliveHorseListHeader(CompoundMessage compoundMessage, int startingPageNumber, String remainingClaimsMessage, String pageNumberMessage) {
		if (livingHorseListed || !deadHorseListed) {
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
	}

	private void buildDeadHorseListHeader(CompoundMessage compoundMessage, int startingPageNumber, String remainingDeathsMessage, String pageNumberMessage) {
		if (deadHorseListed) {
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
	}
	
}