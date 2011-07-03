package de.xcraft.engelier.XcraftGate;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class XcraftGateGate {
	private static XcraftGate plugin;

	public String gateName = null;
	public Location gateLocation = null;
	public String gateTarget = null;

	public XcraftGateGate(XcraftGate instance, String name, Location location) {
		plugin = instance;
		gateLocation = location;
		gateName = name;
	}

	public void portHere(Player player) {
		plugin.justTeleported.put(player.getName(), gateLocation);
		player.teleport(gateLocation);
	}

	public void portHere(PlayerMoveEvent event) {
		plugin.justTeleported.put(event.getPlayer().getName(), gateLocation);
		event.setTo(gateLocation);
	}
	
	public void portToTarget(Player player) {
		if (gateTarget != null) {
			plugin.gates.get(gateTarget).portHere(player);
		}
	}

	public void portToTarget(PlayerMoveEvent event) {
		if (gateTarget != null) {
			plugin.gates.get(gateTarget).portHere(event);
		}
	}
}
