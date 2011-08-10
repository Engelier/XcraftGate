package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.XcraftGateGate;

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
			Location loc = ((Player) sender).getLocation();
			
			if (gateExists(loc)) {
				reply("There is already a gate at this location: " + getGateByLocation(loc).getName());
			} else {
				XcraftGateGate newGate = new XcraftGateGate(plugin, gateName);
				newGate.setLocation(plugin.getSaneLocation(loc));
				plugin.addGate(newGate);
				reply("Gate " + gateName + " created at "	+ plugin.getLocationString(newGate.getLocation()));
			}
		}
	}
}
