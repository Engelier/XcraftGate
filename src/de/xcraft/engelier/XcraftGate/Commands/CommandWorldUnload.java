package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldUnload extends CommandHelperWorld {

	public CommandWorldUnload(XcraftGate instance) {
		super(instance);
	}

	@Override
	public void execute(CommandSender sender, String worldName,
			List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld load <worldname>");
		} else if (!hasWorld(worldName)) {
			reply("Unknown world: " + worldName);
		} else {
			if (!getWorld(worldName).isLoaded()) {
				reply("World " + worldName + " is not loaded.");
			} else {
				if (plugin.getServer().getWorld(worldName).getPlayers().size() > 0) {
					error("Unable to unload world with active players.");
				} else {
					getWorld(worldName).unload();
					reply("Unloaded world " + worldName);
				}
			}
		}
	}

}
