package de.xcraft.engelier.XcraftGate;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class ListenerCreature implements Listener {
	private XcraftGate plugin;

	public ListenerCreature(XcraftGate instance) {
		plugin = instance;
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (plugin.getWorlds().get(event.getLocation().getWorld()) != null)
			plugin.getWorlds().get(event.getLocation().getWorld()).checkCreatureLimit();
	}
}
