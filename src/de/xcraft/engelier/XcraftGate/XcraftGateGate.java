package de.xcraft.engelier.XcraftGate;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class XcraftGateGate {
	private XcraftGate plugin = null;

	public String gateName = null;
	public Location gateLocation = null;
	public String gateTarget = null;

	public XcraftGateGate(XcraftGate instance, String name, Location location) {
		plugin = instance;
		gateLocation = location;
		gateName = name;
	}

	public void portHere(Player player) {
		plugin.justTeleported.put(player.getName(), true);

		// escape from any PLAYER_MOVE events, prevents "moved too quickly"
		XcraftGateGatePort doPort = new XcraftGateGatePort(player, gateLocation);
		plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(plugin, doPort, 1L);
	}

	public void portToTarget(Player player) {
		if (gateTarget != null) {
			plugin.gates.get(gateTarget).portHere(player);
		}
	}
}
