package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xcraft.engelier.XcraftGate.Util;
import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.DataGate;

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
				DataGate newGate = new DataGate(plugin, gateName);
				newGate.setLocation(Util.getSaneLocation(loc));
				plugin.getGates().add(newGate, true);
				reply("Gate " + gateName + " created at "	+ Util.getLocationString(newGate.getLocation()));
			}
		}
	}
}
