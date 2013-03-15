package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.DataWorld;
import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.DataWorld.RespawnLocation;

public class CommandWorldSetRespawnLocation extends CommandHelperWorld {

	public CommandWorldSetRespawnLocation(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName,	List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld setrespawnlocation <worldname> <worldspawn|bedspawn|world <worldname>>");
		} else if (!hasWorld(worldName)) {
			reply("Unknown world: " + worldName);
		} else if (args.size() < 1) {
			error("No location given.");
			reply("Usage: /gworld setrespawnlocation <worldname> <worldspawn|bedspawn|world <worldname>>");
		} else {
			String rsLoc = args.get(0);
			RespawnLocation newRSLoc = null;
					
			for(RespawnLocation thisRLoc: DataWorld.RespawnLocation.values()) {
				if (thisRLoc.toString().equalsIgnoreCase(rsLoc)) {
					newRSLoc = thisRLoc;
				}
			}
		
			if (newRSLoc == null) {
				reply("Unknown respawn location: " + rsLoc);
				reply("Usage: /gworld setrespawnlocation <worldname> <worldspawn|bedspawn|world <worldname>>");
			}
			
			if (newRSLoc == RespawnLocation.WORLD) {
				if (args.size() < 2) {
					error("No respawn world given.");
					reply("Usage: /gworld setrespawnlocation <worldname> <worldspawn|bedspawn|world <worldname>>");
					return;
				} else if (!hasWorld(args.get(1))) {
					reply("Unknown respawn world: " + args.get(1));
					return;
				} else {
					getWorld(worldName).setRespawnWorldName(args.get(1));
				}
			}
			
			getWorld(worldName).setRespawnLocation(newRSLoc);
			reply("RespawnLocation for world " + worldName + " set to " + newRSLoc.toString() + (newRSLoc == RespawnLocation.WORLD ? ": " + args.get(1) : ""));
		}
	}

}
