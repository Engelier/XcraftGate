package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.XcraftGateGate;

public class CommandGateReload extends CommandHelperGate {

	public CommandGateReload(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		
		plugin.gates.clear();
		plugin.gateLocations.clear();
		plugin.loadGates();
		
		for (XcraftGateGate thisGate : plugin.gates.values()) {
			if (plugin.getServer().getWorld(thisGate.getWorldName()) != null) {
				plugin.gateLocations.put(plugin.getLocationString(thisGate.getLocation()), thisGate.gateName);
			}
		}
		
		reply("Loaded " + plugin.gates.size() + " gates.");		
	}

}
