package de.xcraft.engelier.XcraftGate.Commands;

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
		boolean fail = true;
		
		for (int x = -radius; x <= radius; x++) {
			for (int y = (yy - radius > 0 ? -radius : (int)-yy); y <= (y + radius > 127 ? 128 - y : radius); y++) {
				for (int z = -radius; z <= radius; z++) {
					XcraftGateGate thisGate = plugin.getGateByLocation(new Location(now.getWorld(), x + xx, y + yy, z + zz));
					if (thisGate != null) {
						reply("Found " + thisGate.getName() + " at " + plugin.getLocationString(thisGate.getLocation()));
						fail = false;
					}
				}
			}
		}
		
		if (fail) {
			reply("No gates found.");
		}
	}
}
