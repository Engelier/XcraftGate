package de.xcraft.engelier.XcraftGate;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

public class XcraftGatePlayerListener extends PlayerListener {
	private XcraftGate plugin = null;
	
	public XcraftGatePlayerListener (XcraftGate instance) {
		plugin = instance;
	}

	public void onPlayerMove(PlayerMoveEvent event) {
		Location location = event.getTo();
		String gateName = null;
		
		if ((gateName = plugin.gateLocations.get(plugin.getLocationString(location))) != null) {
			if (plugin.justTeleported.get(event.getPlayer().getName()) == null) { 
				plugin.gates.get(gateName).portToTarget(event.getPlayer());
			}
		} else if (plugin.justTeleported.get(event.getPlayer().getName()) != null) {
			plugin.justTeleported.remove(event.getPlayer().getName());
		}

	}	
}
