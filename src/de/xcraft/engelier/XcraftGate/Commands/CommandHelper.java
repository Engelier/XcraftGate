package de.xcraft.engelier.XcraftGate.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public abstract class CommandHelper {
	protected XcraftGate plugin = null;
	protected CommandSender sender = null;
	protected Player player;

	public CommandHelper(XcraftGate instance) {
		plugin = instance;
	}

	public void reply(String message) {
		sender.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getNameBrackets() + ChatColor.DARK_AQUA + message);
	}

	public void error(String message) {
		sender.sendMessage(ChatColor.RED + "Error: " + message);
	}

	public boolean isPermitted(String command, String subcommand) {
		if (player == null) {
			return true;
		}
		
		if (plugin.getPluginManager().getPermissions() != null) {
			if (subcommand != null) {
				return plugin.getPluginManager().getPermissions().has(player, "XcraftGate." + command
						+ "." + subcommand);
			} else {
				return plugin.getPluginManager().getPermissions().has(player, "XcraftGate." + command);
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
