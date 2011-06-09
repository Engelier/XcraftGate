package de.xcraft.engelier.XcraftGate;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class XcraftGateCommandHandler {
	private XcraftGate plugin = null;
	
	public XcraftGateCommandHandler (XcraftGate instance) {
		plugin = instance;
	}
	
	public void printUsage(Player player) {
		player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getNameBrackets() + "by Engelier");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gate create <name>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "creates a new gate at your location");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gate link <name1> <name2>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "links <name1> to <name2>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gate loop <name1> <name2>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "links <name1> to <name2> and vice versa");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gate unlink <name>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "removes destination from <name>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gate unloop <name1> <name2>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "removes double-link between <name1> and <name2>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gate delete <name>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "removes gate <name>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gate listsolo" + ChatColor.WHITE + " | " + ChatColor.AQUA + "list gates with no source/destination");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gate warp <name>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "teleports you to gate <name>");
	}

	public void printWUsage(Player player) {
		player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getNameBrackets() + "by Engelier");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gworld list" + ChatColor.WHITE + " | " + ChatColor.AQUA + "lists active worlds on your server");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gworld info <world>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "displays some basic info about your world");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gworld create <name> [normal|nether|skylands]" + ChatColor.WHITE + " | " + ChatColor.AQUA + "creates a new world");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gworld delete <name>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "deletes a world (but NOT on disk!)");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gworld warpto <name>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "teleports you to world <name>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gworld setborder <world> <#>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "prevents users from exploring a world farther than x/z > #");		
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gworld setcreaturelimit <world> <#>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "limits amount of creatures active to <#> for the world");		
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gworld allowanimals <world> <true|false>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "allows/denys animals to spawn in the world");		
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gworld allowmonsters <world> <true|false>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "allows/denys monsters to spawn in the world");		
	}

	public void reply(Player player, String message) {
		player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getNameBrackets() + ChatColor.DARK_AQUA + message);		
	}
	
	public String parseGate(Player player, String[] args) {
		String error = null;
		
		if (args.length == 0) {
			printUsage(player);
		} else if (args[0].equals("create")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.gate.create"))
				return "You don't have permission to use this command.";
			
			if (args.length < 2) {
				printUsage(player);
				return null;
			}
			
			if (plugin.gates.get(args[1]) != null) {
				error = "Gate " + args[1] + " already exists.";
			} else if ((error = plugin.gateLocations.get(plugin.getLocationString(player.getLocation()))) != null) {
				error = "There is already a gate at this location: " + error;
			} else {
				plugin.createGate(player.getLocation(), args[1]);
				reply(player, "Gate " + args[1] + " created: " + plugin.getLocationString(player.getLocation()));
			}
		} else if (args[0].equals("link")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.gate.link"))
				return "You don't have permission to use this command.";
			
			if (args.length < 3) {
				printUsage(player);
				return null;
			}
			
			if (plugin.gates.get(args[1]) == null) {
				error = "Gate " + args[1] + " not found.";
			} else if (plugin.gates.get(args[2]) == null) {
				error = "Gate " + args[2] + " not found.";
			} else {
				plugin.createGateLink(args[1], args[2]);
				reply(player, "Linked Gate " + args[1] + " to " + args[2]);
			}
		} else if (args[0].equals("loop")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.gate.link"))
				return "You don't have permission to use this command.";
			
			if (args.length < 3) {
				printUsage(player);
				return null;
			}
			
			if (plugin.gates.get(args[1]) == null) {
				error = "Gate " + args[1] + " not found.";
			} else if (plugin.gates.get(args[2]) == null) {
				error = "Gate " + args[2] + " not found.";
			} else {
				plugin.createGateLoop(args[1], args[2]);
				reply(player, "Created loop between gates " + args[1] + " <=> " + args[2]);
			}
		} else if (args[0].equals("unlink")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.gate.unlink"))
				return "You don't have permission to use this command.";
			
			if (args.length < 2) {
				printUsage(player);
				return null;
			}
			
			if (plugin.gates.get(args[1]) == null) {
				error = "Gate " + args[1] + " not found.";
			} else {
				plugin.removeGateLink(args[1]);
				reply(player, "removed link from gate " + args[1]);
			}
		} else if (args[0].equals("unloop")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.gate.unlink"))
				return "You don't have permission to use this command.";
			
			if (args.length < 3) {
				printUsage(player);
				return null;
			}
			
			if (plugin.gates.get(args[1]) == null) {
				error = "Gate " + args[1] + " not found.";
			} else if (plugin.gates.get(args[2]) == null) {
				error = "Gate " + args[2] + " not found.";
			} else if (!plugin.gates.get(args[1]).gateTarget.equals(args[2]) || !plugin.gates.get(args[2]).gateTarget.equals(args[1])) {
				error = "Gates " + args[1] + " and " + args[2] + " aren't linked together";
			} else {
				plugin.removeGateLoop(args[1], args[2]);
				reply(player, "removes gate loop " + args[1] + " <=> " + args[2]);
			}
		} else if (args[0].equals("delete")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.gate.delete"))
				return "You don't have permission to use this command.";
			
			if (args.length < 2) {
				printUsage(player);
				return null;
			}
			
			if (plugin.gates.get(args[1]) == null) {
				error = "Gate not found: " + args[1];
			} else {
				plugin.gateLocations.remove(plugin.getLocationString(plugin.gates.get(args[1]).gateLocation));
				plugin.gates.remove(args[1]);
				for (Map.Entry<String, XcraftGateGate> sourceGate: plugin.gates.entrySet()) {
					if (sourceGate.getValue().gateTarget != null && sourceGate.getValue().gateTarget.equals(args[1])) {
						sourceGate.getValue().gateTarget = null;
					}
				}
				reply(player, "Gate " + args[1] + " removed.");
			}
		} else if (args[0].equals("listsolo")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.gate.list"))
				return "You don't have permission to use this command.";
			
			for (Map.Entry<String, XcraftGateGate> thisGate: plugin.gates.entrySet()) {
				if (thisGate.getValue().gateTarget == null) {
					boolean hasSource = false;
					for (Map.Entry<String, XcraftGateGate> sourceGate: plugin.gates.entrySet()) {
						if (sourceGate.getValue().gateTarget != null && sourceGate.getValue().gateTarget.equals(thisGate.getValue().gateName)) {
							hasSource = true;
						}
					}
					if (!hasSource)
						reply(player, "Found orphan: " + thisGate.getKey());
				}
			}
		} else if (args[0].equals("warp")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.gate.warp"))
				return "You don't have permission to use this command.";
			
			if (args.length < 2) {
				printUsage(player);
				return null;
			}
			
			if (plugin.gates.get(args[1]) == null) {
				error = "Gate not found: " + args[1];
			} else {
				plugin.gates.get(args[1]).portHere(player);
			}
		} else {
			printUsage(player);
		}
		
		return error;
	}

	public String parseWorld(Player player, String[] args) {
		String error = null;
		
		if (args.length == 0) {
			printWUsage(player);
		} else if (args[0].equals("create")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.world.create"))
				return "You don't have permission to use this command.";
			
			if (args.length < 2) {
				printWUsage(player);
				return null;
			}
			
			if (plugin.config.getString("worlds." + args[1] + ".type") != null) {
				error = "World " + args[1] + " already exists.";
			}
			
			if (args.length < 3 || args[2].equalsIgnoreCase("normal")) {
				plugin.config.setProperty("worlds." + args[1] + ".type", "normal");
				plugin.getServer().createWorld(args[1], World.Environment.NORMAL);
				reply(player, "World " + args[1] + " created with environment NORMAL.");
				plugin.config.save();
			} else if (args[2].equalsIgnoreCase("nether")) {
				plugin.config.setProperty("worlds." + args[1] + ".type", "nether");
				plugin.getServer().createWorld(args[1], World.Environment.NETHER);				
				reply(player, "World " + args[1] + " created with environment NETHER.");
				plugin.config.save();
			} else if (args[2].equalsIgnoreCase("skylands")) {
				plugin.config.setProperty("worlds." + args[1] + ".type", "skylands");
				plugin.getServer().createWorld(args[1], World.Environment.SKYLANDS);				
				reply(player, "World " + args[1] + " created with environment SKYLANDS.");
				plugin.config.save();
			} else {
				error = "Unknown environment: " + args[2];
			}
		} else if (args[0].equals("delete")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.world.delete"))
				return "You don't have permission to use this command.";
			
			if (args.length < 2) {
				printWUsage(player);
				return null;
			}
			
			if (plugin.config.getString("worlds." + args[1] + ".type") == null) {
				error = "World " + args[1] + " unknown.";
			}
			
			if (plugin.getServer().getWorld(args[1]) != null) {
				plugin.config.removeProperty("worlds." + args[1]);
				reply(player, "World " + args[1] + " removed.");
				plugin.getServer().unloadWorld(args[1], true);
				plugin.config.save();
			} else {
				error = "World " + args[1] + " known, but not loaded. This should not happen?!";
			}
		} else if (args[0].equals("warpto")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.world.warp"))
				return "You don't have permission to use this command.";
			
			if (args.length < 2) {
				printWUsage(player);
				return null;
			}
			
			if (plugin.config.getString("worlds." + args[1] + ".type") == null) {
				error = "World " + args[1] + " unknown.";
			}
			
			if (plugin.getServer().getWorld(args[1]) != null) {
				Location loc = player.getLocation();
				loc.setWorld(plugin.getServer().getWorld(args[1]));
				player.teleport(loc);
			} else {
				error = "World " + args[1] + " known, but not loaded. This should not happen?!";
			}
		} else if (args[0].equals("setborder")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.world.setborder"))
				return "You don't have permission to use this command.";
			
			if (args.length < 3) {
				printWUsage(player);
				return null;
			}
			
			if (plugin.config.getString("worlds." + args[1] + ".type") == null) {
				error = "World " + args[1] + " unknown.";
			}
			
			if (plugin.getServer().getWorld(args[1]) != null) {
				Integer border;
				try { border = new Integer(args[2]); } catch(Exception ex) { return "Invalid number: " + args[2]; }
				if (border <= 0) {
					plugin.config.removeProperty("worlds." + args[1] + ".border");
					reply(player, "Border of world " + args[1] + " removed.");
				} else {
					plugin.config.setProperty("worlds." + args[1] + ".border", border);
					reply(player, "Border of world " + args[1] + " set to " + border + ".");
				}
				plugin.config.save();
			} else {
				error = "World " + args[1] + " known, but not loaded. This should not happen?!";
			}
		} else if (args[0].equals("setcreaturelimit")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.world.setcreaturelimit"))
				return "You don't have permission to use this command.";
			
			if (args.length < 3) {
				printWUsage(player);
				return null;
			}
			
			if (plugin.config.getString("worlds." + args[1] + ".type") == null) {
				error = "World " + args[1] + " unknown.";
			}
			
			if (plugin.getServer().getWorld(args[1]) != null) {
				Integer limit;
				try { limit = new Integer(args[2]); } catch(Exception ex) { return "Invalid number: " + args[2]; }
				if (limit <= 0) {
					plugin.config.removeProperty("worlds." + args[1] + ".creatureLimit");
					reply(player, "Creature limit of world " + args[1] + " removed.");
				} else {
					plugin.config.setProperty("worlds." + args[1] + ".creatureLimit", limit);
					reply(player, "Creature limit of world " + args[1] + " set to " + limit + ".");
				}
				plugin.config.save();
			} else {
				error = "World " + args[1] + " known, but not loaded. This should not happen?!";
			}
		} else if (args[0].equals("allowanimals")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.world.setcreaturelimit"))
				return "You don't have permission to use this command.";
			
			if (args.length < 3) {
				printWUsage(player);
				return null;
			}
			
			if (plugin.config.getString("worlds." + args[1] + ".type") == null) {
				error = "World " + args[1] + " unknown.";
			}
			
			if (plugin.getServer().getWorld(args[1]) != null) {
				Boolean allowed;
				if (args[2].equalsIgnoreCase("true")) {
					allowed = true;
				} else if (args[2].equalsIgnoreCase("false")) {
					allowed = false;
				} else {
					printWUsage(player);
					return error;
				}
				
				if (allowed) {
					plugin.config.setProperty("worlds." + args[1] + ".animalsAllowed", true);
					plugin.creatureLimiter.allowAnimals(plugin.getServer().getWorld(args[1]));
					reply(player, "Animal spawn on " + args[1] + " enabled.");
				} else {
					plugin.config.setProperty("worlds." + args[1] + ".animalsAllowed", false);
					plugin.creatureLimiter.denyAnimals(plugin.getServer().getWorld(args[1]));
					plugin.creatureLimiter.killAllAnimals(plugin.getServer().getWorld(args[1]));
					reply(player, "Animal spawn on " + args[1] + " disabled.");
				}
				plugin.config.save();
			} else {
				error = "World " + args[1] + " known, but not loaded. This should not happen?!";
			}
		} else if (args[0].equals("allowmonsters")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.world.setcreaturelimit"))
				return "You don't have permission to use this command.";
			
			if (args.length < 3) {
				printWUsage(player);
				return null;
			}
			
			if (plugin.config.getString("worlds." + args[1] + ".type") == null) {
				error = "World " + args[1] + " unknown.";
			}
			
			if (plugin.getServer().getWorld(args[1]) != null) {
				Boolean allowed;
				if (args[2].equalsIgnoreCase("true")) {
					allowed = true;
				} else if (args[2].equalsIgnoreCase("false")) {
					allowed = false;
				} else {
					printWUsage(player);
					return error;
				}
				
				if (allowed) {
					plugin.config.setProperty("worlds." + args[1] + ".monstersAllowed", true);
					plugin.creatureLimiter.allowMonsters(plugin.getServer().getWorld(args[1]));
					reply(player, "Monster spawn on " + args[1] + " enabled.");
				} else {
					plugin.config.setProperty("worlds." + args[1] + ".monstersAllowed", false);
					plugin.creatureLimiter.denyMonsters(plugin.getServer().getWorld(args[1]));
					plugin.creatureLimiter.killAllMonsters(plugin.getServer().getWorld(args[1]));
					reply(player, "Monster spawn on " + args[1] + " disabled.");
				}
				plugin.config.save();
			} else {
				error = "World " + args[1] + " known, but not loaded. This should not happen?!";
			}
		} else if (args[0].equals("info")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.world.info"))
				return "You don't have permission to use this command.";
			
			if (args.length < 2) {
				printWUsage(player);
				return null;
			}
			
			if (plugin.config.getString("worlds." + args[1] + ".type") == null) {
				error = "World " + args[1] + " unknown.";
			}
			
			if (plugin.getServer().getWorld(args[1]) != null) {
				reply(player, "Infos for world " + args[1] + ":");
				player.sendMessage("Worldname: " + args[1]);
				player.sendMessage("Border: " + plugin.config.getInt("worlds." + args[1] + ".border", 0));
				player.sendMessage("Animals allowed: " + (plugin.config.getBoolean("worlds." + args[1] + ".animalsAllowed", true) ? "yes" : "no"));
				player.sendMessage("Monsters allowed: " + (plugin.config.getBoolean("worlds." + args[1] + ".monstersAllowed", true) ? "yes" : "no"));
				player.sendMessage("Creature limit: " + plugin.config.getInt("worlds." + args[1] + ".creatureLimit", 0));
				player.sendMessage("Creature count: " + (plugin.getServer().getWorld(args[1]).getLivingEntities().size() - plugin.getServer().getWorld(args[1]).getPlayers().size()));
				player.sendMessage("Player count: " + plugin.getServer().getWorld(args[1]).getPlayers().size());
			}
		} else if (args[0].equals("list")) {
			if (!plugin.hasOpPermission(player, "XcraftGate.world.info"))
				return "You don't have permission to use this command.";
			
			String worlds = "";
			for (World world: plugin.getServer().getWorlds()) {
				worlds += ", " + world.getName();
			}
			reply(player, "Worlds: " + ChatColor.WHITE + worlds.substring(2));
		} else {
			printWUsage(player);
		}

		return error;
	}
}
