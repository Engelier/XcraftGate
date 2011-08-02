package de.xcraft.engelier.XcraftGate;

import org.bukkit.World;
import org.bukkit.event.world.*;

public class XcraftGateWorldListener extends WorldListener {
	private XcraftGate plugin;
	
	public XcraftGateWorldListener (XcraftGate instance) {
		plugin = instance;
	}
	
	public void onWorldLoad(WorldLoadEvent event) {
		World world = event.getWorld();

		plugin.checkWorld(world);
	}
	
	public void onWorldUnload(WorldUnloadEvent event) {
		World world = event.getWorld();
		plugin.log.info(plugin.getNameBrackets() + "trying to unload world " + world.getName());
		// this is never called?!
	}
}
