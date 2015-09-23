package eu.reborn_minecraft.zhorse.managers;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.commands.ZClaim;

public class EventManager implements Listener {
	private ZHorse zh;
	private boolean displayConsole;
	private static String CLAIM = "claim";
	private static String OWNER_ATTACK = "OWNER_ATTACK";
	private static String PLAYER_ATTACK = "PLAYER_ATTACK";

	public EventManager(ZHorse zh) {
		this.zh = zh;
		this.displayConsole = !(zh.getCM().isConsoleMuted());
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		for (Entity entity : e.getChunk().getEntities()) {
			if (entity instanceof Horse) {
				Horse horse = (Horse)entity;
				if (zh.getUM().isRegistered(horse)) {
					zh.getUM().saveLocation(horse);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Horse) {
			Horse horse = (Horse)e.getEntity();
			if (zh.getUM().isRegistered(horse)) {
				if (zh.getUM().isProtected(horse)) {
					DamageCause damageCause = e.getCause();
					if (!(damageCause == DamageCause.ENTITY_ATTACK ||
						damageCause == DamageCause.ENTITY_EXPLOSION ||
						damageCause == DamageCause.PROJECTILE ||
						damageCause == DamageCause.MAGIC)) {
						if (zh.getCM().isProtectionEnabled(damageCause.name())) {
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Horse) {
			Horse horse = (Horse)e.getEntity();
			if (zh.getUM().isRegistered(horse)) {
				if (zh.getUM().isProtected(horse)) {
					if (e.getDamager() instanceof Player) {
						Player p = (Player)e.getDamager();
						e.setCancelled(!handlePlayerAttackHorse(p, horse));
					}
					else if ((e.getDamager() instanceof Projectile) && ((Projectile)e.getDamager()).getShooter() instanceof Player) {
						Player p = (Player)((Projectile)e.getDamager()).getShooter();
						e.setCancelled(!handlePlayerAttackHorse(p, horse));
					}
					else {
						e.setCancelled(zh.getCM().isProtectionEnabled(e.getCause().name()));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Horse) {
			Horse horse = (Horse)e.getEntity();
			if (zh.getUM().isRegistered(horse)) {
				UUID ownerUUID = zh.getUM().getPlayerUUID(horse);
				for (Player p : zh.getServer().getOnlinePlayers()) {
					if (p.getUniqueId().equals(ownerUUID)) {
						if (displayConsole) {
							String horseName = zh.getUM().getHorseName(ownerUUID, horse);
							zh.getMM().sendMessageHorse((CommandSender)p, zh.getLM().horseDied, horseName);
						}
					}
				}
				zh.getUM().remove(horse);
			}
		}
	}
	
	@EventHandler
	public void onEntityTame(EntityTameEvent e) {
		if (e.getOwner() instanceof Player && e.getEntity() instanceof Horse) {
			if (zh.getCM().shouldClaimOnTame()) {
				((Horse)e.getEntity()).setTamed(true);
				String[] a = {CLAIM};
				new ZClaim(zh, (CommandSender)e.getOwner(), a);
			}
			else if (zh.getPerms().has((Player)e.getOwner(), zh.getLM().zhPrefix + zh.getLM().claim)) {
				if (displayConsole) {
					zh.getMM().sendMessage((CommandSender)e.getOwner(), zh.getLM().horseManuallyTamed);
				}
			}
		}
	}
	
//	@EventHandler
//	public void onHangingBreak(HangingBreakEvent e) { // e.getEntity est une instance de LeashHitch
//		if (e.getEntity().getLeashedEntity() instanceof Horse) { // en attente d'implémentation pour getLeashedEntity()
//			Horse horse = (Horse)e.getEntity().getLeashedEntity();
//			if (zh.getUM().isRegistered(horse)) {
//				if (zh.getUM().isLocked(horse)) {
//					e.setCancelled(true);
//				}
//			}
//		}
//	}

//	@EventHandler
//	public void onHangingBreakByEntity(HangingBreakByEntityEvent e) { // e.getEntity est une instance de LeashHitch
//		if (e.getRemover() instanceof Player && e.getEntity().getLeashedEntity() instanceof Horse) { // en attente d'implémentation pour getLeashedEntity()
//			e.setCancelled(handlePlayerInteractHorse((Player)e.getRemover(), (Horse)e.getEntity(), false));
//		}
//	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player && e.getInventory().getHolder() instanceof Horse) {
			e.setCancelled(!handlePlayerInteractHorse((Player)e.getWhoClicked(), (Horse)e.getInventory().getHolder(), true));
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!zh.getUM().isRegistered(p.getUniqueId())) {
			zh.getUM().registerPlayer(p.getUniqueId());
		}
		else {
			if (!p.getName().equalsIgnoreCase(zh.getUM().getPlayerName(p.getUniqueId()))) {
				zh.getUM().updatePlayer(p);
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeashEntity(PlayerLeashEntityEvent e) {
		if (e.getLeashHolder() instanceof Player && e.getEntity() instanceof Horse) {
			e.setCancelled(!handlePlayerInteractHorse((Player)e.getPlayer(), (Horse)e.getEntity(), false));
		}
	}
	
	@EventHandler
	public void onPlayerUnleashEntity(PlayerUnleashEntityEvent e) {
		if (e.getEntity() instanceof Horse) {
			e.setCancelled(!handlePlayerInteractHorse(e.getPlayer(), (Horse)e.getEntity(), false));
		}
	}
	
	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent e) {
		if (e.getEntered() instanceof Player && e.getVehicle() instanceof Horse) {
			e.setCancelled(!handlePlayerInteractHorse((Player)e.getEntered(), (Horse)e.getVehicle(), false));
		}
	}
	
	private boolean handlePlayerAttackHorse(Player p, Horse horse) {
		boolean allowed = true;
		if (zh.getCM().isProtectionEnabled(PLAYER_ATTACK)) {
			if (!((zh.getUM().isClaimedBy(p.getUniqueId(), horse) && !zh.getCM().isProtectionEnabled(OWNER_ATTACK)) ||
					zh.getPerms().has(p, zh.getLM().zhPrefix + zh.getLM().protect + zh.getLM().adminSuffix))) {
				if (displayConsole) {
					String horseName = zh.getUM().getHorseName(horse);
					zh.getMM().sendMessageHorse((CommandSender)p, zh.getLM().horseIsProtected, horseName);
				}
				allowed = false;
			}
		}
		return allowed;
	}
	
	private boolean handlePlayerInteractHorse(Player p, Horse horse, boolean mustBeShared) {
		boolean allowed = true;
		if (zh.getUM().isRegistered(horse)) {
			if (!(zh.getUM().isClaimedBy(p.getUniqueId(), horse) || zh.getPerms().has(p, zh.getLM().zhPrefix + zh.getLM().lock + zh.getLM().adminSuffix))) {
				if (zh.getUM().isLocked(horse) || (!zh.getUM().isShared(horse) && (!horse.isEmpty() || mustBeShared))) {
					if (displayConsole) {
						String ownerName = zh.getUM().getPlayerName(horse);
						zh.getMM().sendMessagePlayer((CommandSender)p, zh.getLM().horseBelongsTo, ownerName);
					}
					allowed = false;
				}
			}
		}
		return allowed;
	}
}
