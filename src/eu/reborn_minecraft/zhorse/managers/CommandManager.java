package eu.reborn_minecraft.zhorse.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.commands.ZClaim;
import eu.reborn_minecraft.zhorse.commands.ZFree;
import eu.reborn_minecraft.zhorse.commands.ZGive;
import eu.reborn_minecraft.zhorse.commands.ZHeal;
import eu.reborn_minecraft.zhorse.commands.ZHelp;
import eu.reborn_minecraft.zhorse.commands.ZHere;
import eu.reborn_minecraft.zhorse.commands.ZInfo;
import eu.reborn_minecraft.zhorse.commands.ZKill;
import eu.reborn_minecraft.zhorse.commands.ZList;
import eu.reborn_minecraft.zhorse.commands.ZLock;
import eu.reborn_minecraft.zhorse.commands.ZProtect;
import eu.reborn_minecraft.zhorse.commands.ZReload;
import eu.reborn_minecraft.zhorse.commands.ZRename;
import eu.reborn_minecraft.zhorse.commands.ZSettings;
import eu.reborn_minecraft.zhorse.commands.ZShare;
import eu.reborn_minecraft.zhorse.commands.ZTame;
import eu.reborn_minecraft.zhorse.commands.ZTp;

public class CommandManager implements CommandExecutor {
	ZHorse zh;
	List<String> commandList;
	List<String> settingsCommandList;
	
	public CommandManager(ZHorse zh) {
		this.zh = zh;
		commandList = new ArrayList<String>();
		settingsCommandList = new ArrayList<String>();
		commandList.add(zh.getLM().help);
		commandList.add(zh.getLM().claim);
		commandList.add(zh.getLM().free);
		commandList.add(zh.getLM().give);
		commandList.add(zh.getLM().heal);
		commandList.add(zh.getLM().here);
	    commandList.add(zh.getLM().info);
		commandList.add(zh.getLM().kill);
		commandList.add(zh.getLM().list);
		commandList.add(zh.getLM().lock);
		commandList.add(zh.getLM().rename);
		commandList.add(zh.getLM().protect);
		commandList.add(zh.getLM().reload);
		commandList.add(zh.getLM().settings);
		commandList.add(zh.getLM().share);
		commandList.add(zh.getLM().tame);
		commandList.add(zh.getLM().tp);
		settingsCommandList.add(zh.getLM().language);
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		String subCommand;
		if (a.length == 0) {
			subCommand = zh.getLM().help;
		}
		else {
			subCommand = a[0];
		}
		if (subCommand.equalsIgnoreCase(zh.getLM().claim)) {
			new ZClaim(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().free)) {
			new ZFree(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().give)) {
			new ZGive(zh, s ,a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().heal)) {
			new ZHeal(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().help)) {
			new ZHelp(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().here)) {
			new ZHere(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().info)) {
			new ZInfo(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().kill)) {
			new ZKill(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().list)) {
			new ZList(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().lock)) {
			new ZLock(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().rename)) {
			new ZRename(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().protect)) {
			new ZProtect(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().reload)) {
			new ZReload(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().settings)) {
			new ZSettings(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().share)) {
			new ZShare(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().tame)) {
			new ZTame(zh, s, a);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().tp)) {
			new ZTp(zh, s, a);
		}
		else {
			String language = zh.getCM().getDefaultLanguage();
			if (s instanceof Player) {
				language = zh.getUM().getPlayerLanguage(((Player) s).getUniqueId());
			}
			s.sendMessage(zh.getMM().getMessage(language, zh.getLM().unknownCommand));
		}
		return true;
	}
	
	public List<String> getCommandList() {
		return commandList;
	}
	
	public List<String> getSettingsCommandList() {
		return settingsCommandList;
	}
	
}