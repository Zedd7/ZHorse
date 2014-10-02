package eu.reborn_minecraft.zhorse.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZList {

	public ZList(CommandSender s, String[] a, ZHorse zh) {
		if (s instanceof Player) {
			Player p = (Player) s;
			String perm = "zh." + a[0];
			if (zh.getPerms().has(p, perm)) {
				if (zh.getEM().isReadyToPay(p, a[0])) {
					if (a.length == 1) {
						if (displayHorseList(zh, p, p.getUniqueId(), true)) {
							zh.getEM().payCommand(p, a[0]);
						}
					}
					else if (a.length == 2) {
						if (zh.getPerms().has(p, perm + ".others")) {
							String ownerName = a[1];
							UUID ownerUUID = getPlayerUUID(zh, ownerName);
							if (ownerUUID != null) {
								boolean samePlayer = (p.getUniqueId().equals(ownerUUID));
								if (displayHorseList(zh, p, ownerUUID, samePlayer)) {
									zh.getEM().payCommand(p, a[0]);
								}
							}
							else {
								p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().unknownPlayer), ownerName));
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
	
	private boolean displayHorseList(ZHorse zh, Player p, UUID ownerUUID, boolean samePlayer) {
		String ownerName = zh.getUM().getPlayerName(ownerUUID);
		List<String> horseList = zh.getUM().getHorseList(ownerUUID);
		String remainingClaims = getRemainingClaimsMessage(zh, ownerUUID, horseList, samePlayer);
		if (horseList.isEmpty()) {
			if (samePlayer) {
				p.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().noHorseOwned) + remainingClaims);
			}
			else {
				p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().noHorseOwnedOther), ownerName) + remainingClaims);
			}
		}
		else {
			String horseListHeader;
			if (samePlayer) {
				horseListHeader = zh.getLM().getHeaderMessage(zh.getLM().horseListHeader);
				
			}
			else {
				horseListHeader = String.format(zh.getLM().getHeaderMessage(zh.getLM().horseListOtherHeader), ownerName);
			}
			p.sendMessage(String.format(zh.getLM().getHeaderMessage(zh.getLM().headerFormat), horseListHeader + remainingClaims));
			for (int i=1; i<=horseList.size(); i++) {
				String userId = Integer.toString(i);
				String horseName = horseList.get(i-1);
				String message = " " + String.format(zh.getLM().getCommandAnswer(zh.getLM().horseListFormat, true), userId, horseName);
				if (zh.getUM().isProtected(ownerUUID, userId)) {
					message += ChatColor.GREEN + " [" + ChatColor.YELLOW + "Pr" + ChatColor.GREEN + "]";
				}
				if (zh.getUM().isLocked(ownerUUID, userId)) {
					message += ChatColor.GREEN + " [" + ChatColor.YELLOW + "Lo" + ChatColor.GREEN + "]";
				}
				if (zh.getUM().isShared(ownerUUID, userId)) {
					message += ChatColor.GREEN + " [" + ChatColor.YELLOW + "Sh" + ChatColor.GREEN + "]";
				}
				p.sendMessage(message);
			}
			return true;
		}
		return false;
	}
	
	private String getRemainingClaimsMessage(ZHorse zh, UUID ownerUUID, List<String> horseList, boolean samePlayer) {
		String message = "";
		if (samePlayer || isOwnerOnline(zh, ownerUUID)) {
			int maxClaims = zh.getCM().getMaximumClaims(zh.getServer().getPlayer(ownerUUID));
			message = " " + String.format(zh.getLM().getCommandAnswer(zh.getLM().remainingClaims, true), horseList.size(), maxClaims);
		}
		return message;
	}
	
	private boolean isOwnerOnline(ZHorse zh, UUID ownerUUID) {
		Player[] players = zh.getServer().getOnlinePlayers();
		for (Player p : players) {
			if (p.getUniqueId().equals(ownerUUID)) {
				return true;
			}
		}
		return false;
	}
}
