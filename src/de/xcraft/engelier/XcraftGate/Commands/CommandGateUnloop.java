package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandGateUnloop extends CommandHelperGate {

	public CommandGateUnloop(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		String gateTarget = (args.size() > 0 ? args.get(0) : null);
		
		if (gateName == null || gateTarget == null) {
			error("No gate(s) given.");
			reply("Usage: /gate unloop <gatename> <target_gatename>");
		} else if (!gateExists(gateName)) {
			reply("Gate not found: " + gateName);
		} else if (!gateExists(gateTarget)) {
			reply("Gate not found: " + gateTarget);
		} else {
			if (!plugin.gates.get(gateName).gateTarget.equals(gateTarget)
				|| !plugin.gates.get(gateTarget).gateTarget.equals(gateName)) {
				reply("Gates " + gateName + " and " + gateTarget + " aren't linked together");
			} else {
				plugin.removeGateLoop(gateName, gateTarget);
				reply("removed gate loop " + gateName + " <=> " + gateTarget);
				plugin.saveGates();
			}
		}
	}

}
