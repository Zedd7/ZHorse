package eu.reborn_minecraft.zhorse.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZSettings extends Command {
	private static boolean playerOnly = true;

	public ZSettings(ZHorse zh, CommandSender s, String[] a) {
		super(zh, a, s);
		idAllow = false;
		targetAllow = true;
		if (isPlayer(playerOnly)) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (!idMode) {
						executePlayer();
					}
					else if (displayConsole) {
						sendCommandUsage(true);
					}
				}
			}
		}
	}

	private void executePlayer() {
		if (zh.getEM().isReadyToPay(p, command)) {
			if (a.length >= 1) {
				String subCommand = a[0];
				if (subCommand.equals("language")) {
					editLanguage();
				}
				else if (displayConsole) {
					s.sendMessage(zh.getLM().getCommandAnswer(language, zh.getLM().unknownSettingsCommand));
					displaySettingsCommands();
				}
			}
			else if (displayConsole) {
				displaySettingsCommands();
			}
			zh.getEM().payCommand(p, command);
		}
	}

	private void displaySettingsCommands() {
		s.sendMessage(String.format(zh.getLM().getHeaderMessage(language, zh.getLM().headerFormat), zh.getLM().getHeaderMessage(language, zh.getLM().settingsCommandListHeader)));
		for (String subCommand : zh.getCmdM().getSettingsCommandList()) {
			displayConsole = false; //sert à quoi ?
			String fullCommand = command + "." + subCommand;
			if (hasPermission(targetUUID, fullCommand, true)) {
				String message = " " + zh.getLM().getSettingsCommandDescription(language, subCommand);
				String cost = "";
				if (!zh.getEM().isCommandFree(targetUUID, command)) {
					cost = " " + String.format(zh.getLM().getEconomyAnswer(language, zh.getLM().commandCost, true), zh.getCM().getCommandCost(command));
				}
				s.sendMessage(message + cost);
			}
		}
	}

	private void editLanguage() {
		if (a.length == 2) {
			String language = a[1].toUpperCase();
			if (zh.getCM().isLanguageAvailable(language)) {
				zh.getUM().saveLanguage(targetUUID, language);
				if (samePlayer) {
					s.sendMessage(String.format(zh.getLM().getCommandAnswer(language, zh.getLM().languageEdited), language));
				}
				else {
					s.sendMessage(String.format(zh.getLM().getCommandAnswer(language, zh.getLM().languageEditedOther), targetName, language));
				}
			}
			else if (displayConsole) {
				displayAvailableLanguages(zh.getLM().unknownLanguage);
			}
		}
		else if (displayConsole) {
			displayAvailableLanguages(zh.getLM().missingLanguage);
		}
		
	}

	private void displayAvailableLanguages(String index) {
		List<String> availableLanguages = zh.getCM().getAvailableLanguages();
		String availableLanguagesMessage = "";
		for (int i=0; i<availableLanguages.size(); i++) {
			availableLanguagesMessage += "§6" + availableLanguages.get(i);
			if (i < availableLanguages.size()-1) {
				availableLanguagesMessage += "§e, ";
			}
		}
		s.sendMessage(String.format(zh.getLM().getCommandAnswer(language, index), availableLanguagesMessage));
	}

}