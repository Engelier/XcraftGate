package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldWarpto extends CommandHelperWorld {

	public CommandWorldWarpto(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName, List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld warpto <worldname>");
		} else if (!hasWorld(worldName)) {
			reply("World not found: " + worldName);
		} else {
			if (!getWorld(worldName).isLoaded()) {
				getWorld(worldName).load();
			}
			
			Location loc = getWorld(worldName).getWorld().getSpawnLocation();
			if (loc != null)
				((Player) sender).teleport(loc);
			else
				error("Couldn't find a safe spot at your destination");
		}
	}
}
