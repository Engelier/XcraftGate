package de.xcraft.engelier.XcraftGate;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class ListenerEntity extends EntityListener {
	private static XcraftGate plugin;
	
	public ListenerEntity(XcraftGate instance) {
		plugin = instance;
	}
	
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (plugin.getWorlds().get(event.getEntity().getWorld()).isSuppressHealthRegain() && event.getRegainReason() == RegainReason.REGEN) {
			event.setCancelled(true);
		}
	}
	
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		System.out.println("DEBUG: FoodLevelChangeEvent");
		if (plugin.getWorlds().get(event.getEntity().getWorld()).isSuppressHunger()) {
			System.out.println("DEBUG: FoodLevelChangeEvent - should be suppressed");
			if (event.getEntity() instanceof Player) {
				System.out.println("DEBUG: FoodLevelChangeEvent - suppressed");
				event.setFoodLevel(20);
				((Player) event.getEntity()).setSaturation(20);				
				((Player) event.getEntity()).setExhaustion(0);				
			}
		}
	}
}
