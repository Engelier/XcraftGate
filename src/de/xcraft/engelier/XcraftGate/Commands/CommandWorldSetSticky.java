package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldSetSticky extends CommandHelperWorld {

	public CommandWorldSetSticky(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName,	List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld setsticky <worldname> <true|false>");
		} else if (!hasWorld(worldName)) {
			reply("Unknown world: " + worldName);
		} else {
			Boolean sticky;
			
			sticky = (args.size() == 0 || !args.get(0).equalsIgnoreCase("false")) ? true : false;

			plugin.worlds.get(worldName).setSticky(sticky);
			reply((sticky ? "Sticked" : "Unsticked") + " world " + worldName + ".");
			plugin.saveWorlds();
		}
	}

}
