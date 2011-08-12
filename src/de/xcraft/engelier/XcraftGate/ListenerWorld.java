package de.xcraft.engelier.XcraftGate;

import org.bukkit.World;
import org.bukkit.event.world.*;

public class ListenerWorld extends WorldListener {
	private XcraftGate plugin;
	
	public ListenerWorld (XcraftGate instance) {
		plugin = instance;
	}
	
	public void onWorldLoad(WorldLoadEvent event) {
		World world = event.getWorld();

		plugin.getWorlds().onWorldLoad(world);
	}
	
	public void onWorldUnload(WorldUnloadEvent event) {
		World world = event.getWorld();
		plugin.log.info(plugin.getNameBrackets() + "trying to unload world " + world.getName());
		// this is never called?!
	}
}
