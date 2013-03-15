package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xcraft.engelier.XcraftGate.Util;
import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.DataGate;

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
			DataGate thisGate = getGate(gateName);
			plugin.getGates().remove(thisGate);
			thisGate.setLocation(((Player) sender).getLocation());
			plugin.getGates().add(thisGate, true);

			plugin.justTeleported.put(((Player) sender).getName(), thisGate.getLocation());
			plugin.justTeleportedFrom.put(((Player) sender).getName(), thisGate.getLocation());
			reply("Gate " + gateName + " moved to " + Util.getLocationString(thisGate.getLocation()));
		}
	}

}
