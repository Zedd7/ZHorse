package eu.reborn_minecraft.zhorse.managers;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class EconomyManager {
	private ZHorse zh;
	private Economy econ;
	
	public EconomyManager(ZHorse zh) {
		this.zh = zh;
		this.econ = zh.getEcon();
	}
	
	public boolean isReadyToPay(Player p, String command) {
		int amount = zh.getCM().getCommandCost(command);
		if (isCommandFree(p, command) || econ.has(zh.getServer().getOfflinePlayer(p.getUniqueId()), amount)) {
			return true;
		}
		p.sendMessage(String.format(zh.getLM().getEconomyAnswer(zh.getLM().notEnoughMoney), amount));
		return false;
	}
	
	public boolean isCommandFree(Player p, String command) {
		int cost = zh.getCM().getCommandCost(command);
		if (cost == 0 || zh.getPerms().has(p, command + ".free")) {
			return true;
		}
		return false;
	}
	
	public void payCommand(Player p, String command) {
		int amount = zh.getCM().getCommandCost(command);
		if (!isCommandFree(p, command)) {
			econ.withdrawPlayer(zh.getServer().getOfflinePlayer(p.getUniqueId()), amount);
			p.sendMessage(String.format(zh.getLM().getEconomyAnswer(zh.getLM().commandPaid), amount));
		}
	}

}
