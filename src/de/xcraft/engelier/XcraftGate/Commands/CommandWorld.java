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
import de.xcraft.engelier.XcraftGate.XcraftGateWorld.DayTime;
import de.xcraft.engelier.XcraftGate.XcraftGateWorld.Weather;
import de.xcraft.engelier.XcraftGate.Generator.Generator;

public class CommandWorld extends XcraftGateCommandHandler {

	public CommandWorld(XcraftGate instance) {
		super(instance);
	}

	public void printUsage() {
		player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getNameBrackets()
				+ "by Engelier");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld list");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld info <world>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld listenv");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld create <name> [normal|nether|skylands [seed]]");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld delete <name>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld warpto <name>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld setborder <world> <#>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld setcreaturelimit <world> <#>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld allowanimals <world> <true|false>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld allowmonsters <world> <true|false>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld allowpvp <world> <true|false>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld allowweatherchange <world> <true|false>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld setweather <world> <sun|storm>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld timefrozen <world> <true|false>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld settime <world> <sunrise|noon|sunset|midnight>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld suppresshealthregain <world> <true|false>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN
				+ "/gworld listplayers <world>");
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
			} else if (args.length < 2) {
				printUsage();
			} else {
				if (hasWorld(args[1])) {
					error("World " + args[1] + " already exists.");
				} else {
					String env = args.length < 3 ? "normal" : args[2];
					
					Environment worldEnv = null;
					Generator worldGen = null;

					for (Environment thisEnv : World.Environment.values()) {
						if (thisEnv.toString().equalsIgnoreCase(env)) {
							worldEnv = thisEnv;
						}
					}
					
					for (Generator thisGen : Generator.values()) {
						if (thisGen.toString().equalsIgnoreCase(env)) {
							worldGen = thisGen;
							worldEnv = World.Environment.NORMAL;
						}
					}
					
					if (worldEnv == null) {
						reply("Unknown environment: " + env);
						return true;
					}

					XcraftGateWorld thisWorld = new XcraftGateWorld(plugin, args[1], worldEnv, worldGen);
					plugin.worlds.put(args[1], thisWorld);
					plugin.saveWorlds();

					if (args.length <= 3) {
						thisWorld.load();
					} else {
						Long seed = 0L;
						try {
							seed = Long.parseLong(args[3]);
						} catch (Exception ex) {
							seed = (long)args[3].hashCode();
						}
						
						thisWorld.load(seed);
					}
					
					reply("World " + args[1] + " created with environment " + env + ".");
					return true;
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
				if (plugin.getServer().getWorld(args[1]).getPlayers().size() > 0) {
					error("Unable to unload world with active players.");
					args[0] = "listplayers";
				} else {
					plugin.worlds.remove(args[1]);
					reply("World " + args[1] + " removed.");
					plugin.getServer().unloadWorld(args[1], true);
					plugin.saveWorlds();
				}
			}
		} else if (args[0].equals("warpto")) {
			if (!isPermitted("world", "warp")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 2)) {
				printUsage();
			} else if (!hasWorld(args[1])) {
				error("Unkown world: " + args[1]);
			} else {
				plugin.worlds.get(args[1]).load();
				Location loc = plugin.getServer().getWorld(args[1]).getSpawnLocation();
				if (loc != null)
					player.teleport(loc);
				else
					error("Couldn't find a safe spot at your destination");
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
		} else if (args[0].equals("allowpvp")) {
			if (!isPermitted("world", "allowpvp")) {
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

				plugin.worlds.get(args[1]).setAllowPvP(allowed);
				reply("PvP combat on " + args[1] + (allowed ? " enabled." : " disabled."));
				plugin.saveWorlds();
			}
		} else if (args[0].equals("allowweatherchange")) {
			if (!isPermitted("world", "weather")) {
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

				plugin.worlds.get(args[1]).setAllowWeatherChange(allowed);
				reply("Weather changes on " + args[1] + (allowed ? " enabled." : " disabled."));
				plugin.saveWorlds();
			}
		} else if (args[0].equals("setweather")) {
			if (!isPermitted("world", "weather")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 3)) {
				printUsage();
			} else {
				if (!hasWorld(args[1])) {
					error("World " + args[1] + " unknown.");
				} else {
					for (Weather thisWeather : XcraftGateWorld.Weather.values()) {
						if (thisWeather.toString().equalsIgnoreCase(args[2])) {
							plugin.worlds.get(args[1]).setWeather(thisWeather);
							reply("Weather of world " + args[1]
									+ " changed to " + args[2] + ".");
							plugin.saveWorlds();
							return true;
						}
					}

					reply("Unknown weather type: " + args[2] + ". Use \"sun\" or \"storm\"");
				}
			}
		} else if (args[0].equals("settime")) {
			if (!isPermitted("world", "time")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 3)) {
				printUsage();
			} else {
				if (!hasWorld(args[1])) {
					error("World " + args[1] + " unknown.");
				} else {
					for (DayTime thisTime : XcraftGateWorld.DayTime.values()) {
						if (thisTime.toString().equalsIgnoreCase(args[2])) {
							plugin.worlds.get(args[1]).setDayTime(thisTime);
							reply("Time of world " + args[1]
									+ " changed to " + args[2] + ".");
							plugin.saveWorlds();
							return true;
						}
					}

					reply("Unknown time: " + args[2] + ". Use \"sunrise\", \"noon\", \"sunset\" or \"midnight\"");
				}
			}
		} else if (args[0].equals("timefrozen")) {
			if (!isPermitted("world", "time")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 3)) {
				printUsage();
			} else if (!hasWorld(args[1])) {
				error("Unkown world: " + args[1]);
			} else {
				Boolean frozen;
				if (args[2].equalsIgnoreCase("true")) {
					frozen = true;
				} else if (args[2].equalsIgnoreCase("false")) {
					frozen = false;
				} else {
					printUsage();
					return true;
				}

				plugin.worlds.get(args[1]).setTimeFrozen(frozen);
				reply("Time on " + args[1] + (frozen ? " freezed." : " unfreezed."));
				plugin.saveWorlds();
			}
		} else if (args[0].equals("suppresshealthregain")) {
			if (!isPermitted("world", "setcreaturelimit")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 3)) {
				printUsage();
			} else if (!hasWorld(args[1])) {
				error("Unkown world: " + args[1]);
			} else {
				Boolean suppressed;
				if (args[2].equalsIgnoreCase("true")) {
					suppressed = true;
				} else if (args[2].equalsIgnoreCase("false")) {
					suppressed = false;
				} else {
					printUsage();
					return true;
				}

				plugin.worlds.get(args[1]).suppressHealthRegain = suppressed;
				reply("Automatic health regain on " + args[1] + (suppressed ? " suppressed." : " enabled."));
				reply("Remember: This only has effect with allowmonsters set to false.");
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
				reply("Infos for world " + args[1] + ":");
				plugin.worlds.get(args[1]).sendInfo(player);
			}
		} else if (args[0].equals("list")) {
			if (!isPermitted("world", "info")) {
				error("You don't have permission to use this command.");
			} else {
				String worlds = "";
				for (XcraftGateWorld thisWorld : plugin.worlds.values()) {
					worlds += ", " + thisWorld.name;
					if (plugin.getServer().getWorld(thisWorld.name) != null) {
						worlds += "(*)";
					}
				}
				reply("Worlds: " + ChatColor.WHITE + worlds.substring(2));
				reply("World marked with (*) are currently active on the server.");
			}
		} else if (args[0].equals("listenv")) {
			if (!isPermitted("world", "info")) {
				error("You don't have permission to use this command.");
			} else {
				reply("Environments provided by Bukkit:");
				for (Environment thisEnv : World.Environment.values()) {
					sender.sendMessage(thisEnv.toString());
				}
				
				reply("Environments provided by XcraftGate:");
				for (Generator thisEnv : Generator.values()) {
					if (thisEnv.getId() != 0) sender.sendMessage(thisEnv.toString());
				}
			}
		} else if (args[0].equals("listplayers")) {
			if (!isPermitted("world", "info")) {
				error("You don't have permission to use this command.");
			} else if (!checkArgs(args, 2)) {
				printUsage();
			} else if (!hasWorld(args[1])) {
				error("Unkown world: " + args[1]);
			} else {
				String players = "";
				for (Player player : plugin.getServer().getWorld(args[1]).getPlayers()) {
					players += ", " + player.getName();
				}
				
				if (players.length() > 0) {
					reply("Players in world " + args[1] + ": " + players.substring(2));
				} else {
					reply("No players in world " + args[1] + ".");
				}
			}
		} else {
			printUsage();
		}

		return true;
	}

}
