package de.xcraft.engelier.XcraftGate;

import net.minecraft.server.WorldServer;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

public class XcraftGateCreatureLimiter implements Runnable {
	private XcraftGate plugin;
	
	public XcraftGateCreatureLimiter (XcraftGate instance) {
		plugin = instance;
	}
	
	public void checkLimit(World world) {
		Double max = plugin.config.getDouble("worlds." + world.getName() + ".creatureLimit", 0.0);
		Integer alive = world.getLivingEntities().size() - world.getPlayers().size();
		
		if (max <= 0)
			return;

		WorldServer worldS = ((CraftWorld)world).getHandle();
		
		if (alive >= max) {
			// disable creature spawn
			worldS.allowAnimals  = false;
			worldS.allowMonsters = false;
		} else if (alive <= max * 0.8) {
			// enable creature spawn
			worldS.allowAnimals  = true;
			worldS.allowMonsters = true;
		}
	}
	
	public void run() {
		for (World thisWorld: plugin.getServer().getWorlds()) {
			checkLimit(thisWorld);
		}
	}
}
