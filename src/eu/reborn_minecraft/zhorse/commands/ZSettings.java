package eu.reborn_minecraft.zhorse.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.CommandSettingsEnum;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;
import net.md_5.bungee.api.ChatColor;

public class ZSettings extends Command {
	String fullCommand;
	String subCommand;

	public ZSettings(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {			
			if (!idMode) {
				if (isOnHorse(true)) { // select the horse w/ or w/o target
					horse = (Horse) p.getVehicle();
					if (isOwner(targetUUID, true, true)) {
						idMode = true;
						horseID = zh.getDM().getHorseID(horse.getUniqueId()).toString();
					}
				}
				execute();
			}
			else if (isRegistered(targetUUID, horseID)) {
				execute();
			}
		}
	}

	private void execute() {
		if (zh.getEM().canAffordCommand(p, command)) {
			if (!argument.isEmpty()) {
				subCommand = argument.toLowerCase();;
				if (argument.contains(" ")) {
					subCommand = argument.substring(0, argument.indexOf(" "));
				}
				if (subCommand.equals(CommandSettingsEnum.LANGUAGE.getName())) {
					setLanguage();
				}
				else if (subCommand.equals((CommandSettingsEnum.FAVORITE.getName()))) {
					setFavorite();
				}
				else {
					if (displayConsole) {
						zh.getMM().sendMessageValue(s, LocaleEnum.unknownSettingsCommand, subCommand);
					}
					sendCommandSettingsDescriptionList();
				}
			}
			else {
				sendCommandSettingsDescriptionList();
			}
		}
	}
	
	private void setFavorite() {
		fullCommand = command + KeyWordEnum.dot.getValue() + CommandSettingsEnum.FAVORITE.getName().toLowerCase();
		if (hasPermission(s, fullCommand , true, false)) {
			if (argument.split(" ").length >= 2) {
				idMode = true;
				horseID = argument.substring(argument.indexOf(" ") + 1);
			}
			if (idMode) {
				if (isRegistered(targetUUID, horseID)) {
					if (!zh.getDM().getPlayerFavoriteHorseID(targetUUID).toString().equals(horseID)) {
						zh.getDM().updatePlayerFavorite(targetUUID, Integer.parseInt(horseID));
						if (samePlayer) {
							zh.getMM().sendMessageHorse(s, LocaleEnum.favoriteEdited, horseName);
						}
						else {
							zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.favoriteEditedOther, horseName, targetName);
							if (isPlayerOnline(targetUUID, true)) {
								Player target = zh.getServer().getPlayer(targetUUID);
								zh.getMM().sendMessageHorse((CommandSender)target, LocaleEnum.favoriteEdited, horseName);
							}
						}
						zh.getEM().payCommand(p, command);
					}
					else if (displayConsole) {
						if (samePlayer) {
							zh.getMM().sendMessageHorse(s, LocaleEnum.favoriteAlreadySet, horseName);
						}
						else {
							zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.favoriteAlreadySetOther, horseName, targetName);
						}
					}
				}
			}
			else if (displayConsole) {
				zh.getMM().sendMessage(s, LocaleEnum.missingHorseId);
				sendCommandUsage(subCommand, true);
			}
		}
	}

	private void setLanguage() {
		fullCommand = command + KeyWordEnum.dot.getValue() + CommandSettingsEnum.LANGUAGE.getName().toLowerCase();
		if (hasPermission(s, fullCommand , true, false)) {
			if (argument.split(" ").length >= 2) {
				String language = argument.substring(argument.indexOf(" ")+1).toUpperCase();
				if (zh.getCM().isLanguageAvailable(language)) {
					if (!zh.getDM().getPlayerLanguage(targetUUID).equals(language)) {
						zh.getDM().updatePlayerLanguage(targetUUID, language);
						if (samePlayer) {
							zh.getMM().sendMessageLang(s, LocaleEnum.languageEdited, language);
						}
						else {
							zh.getMM().sendMessageLangPlayer(s, LocaleEnum.languageEditedOther, language, targetName);
							if (isPlayerOnline(targetUUID, true)) {
								Player target = zh.getServer().getPlayer(targetUUID);
								zh.getMM().sendMessageLang((CommandSender)target, LocaleEnum.languageEdited, language);
							}
						}
						zh.getEM().payCommand(p, command);
					}
					else if (displayConsole) {
						if (samePlayer) {
							zh.getMM().sendMessageLang(s, LocaleEnum.languageAlreadyUsed, language);
						}
						else {
							zh.getMM().sendMessageLangPlayer(s, LocaleEnum.languageAlreadyUsedOther, language, targetName);
						}
					}
				}
				else if (displayConsole) {
					displayAvailableLanguages(LocaleEnum.unknownLanguage, language);
				}
			}
			else if (displayConsole) {
				displayAvailableLanguages(LocaleEnum.missingLanguage);
				sendCommandUsage(subCommand, true);
			}
		}
	}
	
	private void displayAvailableLanguages(LocaleEnum index) {
		displayAvailableLanguages(index, null);
	}

	private void displayAvailableLanguages(LocaleEnum index, String language) {
		List<String> availableLanguages = zh.getCM().getAvailableLanguages();
		String availableLanguagesMessage = "";
		for (int i = 0; i < availableLanguages.size(); ++i) {
			availableLanguagesMessage += zh.getMM().getMessageValue(s, LocaleEnum.availableOptionFormat, availableLanguages.get(i), true);
			if (i < availableLanguages.size() - 1) {
				availableLanguagesMessage += ", ";
			}
		}
		availableLanguagesMessage += ChatColor.RESET;
		if (language != null) {
			zh.getMM().sendMessageLangValue(s, index, language, availableLanguagesMessage);
		}
		else {
			zh.getMM().sendMessageValue(s, index, availableLanguagesMessage);
		}
	}

}