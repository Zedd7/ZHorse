package com.github.zedd7.zhorse.managers;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.enums.CommandEnum;
import com.github.zedd7.zhorse.enums.KeyWordEnum;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {

	private ZHorse zh;
	private Map<String, Duration> commandCooldownMap = new HashMap<>();
	private Map<UUID, Map<String, Instant>> commandHistoryMap = new HashMap<>();

	public CommandManager(ZHorse zh) {
		this.zh = zh;
		zh.getCommand(zh.getDescription().getName().toLowerCase()).setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (a.length == 0) { // No command provided
			PluginDescriptionFile pluginDescription = zh.getDescription();
			String pluginNameAndVersion = String.format("%s %s", pluginDescription.getName(), pluginDescription.getVersion());
			String author = pluginDescription.getAuthors().get(0);
			String pluginHeader = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.PLUGIN_HEADER) {{ setPlayerName(author); setValue(pluginNameAndVersion); }}, true);
			LocaleEnum helpDescription = LocaleEnum.valueOf(CommandEnum.HELP.getName().toUpperCase() + KeyWordEnum.SEPARATOR.getValue() + KeyWordEnum.DESCRIPTION.getValue());
			zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HEADER_FORMAT) {{ setValue(pluginHeader); }}, true);
			zh.getMM().sendMessage(s, new MessageConfig(helpDescription) {{ setSpaceCount(1); }}, true);
		}
		else { // Instance of a command
			CommandEnum command = CommandEnum.getCommand(a[0]);
			if (command != null) {
				try {
					String commandClassPath = CommandEnum.getCommandClassPath(command.getName());
					Class.forName(commandClassPath).getConstructor(ZHorse.class, CommandSender.class, String[].class).newInstance(zh, s, a);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_COMMAND) {{ setValue(a[0].toLowerCase()); }});
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command c, String alias, String[] a) {
		List<String> tabSuggestionList = new ArrayList<>();
		if (a.length <= 1) { // No command provided or first argument is empty
			for (String command : CommandEnum.getNameList()) {
				String permission = KeyWordEnum.ZH_PREFIX.getValue() + command;
				if (zh.getPM().has(s, permission)) {
					tabSuggestionList.add(command);
				}
			}
		} else if (a.length == 2) { // Instance of a command and second argument is empty
			CommandEnum command = CommandEnum.getCommand(a[0]);
			if (command != null && command.isComplex()) {
				String subCommandEnumPath = CommandEnum.getSubCommandEnumPath(command.getName());
				try {
					Enum[] subCommands = (Enum[]) Class.forName(subCommandEnumPath).getEnumConstants();
					for (Enum subCommand : subCommands) {
						String permission = KeyWordEnum.ZH_PREFIX.getValue() + command + KeyWordEnum.DOT.getValue() + subCommand;
						if (zh.getPM().has(s, permission)) {
							tabSuggestionList.add(subCommand.name().toLowerCase());
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return tabSuggestionList;
	}

	public void loadCommandCooldowns() {
		for (String command : CommandEnum.getNameList()) {
			int cooldown = zh.getCM().getCommandCooldown(command);
			commandCooldownMap.put(command, Duration.ofSeconds(cooldown));
		}
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
