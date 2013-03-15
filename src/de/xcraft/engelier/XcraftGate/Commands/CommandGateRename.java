package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.DataGate;
import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandGateRename extends CommandHelperGate {

	public CommandGateRename(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		
		if (gateName == null || args.size() == 0) {
			error("No gate given.");
			reply("Usage: /gate rename <gatename> <new_gatename>");
		} else if (!gateExists(gateName)) {
			reply("Gate not found: " + gateName);
		} else {
			DataGate thisGate = getGate(gateName);
			plugin.getGates().remove(thisGate);
			thisGate.setName(args.get(0));
			plugin.getGates().add(thisGate, true);

			reply("Gate " + gateName + " renamed to " + thisGate.getName());
		}
	}

}
