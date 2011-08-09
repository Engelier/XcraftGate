package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public abstract class CommandHelperGate extends CommandHelper {
	public CommandHelperGate(XcraftGate plugin) {
		super(plugin);
	}
	
	public abstract void execute(CommandSender sender, String gateName, List<String> args);

	public boolean gateExists(String name) {
		return plugin.gates.containsKey(name);
	}

	public boolean gateExists(Location location) {
		return plugin.gateLocations.get(plugin.getLocationString(location)) != null;
	}

}
