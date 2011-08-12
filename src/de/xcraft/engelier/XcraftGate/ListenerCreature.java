package de.xcraft.engelier.XcraftGate;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityListener;

public class ListenerCreature extends EntityListener {
	private XcraftGate plugin;

	public ListenerCreature(XcraftGate instance) {
		plugin = instance;
	}

	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (plugin.getWorlds().get(event.getLocation().getWorld()) != null)
			plugin.getWorlds().get(event.getLocation().getWorld()).checkCreatureLimit();
	}
}
