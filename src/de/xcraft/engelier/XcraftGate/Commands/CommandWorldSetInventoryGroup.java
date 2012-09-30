package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldSetInventoryGroup extends CommandHelperWorld {

	public CommandWorldSetInventoryGroup(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName,	List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld setinventorygroup <worldname> <groupname>");
		} else if (!hasWorld(worldName)) {
			reply("World not found: " + worldName);
		} else if (args.size() != 1) {
			error("Wrong argument count.");
			reply("Usage: /gworld setinventorygroup <worldname> <groupname>");			
		} else {
			getWorld(worldName).setInventoryGroup(args.get(0));
			reply("Inventory group for world " + worldName + " set to " + args.get(0));
		}
	}

}
