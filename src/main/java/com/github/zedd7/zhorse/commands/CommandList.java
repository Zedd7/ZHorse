package com.github.zedd7.zhorse.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.database.HorseDeathRecord;
import com.github.zedd7.zhorse.database.HorseRecord;
import com.github.zedd7.zhorse.enums.HorseVariantEnum;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.managers.MessageManager;
import com.github.zedd7.zhorse.utils.CompoundMessage;
import com.github.zedd7.zhorse.utils.MessageConfig;

public class CommandList extends AbstractCommand {

	private int livingHorseListStartingPageNumber = CompoundMessage.FIRST_PAGE_NUMBER;
	private int deadHorseListStartingPageNumber = CompoundMessage.FIRST_PAGE_NUMBER;
	private boolean livingHorseListed = false;
	private boolean deadHorseListed = false;
	private boolean deadHorsesPageCreated = false;

	public CommandList(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled() && parseArgument(ArgumentEnum.PAGE_NUMBER)) {
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
		CompoundMessage compoundMessage = new CompoundMessage(true);

		List<HorseRecord> aliveHorseList = zh.getDM().getHorseRecordList(targetUUID, false);
		String remainingClaimsMessage = getRemainingClaimsMessage(targetUUID);
		buildAliveHorseList(compoundMessage, aliveHorseList, remainingClaimsMessage);

		List<HorseDeathRecord> deathHorseList = zh.getDM().getHorseDeathRecordList(targetUUID);
		String remainingDeathsMessage = getRemainingDeathsMessage(targetUUID);
		buildDeadHorseList(compoundMessage, deathHorseList, remainingDeathsMessage);

		int maxPageNumber = compoundMessage.getPageCount();
		String pageNumberMessage = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.PAGE_NUMBER_FORMAT) {{ setAmount(pageNumber); setMax(maxPageNumber); }}, true);
		buildAliveHorseListHeader(compoundMessage, livingHorseListStartingPageNumber, remainingClaimsMessage, pageNumberMessage);
		buildDeadHorseListHeader(compoundMessage, deadHorseListStartingPageNumber, remainingDeathsMessage, pageNumberMessage);

		String message = zh.getMM().getMessage(compoundMessage, pageNumber);
		if (!message.isEmpty()) {
			zh.getMM().sendMessage(s, message);
		}
		zh.getCmdM().updateCommandHistory(s, command);
		zh.getEM().payCommand(p, command);
	}

	private void buildAliveHorseList(CompoundMessage compoundMessage, List<HorseRecord> aliveHorseList, String remainingClaimsMessage) {
		if (!aliveHorseList.isEmpty()) {
			int expectedHorseID = zh.getDM().getDefaultFavoriteHorseID();
			int favoriteHorseID = zh.getDM().getPlayerFavoriteHorseID(targetUUID);
			for (HorseRecord horseRecord : aliveHorseList) {
				UUID horseUUID = UUID.fromString(horseRecord.getUUID());
				if (!variantMode || zh.getDM().isHorseOfType(horseUUID, variant)) {
					livingHorseListed = true;
					int horseID = validateHorseID(horseUUID, horseRecord.getId(), expectedHorseID); // Order assured by DataManager
					String horseName = horseRecord.getName();
					String variantMessage = buildVariantMessage(horseUUID);
					String statusMessage = buildStatusMessage(horseRecord);
					String message;
					if (horseID == favoriteHorseID) {
						message = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.ALIVE_HORSE_LIST_FORMAT_FAVORITE) {{
							setHorseName(horseName); setHorseID(Integer.toString(horseID)); setSpaceCount(1); setValue(variantMessage); setValue(statusMessage);
						}}, true);
					}
					else {
						message = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.ALIVE_HORSE_LIST_FORMAT) {{
							setHorseName(horseName); setHorseID(Integer.toString(horseID)); setSpaceCount(1); setValue(variantMessage); setValue(statusMessage);
						}}, true);
					}
					if (horseID == expectedHorseID) expectedHorseID++;
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

	private int validateHorseID(UUID horseUUID, int horseID, int expectedHorseID) {
		if (horseID < expectedHorseID) {
			horseID = zh.getDM().getNextHorseID(targetUUID);
			zh.getDM().updateHorseID(horseUUID, horseID, false, null);
		}
		else if (horseID > expectedHorseID) {
			horseID--;
			zh.getDM().updateHorseID(horseUUID, horseID, false, null);
		}
		return horseID;
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

	private String buildStatusMessage(HorseRecord horseRecord) {
		String statusMessage = "";
		if (horseRecord.isLocked()) {
			statusMessage += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.LOCKED) {{ setSpaceCount(1); }}, true);
		}
		else if (horseRecord.isShared()) {
			statusMessage += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.SHARED) {{ setSpaceCount(1); }}, true);
		}
		else {
			statusMessage += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.RESTRICTED) {{ setSpaceCount(1); }}, true);
		}
		if (horseRecord.isProtected()) {
			statusMessage += zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.PROTECTED) {{ setSpaceCount(1); }}, true);
		}
		return statusMessage;
	}

	private void buildDeadHorseList(CompoundMessage compoundMessage, List<HorseDeathRecord> deathHorseList, String remainingDeathsMessage) {
		if (!deathHorseList.isEmpty()) {
			for (HorseDeathRecord deathRecord : deathHorseList) {
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
		if (livingHorseListed) {
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