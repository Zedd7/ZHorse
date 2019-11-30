package com.github.zedd7.zhorse.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.utils.CallbackListener;
import com.github.zedd7.zhorse.utils.CallbackResponse;

import net.milkbowl.vault.permission.Permission;

public class PermissionManager {

	private ZHorse zh;
	private Permission perms;
	
	public PermissionManager(ZHorse zh) {
		this.zh = zh;
		RegisteredServiceProvider<Permission> rsp = zh.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp != null) {
	        perms = rsp.getProvider();
        } else {
        	zh.getLogger().severe(String.format("No permission manager found ! Disabling %s...", zh.getDescription().getName()));
			zh.getServer().getPluginManager().disablePlugin(zh);
        }
	}

	public String getPrimaryGroup(Player player) {
		return perms.getPrimaryGroup(player);
	}
	
	public void getPrimaryGroup(String world, OfflinePlayer op, CallbackListener<String> listener) {
		CallbackResponse<String> response = new CallbackResponse<>();
		new BukkitRunnable() {

			@Override
			public void run() {
				String primaryGroupp = perms.getPrimaryGroup(world, op);
				response.setResult(primaryGroupp);
				if (listener != null) {
					new BukkitRunnable() { // Go back to main (sync) loop

						@Override
						public void run() {
							listener.callback(response);
						}

					}.runTask(zh); // Use runTask() to sync with the next tick
				}
			}
		}.runTaskAsynchronously(zh);
	}

	public boolean has(Player player, String permission) {
		return perms.has(player, permission);
	}

	public boolean has(CommandSender commandSender, String permission) {
		return perms.has(commandSender, permission);
	}

}
