package eu.reborn_minecraft.zhorse.commands;

import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZSettings extends Command {

	public ZSettings(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		idAllow = false;
		targetAllow = false;
		if (isPlayer()) {
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
		if (zh.getEM().canAffordCommand(p, command)) {
			if (a.length >= 1) {
				String subCommand = a[0];
				if (subCommand.equals("language")) {
					editLanguage();
				}
				else if (displayConsole) {
					s.sendMessage(zh.getMM().getMessage(language, zh.getLM().unknownSettingsCommand));
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
		s.sendMessage(zh.getMM().getHeaderContent(language, zh.getLM().headerFormat, zh.getLM().settingsCommandListHeader, true));
		for (String subCommand : zh.getCmdM().getSettingsCommandList()) {
			String fullCommand = command + "." + subCommand;
			if (hasPermission(targetUUID, fullCommand, true, true)) {
				if (zh.getEM().isCommandFree(targetUUID, command)) {
					s.sendMessage(zh.getMM().getSettingsCommandDescription(language, " ", subCommand, true));
				}
				else {
					String cost = Integer.toString(zh.getCM().getCommandCost(command));
					s.sendMessage(zh.getMM().getCommandDescriptionCostValue(language, " ", command, zh.getLM().commandCost, cost, zh.getLM().getEconomyAnswer(language, zh.getLM().currencySymbol, true), true));
				}
			}
		}
	}

	private void editLanguage() {
		if (a.length == 2) {
			String language = a[1].toUpperCase();
			if (zh.getCM().isLanguageAvailable(language)) {
				zh.getUM().saveLanguage(targetUUID, language);
				if (samePlayer) {
					s.sendMessage(zh.getMM().getMessageLang(language, zh.getLM().languageEdited, language));
				}
				else {
					String playerLanguage = zh.getUM().getPlayerLanguage(p.getUniqueId());
					s.sendMessage(zh.getMM().getMessagePlayerLang(playerLanguage, zh.getLM().languageEditedOther, targetName, language));
					if (isPlayerOnline(targetUUID, true)) {
						Player target = zh.getServer().getPlayer(targetUUID);
						target.sendMessage(zh.getMM().getMessageLang(language, zh.getLM().languageEdited, language));
					}
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
			availableLanguagesMessage += zh.getMM().getHeaderLang(language, zh.getLM().availableLanguageFormat, availableLanguages.get(i), true);
			if (i < availableLanguages.size()-1) {
				availableLanguagesMessage += ", ";
			}
		}
		availableLanguagesMessage += ChatColor.RESET;
		s.sendMessage(zh.getMM().getMessageLang(language, index, availableLanguagesMessage));
	}

}