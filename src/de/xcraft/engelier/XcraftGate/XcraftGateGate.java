package de.xcraft.engelier.XcraftGate;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class XcraftGateGate {
	private static XcraftGate plugin;
	
	private double x;
	private double y;
	private double z;
	private float pitch;
	private float yaw;
	private String worldName;

	private String gateName = null;
	private String gateTargetName = null;
	private XcraftGateGate gateTarget = null;

	public XcraftGateGate(XcraftGate instance, String name) {
		plugin = instance;
		gateName = name;
		
		if (plugin.getServer().getPluginManager().getPermission("XcraftGate.use." + name) == null) {
			Permission gatePerm = new Permission("XcraftGate.use." + name, PermissionDefault.TRUE);
			plugin.getServer().getPluginManager().addPermission(gatePerm);
			plugin.resetSuperPermission(name);
		}
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> values = new HashMap<String, Object>();
		
		values.put("name", gateName);
		values.put("world", worldName);
		values.put("locX", x);
		values.put("locY", y);
		values.put("locZ", z);
		values.put("locP", pitch);
		values.put("locYaw", yaw);
		values.put("target", gateTargetName);
		
		return values;
	}

	public void setLocation(Location loc) {
		loc = plugin.getSaneLocation(loc);
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
	
	public String getName() {
		return gateName;
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
	
	public boolean hasTarget() {
		return gateTarget != null;
	}
	
	public XcraftGateGate getTarget() {
		return gateTarget;
	}
	
	public void linkTo(XcraftGateGate gate) {
		gateTarget = gate;
		
		if (gate != null) {
			gateTargetName = gate.getName();
		} else {
			gateTargetName = null;
		}
		
		plugin.saveGates();
	}
	
	public void linkTo(String gateName) {
		linkTo(plugin.getGate(gateName));
	}
	
	public void unlink() {
		gateTarget = null;
		gateTargetName = null;
	}
	
	private void checkWorld() {
		if (!plugin.getWorld(worldName).isLoaded()) {
			plugin.getWorld(worldName).load();
		}		
	}
	
	public void portHere(Player player) {
		checkWorld();
		plugin.justTeleported.put(player.getName(), getLocation());
		player.teleport(getLocation());
	}

	public void portHere(PlayerMoveEvent event) {
		checkWorld();
		plugin.justTeleported.put(event.getPlayer().getName(), getLocation());
		event.setTo(getLocation());
	}
	
	public void portToTarget(Player player) {
		if (gateTarget != null) {
			gateTarget.portHere(player);
		}
	}

	public void portToTarget(PlayerMoveEvent event) {
		if (gateTarget != null) {
			gateTarget.portHere(event);
		}
	}
}
