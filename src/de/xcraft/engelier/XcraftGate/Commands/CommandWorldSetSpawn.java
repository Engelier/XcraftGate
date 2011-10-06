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
		
		Location loc = ((Player)sender).getLocation();
		loc.getWorld().setSpawnLocation((int)Math.floor(loc.getX()), (int)Math.floor(loc.getY()), (int)Math.floor(loc.getZ()));
		reply("Set spawn location of " + loc.getWorld().getName() + " to your current position.");
	}

}
