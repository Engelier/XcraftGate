package de.xcraft.engelier.XcraftGate;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class DataGate {
	private static XcraftGate plugin;
	
	private double x;
	private double y;
	private double z;
	private float pitch;
	private float yaw;
	private String worldName;
	private double toll = 0.00;
	private boolean denysilent = false;

	private String gateName = null;
	private String gateTargetName = null;
	private DataGate gateTarget = null;

	public DataGate(XcraftGate instance, String name) {
		plugin = instance;
		gateName = name;		
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
		values.put("toll", toll);
		values.put("denysilent", denysilent);
		
		return values;
	}

	public void setLocation(Location loc) {
		loc = Util.getSaneLocation(loc);
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
		return Util.getSaneLocation(ret);
	}
	
	public Location getPortLocation() {
		if (plugin.getServer().getWorld(worldName) == null) return null;
		
		Location ret = new Location(plugin.getServer().getWorld(worldName), x, y + 1.5, z, yaw, pitch);
		return Util.getSaneLocation(ret);
	}

	public void setToll(Double toll) {
		this.toll = (toll != null) ? toll : 0.00;
	}
	
	public double getToll() {
		return this.toll;
	}
	
	public void setDenySilent(Boolean denysilent) {
		this.denysilent = denysilent != null ? denysilent : false;
	}
	
	public boolean getDenySilent() {
		return this.denysilent;
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
	
	public DataGate getTarget() {
		return gateTarget;
	}

	public void linkTo(String gateName) {
		linkTo(plugin.getGates().get(gateName));
	}

	public void linkTo(String gateName, boolean save) {
		linkTo(plugin.getGates().get(gateName), save);
	}

	public void linkTo(DataGate gate) {
		linkTo(gate, true);
	}

	public void linkTo(DataGate gate, boolean save) {
		gateTarget = gate;
		
		if (gate != null) {
			gateTargetName = gate.getName();
		} else {
			gateTargetName = null;
		}
		
		if (save) plugin.getGates().save();
	}
		
	public void unlink() {
		gateTarget = null;
		gateTargetName = null;
	}
	
	private boolean checkWorld() {
		if (plugin.getWorlds().get(worldName) == null) {
			return false;
		}
		
		if (!plugin.getWorlds().get(worldName).isLoaded()) {
			plugin.getWorlds().get(worldName).load();
		}		
		
		return true;
	}
	
	public void portHere(Player player) {
		if (!checkWorld()) {
			player.sendMessage(ChatColor.RED + "Error: Target world '" + worldName + "' doesn't exist. Please alert your administrator!");
			return;
		}

		plugin.justTeleported.put(player.getName(), getLocation());
		player.teleport(getLocation());
	}

	public void portHere(PlayerMoveEvent event) {
		/*
		 * disabled due to event.setTo() not working as expected
		 * 
		if (!checkWorld()) {
			event.getPlayer().sendMessage(ChatColor.RED + "Error: Target world '" + worldName + "' doesn't exist. Please alert your administrator!");
			return;
		}

		plugin.justTeleported.put(event.getPlayer().getName(), getLocation());
		event.setTo(getPortLocation());
		*/
		portHere(event.getPlayer());
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
	
	public void sendInfo(CommandSender sender) {
		sender.sendMessage("Name: " + getName());

		if (plugin.getWorlds().get(getWorldName()).isLoaded()) {
			sender.sendMessage("Position: " + Util.getLocationString(getLocation()));
		} else {
			sender.sendMessage("Position: World " + getWorldName() + " is not loaded!");				
		}

		sender.sendMessage("Destination: " + (getTarget() != null ? getTarget().getName() : "none"));
		if (plugin.getPluginManager().getEcoMethod() != null) {
			sender.sendMessage("Toll: " + plugin.getPluginManager().getEcoMethod().format(toll));
		}
		sender.sendMessage("Deny usage silently: " + (denysilent ? "Yes" : "No"));
		sender.sendMessage("Permission-Node: XcraftGate.use." + getName());
	}
}
