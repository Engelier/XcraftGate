package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldSetBorder extends CommandHelperWorld {

	public CommandWorldSetBorder(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName, List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld setborder <worldname> <#border>");
		} else if (!hasWorld(worldName)) {
			reply("World not found: " + worldName);
		} else {
			Integer border;

			try {
				border = new Integer(args.get(0));
			} catch (Exception ex) {
				reply("Invalid number: " + (args.size() > 0 ? args.get(0) : "<null>"));
				reply("Usage: /gworld setborder <worldname> <#border>");
				return;
			}

			if (border <= 0) {
				plugin.worlds.get(worldName).setBorder(0);
				reply("Border of world " + worldName + " removed.");
			} else {
				plugin.worlds.get(worldName).setBorder(border);
				reply("Border of world " + worldName + " set to " + border + ".");
			}
			
			plugin.saveWorlds();
		}
	}

}
