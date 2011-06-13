package de.xcraft.engelier.XcraftGate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import de.xcraft.engelier.XcraftGate.Commands.*;

public class XcraftGate extends JavaPlugin {
	private XcraftGatePluginListener pluginListener = new XcraftGatePluginListener(
			this);
	private XcraftGatePlayerListener playerListener = new XcraftGatePlayerListener(
			this);
	private XcraftGateCreatureListener creatureListener = new XcraftGateCreatureListener(
			this);

	public PermissionHandler permissions = null;

	public Map<String, XcraftGateWorld> worlds = new HashMap<String, XcraftGateWorld>();
	public Map<String, XcraftGateGate> gates = new HashMap<String, XcraftGateGate>();
	public Map<String, String> gateLocations = new HashMap<String, String>();
	public Map<String, Boolean> justTeleported = new HashMap<String, Boolean>();
	public Map<String, Integer> creatureCounter = new HashMap<String, Integer>();

	public Logger log = Logger.getLogger("Minecraft");

	class RunCreatureLimit implements Runnable {
		public void run() {
			for(Map.Entry<String, XcraftGateWorld> thisWorld: worlds.entrySet()) {
				thisWorld.getValue().checkCreatureLimit();
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

		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CREATURE_SPAWN, creatureListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener,
				Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLUGIN_DISABLE, pluginListener,
				Event.Priority.Monitor, this);

		Plugin permissionsCheck = pm.getPlugin("Permissions");
		if (permissionsCheck != null && permissionsCheck.isEnabled()) {
			permissions = ((Permissions) permissionsCheck).getHandler();
			log.info(getNameBrackets() + "hooked into Permissions "
					+ permissionsCheck.getDescription().getVersion());
		}

		log.info(getNameBrackets() + "by Engelier loaded.");

		loadWorlds();
		loadGates();

		getServer().getScheduler().scheduleAsyncRepeatingTask(this,	new RunCreatureLimit(), 600, 600);
		getCommand("gate").setExecutor(new CommandGate(this));
		getCommand("gworld").setExecutor(new CommandWorld(this));
	}

	public Boolean hasOpPermission(Player player, String permission) {
		if (permissions != null) {
			return permissions.has(player, permission);
		} else {
			return player.isOp();
		}
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

	public void createGate(Location location, String name) {
		XcraftGateGate newGate = new XcraftGateGate(this, name, location);
		gates.put(name, newGate);
		gateLocations.put(getLocationString(location), name);
		saveGates();
	}

	public void createGateLink(String source, String destination) {
		gates.get(source).gateTarget = destination;
		saveGates();
	}

	public void createGateLoop(String gate1, String gate2) {
		createGateLink(gate1, gate2);
		createGateLink(gate2, gate1);
	}

	public void removeGateLink(String gate) {
		gates.get(gate).gateTarget = null;
		saveGates();
	}

	public void removeGateLoop(String gate1, String gate2) {
		removeGateLink(gate1);
		removeGateLink(gate2);
	}

	public String getNameBrackets() {
		return "[" + this.getDescription().getFullName() + "] ";
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
			for (Map.Entry<String, Object> thisWorld : ((Map<String, Object>)worldsYaml.get("worlds")).entrySet()) {
				String worldName = thisWorld.getKey();
				Map<String, Object> worldData = (Map<String, Object>) thisWorld.getValue();

				String env = (String) worldData.get("type");
				
				XcraftGateWorld newWorld = new XcraftGateWorld(this);
				for(Environment thisEnv: World.Environment.values()) {
					if (thisEnv.toString().equalsIgnoreCase(env)) {
						newWorld.load(worldName, thisEnv);
					}
				}
				
				if (newWorld.name == null) newWorld.load(worldName, World.Environment.NORMAL);

				newWorld.setBorder((Integer)worldData.get("border"));
				newWorld.setAllowAnimals((Boolean)worldData.get("allowAnimals"));
				newWorld.setAllowMonsters((Boolean)worldData.get("allowMonsters"));
				newWorld.setCreatureLimit((Integer)worldData.get("creatureLimit"));

				worlds.put(worldName, newWorld);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void saveWorlds() {
		File configFile = new File(getDataFolder(), "worlds.yml");
		
		Map<String, Object> toDump = new HashMap<String, Object>();

		for (Map.Entry<String, XcraftGateWorld> thisWorld : worlds.entrySet()) {
			String worldName = thisWorld.getKey();

			Map<String, Object> values = new HashMap<String, Object>();
			values.put("name", thisWorld.getValue().name);
			values.put("type", thisWorld.getValue().environment.toString());
			values.put("creatureLimit", thisWorld.getValue().creatureLimit);
			values.put("allowAnimals", thisWorld.getValue().allowAnimals);
			values.put("allowMonsters", thisWorld.getValue().allowMonsters);

			toDump.put(worldName, values);
		}

		Yaml yaml = new Yaml();
		String dump = yaml.dump(new HashMap<String, Object>().put("worlds", toDump));

		try {
			FileOutputStream fh = new FileOutputStream(configFile);
			new PrintStream(fh).println(dump);
			fh.flush();
			fh.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadGates() {
		File configFile = new File(getDataFolder(), "gates.yml");

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
				Map<String, Object> gateData = (Map<String, Object>) thisGate
						.getValue();

				if (getServer().getWorld((String) gateData.get("world")) == null) {
					log.severe(getNameBrackets() + "gate " + gateName
							+ " found in unkown world " + gateData.get("world")
							+ ". REMOVED!");
					continue;
				}

				Location gateLocation = new Location(getServer().getWorld(
						(String) gateData.get("world")),
						(Double) gateData.get("locX"),
						(Double) gateData.get("locY"),
						(Double) gateData.get("locZ"),
						((Double) gateData.get("locYaw")).floatValue(),
						((Double) gateData.get("locP")).floatValue());

				XcraftGateGate newGate = new XcraftGateGate(this, gateName,
						gateLocation);
				if (gateData.get("target") != null)
					newGate.gateTarget = (String) gateData.get("target");

				gates.put(gateName, newGate);
				gateLocations.put(getLocationString(gateLocation), gateName);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for (Map.Entry<String, XcraftGateGate> thisGate : gates.entrySet()) {
			if (gates.get(thisGate.getValue().gateTarget) == null) {
				log.severe(getNameBrackets() + "gate " + thisGate.getKey()
						+ " has an invalid destination. Destination removed.");
				thisGate.getValue().gateTarget = null;
			}
		}

	}

	private void saveGates() {
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

		for (Map.Entry<String, XcraftGateGate> thisGate : gates.entrySet()) {
			String gateName = thisGate.getKey();

			Map<String, Object> values = new HashMap<String, Object>();
			Location location = gates.get(gateName).gateLocation;

			values.put("world", location.getWorld().getName());
			values.put("locX", location.getX());
			values.put("locY", location.getY());
			values.put("locZ", location.getZ());
			values.put("locP", location.getPitch());
			values.put("locYaw", location.getYaw());
			values.put("target", gates.get(gateName).gateTarget);

			toDump.put(gateName, values);
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

}
