package eu.reborn_minecraft.zhorse.commands;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class ZSettings extends Command {
	private static String LANGUAGE = "language";

	public ZSettings(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			if (!idMode) {
				if (!targetMode || isRegistered(targetUUID) && isPlayerOnline(targetUUID, false)) {
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
			if (!argument.isEmpty()) {
				String settingsCommand;
				if (argument.contains(" ")) {
					settingsCommand = argument.substring(0, argument.indexOf(" ")).toLowerCase();
				}
				else {
					settingsCommand = argument;
				}
				if (settingsCommand.equals(LANGUAGE)) {
					editLanguage();
				}
				else {
					if (displayConsole) {
						zh.getMM().sendMessageValue(s, LocaleEnum.unknownSettingsCommand, settingsCommand);
					}
					displaySettingsCommands();
				}
			}
			else {
				System.out.println("test1");
				displaySettingsCommands();
			}
		}
	}

	private void displaySettingsCommands() {
		List <String> settingsCommandList = new ArrayList<String>();
		for (String settingsCommand : zh.getCmdM().getSettingsCommandList()) {
			settingsCommandList.add(command + "." + settingsCommand);
		}
		displayCommandList(settingsCommandList, zh.getMM().getMessage(s, LocaleEnum.settingsCommandListHeader, true));
	}

	private void editLanguage() {
		if (argument.split(" ").length >= 2) {
			String language = argument.substring(argument.indexOf(" ")+1).toUpperCase();
			if (zh.getCM().isLanguageAvailable(language)) {
				if (!zh.getUM().getLanguage(targetUUID).equals(language)) {
					zh.getUM().saveLanguage(targetUUID, language);
					if (samePlayer) {
						zh.getMM().sendMessageLang(s, zh.getLM().languageEdited, language);
					}
					else {
						zh.getMM().sendMessagePlayerLang(s, zh.getLM().languageEditedOther, targetName, language);
						if (isPlayerOnline(targetUUID, true)) {
							Player target = zh.getServer().getPlayer(targetUUID);
							zh.getMM().sendMessageLang((CommandSender)target, zh.getLM().languageEdited, language);
						}
					}
					zh.getEM().payCommand(p, command);
				}
				else if (displayConsole) {
					if (samePlayer) {
						zh.getMM().sendMessageLang(s, zh.getLM().languageAlreadyUsed, language);
					}
					else {
						zh.getMM().sendMessagePlayerLang(s, zh.getLM().languageAlreadyUsedOther, targetName, language);
					}
				}
			}
			else if (displayConsole) {
				displayAvailableLanguages(zh.getLM().unknownLanguage, language);
			}
		}
		else if (displayConsole) {
			displayAvailableLanguages(zh.getLM().missingLanguage);
		}
		
	}
	
	private void displayAvailableLanguages(String index) {
		displayAvailableLanguages(index, null);
	}

	private void displayAvailableLanguages(String index, String language) {
		List<String> availableLanguages = zh.getCM().getAvailableLanguages();
		String availableLanguagesMessage = "";
		for (int i=0; i<availableLanguages.size(); i++) {
			availableLanguagesMessage += zh.getMM().getHeaderValue(s, zh.getLM().availableLanguageFormat, 0, availableLanguages.get(i), true);
			if (i < availableLanguages.size()-1) {
				availableLanguagesMessage += ", ";
			}
		}
		availableLanguagesMessage += ChatColor.RESET;
		if (language != null) {
			zh.getMM().sendMessageValueLang(s, index, availableLanguagesMessage, language);
		}
		else {
			zh.getMM().sendMessageValue(s, index, availableLanguagesMessage);
		}
	}

}