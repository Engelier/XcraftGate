package de.xcraft.engelier.XcraftGate;

import java.util.Map;

import org.bukkit.ChatColor;
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
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/gate warp <name>" + ChatColor.WHITE + " | " + ChatColor.AQUA + "teleporst you to gate <name>");
	}

	public void reply(Player player, String message) {
		player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getNameBrackets() + ChatColor.DARK_AQUA + message);		
	}
	
	public String parse(Player player, String[] args) {
		String error = null;
		
		if (!plugin.hasOpPermission(player, "XcraftGate.admin"))
			return "You don't have permission to use this command.";
		
		if (args.length == 0) {
			printUsage(player);
		} else if (args[0].equals("create")) {
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
}
