package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.XcraftGateGate;

public class CommandGateListsolo extends CommandHelperGate {

	public CommandGateListsolo(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		
		for (XcraftGateGate thisGate : plugin.gates.values()) {
			if (thisGate.gateTarget == null) {
				boolean hasSource = false;
				
				for (XcraftGateGate sourceGate : plugin.gates.values()) {
					if (sourceGate.gateTarget != null 
							&& sourceGate.gateTarget.equals(thisGate.gateName)) {
						hasSource = true;
					}
				}
				
				if (!hasSource)
					reply("Found orphan: " + thisGate.gateName);
			}
		}
		
	}

}
