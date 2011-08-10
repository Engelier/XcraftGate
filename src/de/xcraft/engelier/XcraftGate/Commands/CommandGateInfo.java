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
			XcraftGateGate thisGate = getGate(gateName);

			reply("Info for gate " + gateName);
			sender.sendMessage("Name: " + thisGate.getName());

			if (plugin.getWorld(thisGate.getWorldName()).isLoaded()) {
				sender.sendMessage("Position: " + plugin.getLocationString(thisGate.getLocation()));
			} else {
				sender.sendMessage("Position: World " + thisGate.getWorldName() + " is not loaded!");				
			}

			sender.sendMessage("Destination: " + thisGate.getTarget().getName());
			sender.sendMessage("Permission-Node: XcraftGate.use." + thisGate.getName());
		}
	}

}
