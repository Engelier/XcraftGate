package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.XcraftGateGate;

public class CommandGateInfo extends CommandHelperGate {
	public CommandGateInfo(XcraftGate instance) {
		super(instance);
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		
		if (gateName == null) {
			error("No gate given.");
			reply("Usage: /gate info <gatename>");
		} else if (!gateExists(gateName)) {
			reply("Gate not found: " + gateName);
		} else {
			reply("Info for gate " + gateName);
			XcraftGateGate thisGate = plugin.gates.get(gateName);
			sender.sendMessage("Name: " + thisGate.gateName);
			// TODO: what to do if the corresponding world isn't loaded?
			sender.sendMessage("Position: " + plugin.getLocationString(thisGate.getLocation()));
			sender.sendMessage("Destination: " + thisGate.gateTarget);
			sender.sendMessage("Permission-Node: XcraftGate.use." + thisGate.gateName);
		}
	}

}
