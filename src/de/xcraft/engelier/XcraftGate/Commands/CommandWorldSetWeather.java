package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.DataWorld;
import de.xcraft.engelier.XcraftGate.DataWorld.Weather;

public class CommandWorldSetWeather extends CommandHelperWorld {

	public CommandWorldSetWeather(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName,	List<String> args) {
		this.sender = sender;
		
		if (worldName == null) {
			error("No gate given.");
			reply("Usage: /gworld setweather <worldname> <sun|storm>");
		} else if (args.size() == 0) {
			error("No weather given.");
			reply("Usage: /gworld setweather <worldname> <sun|storm>");
		} else if (!hasWorld(worldName)) {
			reply("World not found: " + worldName);
		} else {
			for (Weather thisWeather : DataWorld.Weather.values()) {
				if (thisWeather.toString().equalsIgnoreCase(args.get(0))) {
					getWorld(worldName).setWeather(thisWeather);
					reply("Weather of world " + worldName + " changed to " + args.get(0) + ".");
					return;
				}
			}

			reply("Unknown weather type: " + args.get(0) + ". Use \"sun\" or \"storm\"");
		}
	}

}
