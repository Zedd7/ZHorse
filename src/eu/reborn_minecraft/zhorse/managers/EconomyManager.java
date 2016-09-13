package eu.reborn_minecraft.zhorse.managers;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;
import net.milkbowl.vault.economy.Economy;

public class EconomyManager {
	
	private ZHorse zh;
	private Economy econ;
	private boolean noEcon = false;
	
	public EconomyManager(ZHorse zh) {
		this.zh = zh;
		RegisteredServiceProvider<Economy> rsp = zh.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
        	econ = rsp.getProvider();
        }
        if (econ == null) {
        	zh.getLogger().warning("No economy plugin found ! Transactions disabled.");
        	noEcon = true;
        }
	}
	
	public boolean canAffordCommand(Player p, String command) {
		if (noEcon) {
			return true;
		}
		if (p != null) {
			int amount = zh.getCM().getCommandCost(command);
			if (isCommandFree(p, command) || econ.has(zh.getServer().getOfflinePlayer(p.getUniqueId()), amount)) {
				return true;
			}
			String language = zh.getDM().getPlayerLanguage(p.getUniqueId());
			String currencySymbol = zh.getLM().getMessage(LocaleEnum.currencySymbol.getIndex(), language, true);
			zh.getMM().sendMessageAmountValue((CommandSender)p, LocaleEnum.notEnoughMoney, amount, currencySymbol);
		}
		return false;
	}
	
	public boolean isCommandFree(UUID playerUUID, String command) {
		if (noEcon) {
			return true;
		}
		if (isPlayerOnline(playerUUID)) {
			Player p = zh.getServer().getPlayer(playerUUID);
			return isCommandFree(p, command);
		}
		return false;
	}
	
	public boolean isCommandFree(Player p, String command) {
		if (noEcon) {
			return true;
		}
		int cost = zh.getCM().getCommandCost(command);
		if (cost == 0 || zh.getPM().has(p, KeyWordEnum.zhPrefix.getValue() + command + KeyWordEnum.freeSuffix.getValue())) {
			return true;
		}
		return false;
	}
	
	public void payCommand(Player p, String command) {
		if (noEcon) {
			return;
		}
		int amount = zh.getCM().getCommandCost(command);
		if (!isCommandFree(p, command)) {
			econ.withdrawPlayer(zh.getServer().getOfflinePlayer(p.getUniqueId()), amount);
			String language = zh.getDM().getPlayerLanguage(p.getUniqueId());
			String currencySymbol = zh.getLM().getMessage(LocaleEnum.currencySymbol.getIndex(), language, true);
			zh.getMM().sendMessageAmountValue((CommandSender)p, LocaleEnum.commandPaid, amount, currencySymbol);
		}
	}
	
	private boolean isPlayerOnline(UUID playerUUID) {
		for (Player p : zh.getServer().getOnlinePlayers()) {
			if (p.getUniqueId().equals(playerUUID)) {
				return true;
			}
		}
		return false;
	}

}
