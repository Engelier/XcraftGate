package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldSetCreatureLimit extends CommandHelperWorld {

	public CommandWorldSetCreatureLimit(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName, List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld setcreaturelimit <worldname> <#limit>");
		} else if (!hasWorld(worldName)) {
			reply("World not found: " + worldName);
		} else {
			Integer limit;

			try {
				limit = new Integer(args.get(0));
			} catch (Exception ex) {
				reply("Invalid number: " + (args.size() > 0 ? args.get(0) : "<null>"));
				reply("Usage: /gworld setborder <worldname> <#limit>");
				return;
			}

			if (limit <= 0) {
				plugin.worlds.get(worldName).setCreatureLimit(0);
				reply("Creature limit of world " + worldName + " removed.");
			} else {
				plugin.worlds.get(worldName).setCreatureLimit(limit);
				reply("Creature limit of world " + worldName + " set to " + limit + ".");
			}

			plugin.saveWorlds();
		}
	}

}
