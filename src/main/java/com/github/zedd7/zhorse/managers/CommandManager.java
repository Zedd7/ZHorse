package com.github.zedd7.zhorse.managers;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.enums.CommandEnum;
import com.github.zedd7.zhorse.enums.KeyWordEnum;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.MessageConfig;

public class CommandManager implements CommandExecutor {

	private ZHorse zh;
	private Map<String, Duration> commandCooldownMap = new HashMap<>();
	private Map<UUID, Map<String, Instant>> commandHistoryMap = new HashMap<>();

	public CommandManager(ZHorse zh) {
		this.zh = zh;
		zh.getCommand(zh.getDescription().getName().toLowerCase()).setExecutor(this);
	}

	public void loadCommandCooldowns() {
		for (String command : CommandEnum.getNameList()) {
			int cooldown = zh.getCM().getCommandCooldown(command);
			commandCooldownMap.put(command, Duration.ofSeconds(cooldown));
		}
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (a.length == 0) {
			PluginDescriptionFile pluginDescription = zh.getDescription();
			String pluginNameAndVersion = String.format("%s %s", pluginDescription.getName(), pluginDescription.getVersion());
			String author = pluginDescription.getAuthors().get(0);
			String pluginHeader = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.PLUGIN_HEADER) {{ setPlayerName(author); setValue(pluginNameAndVersion); }}, true);
			LocaleEnum helpDescription = LocaleEnum.valueOf(CommandEnum.HELP.getName().toUpperCase() + KeyWordEnum.SEPARATOR.getValue() + KeyWordEnum.DESCRIPTION.getValue());
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HEADER_FORMAT) {{ setValue(pluginHeader); }}, true);
			zh.getMM().sendMessage(s, new MessageConfig(helpDescription) {{ setSpaceCount(1); }}, true);
		}
		else {
			String command = a[0].toLowerCase();
			boolean commandValid = false;
			for (CommandEnum commandEnum : CommandEnum.values()) {
				if (command.equalsIgnoreCase(commandEnum.getName())) {
					commandValid = true;
					try {
						Class.forName(commandEnum.getClassPath()).getConstructor(ZHorse.class, CommandSender.class, String[].class).newInstance(new Object[] {zh, s, a});
					} catch (Exception  e) {
						e.printStackTrace();
					}
					break;
				}
			}
			if (!commandValid) {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_COMMAND) {{ setValue(command); }});
			}
		}
		return true;
	}

	public long getRemainingCooldown(CommandSender s, String command) {
		if (!(s instanceof Player)) return 0;

		UUID playerUUID = ((Player) s).getUniqueId();
		if (!commandHistoryMap.containsKey(playerUUID)) {
			commandHistoryMap.put(playerUUID, new HashMap<>());
		}
		if (!commandHistoryMap.get(playerUUID).containsKey(command)) {
			commandHistoryMap.get(playerUUID).put(command, Instant.EPOCH);
		}
		Instant lastExecution = commandHistoryMap.get(playerUUID).get(command);
		Instant currentInstant = Instant.now();
		Duration elapsedTime = Duration.between(lastExecution, currentInstant);
		Duration baseCooldown = commandCooldownMap.get(command);
		long remainingCooldown = baseCooldown.getSeconds() - elapsedTime.getSeconds();
		return remainingCooldown;
	}

	public void updateCommandHistory(CommandSender s, String command) {
		if (!(s instanceof Player)) return;

		UUID playerUUID = ((Player) s).getUniqueId();
		if (!commandHistoryMap.containsKey(playerUUID)) {
			commandHistoryMap.put(playerUUID, new HashMap<>());
		}
		commandHistoryMap.get(playerUUID).put(command, Instant.now());
	}

}
