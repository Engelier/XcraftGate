package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.DataGate;

public abstract class CommandHelperGate extends CommandHelper {
	public CommandHelperGate(XcraftGate plugin) {
		super(plugin);
	}
	
	public abstract void execute(CommandSender sender, String gateName, List<String> args);

	public boolean gateExists(String name) {
		return plugin.getGates().has(name);
	}

	public boolean gateExists(Location location) {
		return plugin.getGates().getByLocation(location) != null;
	}

	public DataGate getGate(String name) {
		return plugin.getGates().get(name);
	}
	
	public DataGate getGateByLocation(Location loc) {
		return plugin.getGates().getByLocation(loc);
	}
}
