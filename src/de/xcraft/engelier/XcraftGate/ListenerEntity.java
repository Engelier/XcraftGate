package de.xcraft.engelier.XcraftGate;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ListenerEntity extends EntityListener {
	private static XcraftGate plugin;
	
	public ListenerEntity(XcraftGate instance) {
		plugin = instance;
	}
	
	public void onEntityDeath(EntityDeathEvent event) {
		if (event instanceof PlayerDeathEvent) {
			DataWorld world = plugin.getWorlds().get(event.getEntity().getWorld());
				
			if (world == null) return;
				
			if (!world.getAnnouncePlayerDeath()) {
				((PlayerDeathEvent)event).setDeathMessage("");
			}
		}
	}
	
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (plugin.getWorlds().get(event.getEntity().getWorld()).isSuppressHealthRegain() && 
				(event.getRegainReason() == RegainReason.REGEN || event.getRegainReason() == RegainReason.SATIATED)) {
			event.setCancelled(true);
		}
	}
	
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (plugin.getWorlds().get(event.getEntity().getWorld()).isSuppressHunger()) {
			if (event.getEntity() instanceof Player) {
				event.setFoodLevel(20);
				((Player) event.getEntity()).setSaturation(20);				
				((Player) event.getEntity()).setExhaustion(0);				
			}
		}
	}
}
