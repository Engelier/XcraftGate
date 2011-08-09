package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandGateMove extends CommandHelperGate {

	public CommandGateMove(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		
		if (gateName == null) {
			error("No gate given.");
			reply("Usage: /gate move <gatename>");
		} else if (!gateExists(gateName)) {
			reply("Gate not found: " + gateName);
		} else {
			// TODO: oldLoc world loaded?
			String oldLoc = plugin.getLocationString(plugin.gates.get(gateName).getLocation());
			String newLoc = plugin.getLocationString(((Player) sender).getLocation());
			plugin.gates.get(gateName).setLocation(plugin.getSaneLocation(((Player) sender).getLocation()));
			plugin.gateLocations.remove(oldLoc);
			plugin.gateLocations.put(newLoc, gateName);
			plugin.justTeleported.put(((Player) sender).getName(), plugin.gates.get(gateName).getLocation());
			plugin.justTeleportedFrom.put(((Player) sender).getName(), plugin.gates.get(gateName).getLocation());
			reply("Gate " + gateName + " moved to " + newLoc);
		}
	}

}
