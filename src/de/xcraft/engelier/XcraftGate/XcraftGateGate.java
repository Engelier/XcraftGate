package de.xcraft.engelier.XcraftGate;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class XcraftGateGate {
	private static XcraftGate plugin;
	
	private double x;
	private double y;
	private double z;
	private float pitch;
	private float yaw;
	private String worldName;

	public String gateName = null;
	public String gateTarget = null;

	public XcraftGateGate(XcraftGate instance, String name) {
		plugin = instance;
		gateName = name;
	}
	
	public void setLocation(Location loc) {
		setLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}
	
	public void setLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}
	
	public Location getLocation() {
		if (plugin.getServer().getWorld(worldName) == null) return null;
		
		Location ret = new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
		return plugin.getSaneLocation(ret);
	}
	
	public String getWorldName() {
		return worldName;
	}

	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public void linkTo(XcraftGateGate gate) {
		linkTo(gate.gateName);
	}
	
	public void linkTo(String gateName) {
		gateTarget = gateName;
	}
	
	public void portHere(Player player) {
		plugin.worlds.get(worldName).load();
		plugin.justTeleported.put(player.getName(), getLocation());
		player.teleport(getLocation());
	}

	public void portHere(PlayerMoveEvent event) {
		plugin.worlds.get(worldName).load();
		plugin.justTeleported.put(event.getPlayer().getName(), getLocation());
		event.setTo(getLocation());
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
