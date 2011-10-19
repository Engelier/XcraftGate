package de.xcraft.engelier.XcraftGate.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandHandlerGate extends CommandHelper implements CommandExecutor {
	private static Map<String, CommandHelperGate> subcommands = new HashMap<String, CommandHelperGate>();
	private static Map<String, String> permNodes = new HashMap<String, String>();

	public CommandHandlerGate(XcraftGate instance) {
		super(instance);
				
		permNodes.put("create", "create");
		permNodes.put("move", "create");
		permNodes.put("rename", "create");
		permNodes.put("link", "link");
		permNodes.put("loop", "link");
		permNodes.put("unlink", "unlink");
		permNodes.put("unloop", "unlink");
		permNodes.put("delete", "delete");
		permNodes.put("info", "info");
		permNodes.put("list", "info");
		permNodes.put("listnear", "info");
		permNodes.put("listsolo", "info");
		permNodes.put("warp", "warp");
		permNodes.put("reload", "reload");
		permNodes.put("setdenysilent", "denysilent");
		permNodes.put("settoll", "toll");
		
		subcommands.put("create", new CommandGateCreate(plugin));
		subcommands.put("move", new CommandGateMove(plugin));
		subcommands.put("rename", new CommandGateRename(plugin));
		subcommands.put("link", new CommandGateLink(plugin));
		subcommands.put("loop", new CommandGateLoop(plugin));
		subcommands.put("unlink", new CommandGateUnlink(plugin));
		subcommands.put("unloop", new CommandGateUnloop(plugin));
		subcommands.put("delete", new CommandGateDelete(plugin));
		subcommands.put("info", new CommandGateInfo(plugin));
		subcommands.put("list", new CommandGateList(plugin));
		subcommands.put("listnear", new CommandGateListnear(plugin));
		subcommands.put("listsolo", new CommandGateListsolo(plugin));
		subcommands.put("warp", new CommandGateWarp(plugin));
		subcommands.put("reload", new CommandGateReload(plugin));
		subcommands.put("setdenysilent", new CommandGateSetDenySilent(plugin));
		subcommands.put("settoll", new CommandGateSetToll(plugin));
	}
	
	public void printUsage() {
		sender.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getNameBrackets() + "by Engelier");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate info <name>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate create <name>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate move <name>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate rename <name> <new_name>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate link <name1> <name2>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate loop <name1> <name2>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate unlink <name>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate unloop <name1> <name2>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate delete <name>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate settoll <name> <amount>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate setdenysilent <name> <true|false>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate list");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate listnear [radius]");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate listsolo");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gate warp <name>");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd,	String commandLabel, String[] args) {
		this.sender = sender;
		player = (sender instanceof Player) ? (Player) sender : null;
		
		if (player == null) {
			error("/gate cannot be used from the console");
			return true;
		}
		
		if (!isPermitted("gate", (args.length > 0 ? permNodes.get(args[0]) : null))) {
			error("You don't have permission to use this command");
			return true;
		}
		
		if (args.length == 0) {
			printUsage();
			return true;
		}
		
		if (subcommands.get(args[0].toLowerCase()) == null) {
			printUsage();
			error("Unkown gate command: " + args[0].toLowerCase());
		} else {
			List<String> largs = Arrays.asList(args);
			largs = largs.subList(1, largs.size());
			String gateName = (largs.size() > 0 ? largs.get(0) : null);
			
			subcommands.get(args[0].toLowerCase()).execute(sender, gateName, (largs.size() > 1 ? largs.subList(1, largs.size()) : new ArrayList<String>()));
		}
		
		return true;
	}
}
