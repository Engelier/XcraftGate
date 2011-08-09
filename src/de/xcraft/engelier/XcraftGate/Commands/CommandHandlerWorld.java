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

public class CommandHandlerWorld extends CommandHelper implements CommandExecutor {
	private static Map<String, CommandHelperWorld> subcommands = new HashMap<String, CommandHelperWorld>();
	private static Map<String, String> permNodes = new HashMap<String, String>();

	public CommandHandlerWorld(XcraftGate instance) {
		super(instance);
		
		permNodes.put("create", "create");
		permNodes.put("delete", "delete");
		permNodes.put("warpto", "warp");
		permNodes.put("setborder", "setborder");
		permNodes.put("setcreaturelimit", "setcreaturelimit");
		permNodes.put("allowanimals", "setcreaturelimit");
		permNodes.put("allowmonsters", "setcreaturelimit");
		permNodes.put("suppresshealthregain", "setcreaturelimit");
		permNodes.put("allowpvp", "pvp");
		permNodes.put("allowweatherchange", "weather");
		permNodes.put("setweather", "weather");
		permNodes.put("timefrozen", "time");
		permNodes.put("settime", "time");
		permNodes.put("info", "info");
		permNodes.put("list", "info");
		permNodes.put("listenv", "info");
		permNodes.put("listplayers", "info");

		subcommands.put("create", new CommandWorldCreate(plugin));
		subcommands.put("info", new CommandWorldInfo(plugin));
		subcommands.put("listenv", new CommandWorldListEnv(plugin));
		subcommands.put("list", new CommandWorldList(plugin));
		subcommands.put("delete", new CommandWorldDelete(plugin));
		subcommands.put("warpto", new CommandWorldWarpto(plugin));
		subcommands.put("setborder", new CommandWorldSetBorder(plugin));
		subcommands.put("setcreaturelimit", new CommandWorldSetCreatureLimit(plugin));
		subcommands.put("allowanimals", new CommandWorldAllowAnimals(plugin));
		subcommands.put("allowmonsters", new CommandWorldAllowMonsters(plugin));
		subcommands.put("allowpvp", new CommandWorldAllowPvP(plugin));
		subcommands.put("allowweatherchange", new CommandWorldAllowWeatherchange(plugin));
		subcommands.put("setweather", new CommandWorldSetWeather(plugin));
		subcommands.put("timefrozen", new CommandWorldTimeFrozen(plugin));
		subcommands.put("settime", new CommandWorldSetTime(plugin));
		subcommands.put("suppresshealthregain", new CommandWorldSuppressHealthregain(plugin));
		subcommands.put("listplayers", new CommandWorldListPlayers(plugin));
	}

	public void printUsage() {
		player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getNameBrackets() + "by Engelier");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld list");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld info <world>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld listenv");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld create <name> [normal|nether|skylands [seed]]");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld delete <name>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld warpto <name>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld setborder <world> <#>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld setcreaturelimit <world> <#>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld allowanimals <world> <true|false>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld allowmonsters <world> <true|false>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld allowpvp <world> <true|false>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld allowweatherchange <world> <true|false>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld setweather <world> <sun|storm>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld timefrozen <world> <true|false>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld settime <world> <sunrise|noon|sunset|midnight>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld suppresshealthregain <world> <true|false>");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN	+ "/gworld listplayers <world>");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,	String commandLabel, String[] args) {
		this.sender = sender;
		player = (sender instanceof Player) ? (Player) sender : null;
		
		if (!isPermitted("world", (args.length > 0 ? permNodes.get(args[0]) : null))) {
			error("You don't have permission to use this command");
		}
		
		if (args.length == 0) {
			printUsage();
			return true;
		}

		if (player == null && args[0].equals("warpto")) {
			error("/gworld warpto cannot be used from the console");
			return true;
		}
		
		if (subcommands.get(args[0].toLowerCase()) == null) {
			printUsage();
			error("Unkown gworld command: " + args[0].toLowerCase());
		} else {
			List<String> largs = Arrays.asList(args);
			largs = largs.subList(1, largs.size());
			
			subcommands.get(args[0].toLowerCase()).execute(
					sender,
					(largs.size() > 0 ? largs.get(0) : null),
					(largs.size() > 1 ? largs.subList(1, largs.size()) : new ArrayList<String>())
					);
		}
		
		return true;
	}
}
