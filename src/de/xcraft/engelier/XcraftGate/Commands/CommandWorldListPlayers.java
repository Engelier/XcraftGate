package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandWorldListPlayers extends CommandHelperWorld {

	public CommandWorldListPlayers(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName,	List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No world given.");
			reply("Usage: /gworld listplayers <worldname>");
		} else if (!hasWorld(worldName)) {
			reply("World not found: " + worldName);
		} else {
			String players = "";
			for (Player player : plugin.getServer().getWorld(worldName).getPlayers()) {
				players += ", " + player.getName();
			}
			
			if (players.length() > 0) {
				reply("Players in world " + worldName + ": " + players.substring(2));
			} else {
				reply("No players in world " + worldName + ".");
			}
		}
	}

}
