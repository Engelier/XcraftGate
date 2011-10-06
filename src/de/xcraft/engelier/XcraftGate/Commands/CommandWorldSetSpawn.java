package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldSetSpawn extends CommandHelperWorld {

	public CommandWorldSetSpawn(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName,	List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld setspawn <worldname>");
		} else if (!hasWorld(worldName)) {
			reply("World not found: " + worldName);
		} else {
			Location loc = ((Player)sender).getLocation();
			getWorld(worldName).getWorld().setSpawnLocation((int)Math.floor(loc.getX()), (int)Math.floor(loc.getY()), (int)Math.floor(loc.getZ()));
			reply("Set spawn location of " + worldName + " to your current position.");
		}
	}

}
