package de.xcraft.engelier.XcraftGate;

import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

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
}
