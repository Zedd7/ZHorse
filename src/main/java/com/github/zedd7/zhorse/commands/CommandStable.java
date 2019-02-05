package com.github.zedd7.zhorse.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.database.HorseStableRecord;
import com.github.zedd7.zhorse.enums.StableSubCommandEnum;
import com.github.zedd7.zhorse.enums.KeyWordEnum;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.CallbackListener;
import com.github.zedd7.zhorse.utils.CallbackResponse;
import com.github.zedd7.zhorse.utils.MessageConfig;

public class CommandStable extends AbstractCommand {

	private String fullCommand;
	private String subCommand;

	public CommandStable(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled()) {
			if (!idMode) {
				if (!targetMode) {
					boolean ownsHorse = ownsHorse(targetUUID, true);
					if (isOnHorse(ownsHorse)) {
						horse = (AbstractHorse) p.getVehicle();
						if (isRegistered(horse)) {
							execute();
						}
					}
					else if (ownsHorse) {
						horseID = zh.getDM().getPlayerFavoriteHorseID(p.getUniqueId(), true, null).toString();
						execute(p.getUniqueId(), horseID);
					}
				}
				else {
					sendCommandUsage();
				}
			}
			else {
				execute(targetUUID, horseID);
			}
		}
	}

	private void execute(UUID ownerUUID, String horseID) {
		if (isRegistered(ownerUUID, horseID)) {
			horse = zh.getHM().getHorse(ownerUUID, Integer.parseInt(horseID));
			if (isHorseLoaded(true)) {
				execute();
			}
		}
	}

	private void execute() {
		if (!args.isEmpty()) {
			subCommand = args.get(0);
			if (subCommand.equalsIgnoreCase(StableSubCommandEnum.GO.getName())) {
				fullCommand = command + KeyWordEnum.DOT.getValue() + StableSubCommandEnum.GO.getName();
				teleportToStable();
			}
			else if (subCommand.equalsIgnoreCase(StableSubCommandEnum.SET.getName())) {
				fullCommand = command + KeyWordEnum.DOT.getValue() + StableSubCommandEnum.SET.getName();
				setStableLocation();
			}
			else if (subCommand.equalsIgnoreCase(StableSubCommandEnum.UNSET.getName())) {
				fullCommand = command + KeyWordEnum.DOT.getValue() + StableSubCommandEnum.UNSET.getName();
				unsetStableLocation();
			}
			else {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.UNKNOWN_SUB_COMMAND) {{ setValue(subCommand); setValue(command); }});
				sendSubCommandDescriptionList(StableSubCommandEnum.class);
			}
		}
		else {
			sendSubCommandDescriptionList(StableSubCommandEnum.class);
		}
	}

	private void teleportToStable() {
		if (hasPermission(s, fullCommand , true, false)) {
			if (isOwner(true) && !isHorseMounted() && !isHorseLeashed()) {
				Location stableLocation = null;
				if (zh.getDM().isHorseStableRegistered(horse.getUniqueId(), true, null)) {
					stableLocation = zh.getDM().getHorseStableLocation(horse.getUniqueId(), true, null);
				}
				else if (zh.getCM().shouldUseDefaultStable()) {
					stableLocation = zh.getCM().getDefaultStableLocation();
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.STABLE_NOT_SET) {{ setHorseName(horseName); }});
				}
				if (stableLocation != null && isWorldCrossable(stableLocation.getWorld()) && isHorseInRangeStable(stableLocation)) {
					horse = zh.getHM().teleportHorse(horse, stableLocation, true);
					if (horse != null) {
						zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_TELEPORTED_TO_STABLE) {{ setHorseName(horseName); }});
						zh.getCmdM().updateCommandHistory(s, command);
						zh.getEM().payCommand(p, command);
					}
					else {
						zh.getMM().sendMessage(s, ChatColor.RED + "It seems that horses cannot spawn here, please report this to your server's staff.");
					}
				}
			}
		}
	}

	private void setStableLocation() {
		if (hasPermission(s, fullCommand , true, false)) {
			if (isOwner(true)) {
				CallbackListener<Boolean> removeHorseStableListener = new CallbackListener<Boolean>() {

					@Override
					public void callback(CallbackResponse<Boolean> response) {
						if (response.getResult()) {
							Location playerLocation = getGroundedLocation(p.getLocation());
							HorseStableRecord stableRecord = new HorseStableRecord(horse.getUniqueId().toString(), playerLocation);
							zh.getDM().registerHorseStable(stableRecord, false, new CallbackListener<Boolean>() {

								@Override
								public void callback(CallbackResponse<Boolean> response) {
									if (response.getResult()) {
										zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.STABLE_SET) {{ setHorseName(horseName); }});
										zh.getCmdM().updateCommandHistory(s, command);
										zh.getEM().payCommand(p, command);
									}
								}

							});
						}
					}

				};
				if (zh.getDM().isHorseStableRegistered(horse.getUniqueId(), true, null)) {
					zh.getDM().removeHorseStable(horse.getUniqueId(), false, removeHorseStableListener);
				}
				else {
					removeHorseStableListener.callback(new CallbackResponse<>(true));
				}
			}
		}
	}

	private void unsetStableLocation() {
		if (hasPermission(s, fullCommand , true, false)) {
			if (isOwner(true)) {
				if (zh.getDM().isHorseStableRegistered(horse.getUniqueId(), true, null)) {
					zh.getDM().removeHorseStable(horse.getUniqueId(), false, new CallbackListener<Boolean>() {

						@Override
						public void callback(CallbackResponse<Boolean> response) {
							if (response.getResult()) {
								zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.STABLE_UNSET) {{ setHorseName(horseName); }});
								zh.getCmdM().updateCommandHistory(s, command);
								zh.getEM().payCommand(p, command);
							}
						}

					});
				}
				else {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.STABLE_NOT_SET) {{ setHorseName(horseName); }});
				}
			}
		}
	}

}