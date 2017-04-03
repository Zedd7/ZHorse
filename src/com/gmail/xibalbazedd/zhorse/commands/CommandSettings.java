package com.gmail.xibalbazedd.zhorse.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.enums.CommandSettingsEnum;
import com.gmail.xibalbazedd.zhorse.enums.KeyWordEnum;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;

import net.md_5.bungee.api.ChatColor;

public class CommandSettings extends AbstractCommand {
	
	private String fullCommand;
	private String subCommand;

	public CommandSettings(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {			
			if (!idMode) {
				if (isOnHorse(true)) { // select the horse w/ or w/o target
					horse = (AbstractHorse) p.getVehicle();
					if (isOwner(targetUUID, true, true)) {
						idMode = true;
						Integer horseIDInt = zh.getDM().getHorseID(horse.getUniqueId());
						horseID = horseIDInt != null ? horseIDInt.toString() : null;
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
		if ((!targetMode || samePlayer || hasPermissionAdmin(false)) && zh.getEM().canAffordCommand(p, command)) {
			if (!argument.isEmpty()) {
				subCommand = argument.contains(" ") ? argument.substring(0, argument.indexOf(" ")) : argument;
				if (subCommand.equalsIgnoreCase(CommandSettingsEnum.LANGUAGE.getName())) {
					setLanguage();
				}
				else if (subCommand.equalsIgnoreCase((CommandSettingsEnum.FAVORITE.getName()))) {
					setFavorite();
				}
				else {
					if (displayConsole) {
						zh.getMM().sendMessageValue(s, LocaleEnum.UNKNOWN_SETTINGS_COMMAND, subCommand);
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
		fullCommand = command + KeyWordEnum.DOT.getValue() + CommandSettingsEnum.FAVORITE.getName().toLowerCase();
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
							zh.getMM().sendMessageHorse(s, LocaleEnum.FAVORITE_EDITED, horseName);
						}
						else {
							zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.FAVORITE_EDITED_OTHER, horseName, targetName);
							if (isPlayerOnline(targetUUID, true)) {
								Player target = zh.getServer().getPlayer(targetUUID);
								zh.getMM().sendMessageHorse((CommandSender)target, LocaleEnum.FAVORITE_EDITED, horseName);
							}
						}
						zh.getEM().payCommand(p, command);
					}
					else if (displayConsole) {
						if (samePlayer) {
							zh.getMM().sendMessageHorse(s, LocaleEnum.FAVORITE_ALREADY_SET, horseName);
						}
						else {
							zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.FAVORITE_ALREADY_SET_OTHER, horseName, targetName);
						}
					}
				}
			}
			else if (displayConsole) {
				zh.getMM().sendMessage(s, LocaleEnum.MISSING_HORSE_ID);
				sendCommandUsage(subCommand, true, true);
			}
		}
	}

	private void setLanguage() {
		fullCommand = command + KeyWordEnum.DOT.getValue() + CommandSettingsEnum.LANGUAGE.getName();
		if (hasPermission(s, fullCommand , true, false)) {
			if (argument.split(" ").length >= 2) {
				String language = argument.substring(argument.indexOf(" ")+1).toUpperCase();
				if (zh.getCM().isLanguageAvailable(language)) {
					if (!zh.getDM().getPlayerLanguage(targetUUID).equals(language)) {
						zh.getDM().updatePlayerLanguage(targetUUID, language);
						if (samePlayer) {
							zh.getMM().sendMessageLang(s, LocaleEnum.LANGUAGE_EDITED, language);
						}
						else {
							zh.getMM().sendMessageLangPlayer(s, LocaleEnum.LANGUAGE_EDITED_OTHER, language, targetName);
							if (isPlayerOnline(targetUUID, true)) {
								Player target = zh.getServer().getPlayer(targetUUID);
								zh.getMM().sendMessageLang((CommandSender)target, LocaleEnum.LANGUAGE_EDITED, language);
							}
						}
						zh.getEM().payCommand(p, command);
					}
					else if (displayConsole) {
						if (samePlayer) {
							zh.getMM().sendMessageLang(s, LocaleEnum.LANGUAGE_ALREADY_USED, language);
						}
						else {
							zh.getMM().sendMessageLangPlayer(s, LocaleEnum.LANGUAGE_ALREADY_USED_OTHER, language, targetName);
						}
					}
				}
				else if (displayConsole) {
					displayAvailableLanguages(LocaleEnum.UNKNOWN_LANGUAGE, language);
				}
			}
			else if (displayConsole) {
				displayAvailableLanguages(LocaleEnum.MISSING_LANGUAGE);
				sendCommandUsage(subCommand, true, true);
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
			availableLanguagesMessage += zh.getMM().getMessageValue(s, LocaleEnum.AVAILABLE_OPTION_FORMAT, availableLanguages.get(i), true);
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