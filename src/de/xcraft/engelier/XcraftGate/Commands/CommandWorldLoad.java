package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldLoad extends CommandHelperWorld {

	public CommandWorldLoad(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName,	List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld load <worldname>");
		} else if (!hasWorld(worldName)) {
			reply("Unknown world: " + worldName);
		} else {
			if (getWorld(worldName).world != null) {
				reply("World " + worldName + " already loaded.");
			} else {
				getWorld(worldName).load();
				reply("Loaded world " + worldName);
			}
		}
	}

}
