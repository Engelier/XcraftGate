package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandGateReload extends CommandHelperGate {

	public CommandGateReload(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		
		plugin.reloadGates();
		
		reply("Loaded " + plugin.getGateCollection().size() + " gates.");		
	}

}
