package de.xcraft.engelier.XcraftGate;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

public class XcraftGatePlayerListener extends PlayerListener {
	private Location location;
	private Location portTo;
	private String gateName = null;	
	private XcraftGate plugin = null;

	public XcraftGatePlayerListener(XcraftGate instance) {
		plugin = instance;
	}

	public void onPlayerMove(PlayerMoveEvent event) {
		location = event.getTo();
		
		if (!plugin.worlds.get(location.getWorld().getName()).checkBorder(location)) {
				event.setCancelled(true);
				event.getPlayer().teleport(event.getFrom());
				event.getPlayer()
						.sendMessage(
								ChatColor.RED
										+ "You reached the border of this world.");
		}

		if ((portTo = plugin.justTeleported.get(event.getPlayer().getName())) != null) {
			if (Math.floor(portTo.getX()) != Math.floor(location.getX()) ||
					Math.floor(portTo.getZ()) != Math.floor(location.getZ()))
				plugin.justTeleported.remove(event.getPlayer().getName());
		} else if ((gateName = plugin.gateLocations.get(plugin.getLocationString(location))) != null) {
			plugin.gates.get(gateName).portToTarget(event.getPlayer());
		}
	}
}
