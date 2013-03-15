package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.DataGate;

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
			DataGate loop1 = getGate(gateName);
			DataGate loop2 = getGate(gateTarget);
			
			if (!loop1.getTarget().equals(loop2) || !loop2.getTarget().equals(loop1)) {
				reply("Gates " + gateName + " and " + gateTarget + " aren't linked together");
			} else {
				loop1.unlink();
				loop2.unlink();
				reply("removed gate loop " + gateName + " <=> " + gateTarget);
			}
		}
	}

}
