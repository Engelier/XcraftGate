package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandGateCreate extends CommandHelperGate {

	public CommandGateCreate(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		
		if (gateName == null) {
			error("No gate given.");
			reply("Usage: /gate create <gatename>");
		} else if (gateExists(gateName)) {
			reply("Gate already exists: " + gateName);
		} else {
			if (gateExists(((Player) sender).getLocation())) {
				reply("There is already a gate at this location!");
			} else {
				plugin.createGate(((Player) sender).getLocation(), gateName);
				reply("Gate " + gateName + " created at "	+ plugin.getLocationString(((Player) sender).getLocation()));
				plugin.saveGates();
			}
		}
	}
}
