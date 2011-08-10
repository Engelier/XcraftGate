package de.xcraft.engelier.XcraftGate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.yaml.snakeyaml.Yaml;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import de.xcraft.engelier.XcraftGate.XcraftGateWorld.Weather;
import de.xcraft.engelier.XcraftGate.Commands.*;
import de.xcraft.engelier.XcraftGate.Generator.Generator;

public class XcraftGate extends JavaPlugin {
	private final XcraftGatePluginListener pluginListener = new XcraftGatePluginListener(this);
	private final XcraftGatePlayerListener playerListener = new XcraftGatePlayerListener(this);
	private final XcraftGateCreatureListener creatureListener = new XcraftGateCreatureListener(this);
	private final XcraftGateEntityListener entityListener = new XcraftGateEntityListener(this);
	private final XcraftGateWeatherListener weatherListener = new XcraftGateWeatherListener(this);
	private final XcraftGateWorldListener worldListener = new XcraftGateWorldListener(this);

	private Map<String, XcraftGateWorld> worlds = new HashMap<String, XcraftGateWorld>();
	private Map<String, XcraftGateGate> gates = new HashMap<String, XcraftGateGate>();
	private Map<String, String> gateLocations = new HashMap<String, String>();
	
	public PermissionHandler permissions = null;
	public Configuration config = null;

	public Map<String, Location> justTeleported = new HashMap<String, Location>();
	public Map<String, Location> justTeleportedFrom = new HashMap<String, Location>();
	public Map<String, Integer> creatureCounter = new HashMap<String, Integer>();

	public final Logger log = Logger.getLogger("Minecraft");
	public final Properties serverconfig = new Properties(); 

	class RunCreatureLimit implements Runnable {
		public void run() {
			for (XcraftGateWorld thisWorld: worlds.values()) {
				thisWorld.checkCreatureLimit();
			}
		}
	}
	
	class RunTimeFrozen implements Runnable {
		public void run() {
			for (XcraftGateWorld thisWorld: worlds.values()) {
				if (thisWorld.isTimeFrozen()) {
					thisWorld.resetFrozenTime();
				}
			}
		}
	}
	
	class RunCheckWorldInactive implements Runnable {
		@Override
		public void run() {
			for (World thisWorld : getServer().getWorlds()) {
				if (worlds.get(thisWorld.getName()).checkInactive() && !thisWorld.getName().equalsIgnoreCase(serverconfig.getProperty("level-name"))) {
					log.info(getNameBrackets() + "World '" + thisWorld.getName() + "' inactive. Unloading.");
					
					for (XcraftGateGate thisGate : gates.values()) {
						if (thisGate.getWorldName().equalsIgnoreCase(thisWorld.getName())) {
							gateLocations.remove(getLocationString(thisGate.getLocation()));
						}
					}

					worlds.get(thisWorld.getName()).unload();
				}
			}						
		}		
	}
	
	class RunLoadAllWorlds implements Runnable {
		@Override
		public void run() {
			for (XcraftGateWorld thisWorld : worlds.values()) {
				if (!thisWorld.isLoaded() && (config.getBoolean("dynworld.enabled", true) == false || thisWorld.isSticky())) {
					thisWorld.load();
				}
			}
		}		
	}
	
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		saveGates();
		saveWorlds();
	}

	public void onEnable() {
		PluginManager pm = this.getServer().getPluginManager();

		pm.registerEvent(Event.Type.CREATURE_SPAWN, creatureListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_REGAIN_HEALTH, entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLUGIN_DISABLE, pluginListener,	Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.WEATHER_CHANGE, weatherListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.WORLD_LOAD, worldListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.WORLD_UNLOAD, worldListener, Event.Priority.Highest, this);

		Plugin permissionsCheck = pm.getPlugin("Permissions");
		if (permissionsCheck != null && permissionsCheck.isEnabled()) {
			permissions = ((Permissions) permissionsCheck).getHandler();
			log.info(getNameBrackets() + "hooked into Permissions "
					+ permissionsCheck.getDescription().getVersion());
		}
		
		File serverconfigFile = new File("server.properties");
		if (!serverconfigFile.exists()) {
			log.severe(getNameBrackets() + "unable to load server.properties.");
		} else {
			try {
				serverconfig.load(new FileInputStream(serverconfigFile));
			} catch (Exception ex) {
				log.severe(getNameBrackets() + "error loading " + serverconfigFile);
				ex.printStackTrace();
			}
		}

		log.info(getNameBrackets() + "by Engelier loaded.");

		config = getConfiguration();
		setConfigDefaults();
		loadWorlds();
		loadGates();

		for(World thisWorld : getServer().getWorlds()) {
			checkWorld(thisWorld);
		}
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new RunCreatureLimit(), 600, 600);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new RunTimeFrozen(), 200, 200);
		
		if (config.getBoolean("dynworld.enabled", true)) {
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new RunCheckWorldInactive(), config.getInt("dynworld.checkInterval", 60) * 20, config.getInt("dynworld.checkInterval", 60) * 20);
		}
		
		getServer().getScheduler().scheduleSyncDelayedTask(this, new RunLoadAllWorlds());
		
		try {
			getCommand("gate").setExecutor(new CommandHandlerGate(this));
			getCommand("gworld").setExecutor(new CommandHandlerWorld(this));
		} catch (Exception ex) {
			log.warning(getNameBrackets() + "getCommand().setExecutor() failed! Seems I got enabled by another plugin. Nag the bukkit team about this!");
		}
	}
	
	private void setConfigDefaults() {
		config.getBoolean("dynworld.enabled", true);
		config.getInt("dynworld.checkInterval", 60);
		config.getInt("dynworld.maxInactiveTime", 300);
		
		config.getInt("biomes.desert.chanceCactus", 1);
		config.getInt("biomes.desert.chanceDeadShrub", 2);
		config.getInt("biomes.forest.chanceLakeWater", 1);
		config.getInt("biomes.forest.chanceTreeNormal", 32);
		config.getInt("biomes.forest.chanceTreeBig", 2);
		config.getInt("biomes.forest.chanceTreeBirch", 32);
		config.getInt("biomes.forest.chanceTreeRedwood", 16);
		config.getInt("biomes.forest.chanceTreeTallRedwood", 2);
		config.getInt("biomes.forest.chanceFlowerYellow", 4);
		config.getInt("biomes.forest.chanceFlowerRedRose", 4);
		config.getInt("biomes.forest.chanceGrassTall", 50);
		config.getInt("biomes.plains.chanceTreeNormal", 1);
		config.getInt("biomes.plains.chanceFlowerYellow", 10);
		config.getInt("biomes.plains.chanceFlowerRedRose", 10);
		config.getInt("biomes.plains.chanceGrassTall", 150);
		config.getInt("biomes.rainforest.chanceLakeWater", 3);
		config.getInt("biomes.rainforest.chanceTreeNormal", 28);
		config.getInt("biomes.rainforest.chanceTreeBig", 2);
		config.getInt("biomes.rainforest.chanceTreeBirch", 28);
		config.getInt("biomes.rainforest.chanceTreeRedwood", 32);
		config.getInt("biomes.rainforest.chanceTreeTallRedwood", 2);
		config.getInt("biomes.rainforest.chanceFlowerYellow", 5);
		config.getInt("biomes.rainforest.chanceFlowerRedRose", 5);
		config.getInt("biomes.rainforest.chanceGrassFern", 30);
		config.getInt("biomes.rainforest.chanceGrassTall", 70);
		config.getInt("biomes.savanna.chanceTreeNormal", 1);
		config.getInt("biomes.seasonalforest.chanceLakeWater", 2);
		config.getInt("biomes.seasonalforest.chanceTreeNormal", 32);
		config.getInt("biomes.seasonalforest.chanceTreeBig", 2);
		config.getInt("biomes.seasonalforest.chanceTreeBirch", 32);
		config.getInt("biomes.seasonalforest.chanceTreeRedwood", 28);
		config.getInt("biomes.seasonalforest.chanceTreeTallRedwood", 2);
		config.getInt("biomes.seasonalforest.chanceFlowerYellow", 4);
		config.getInt("biomes.seasonalforest.chanceFlowerRedRose", 4);
		config.getInt("biomes.seasonalforest.chanceGrassTall", 70);
		config.getInt("biomes.shrubland.chanceLakeLava", 1);
		config.getInt("biomes.shrubland.chanceTreeNormal", 3);
		config.getInt("biomes.shrubland.chanceGrassShrub", 5);
		config.getInt("biomes.swampland.chanceSugarCane", 75);
		config.getInt("biomes.swampland.chanceLakeWater", 10);
		config.getInt("biomes.taiga.chanceTreeRedwood", 4);
		config.getInt("biomes.taiga.chanceGrassTall", 2);
		config.getInt("biomes.tundra.chanceLakeWater", 1);
		
		config.save();
	}

	public boolean onCommand(CommandSender sender, Command cmd,	String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("gate")) {
			getCommand("gate").setExecutor(new CommandHandlerGate(this));
			getCommand("gate").execute(sender, commandLabel, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("gworld")) {
			getCommand("gworld").setExecutor(new CommandHandlerWorld(this));						
			getCommand("gworld").execute(sender, commandLabel, args);
			return true;
		} else {
			return false;
		}
	}
	
	public void resetSuperPermission(String gatePerm) {
		gatePerm = "XcraftGate.use." + gatePerm;
		
		Permission superPerm = getServer().getPluginManager().getPermission("XcraftGate.use.*");
		if (superPerm != null) {
			if (superPerm.getChildren().containsKey(gatePerm)) return;
			getServer().getPluginManager().removePermission("xcraftgate.use.*");	
		}

		String descr = "Permission to use all gates";
		
		Map<String, Boolean> children = new HashMap<String, Boolean>();
		
		for (String name : gates.keySet()) {
			children.put("XcraftGate.use." + name, true);
		}
		
		superPerm = new Permission("XcraftGate.use.*", descr, (superPerm != null ? superPerm.getDefault() : PermissionDefault.TRUE), children);
		getServer().getPluginManager().addPermission(superPerm);
	}

	public String getLocationString(Location location) {
		if (location.getWorld() != null) {
			return location.getWorld().getName() + ","
					+ Math.floor(location.getX()) + ","
					+ Math.floor(location.getY()) + ","
					+ Math.floor(location.getZ());
		} else {
			return null;
		}
	}

	public Location getSaneLocation(Location loc) {
		double x = Math.floor(loc.getX()) + 0.5;
		double y = loc.getY();
		double z = Math.floor(loc.getZ()) + 0.5;
		
		return new Location(loc.getWorld(), x, y, z, loc.getYaw(), loc.getPitch());
	}

	public String getNameBrackets() {
		return "[" + this.getDescription().getFullName() + "] ";
	}
	
	public void checkWorld(World world) {
		if (worlds.get(world.getName()) != null) {
			log.info(getNameBrackets() + "World '" + world.getName() + "' loaded. Applying config.");
			worlds.get(world.getName()).setWorld(world);
			worlds.get(world.getName()).setParameters();
		} else {
			log.info(getNameBrackets() + "World '" + world.getName() + "' detected. Adding to config.");
			XcraftGateWorld newWorld = new XcraftGateWorld(this, world.getName(), world.getEnvironment());
			worlds.put(world.getName(), newWorld);
			saveWorlds();
		}

		int gateCounter = 0;
		
		for (XcraftGateGate thisGate : gates.values()) {
			if (thisGate.getWorldName().equalsIgnoreCase(world.getName())) {
				gateLocations.put(getLocationString(thisGate.getLocation()), thisGate.getName());
				gateCounter++;
			}
		}
		
		log.info(getNameBrackets() + "loaded " + gateCounter + " gates for world '" + world.getName() + "'");
	}

	@SuppressWarnings("unchecked")
	private void loadWorlds() {
		File configFile = new File(getDataFolder(), "worlds.yml");
	
		if (!configFile.exists()) {
			getDataFolder().mkdir();
			getDataFolder().setWritable(true);
			getDataFolder().setExecutable(true);

			try {
				configFile.createNewFile();
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}

		try {
			Yaml yaml = new Yaml();
			Map<String, Object> worldsYaml = (Map<String, Object>) yaml.load(new FileInputStream(configFile));
			
			XcraftGateWorld newWorld;
			
			for (Map.Entry<String, Object> thisWorld : worldsYaml.entrySet()) {
				String worldName = thisWorld.getKey();
				Map<String, Object> worldData = (Map<String, Object>) thisWorld.getValue();

				Environment env = null;
				Generator gen = null;
				
				String checkEnv = (String) worldData.get("type");
				
				for(Environment thisEnv: World.Environment.values()) {
					if (thisEnv.toString().equalsIgnoreCase(checkEnv)) {
						env = thisEnv;
					}
				}
				
				if (env == null) env = World.Environment.NORMAL;

				String checkGen = (String) worldData.get("generator");
				
				for (Generator thisGen : Generator.values()) {
					if (thisGen.toString().equalsIgnoreCase(checkGen)) {
						gen = thisGen;
					}
				}
				
				newWorld = new XcraftGateWorld(this, worldName, env, gen);
				
				newWorld.setBorder((Integer)worldData.get("border"));
				newWorld.setAllowPvP((Boolean)worldData.get("allowPvP"));
				newWorld.setAllowAnimals((Boolean)worldData.get("allowAnimals"));
				newWorld.setAllowMonsters((Boolean)worldData.get("allowMonsters"));
				newWorld.setCreatureLimit(castInt(worldData.get("creatureLimit")));
				newWorld.setAllowWeatherChange((Boolean)worldData.get("allowWeatherChange"));
				newWorld.setTimeFrozen((Boolean)worldData.get("timeFrozen"));
				newWorld.setDayTime(castInt(worldData.get("setTime")));
				newWorld.setSuppressHealthRegain((Boolean)worldData.get("suppressHealthRegain"));
				newWorld.setSticky((Boolean)worldData.get("sticky"));
				
				worlds.put(worldName, newWorld);

				String weather = (String) worldData.get("setWeather");
				for(Weather thisWeather: XcraftGateWorld.Weather.values()) {
					if (thisWeather.toString().equalsIgnoreCase(weather)) {
						newWorld.setWeather(thisWeather);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}

	public void saveWorlds() {
		File configFile = new File(getDataFolder(), "worlds.yml");
		
		Map<String, Object> toDump = new HashMap<String, Object>();

		for (Map.Entry<String, XcraftGateWorld> thisWorld : worlds.entrySet()) {
			toDump.put(thisWorld.getKey(), thisWorld.getValue().toMap());
		}

		Yaml yaml = new Yaml();
		String dump = yaml.dump(toDump);

		try {
			FileOutputStream fh = new FileOutputStream(configFile);
			new PrintStream(fh).println(dump);
			fh.flush();
			fh.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Collection<XcraftGateWorld> getWorldCollection() {
		return worlds.values();
	}
	
	public void addWorld(String worldName, XcraftGateWorld world) {
		worlds.put(worldName, world);
	}
	
	public void delWorld(String worldName) {
		worlds.remove(worldName);
	}
	
	public XcraftGateWorld getWorld(World world) {
		return getWorld(world.getName());
	}
	 
	public XcraftGateWorld getWorld(String name) {
		return worlds.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public void loadGates() {
		File configFile = new File(getDataFolder(), "gates.yml");
		int counter = 0;

		if (!configFile.exists()) {
			log.warning(getNameBrackets()
					+ "gates file not found. Create some gates!");
			return;
		}

		try {
			Yaml yaml = new Yaml();
			Map<String, Object> gatesYaml = (Map<String, Object>) yaml
					.load(new FileInputStream(configFile));
			for (Map.Entry<String, Object> thisGate : gatesYaml.entrySet()) {
				String gateName = thisGate.getKey();
				Map<String, Object> gateData = (Map<String, Object>) thisGate.getValue();

				if (worlds.get((String) gateData.get("world")) == null) {
					log.severe(getNameBrackets() + "gate " + gateName
							+ " found in unkown world " + gateData.get("world")
							+ ". REMOVED!");
					continue;
				}


				XcraftGateGate newGate = new XcraftGateGate(this, gateName);
				newGate.setLocation(
						(String) gateData.get("world"),
						(Double) gateData.get("locX"),
						(Double) gateData.get("locY"),
						(Double) gateData.get("locZ"),
						((Double) gateData.get("locYaw")).floatValue(),
						((Double) gateData.get("locP")).floatValue());


				gates.put(gateName, newGate);
				counter++;
			}

			for (Map.Entry<String, Object> thisGate : gatesYaml.entrySet()) {
				String gateName = thisGate.getKey();
				Map<String, Object> gateData = (Map<String, Object>) thisGate.getValue();

				if (gateData.get("target") != null) {
					XcraftGateGate thisTarget = getGate((String) gateData.get("target"));
					
					if (thisTarget == null) {
						log.warning(getNameBrackets() + "ignored invalid destination for gate " + gateName);
					} else {
						getGate(gateName).linkTo(thisTarget, false);
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		log.info(getNameBrackets() + "loaded " + counter + " gates");
	}

	public void saveGates() {
		File configFile = new File(getDataFolder(), "gates.yml");

		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}

		Map<String, Object> toDump = new HashMap<String, Object>();

		for (XcraftGateGate thisGate : gates.values()) {
			toDump.put(thisGate.getName(), thisGate.toMap());
		}

		Yaml yaml = new Yaml();
		String dump = yaml.dump(toDump);
		
		try {
			FileOutputStream fh = new FileOutputStream(configFile);
			new PrintStream(fh).println(dump);
			fh.flush();
			fh.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void reloadGates() {
		gates.clear();
		gateLocations.clear();
		loadGates();
		
		for (XcraftGateGate thisGate : gates.values()) {
			if (getServer().getWorld(thisGate.getWorldName()) != null) {
				gateLocations.put(getLocationString(thisGate.getLocation()), thisGate.getName());
			}
		}
	}
	
	public void addGate(XcraftGateGate gate) {
		addGate(gate, false);
	}
	
	public void addGate(XcraftGateGate gate, boolean save) {
		gates.put(gate.getName(), gate);
		gateLocations.put(getLocationString(gate.getLocation()), gate.getName());
		if (save) saveGates();
	}

	public void delGate(String gateName) {
		delGate(getGate(gateName));
	}
	
	public void delGate(XcraftGateGate gate) {
		gates.remove(gate.getName());
		
		if (getWorld(gate.getWorldName()).isLoaded()) {
			gateLocations.remove(getLocationString(gate.getLocation()));
		}
		
		saveGates();
	}
	
	public boolean hasGate(String name) {
		return gates.containsKey(name);
	}
	
	public XcraftGateGate getGate(String gateName) {
		return gates.get(gateName);
	}
	
	public XcraftGateGate getGateByLocation(Location loc) {
		String gateName = gateLocations.get(getLocationString(loc));
		return getGate(gateName);
	}
	
	public Collection<XcraftGateGate> getGateCollection() {
		return gates.values();
	}
	
	private static Integer castInt(Object o) {
		if (o == null) {
			return 0;
		} else if (o instanceof Byte) {
			return (int)(Byte)o;
		} else if (o instanceof Integer) {
			return (Integer)o;
		} else if (o instanceof Double) {
			return (int)(double)(Double)o;
		} else if (o instanceof Float) {
			return (int)(float)(Float)o;
		} else if (o instanceof Long) {
			return (int)(long)(Long)o;
		} else {
			return 0;
		}
	}
	
	public Boolean castBoolean(Object o) {
		if (o == null) {
			return false;
		} else if (o instanceof Boolean) {
			return (Boolean)o;
		} else if (o instanceof String) {
			return ((String)o).equalsIgnoreCase("true") ? true : false;
		} else {
			return false;
		}
	}
	
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		for (Generator thisGen : Generator.values()) {
			if (thisGen.toString().equalsIgnoreCase(id)) {
				return thisGen.getChunkGenerator(this);
			}
		}

		return null;
	}
}
