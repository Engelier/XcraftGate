package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandGateSetDenySilent extends CommandHelperGate {

	public CommandGateSetDenySilent(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		
		if (gateName == null) {
			error("No gate given.");
			reply("Usage: /gate setdenysilent <gatename> <true|false>");
		} else if (!gateExists(gateName)) {
			reply("Gate not found: " + gateName);
		} else {
			Boolean denysilent;
			
			denysilent = (args.size() == 0 || args.get(0).equalsIgnoreCase("true")) ? true : false;

			getGate(gateName).setDenySilent(denysilent);
			reply("Gate " + gateName + " denys usage " + (denysilent ? "silently." : "loudly."));
		}
	}

}
