package eu.reborn_minecraft.zhorse.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
import eu.reborn_minecraft.zhorse.commands.ZShare;
import eu.reborn_minecraft.zhorse.commands.ZTame;
import eu.reborn_minecraft.zhorse.commands.ZTp;

public class CommandManager implements CommandExecutor {
	ZHorse zh;
	List<String> commandList;
	
	public CommandManager(ZHorse zh) {
		this.zh = zh;
		commandList = new ArrayList<String>();
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
		commandList.add(zh.getLM().share);
		commandList.add(zh.getLM().tame);
		commandList.add(zh.getLM().tp);
		//Map<String,Class> m; // remplacer tous les if par un for sur une map
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		String subCommand = "";
		if (a.length == 0) {
			subCommand = zh.getLM().help;
		}
		else {
			subCommand = a[0];
		}
		if (subCommand.equalsIgnoreCase(zh.getLM().help)) {
			new ZHelp(s, c, a, zh, commandList);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().claim)) {
			new ZClaim(zh, a, s);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().free)) {
			new ZFree(zh, a, s);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().give)) {
			new ZGive(zh, a ,s);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().heal)) {
			new ZHeal(s, a, zh);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().here)) {
			new ZHere(s, a, zh);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().info)) {
			new ZInfo(s, a, zh);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().kill)) {
			new ZKill(s, a, zh);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().list)) {
			new ZList(s, a, zh);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().lock)) {
			new ZLock(s, a, zh);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().rename)) {
			new ZRename(s, a, zh);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().protect)) {
			new ZProtect(s, a, zh);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().reload)) {
			new ZReload(s, a, zh);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().share)) {
			new ZShare(s, a, zh);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().tame)) {
			new ZTame(s, a, zh);
		}
		else if (subCommand.equalsIgnoreCase(zh.getLM().tp)) {
			new ZTp(s, a, zh);
		}
		else {
			s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().unknownCommand));
		}
		return true;
	}
	
	public List<String> getCommandList() {
		return commandList;
	}
	
}