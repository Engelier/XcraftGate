package de.xcraft.engelier.XcraftGate;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityListener;

public class XcraftGateCreatureListener extends EntityListener {
	private XcraftGate plugin;

	public XcraftGateCreatureListener(XcraftGate instance) {
		plugin = instance;
	}

	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (plugin.worlds.get(event.getLocation().getWorld().getName()) != null)
			plugin.worlds.get(event.getLocation().getWorld().getName()).checkCreatureLimit();
	}
}
