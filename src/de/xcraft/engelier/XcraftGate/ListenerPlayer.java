package de.xcraft.engelier.XcraftGate;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ListenerPlayer extends PlayerListener {
	private Location location;
	private DataGate gate = null;	
	private XcraftGate plugin = null;

	public ListenerPlayer(XcraftGate instance) {
		plugin = instance;
	}

	public void onPlayerMove(PlayerMoveEvent event) {
		location = event.getTo();

		Location portTo = null;
		Location portFrom = null;
		
		if (plugin.getWorlds().get(location.getWorld()) == null) return;
		
		int border = plugin.getWorlds().get(location.getWorld()).getBorder();
		if (border > 0) {
			double x = location.getX();
			double z = location.getZ();
		
			if (Math.abs(x) >= border || Math.abs(z) >= border) {
				x = Math.abs(x) >= border ? (x > 0 ? border - 1 : -border + 1) : x;
				z = Math.abs(z) >= border ? (z > 0 ? border - 1 : -border + 1) : z;
				
				Location back = new Location(location.getWorld(), x, location.getY(), z, location.getYaw(), location.getPitch());
				
				event.setCancelled(true);
				event.getPlayer().teleport(back);
				event.getPlayer().sendMessage(ChatColor.RED	+ "You reached the border of this world.");
				return;
			}			
		}
				
		portTo = plugin.justTeleported.get(event.getPlayer().getName());		
		portFrom = plugin.justTeleportedFrom.get(event.getPlayer().getName());
		
		if (portTo != null && portFrom == null)
			plugin.justTeleported.remove(event.getPlayer().getName());
		
		if (portTo == null && portFrom != null)
			plugin.justTeleportedFrom.remove(event.getPlayer().getName());
		
		if (portTo != null && portFrom != null) {
			if ((Math.floor(portTo.getX()) != Math.floor(location.getX()) || Math.floor(portTo.getZ()) != Math.floor(location.getZ()))
				&& (Math.floor(portFrom.getX()) != Math.floor(location.getX()) || Math.floor(portFrom.getZ()) != Math.floor(location.getZ()))) {
				plugin.justTeleported.remove(event.getPlayer().getName());
				plugin.justTeleportedFrom.remove(event.getPlayer().getName());
			}
		} else if ((gate = plugin.getGates().getByLocation(location)) != null) {
			if (plugin.getPluginManager().getPermissions() == null ?
					event.getPlayer().hasPermission("XcraftGate.use." + gate.getName()) :
					plugin.getPluginManager().getPermissions().has(event.getPlayer(), "XcraftGate.use." + gate.getName())) {
				plugin.justTeleportedFrom.put(event.getPlayer().getName(), gate.getLocation());
				if (plugin.getPluginManager().getEcoMethod() != null && gate.getToll() > 0) {
					if (plugin.getPluginManager().getEcoMethod().getAccount(event.getPlayer().getName()).hasEnough(gate.getToll())) {
						plugin.getPluginManager().getEcoMethod().getAccount(event.getPlayer().getName()).subtract(gate.getToll());
						event.getPlayer().sendMessage(ChatColor.AQUA + "Took " + plugin.getPluginManager().getEcoMethod().format(gate.getToll()) + " from your account for using this gate.");
						gate.portToTarget(event);
					} else {
						if (!gate.getDenySilent()) {
							event.getPlayer().sendMessage(ChatColor.RED + "You don't have enough money to use this gate (Requires: " + plugin.getPluginManager().getEcoMethod().format(gate.getToll()) + ")");
						}						
					}
				} else {
					gate.portToTarget(event);
				}
			} else {
				if (!gate.getDenySilent()) {
					event.getPlayer().sendMessage(ChatColor.RED + "You're not allowed to use this gate!");
				}
			}
		}
	}
}
