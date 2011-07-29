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

		if (plugin.worlds.get(world.getName()) != null) {
			plugin.log.info(plugin.getNameBrackets() + "World '" + world.getName() + "' loaded. Applying config.");
			plugin.worlds.get(world.getName()).world = world;
			plugin.worlds.get(world.getName()).setParameters();
		} else {
			plugin.log.info(plugin.getNameBrackets() + "World '" + world.getName() + "' detected. Adding to config.");
			XcraftGateWorld newWorld = new XcraftGateWorld(plugin, world.getName(), world.getEnvironment());
			plugin.worlds.put(world.getName(), newWorld);
			plugin.saveWorlds();
		}

		int gateCounter = 0;
		
		for (XcraftGateGate thisGate : plugin.gates.values()) {
			if (thisGate.getWorldName().equalsIgnoreCase(world.getName())) {
				plugin.gateLocations.put(plugin.getLocationString(thisGate.getLocation()), thisGate.gateName);
				gateCounter++;
			}
		}
		
		plugin.log.info(plugin.getNameBrackets() + "loaded " + gateCounter + " gates for world '" + world.getName() + "'");
	}
	
	public void onWorldUnload(WorldUnloadEvent event) {
		World world = event.getWorld();
		plugin.log.info(plugin.getNameBrackets() + "trying to unload world " + world.getName());
		// this is never called?!
	}
}
