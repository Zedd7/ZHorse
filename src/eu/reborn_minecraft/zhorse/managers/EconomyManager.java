package eu.reborn_minecraft.zhorse.managers;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;
import net.milkbowl.vault.economy.Economy;

public class EconomyManager {
	private ZHorse zh;
	private Economy econ;
	
	public EconomyManager(ZHorse zh) {
		this.zh = zh;
		this.econ = zh.getEcon();
	}
	
	public boolean canAffordCommand(Player p, String command) {
		if (p != null) {
			int amount = zh.getCM().getCommandCost(command);
			if (isCommandFree(p, command) || econ.has(zh.getServer().getOfflinePlayer(p.getUniqueId()), amount)) {
				return true;
			}
			zh.getMM().sendMessageAmountValue((CommandSender)p, LocaleEnum.notEnoughMoney, amount, zh.getLM().getMessage(LocaleEnum.currencySymbol.getIndex(), zh.getUM().getLanguage(p.getUniqueId()), true));
		}
		return false;
	}
	
	public boolean isCommandFree(UUID playerUUID, String command) {
		if (isPlayerOnline(playerUUID)) {
			Player p = zh.getServer().getPlayer(playerUUID);
			return isCommandFree(p, command);
		}
		return false;
	}
	
	public boolean isCommandFree(Player p, String command) {
		int cost = zh.getCM().getCommandCost(command);
		if (cost == 0 || zh.getPerms().has(p, KeyWordEnum.zhPrefix.getValue() + command + KeyWordEnum.freeSuffix.getValue())) {
			return true;
		}
		return false;
	}
	
	private boolean isPlayerOnline(UUID playerUUID) {
		for (Player p : zh.getServer().getOnlinePlayers()) {
			if (p.getUniqueId().equals(playerUUID)) {
				return true;
			}
		}
		return false;
	}
	
	public void payCommand(Player p, String command) {
		int amount = zh.getCM().getCommandCost(command);
		if (!isCommandFree(p, command)) {
			econ.withdrawPlayer(zh.getServer().getOfflinePlayer(p.getUniqueId()), amount);
			zh.getMM().sendMessageAmountValue((CommandSender)p, LocaleEnum.commandPaid, amount, zh.getLM().getMessage(LocaleEnum.currencySymbol.getIndex(), zh.getUM().getLanguage(p.getUniqueId()), true));
		}
	}

}
