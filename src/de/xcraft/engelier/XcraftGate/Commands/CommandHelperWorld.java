package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.World;
import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.XcraftGateWorld;

public abstract class CommandHelperWorld extends CommandHelper {	
	public CommandHelperWorld(XcraftGate plugin) {
		super(plugin);
	}
	
	public abstract void execute(CommandSender sender, String worldName, List<String> args);
	
	public boolean hasWorld(World world) {
		return hasWorld(world.getName());
	}

	public boolean hasWorld(String world) {
		return (plugin.getWorld(world) != null);
	}
	
	public XcraftGateWorld getWorld(World world) {
		return getWorld(world.getName());
	}
	
	public XcraftGateWorld getWorld(String name) {
		return plugin.getWorld(name);
	}

}
