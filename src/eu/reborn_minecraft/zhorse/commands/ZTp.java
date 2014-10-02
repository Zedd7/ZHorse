package eu.reborn_minecraft.zhorse.commands;

import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZTp {

	public ZTp(CommandSender s, String[] a, ZHorse zh) {
		if (s instanceof Player) {
			Player p = (Player) s;
			if (zh.getCM().isWorldEnabled(p.getWorld())) {
				String perm = "zh." + a[0];
				if(zh.getPerms().has(p, perm)) {
					if (zh.getEM().isReadyToPay(p, a[0])) {
						if (a.length == 2) {
							UUID playerUUID = p.getUniqueId();
							String userID = a[1];
							if (zh.getUM().isRegistered(playerUUID, userID)) {
								if (!zh.getUM().isSpawned(playerUUID, userID)) {
									Location location = zh.getUM().getLocation(playerUUID, userID);
									Chunk chunk = location.getWorld().getChunkAt(location);
									if (!chunk.isLoaded()) {
										chunk.load();
									}
								}
								Horse horse = zh.getUM().getHorse(playerUUID, userID);
								String horseName = zh.getUM().getHorseName(playerUUID, userID);
								if (horse != null) {
									if (p.getWorld().equals(horse.getLocation().getWorld()) || zh.getPerms().has(p, perm + zh.getLM().multiworld)) {
										if (p.getVehicle() != horse) {
											p.teleport(horse);
											p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().teleportedToHorse), horseName));
											zh.getEM().payCommand(p, a[0]);
										}
										else {
											p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseMounted), horseName));
										}
									}
									else {
										p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().differentWorld), horseName));
									}
								}
								else {
									p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseNotFound), horseName));
								}
							}
							else {
								p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().unknownHorseId), userID));
							}
						}
						else if (a.length == 3) {
							if(zh.getPerms().has(p, perm + zh.getLM().others)) {
								String targetName = a[1];
								String userID = a[2];
								UUID targetUUID = getPlayerUUID(zh, targetName);
								if (targetUUID != null) {
									if (zh.getUM().isRegistered(targetUUID, userID)) {
										if (!zh.getUM().isSpawned(targetUUID, userID)) {
											Location location = zh.getUM().getLocation(targetUUID, userID);
											Chunk chunk = location.getWorld().getChunkAt(location);
											if (!chunk.isLoaded()) {
												chunk.load();
											}
										}
										Horse horse = zh.getUM().getHorse(targetUUID, userID);
										String horseName = zh.getUM().getHorseName(targetUUID, userID);
										if (horse != null) {
											if (p.getWorld().equals(horse.getLocation().getWorld()) || zh.getPerms().has(p, perm + ".multiworld")) {
												if (p.getVehicle() != horse) {
													p.teleport(horse);
													p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().teleportedToHorse), horseName));
												}
												else {
													p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseMounted), horseName));
												}
											}
											else {
												p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().differentWorld), horseName));
											}
										}
										else {
											p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseNotFound), horseName));
										}
									}
									else {
										p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().unknownHorseIdOther), targetName, userID));
									}
								}
								else {
									p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().unknownPlayer), targetName));
								}
							}
							else {
								p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().missingPermission), perm + zh.getLM().others));
							}							
						}
						else {
							p.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().commandIncorrect));
						}
					}
				}
				else {
					p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().missingPermission), perm));
				}
			}
			else {
				p.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().worldDisabled));
			}
		}
		else {
			s.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().playerCommand));
		}
	}
	
	@SuppressWarnings("deprecation")
	private UUID getPlayerUUID(ZHorse zh, String playerName) {
		if (zh.getUM().isRegistered(playerName)) {
			return zh.getUM().getPlayerUUID(playerName);
		}
		else {
			if (zh.getServer().getOfflinePlayer(playerName).hasPlayedBefore()) {
				return zh.getServer().getOfflinePlayer(playerName).getUniqueId();
			}
			return null;
		}
	}

}
