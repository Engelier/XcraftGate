package de.xcraft.engelier.XcraftGate;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class ListenerEntity implements Listener {
	private static XcraftGate plugin;
	
	public ListenerEntity(XcraftGate instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (plugin.getWorlds().get(event.getEntity().getWorld()).isSuppressHealthRegain() && 
				(event.getRegainReason() == RegainReason.REGEN || event.getRegainReason() == RegainReason.SATIATED)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
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
