package eu.reborn_minecraft.zhorse.managers;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.commands.ZClaim;
import eu.reborn_minecraft.zhorse.enums.CommandEnum;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;
import eu.reborn_minecraft.zhorse.utils.AsyncChunckLoad;
import eu.reborn_minecraft.zhorse.utils.AsyncChunckUnload;
import eu.reborn_minecraft.zhorse.utils.AsyncPlayerJoin;

public class EventManager implements Listener {
	
	private static final int NONE = -1;
	private static final int MAIN_HAND = 0;
	private static final int OFF_HAND = 1;
	private static final String OWNER_ATTACK = "OWNER_ATTACK";
	private static final String PLAYER_ATTACK = "PLAYER_ATTACK";
	
	private ZHorse zh;
	private boolean displayConsole;

	public EventManager(ZHorse zh) {
		this.zh = zh;
		this.displayConsole = !(zh.getCM().isConsoleMuted());
		zh.getServer().getPluginManager().registerEvents(this, zh);
	}
	
	/* Allows ZHorse to cancel onPlayerLeashEntityEvent */
	private class PlayerLeashDeadEntityEvent extends PlayerLeashEntityEvent {
		public PlayerLeashDeadEntityEvent(Entity leashedEntity, Entity leashHolder, Player p) {
			super(leashedEntity, leashHolder, p);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkLoad(ChunkLoadEvent e) {
		new AsyncChunckLoad(zh, e.getChunk());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkUnload(ChunkUnloadEvent e) {
		new AsyncChunckUnload(zh, e.getChunk());
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Horse) {
			Horse horse = (Horse) e.getEntity();
			if (zh.getUM().isRegistered(horse)) {
				if (zh.getUM().isProtected(horse)) {
					DamageCause damageCause = e.getCause();
					
					/* if the damage is not already handled by onEntityDamageByEntity */
					if (!(damageCause.equals(DamageCause.ENTITY_ATTACK)
							|| damageCause.equals(DamageCause.ENTITY_EXPLOSION)
							|| damageCause.equals(DamageCause.PROJECTILE)
							|| damageCause.equals(DamageCause.MAGIC))) {
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
			Horse horse = (Horse) e.getEntity();
			if (zh.getUM().isRegistered(horse)) {
				if (zh.getUM().isProtected(horse)) {
					if (e.getDamager() instanceof Player) {
						Player p = (Player) e.getDamager();
						e.setCancelled(!isPlayerAllowedToAttack(p, horse));
					}
					else if ((e.getDamager() instanceof Projectile) && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
						Player p = (Player)((Projectile) e.getDamager()).getShooter();
						e.setCancelled(!isPlayerAllowedToAttack(p, horse));
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
			Horse horse = (Horse) e.getEntity();
			if (zh.getUM().isRegistered(horse)) {
				UUID ownerUUID = zh.getUM().getPlayerUUID(horse);
				for (Player p : zh.getServer().getOnlinePlayers()) {
					if (p.getUniqueId().equals(ownerUUID)) {
						if (displayConsole) {
							String horseName = zh.getUM().getHorseName(ownerUUID, horse);
							zh.getMM().sendMessageHorse((CommandSender)p, LocaleEnum.horseDied, horseName);
						}
					}
				}
				zh.getUM().unRegisterHorse(horse);
			}
		}
	}
	
	@EventHandler
	public void onEntityTame(EntityTameEvent e) {
		if (e.getOwner() instanceof Player && e.getEntity() instanceof Horse) {
			if (zh.getCM().shouldClaimOnTame()) {
				((Horse) e.getEntity()).setTamed(true);
				String[] a = {CommandEnum.CLAIM.getName()};
				new ZClaim(zh, (CommandSender) e.getOwner(), a);
			}
			else if (zh.getPM().has((Player) e.getOwner(), KeyWordEnum.zhPrefix.getValue() + CommandEnum.CLAIM.getName())) {
				if (displayConsole) {
					zh.getMM().sendMessage((CommandSender) e.getOwner(), LocaleEnum.horseManuallyTamed);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityPortal(EntityPortalEvent e) {
		if (e.getEntity() instanceof Horse) {
			Horse horse = (Horse) e.getEntity();
			if (zh.getUM().isRegistered(horse)) {
				e.setCancelled(true);
				if (zh.getCM().isWorldEnabled(e.getTo().getWorld())) {
					zh.getHM().teleport(horse, e.getTo());
				}
			}
		}
	}
	
	@EventHandler
	public void onHangingBreak(HangingBreakEvent e) {
		RemoveCause removeCause = e.getCause();
		
		/* if the remove cause is not already handled by onHangingBreakByEntity */
		if (!removeCause.equals(RemoveCause.ENTITY)) {
			if (e.getEntity() instanceof LeashHitch) {
				LeashHitch leashHitch = (LeashHitch) e.getEntity();
				for (Horse horse : zh.getHM().getLoadedHorses().values()) {
					if (horse.isLeashed()) {
						Entity leashHolder = horse.getLeashHolder();
						if (leashHitch.equals(leashHolder)) {
							e.setCancelled(zh.getUM().isLocked(horse));
							break;
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent e) {
		if (e.getEntity() instanceof LeashHitch) {
			LeashHitch leashHitch = (LeashHitch) e.getEntity();
			for (Horse horse : zh.getHM().getLoadedHorses().values()) {
				if (horse.isLeashed()) {
					Entity leashHolder = horse.getLeashHolder();
					if (leashHitch.equals(leashHolder)) {
						if (e.getRemover() instanceof Player) {
							e.setCancelled(!isPlayerAllowedToInteract((Player) e.getRemover(), horse, false));
							break;
						}
						else {
							e.setCancelled(zh.getUM().isLocked(horse));
						}
					}
				}
			}
		}
	}	
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player && e.getInventory().getHolder() instanceof Horse) {
			e.setCancelled(!isPlayerAllowedToInteract((Player) e.getWhoClicked(), (Horse) e.getInventory().getHolder(), true));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Horse) {
			Horse horse = (Horse) e.getRightClicked();
			if (horse.getVariant() == Variant.SKELETON_HORSE || horse.getVariant() == Variant.UNDEAD_HORSE) {
				Player p = e.getPlayer();
				if (!horse.isLeashed() && zh.getCM().isLeashOnDeadHorseAllowed()) {
					int holdingHand = getHoldingHand(p, new ItemStack(Material.LEASH));
					if (holdingHand == MAIN_HAND || holdingHand == OFF_HAND) { // if player is holding leash
						cancelEvent(e, p, true, true);
						PlayerLeashDeadEntityEvent ev = new PlayerLeashDeadEntityEvent(horse, p, p);
						zh.getServer().getPluginManager().callEvent(ev);
						if (!ev.isCancelled()) {
							consumeItem(p, holdingHand);
							horse.setLeashHolder(p);
						}
					}
				}					
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		new AsyncPlayerJoin(zh, e);
	}
	
	@EventHandler
	public void onPlayerLeashEntity(PlayerLeashEntityEvent e) {
		if (e.getLeashHolder() instanceof Player && e.getEntity() instanceof Horse) {
			Player p = (Player) e.getPlayer();
			ItemStack item = getItem(p, getHoldingHand(p, new ItemStack(Material.LEASH)));
			int savedAmount = item.getAmount();
			e.setCancelled(!isPlayerAllowedToInteract(p, (Horse) e.getEntity(), false));
			if (e.isCancelled()) {
				item.setAmount(savedAmount);
				updateInventory(p);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		ejectPlayer(e.getPlayer());
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		ejectPlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerUnleashEntity(PlayerUnleashEntityEvent e) {
		if (e.getEntity() instanceof Horse) {
			e.setCancelled(!isPlayerAllowedToInteract(e.getPlayer(), (Horse) e.getEntity(), false));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onVehicleEnter(VehicleEnterEvent e) {
		if (e.getEntered() instanceof Player && e.getVehicle() instanceof Horse) {
			Player p = (Player) e.getEntered();
			boolean cancel = !isPlayerAllowedToInteract(p, (Horse) e.getVehicle(), false);
			cancelEvent(e, p, cancel, true);
		}
	}
	
	private void cancelEvent(Cancellable e, Player p, boolean cancel, boolean restoreLocation) {
		Location savedLocation = p.getLocation();
		e.setCancelled(cancel);
		if (cancel && restoreLocation) {
			p.teleport(savedLocation);
		}
	}
	
	private void consumeItem(Player p, int holdingHand) {
		if (p.getGameMode() != GameMode.CREATIVE) {
			ItemStack item = getItem(p, holdingHand);
			if (item != null) {
				int amount = item.getAmount();
				if (amount > 1) {
					item.setAmount(amount - 1);
				}
				else {
					p.getInventory().remove(item);
				}
				updateInventory(p);
			}
		}
	}
	
	private void ejectPlayer(Player p) {
		if (p.getVehicle() instanceof Horse) {
			Horse horse = (Horse) p.getVehicle();
			if (zh.getUM().isRegistered(horse)) {
				horse.eject();
			}
		}
	}
	
	private int getHoldingHand(Player p, ItemStack item) {
		ItemStack mainHandItem = p.getInventory().getItemInMainHand();
		ItemStack offHandItem = p.getInventory().getItemInOffHand();
		if (mainHandItem != null && mainHandItem.isSimilar(item)) {
			return MAIN_HAND;
		}
		else if (offHandItem != null && offHandItem.isSimilar(item)) {
			return OFF_HAND;
		}
		return NONE;
	}
	
	private ItemStack getItem(Player p, int holdingHand) {
		ItemStack item = null;
		switch (holdingHand) {
		case MAIN_HAND:
			item = p.getInventory().getItemInMainHand();
			break;
		case OFF_HAND:
			item = p.getInventory().getItemInOffHand();
			break;
		}
		return item;
	}
	
	private boolean isPlayerAllowedToAttack(Player p, Horse horse) {
		boolean allowed = true;
		if (zh.getCM().isProtectionEnabled(PLAYER_ATTACK)) {
			boolean isOwner = zh.getUM().isClaimedBy(p.getUniqueId(), horse);
			boolean isOwnerAttackBlocked = zh.getCM().isProtectionEnabled(OWNER_ATTACK);
			boolean hasAdminPerm = zh.getPM().has(p, KeyWordEnum.zhPrefix.getValue() + CommandEnum.PROTECT.getName() + KeyWordEnum.adminSuffix.getValue());
			if (!((isOwner && !isOwnerAttackBlocked) ||	hasAdminPerm)) {
				if (displayConsole) {
					String horseName = zh.getUM().getHorseName(horse);
					zh.getMM().sendMessageHorse((CommandSender)p, LocaleEnum.horseIsProtected, horseName);
				}
				allowed = false;
			}
		}
		return allowed;
	}
	
	private boolean isPlayerAllowedToInteract(Player p, Horse horse, boolean mustBeShared) {
		if (zh.getUM().isRegistered(horse)) {
			boolean isClaimedBy = zh.getUM().isClaimedBy(p.getUniqueId(), horse);
			boolean hasPerm = zh.getPM().has(p, KeyWordEnum.zhPrefix.getValue() + CommandEnum.LOCK.getName() + KeyWordEnum.adminSuffix.getValue());
			if (!(isClaimedBy || hasPerm)) {
				if (zh.getUM().isLocked(horse) || (!zh.getUM().isShared(horse) && (!horse.isEmpty() || mustBeShared))) {
					if (displayConsole) {
						String ownerName = zh.getUM().getPlayerName(horse);
						zh.getMM().sendMessagePlayer((CommandSender)p, LocaleEnum.horseBelongsTo, ownerName);
					}
					return false;
				}
			}
		}
		return true;
	}
	
	private void updateInventory(Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				p.updateInventory();
			}
		}.runTaskLater(zh, 0);
	}
}
