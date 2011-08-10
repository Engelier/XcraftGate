package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandGateLink extends CommandHelperGate {

	public CommandGateLink(XcraftGate plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		String gateTarget = (args.size() > 0 ? args.get(0) : null);
		
		if (gateName == null || gateTarget == null) {
			error("No gate(s) given.");
			reply("Usage: /gate link <gatename> <target_gatename>");
		} else if (!gateExists(gateName)) {
			reply("Gate not found: " + gateName);
		} else if (!gateExists(gateTarget)) {
			reply("Gate not found: " + gateTarget);
		} else {
			getGate(gateName).linkTo(gateTarget);
			reply("Linked Gate " + gateName + " to " + gateTarget);
			plugin.saveGates();
		}
	}
}
