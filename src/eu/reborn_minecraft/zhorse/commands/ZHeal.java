package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZHeal extends Command {

	public ZHeal(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		idAllow = true;
		targetAllow = false;
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (!(idMode || targetMode)) {
							if (isOnHorse()) {
								horse = (Horse)p.getVehicle();
								if (isRegistered()) {
									execute();
								}
							}
						}
						else {
							if (idMode) {
								if (isRegistered(targetUUID, userID)) {
									horse = zh.getUM().getHorse(targetUUID, userID);
									if (isHorseLoaded()) {
										execute();
									}
								}
							}
							else if (displayConsole){
								sendCommandUsage();
							}
						}
					}
				}
			}
		}
	}

	private void execute() {
		if (isOwner()) {
			if (zh.getEM().canAffordCommand(p, command)) {
				Damageable dm = horse;
				dm.setHealth(dm.getMaxHealth());
				if (displayConsole) {
					s.sendMessage(zh.getMM().getMessageHorse(language, zh.getLM().horseHealed, horseName));
				}
				zh.getEM().payCommand(p, command);
			}
		}
	}

}