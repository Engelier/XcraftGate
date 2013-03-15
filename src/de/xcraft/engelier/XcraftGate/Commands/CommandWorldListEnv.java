package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.Generator.Generator;

public class CommandWorldListEnv extends CommandHelperWorld {

	public CommandWorldListEnv(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String worldName, List<String> args) {
		this.sender = sender;
		
		reply("Environments provided by Bukkit:");
		for (Environment thisEnv : World.Environment.values()) {
			sender.sendMessage(thisEnv.toString());
		}
		
		reply("Environments provided by XcraftGate:");
		for (Generator thisEnv : Generator.values()) {
			if (thisEnv.getId() != 0) sender.sendMessage(thisEnv.toString());
		}
	}
}
