package de.xcraft.engelier.XcraftGate;

import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class XcraftGateEntityListener extends EntityListener {
	private static XcraftGate plugin;
	
	public XcraftGateEntityListener(XcraftGate instance) {
		plugin = instance;
	}
	
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (plugin.worlds.get(event.getEntity().getWorld().getName()).suppressHealthRegain && event.getRegainReason() == RegainReason.REGEN) {
			event.setCancelled(true);
		}
	}
}
