package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.XcraftGateGate;

public abstract class CommandHelperGate extends CommandHelper {
	public CommandHelperGate(XcraftGate plugin) {
		super(plugin);
	}
	
	public abstract void execute(CommandSender sender, String gateName, List<String> args);

	public boolean gateExists(String name) {
		return plugin.hasGate(name);
	}

	public boolean gateExists(Location location) {
		return plugin.getGateByLocation(location) != null;
	}

	public XcraftGateGate getGate(String name) {
		return plugin.getGate(name);
	}
	
	public XcraftGateGate getGateByLocation(Location loc) {
		return plugin.getGateByLocation(loc);
	}
}
