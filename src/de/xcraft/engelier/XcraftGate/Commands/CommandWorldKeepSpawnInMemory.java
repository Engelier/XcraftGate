package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldKeepSpawnInMemory extends CommandHelperWorld {

	public CommandWorldKeepSpawnInMemory(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName,
			List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld keepspawninmemory <worldname> <true|false>");
		} else if (!hasWorld(worldName)) {
			reply("World not found: " + worldName);
		} else {
			Boolean keep;
			
			keep = (args.size() == 0 || !args.get(0).equalsIgnoreCase("false")) ? true : false;

			getWorld(worldName).setKeepSpawnInMemory(keep);
			reply("Spawnarea on " + worldName + (keep ? " stays in memory." : " will get unloaded normally."));
		}		
	}

}
