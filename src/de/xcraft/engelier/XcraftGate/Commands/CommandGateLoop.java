package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandGateLoop extends CommandHelperGate {

	public CommandGateLoop(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		String gateTarget = (args.size() > 0 ? args.get(0) : null);
		
		if (gateName == null || gateTarget == null) {
			error("No gate(s) given.");
			reply("Usage: /gate loop <gatename> <target_gatename>");
		} else if (!gateExists(gateName)) {
			reply("Gate not found: " + gateName);
		} else if (!gateExists(gateTarget)) {
			reply("Gate not found: " + gateTarget);
		} else {
			getGate(gateName).linkTo(gateTarget, false);
			getGate(gateTarget).linkTo(gateName);
			reply("Looped Gates " + gateName + " <=> " + gateTarget);
		}
	}

}
