package de.xcraft.engelier.XcraftGate;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.*;

public class ListenerWorld implements Listener {
	private XcraftGate plugin;
	
	public ListenerWorld (XcraftGate instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldLoad(WorldLoadEvent event) {
		World world = event.getWorld();

		plugin.getWorlds().onWorldLoad(world);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldUnload(WorldUnloadEvent event) {
		World world = event.getWorld();
		plugin.log.info(plugin.getNameBrackets() + "trying to unload world " + world.getName());
		// this is never called?!
	}
}
