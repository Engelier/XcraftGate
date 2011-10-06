package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.Difficulty;
import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldSetDifficulty extends CommandHelperWorld {

	public CommandWorldSetDifficulty(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName,	List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld setdifficulty <worldname> <peaceful|easy|normal|hard>");
		} else if (!hasWorld(worldName)) {
			reply("World not found: " + worldName);
		} else {
			Difficulty newDif = null;
			
			for (Difficulty thisDif : Difficulty.values()) {
				if (thisDif.toString().equalsIgnoreCase(args.get(0))) {
					newDif = thisDif;
				}
			}
			
			if (newDif == null) {
				error("Unknown difficulty.");
				return;
			}
			
			getWorld(worldName).setDifficulty(newDif.getValue());
			reply("Difficulty on world " + worldName + " set to " + newDif.toString());
		}
	}

}
