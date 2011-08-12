package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldDelete extends CommandHelperWorld {

	public CommandWorldDelete(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName, List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld delete <worldname>");
		} else if (!hasWorld(worldName)) {
			reply("World not found: " + worldName);
		} else {
			if (getWorld(worldName).isLoaded()) {
				if (plugin.getServer().getWorld(worldName).getPlayers().size() > 0) {
					error("Unable to unload world with active players.");
					return;
				} else {
					getWorld(worldName).unload();
				}
			}
			
			plugin.getWorlds().remove(worldName);
			reply("World " + worldName + " removed.");
		}
	}
}
