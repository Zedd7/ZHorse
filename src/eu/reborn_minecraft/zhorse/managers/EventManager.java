package eu.reborn_minecraft.zhorse.managers;

import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import eu.reborn_minecraft.zhorse.ZHorse;

public class EventManager implements Listener {
	private ZHorse zh;
	private boolean displayConsole;
	private Chunk lastChunk = null;

	public EventManager(ZHorse zh) {
		this.zh = zh;
		this.displayConsole = !(zh.getCM().isConsoleMuted());
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		Chunk chunk = e.getChunk();
		if (lastChunk == null || !lastChunk.equals(chunk)) {
			lastChunk = chunk;
			e.setCancelled(true);
			Entity[] entities = chunk.getEntities();
			for (Entity entity : entities) {
				if (isHorseClaimed(entity)) {
					zh.getUM().saveLocation((Horse) entity);
				}
			}
			chunk.unload(true, true);
		}
	}
	
	@EventHandler
	public void onDamageHorse(EntityDamageEvent e) {
		Entity entityHorse = e.getEntity();
		if (isHorseClaimed(entityHorse)) {
			Horse horse = (Horse)entityHorse;
			if (zh.getUM().isProtected(horse)) {
				DamageCause damageCause = e.getCause();
				boolean cancel = false;
				if (damageCause.equals(DamageCause.MAGIC)) {
					cancel = true;
				}
				else if (damageCause.equals(DamageCause.POISON)) {
					cancel = true;
				}
				else if (damageCause.equals(DamageCause.PROJECTILE)) {
					cancel = true;
				}
				else if (damageCause.equals(DamageCause.THORNS)) {
					cancel = true;
				}
				e.setCancelled(cancel);
			}
		}
	}
	
	@EventHandler
	public void onHorseDeath(EntityDeathEvent e) {
		Entity entity = e.getEntity();
		if (isHorseClaimed(entity)) {
			Horse horse = (Horse)entity;
			UUID ownerUUID = zh.getUM().getPlayerUUID(horse);
			for (Player p : zh.getServer().getOnlinePlayers()) {
				if (p.getUniqueId().equals(ownerUUID)) {
					if (displayConsole) {
						String horseName = zh.getUM().getHorseName(ownerUUID, horse);
						p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseDied), horseName));
					}
				}
			}
			zh.getUM().remove(horse);
		}
	}
	
	@EventHandler
	public void onHorseTame(EntityTameEvent e) {
		if (e.getEntity() instanceof Horse && e.getOwner() instanceof Player) {
			if (displayConsole) {
				Player p = (Player)e.getOwner();
				p.sendMessage(zh.getLM().getCommandAnswer(zh.getLM().horseManuallyTamed));
			}
		}
	}
	
	@EventHandler
	public void onPlayerClickHorse(PlayerInteractEntityEvent e) {
		e.setCancelled(!canPlayerInteractHorse(e.getPlayer(), e.getRightClicked()));
	}
	
	@EventHandler
	public void onPlayerDamageHorse(EntityDamageByEntityEvent e) {
		if (isHorseClaimed(e.getEntity())) {
			Horse horse = (Horse)e.getEntity();
			if (zh.getUM().isProtected(horse)) {
				if (e.getDamager() instanceof Player) {
					Player p = (Player)e.getDamager();
					if (!(zh.getUM().isClaimedBy(p.getUniqueId(), horse) || zh.getPerms().has(p, "zh."+ zh.getLM().protect + zh.getLM().admin))) {
						if (displayConsole) {
							String horseName = zh.getUM().getHorseName(horse);
							p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseIsProtected), horseName));
						}
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!zh.getUM().isRegistered(p.getUniqueId())) {
			if (!zh.getUM().registerPlayer(p.getUniqueId())) {
				zh.getLogger().severe(String.format(zh.getLM().getCommandAnswer(zh.getLM().playerNotRegistered), p.getName() + " " + p.getUniqueId().toString()));
			}
		}
		else {
			if (!p.getName().equalsIgnoreCase(zh.getUM().getPlayerName(p.getUniqueId()))) {
				zh.getUM().updatePlayer(p);
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeashHorse(PlayerLeashEntityEvent e) {
		e.setCancelled(!canPlayerInteractHorse(e.getPlayer(), e.getEntity()));
	}
	
	@EventHandler
	public void onPlayerOpenIntentory(InventoryOpenEvent e) {
		Player p = (Player)e.getPlayer();
		if (p.isInsideVehicle()) {
			if (isHorseClaimed(p.getVehicle())) {
				Horse horse = (Horse)p.getVehicle();
				if (!((zh.getUM().isClaimedBy(p.getUniqueId(), horse)) || zh.getUM().isShared(horse) || zh.getPerms().has(p, "zh." + zh.getLM().lock + zh.getLM().admin))) {
					if (displayConsole) {
						String ownerName = zh.getUM().getPlayerName(horse);
						p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseBelongsTo), ownerName));
					}
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerUnleashHorse(PlayerUnleashEntityEvent e) {
		e.setCancelled(!canPlayerInteractHorse(e.getPlayer(), e.getEntity()));
	}
	
	private boolean canPlayerInteractHorse(Player p, Entity entity) {
		if (isHorseClaimed(entity)) {
			Horse horse = (Horse)entity;
			if (!(zh.getUM().isClaimedBy(p.getUniqueId(), horse) || zh.getPerms().has(p, "zh." + zh.getLM().lock + zh.getLM().admin))) {
				if (zh.getUM().isLocked(horse) || !(horse.isEmpty() || zh.getUM().isShared(horse))) {
					if (displayConsole) {
						String ownerName = zh.getUM().getPlayerName(horse);
						p.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseBelongsTo), ownerName));
					}
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isHorseClaimed(Entity entity) {
		if (entity instanceof Horse) {
			Horse horse = (Horse)entity;
			return zh.getUM().isRegistered(horse);
		}
		return false;
	}
}
