package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldSetGameMode extends CommandHelperWorld {

	public CommandWorldSetGameMode(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName,	List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld setgamemode <worldname> <survival|creative|adventure>");
		} else if (!hasWorld(worldName)) {
			reply("World not found: " + worldName);
		} else {
			GameMode newGM = null;
			
			for (GameMode thisGM : GameMode.values()) {
				if (thisGM.toString().equalsIgnoreCase(args.get(0))) {
					newGM = thisGM;
				}
			}
			
			if (newGM == null) {
				error("Unknown gamemode.");
				return;
			}
			
			getWorld(worldName).setGameMode(newGM.getValue());
			reply("GameMode for world " + worldName + " set to " + newGM.toString());
		}
	}

}
