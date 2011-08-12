package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.DataGate;

public class CommandGateDelete extends CommandHelperGate {

	public CommandGateDelete(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		
		if (gateName == null) {
			error("No gate given.");
			reply("Usage: /gate delete <gatename>");
		} else if (!gateExists(gateName)) {
			reply("Gate not found: " + gateName);
		} else {
			DataGate thisGate = getGate(gateName);
			
			for (DataGate checkGate : plugin.getGates()) {
				if (checkGate.hasTarget() && checkGate.getTarget().equals(thisGate)) {
					checkGate.unlink();
				}
			}

			plugin.getGates().remove(thisGate);
			reply("Gate " + gateName + " removed.");
		}
	}

}
