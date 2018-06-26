package com.github.zedd7.zhorse.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.github.zedd7.zhorse.ZHorse;

import net.milkbowl.vault.permission.Permission;

public class PermissionManager {
	
	private Permission perms;
	
	public PermissionManager(ZHorse zh) {
		RegisteredServiceProvider<Permission> rsp = zh.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        if (perms == null) {
        	zh.getLogger().severe(String.format("No permission manager found ! Disabling %s...", zh.getDescription().getName()));
			zh.getServer().getPluginManager().disablePlugin(zh);
        }
	}

	public String getPrimaryGroup(Player player) {
		return perms.getPrimaryGroup(player);
	}
	
	public String getPrimaryGroup(String world, OfflinePlayer op) {
		return perms.getPrimaryGroup(world, op);
	}

	public boolean has(Player player, String permission) {
		return perms.has(player, permission);
	}

	public boolean has(CommandSender commandSender, String permission) {
		return perms.has(commandSender, permission);
	}

}
