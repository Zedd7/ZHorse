package eu.reborn_minecraft.zhorse.commands;

import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZHere {

	public ZHere(CommandSender s, String[] a, ZHorse zh) {
		if (s instanceof Player) {
			Player p = (Player) s;
			if (zh.getCM().isWorldEnabled(p.getWorld())) {
				String perm = "zh." + a[0];
				if(zh.getPerms().has(p, perm)) {
					if (zh.getEM().isReadyToPay(p, a[0])) {
						if (a.length == 2) {
							String userID = a[1];
							UUID playerUUID = p.getUniqueId();
							if (zh.getUM().isRegistered(playerUUID, userID)) {
								Chunk chunk = null;
								if (!zh.getUM().isSpawned(playerUUID, userID)) {
									Location location = zh.getUM().getLocation(playerUUID, userID);
									chunk = location.getWorld().getChunkAt(location);
									if (!chunk.isLoaded()) {
										chunk.load();
									}
									else {
										chunk = null;
									}
								}
								Horse horse = zh.getUM().getHorse(playerUUID, userID);
								String horseName = zh.getUM().getHorseName(playerUUID, userID);
								if (horse != null) {
									if (p.getWorld().equals(horse.getLocation().getWorld()) || zh.getPerms().has(p, perm + zh.getLM().multiworld)) {
										if (p.getVehicle() != horse) {
											Entity passenger = horse.getPassenger();
											if (passenger == null) {
												horse.teleport(p);
												p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseTeleported), horseName));
												zh.getEM().payCommand(p, a[0]);
											}
											else {
												String passengerName = ((Player)passenger).getName();
												p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseMountedBy), horseName, passengerName));
											}
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
								if (chunk != null) {
									chunk.unload();
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
										Chunk chunk = null;
										if (!zh.getUM().isSpawned(targetUUID, userID)) {
											Location location = zh.getUM().getLocation(targetUUID, userID);
											chunk = location.getWorld().getChunkAt(location);
											if (!chunk.isLoaded()) {
												chunk.load();
											}
											else {
												chunk = null;
											}
										}
										Horse horse = zh.getUM().getHorse(targetUUID, userID);
										String horseName = zh.getUM().getHorseName(targetUUID, userID);
										if (horse != null) {
											if (p.getWorld().equals(horse.getLocation().getWorld()) || zh.getPerms().has(p, perm + zh.getLM().multiworld)) {
												if (p.getVehicle() != horse) {
													Entity passenger = horse.getPassenger();
													if (passenger == null) {
														horse.teleport(p);
														p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseTeleported), horseName));
														zh.getEM().payCommand(p, a[0]);
													}
													else {
														String passengerName = ((Player)passenger).getName();
														p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseMountedBy), horseName, passengerName));
													}
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
										if (chunk != null) {
											chunk.unload();
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