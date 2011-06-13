package de.xcraft.engelier.XcraftGate.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xcraft.engelier.XcraftGate.XcraftGate;
import de.xcraft.engelier.XcraftGate.XcraftGateCommandHandler;
import de.xcraft.engelier.XcraftGate.XcraftGateWorld;

public class CommandWorld extends XcraftGateCommandHandler {

	public CommandWorld(XcraftGate instance) {
		super(instance);
	}

	public void printUsage() {
		player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getNameBrackets()
				+ "by Engelier");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld list" + ChatColor.WHITE + " | " + ChatColor.AQUA
				+ "lists active worlds on your server");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld info <world>" + ChatColor.WHITE + " | "
				+ ChatColor.AQUA + "displays some basic info about your world");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld create <name> [normal|nether|skylands]"
				+ ChatColor.WHITE + " | " + ChatColor.AQUA
				+ "creates a new world");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld delete <name>" + ChatColor.WHITE + " | "
				+ ChatColor.AQUA + "deletes a world (but NOT on disk!)");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld warpto <name>" + ChatColor.WHITE + " | "
				+ ChatColor.AQUA + "teleports you to world <name>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld setborder <world> <#>" + ChatColor.WHITE + " | "
				+ ChatColor.AQUA
				+ "prevents users from exploring a world farther than x/z > #");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld setcreaturelimit <world> <#>" + ChatColor.WHITE
				+ " | " + ChatColor.AQUA
				+ "limits amount of creatures active to <#> for the world");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld allowanimals <world> <true|false>" + ChatColor.WHITE
				+ " | " + ChatColor.AQUA
				+ "allows/denys animals to spawn in the world");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld allowmonsters <world> <true|false>"
				+ ChatColor.WHITE + " | " + ChatColor.AQUA
				+ "allows/denys monsters to spawn in the world");
	}

	public boolean hasWorld(String world) {
		return (plugin.worlds.get(world) != null);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {

		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			plugin.log.warning(plugin.getNameBrackets()
					+ " this command cannot be used from the console");
			return true;
		}

		if (!isPermitted("world", null)) {
			error("You don't have permission to use this command.");
			return true;
		}

		if (args.length == 0) {
			printUsage();
		} else if (args[0].equals("create")) {
			if (!isPermitted("world", "create")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 3)) {
				printUsage();
			} else {
				if (hasWorld(args[1])) {
					error("World " + args[1] + " already exists.");
				} else {
					String env = args.length < 3 ? "normal" : args[2];

					for (Environment thisEnv : World.Environment.values()) {
						if (thisEnv.toString().equalsIgnoreCase(env)) {
							XcraftGateWorld thisWorld = new XcraftGateWorld(plugin);
							thisWorld.load(args[1], thisEnv);
							plugin.worlds.put(args[1], thisWorld);
							reply("World " + args[1]
									+ " created with environment " + env + ".");
							plugin.saveWorlds();
							return true;
						}
					}

					reply("Unknown environment: " + env);
				}
			}
		} else if (args[0].equals("delete")) {
			if (!isPermitted("world", "delete")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 2)) {
				printUsage();
			} else if (!hasWorld(args[1])) {
				error("Unkown world: " + args[1]);
			} else {
				plugin.worlds.remove(args[1]);
				reply("World " + args[1] + " removed.");
				plugin.getServer().unloadWorld(args[1], true);
				plugin.saveWorlds();
			}
		} else if (args[0].equals("warpto")) {
			if (!isPermitted("world", "warp")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 2)) {
				printUsage();
			} else if (!hasWorld(args[1])) {
				error("Unkown world: " + args[1]);
			} else {
				Location loc = player.getLocation();
				loc.setWorld(plugin.getServer().getWorld(args[1]));
				player.teleport(loc);
			}
		} else if (args[0].equals("setborder")) {
			if (!isPermitted("world", "setborder")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 3)) {
				printUsage();
			} else if (!hasWorld(args[1])) {
				error("Unkown world: " + args[1]);
			} else {
				Integer border;
				try {
					border = new Integer(args[2]);
				} catch (Exception ex) {
					reply("Invalid number: " + args[2]);
					return true;
				}
				if (border <= 0) {
					plugin.worlds.get(args[1]).border = 0;
					reply("Border of world " + args[1] + " removed.");
				} else {
					plugin.worlds.get(args[1]).border = border;
					reply("Border of world " + args[1] + " set to " + border
							+ ".");
				}
				plugin.saveWorlds();
			}
		} else if (args[0].equals("setcreaturelimit")) {
			if (!isPermitted("world", "setcreaturelimit")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 3)) {
				printUsage();
			} else if (!hasWorld(args[1])) {
				error("Unkown world: " + args[1]);
			} else {
				Integer limit;
				try {
					limit = new Integer(args[2]);
				} catch (Exception ex) {
					reply("Invalid number: " + args[2]);
					return true;
				}
				if (limit <= 0) {
					plugin.worlds.get(args[1]).creatureLimit = 0;
					reply("Creature limit of world " + args[1] + " removed.");
				} else {
					plugin.worlds.get(args[1]).creatureLimit = limit;
					reply("Creature limit of world " + args[1] + " set to "
							+ limit + ".");
				}
				plugin.saveWorlds();
			}
		} else if (args[0].equals("allowanimals")) {
			if (!isPermitted("world", "setcreaturelimit")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 3)) {
				printUsage();
			} else if (!hasWorld(args[1])) {
				error("Unkown world: " + args[1]);
			} else {
				Boolean allowed;
				if (args[2].equalsIgnoreCase("true")) {
					allowed = true;
				} else if (args[2].equalsIgnoreCase("false")) {
					allowed = false;
				} else {
					printUsage();
					return true;
				}

				plugin.worlds.get(args[1]).setAllowAnimals(allowed);
				reply("Animal spawn on " + args[1] + (allowed ? " enabled." : " disabled."));
				plugin.saveWorlds();
			}
		} else if (args[0].equals("allowmonsters")) {
			if (!isPermitted("world", "setcreaturelimit")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 3)) {
				printUsage();
			} else if (!hasWorld(args[1])) {
				error("Unkown world: " + args[1]);
			} else {
				Boolean allowed;
				if (args[2].equalsIgnoreCase("true")) {
					allowed = true;
				} else if (args[2].equalsIgnoreCase("false")) {
					allowed = false;
				} else {
					printUsage();
					return true;
				}

				plugin.worlds.get(args[1]).setAllowMonsters(allowed);
				reply("Monster spawn on " + args[1] + (allowed ? " enabled." : " disabled."));
				plugin.saveWorlds();
			}
		} else if (args[0].equals("info")) {
			if (!isPermitted("world", "info")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 2)) {
				printUsage();
			} else if (!hasWorld(args[1])) {
				error("Unkown world: " + args[1]);
			} else {
				XcraftGateWorld thisWorld = plugin.worlds.get(args[1]);
				reply("Infos for world " + args[1] + ":");
				player.sendMessage("Worldname: " + args[1]);
				player.sendMessage("Border: " + thisWorld.border);
				player.sendMessage("Animals allowed: " + (thisWorld.allowAnimals ? "yes" : "no"));
				player.sendMessage("Monsters allowed: " + (thisWorld.allowMonsters ? "yes" : "no"));
				player.sendMessage("Creature limit: " + thisWorld.creatureLimit);
				player.sendMessage("Creature count: "
						+ (plugin.getServer().getWorld(args[1])
								.getLivingEntities().size() - plugin
								.getServer().getWorld(args[1]).getPlayers()
								.size()));
				player.sendMessage("Player count: "
						+ plugin.getServer().getWorld(args[1]).getPlayers()
								.size());
			}
		} else if (args[0].equals("list")) {
			if (!isPermitted("world", "info")) {
				error("You don't have permission to use this command.");
			} else {
				String worlds = "";
				for (World world : plugin.getServer().getWorlds()) {
					worlds += ", " + world.getName();
				}
				reply("Worlds: " + ChatColor.WHITE + worlds.substring(2));
			}
		} else {
			printUsage();
		}

		return true;
	}

}
