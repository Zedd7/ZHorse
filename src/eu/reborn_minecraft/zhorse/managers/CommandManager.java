package eu.reborn_minecraft.zhorse.managers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.CommandEnum;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class CommandManager implements CommandExecutor {
	private ZHorse zh;
	
	public CommandManager(ZHorse zh) {
		this.zh = zh;
		zh.getCommand(zh.getDescription().getName().toLowerCase()).setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (a.length == 0) {
			PluginDescriptionFile pluginDescription = zh.getDescription();
			String pluginNameAndVersion = String.format("%s %s", pluginDescription.getName(), pluginDescription.getVersion());
			String author = pluginDescription.getAuthors().get(0);
			String pluginHeader = zh.getMM().getMessagePlayerValue(s, LocaleEnum.pluginHeader, author, pluginNameAndVersion, true);			
			LocaleEnum helpDescription = LocaleEnum.valueOf(CommandEnum.help.getName() + KeyWordEnum.description.getValue());
			zh.getMM().sendMessageValue(s, LocaleEnum.headerFormat, pluginHeader, true);
			zh.getMM().sendMessageSpacer(s, helpDescription, 1, true);
		}
		else {
			String command = a[0].toLowerCase();
			boolean commandValid = false;
			for (CommandEnum commandEnum : CommandEnum.values()) {
				if (command.equals(commandEnum.getName())) {
					commandValid = true;
					try {
						Class.forName(commandEnum.getClassPath()).getConstructor(ZHorse.class, CommandSender.class, String[].class).newInstance(new Object[] {zh, s, a});
					} catch (Exception  e) {
						e.printStackTrace();
					}
					break;
				}
			}
			if (!commandValid && !zh.getCM().isConsoleMuted()) {
				zh.getMM().sendMessageValue(s, LocaleEnum.unknownCommand, command);
			}
		}
		return true;
	}
	
}
