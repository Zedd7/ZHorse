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
						userID = zh.getUM().getUserID(targetUUID, horse);
					}
				}
				execute();
			}
			else if (isRegistered(targetUUID, userID)) {
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
				if (subCommand.equals(CommandSettingsEnum.language.name())) {
					setLanguage();
				}
				else if (subCommand.equals((CommandSettingsEnum.favorite.name()))) {
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
		fullCommand = command + KeyWordEnum.dot.getValue() + CommandSettingsEnum.favorite.getName().toLowerCase();
		if (hasPermission(s, fullCommand , true, false)) {
			if (argument.split(" ").length >= 2) {
				idMode = true;
				userID = argument.substring(argument.indexOf(" ")+1);
			}
			if (idMode) {
				if (isRegistered(targetUUID, userID)) {
					if (!zh.getUM().getFavoriteUserID(targetUUID).equals(userID)) {
						zh.getUM().saveFavorite(targetUUID, userID);
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
		fullCommand = command + KeyWordEnum.dot.getValue() + CommandSettingsEnum.language.getName().toLowerCase();
		if (hasPermission(s, fullCommand , true, false)) {
			if (argument.split(" ").length >= 2) {
				String language = argument.substring(argument.indexOf(" ")+1).toUpperCase();
				if (zh.getCM().isLanguageAvailable(language)) {
					if (!zh.getUM().getLanguage(targetUUID).equals(language)) {
						zh.getUM().saveLanguage(targetUUID, language);
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
		for (int i=0; i<availableLanguages.size(); i++) {
			availableLanguagesMessage += zh.getMM().getMessageValue(s, LocaleEnum.availableLanguageFormat, availableLanguages.get(i), true);
			if (i < availableLanguages.size()-1) {
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