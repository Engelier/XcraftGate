package de.xcraft.engelier.XcraftGate.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.XcraftGateGate;

public class CommandGateListnear extends CommandHelperGate {

	public CommandGateListnear(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		Integer radius = 10;
		
		if (args.size() > 0) {
			try {
				radius = Integer.parseInt(args.get(0));
			} catch (Exception ex) {
				error("Invalid radius number: " + args.get(0));
				return;
			}
		}
		
		Location now = ((Player) sender).getLocation();
		double xx = now.getX();
		double yy = now.getY();
		double zz = now.getZ();
		List<String> gatesFound = new ArrayList<String>();
		
		for (int x = -radius; x <= radius; x++) {
			for (int y = (radius > 127 ? -127 : -radius); y <= (radius > 127 ? 127 : radius); y++) {
				for (int z = -radius; z <= radius; z++) {
					String thisGateName = plugin.gateLocations.get(plugin.getLocationString(new Location(now.getWorld(), x + xx, y + yy, z + zz)));
					if (thisGateName != null) {
						gatesFound.add(thisGateName);
					}
				}
			}
		}
		
		if (gatesFound.size() == 0) {
			reply("No gates found.");
		} else {
			Object[] found = gatesFound.toArray();
			java.util.Arrays.sort(found);
			for (Object foundO : found) {
				XcraftGateGate gate = plugin.gates.get((String) foundO);
				reply("Found " + gate.gateName + " at " + plugin.getLocationString(gate.getLocation()));
			}
		}	}

}
