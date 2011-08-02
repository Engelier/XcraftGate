package de.xcraft.engelier.XcraftGate;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class XcraftGateCommandHandler implements CommandExecutor {
	protected XcraftGate plugin = null;
	protected Player player;

	public XcraftGateCommandHandler(XcraftGate instance) {
		plugin = instance;
	}

	public abstract void printUsage();

	public abstract boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args);

	public void reply(String message) {
		player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getNameBrackets()
				+ ChatColor.DARK_AQUA + message);
	}

	public void error(String message) {
		player.sendMessage(ChatColor.RED + "Error: " + message);
	}

	public boolean checkArgs(String[] args, Integer amount) {
		return args.length == amount;
	}

	public boolean isPermitted(String command, String subcommand) {
		if (plugin.permissions != null) {
			if (subcommand != null) {
				return plugin.permissions.has(player, "XcraftGate." + command
						+ "." + subcommand);
			} else {
				return plugin.permissions.has(player, "XcraftGate." + command);
			}
		} else {
			if (subcommand != null) {
				return player.hasPermission("XcraftGate." + command + "." + subcommand);
			} else {
				return player.hasPermission("XcraftGate." + command);
			}
		}
	}
}
