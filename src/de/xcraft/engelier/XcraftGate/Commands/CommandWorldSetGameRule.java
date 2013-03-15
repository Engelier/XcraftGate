package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldSetGameRule extends CommandHelperWorld {

	public CommandWorldSetGameRule(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName,	List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld setgamerule <worldname> <rulename> <value>");
		} else if (!hasWorld(worldName)) {
			reply("World not found: " + worldName);
		} else if (args.size() != 2) {
			error("Wrong argument count.");
			reply("Usage: /gworld setgamerule <worldname> <rulename> <value>");
 		} else {
			String rule = args.get(0);
			String value = args.get(1);
			
			if (getWorld(worldName).getWorld().getGameRuleValue(rule) == null) {
				reply("Unknown gamerule '" + rule + "'");
			} else {
				getWorld(worldName).getWorld().setGameRuleValue(rule, value);
				reply("GameRule '" + rule + "' for world " + worldName + " set to " + value);
			}
		}
	}

}
