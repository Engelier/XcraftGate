package de.xcraft.engelier.XcraftGate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.yaml.snakeyaml.Yaml;

public class SetGate implements Iterable<DataGate> {
	private static XcraftGate plugin;
	private Map<String, DataGate> gates = new HashMap<String, DataGate>();
	private Map<String, String> gateLocations = new HashMap<String, String>();
	
	public SetGate (XcraftGate plugin) {
		SetGate.plugin = plugin;
	}
	
	@SuppressWarnings("unchecked")
	public void load() {
		File configFile = plugin.getConfigFile("gates.yml");
		
		int counter = 0;

		try {
			Yaml yaml = new Yaml();
			Map<String, Object> gatesYaml = (Map<String, Object>) yaml.load(new FileInputStream(configFile));
			
			for (Map.Entry<String, Object> thisGate : gatesYaml.entrySet()) {
				String gateName = thisGate.getKey();
				Map<String, Object> gateData = (Map<String, Object>) thisGate.getValue();

				DataGate newGate = new DataGate(plugin, gateName);
				newGate.setLocation(
						(String) gateData.get("world"),
						(Double) gateData.get("locX"),
						(Double) gateData.get("locY"),
						(Double) gateData.get("locZ"),
						((Double) gateData.get("locYaw")).floatValue(),
						((Double) gateData.get("locP")).floatValue());


				add(newGate);
				counter++;
			}

			for (Map.Entry<String, Object> thisGate : gatesYaml.entrySet()) {
				String gateName = thisGate.getKey();
				Map<String, Object> gateData = (Map<String, Object>) thisGate.getValue();

				if (gateData.get("target") != null) {
					DataGate thisTarget = get((String) gateData.get("target"));
					
					if (thisTarget == null) {
						plugin.log.warning(plugin.getNameBrackets() + "ignored invalid destination for gate " + gateName);
					} else {
						get(gateName).linkTo(thisTarget, false);
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		plugin.log.info(plugin.getNameBrackets() + "loaded " + counter + " gates");
	}

	public void save() {
		File configFile = plugin.getConfigFile("gates.yml");

		Map<String, Object> toDump = new HashMap<String, Object>();

		for (DataGate thisGate : gates.values()) {
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

	public void reload() {
		gates.clear();
		gateLocations.clear();
		load();
		
		for (DataGate thisGate : gates.values()) {
			if (plugin.getServer().getWorld(thisGate.getWorldName()) != null) {
				gateLocations.put(Util.getLocationString(thisGate.getLocation()), thisGate.getName());
			}
		}
	}
	
	public void add(DataGate gate) {
		add(gate, false);
	}
	
	public void add(DataGate gate, boolean save) {
		gates.put(gate.getName(), gate);
		gateLocations.put(Util.getLocationString(gate.getLocation()), gate.getName());
		
		resetSuperPermission(gate.getName());
		if (save) save();
	}

	public void remove(String gateName) {
		remove(get(gateName));
	}
	
	public void remove(DataGate gate) {
		gates.remove(gate.getName());
		
		if (plugin.getWorlds().get(gate.getWorldName()).isLoaded()) {
			gateLocations.remove(Util.getLocationString(gate.getLocation()));
		}
		
		save();
	}
	
	public boolean has(String name) {
		return gates.containsKey(name);
	}
	
	public DataGate get(String gateName) {
		return gates.get(gateName);
	}
	
	public DataGate getByLocation(Location loc) {
		String gateName = gateLocations.get(Util.getLocationString(loc));
		return get(gateName);
	}
	
	public void resetSuperPermission(String gatePerm) {
		PluginManager pm = plugin.getServer().getPluginManager();
		gatePerm = "XcraftGate.use." + gatePerm;
		
		if (pm.getPermission(gatePerm) == null) {
			pm.addPermission(new Permission(gatePerm, PermissionDefault.TRUE));
		}
		
		Permission superPerm = pm.getPermission("XcraftGate.use.*");
		if (superPerm != null) {
			if (superPerm.getChildren().containsKey(gatePerm)) return;
			pm.removePermission("xcraftgate.use.*");	
		}

		String descr = "Permission to use all gates";
		
		Map<String, Boolean> children = new HashMap<String, Boolean>();
		
		for (String name : gates.keySet()) {
			children.put("XcraftGate.use." + name, true);
		}
		
		superPerm = new Permission("XcraftGate.use.*", descr, (superPerm != null ? superPerm.getDefault() : PermissionDefault.TRUE), children);
		pm.addPermission(superPerm);
	}
	
	public void onWorldLoad(World world) {
		onWorldLoad(plugin.getWorlds().get(world));
	}
	
	public void onWorldLoad(DataWorld world) {
		int gateCounter = 0;
		
		for (DataGate thisGate : gates.values()) {
			if (thisGate.getWorldName().equalsIgnoreCase(world.getName())) {
				gateLocations.put(Util.getLocationString(thisGate.getLocation()), thisGate.getName());
				gateCounter++;
			}
		}
		
		plugin.log.info(plugin.getNameBrackets() + "loaded " + gateCounter + " gates for world '" + world.getName() + "'");
	}
	
	public void onWorldUnload(World world) {
		plugin.getWorlds().get(world);
	}
	
	public void onWorldUnload(DataWorld world) {
		for (DataGate thisGate : gates.values()) {
			if (thisGate.getWorldName().equalsIgnoreCase(world.getName())) {
				gateLocations.remove(Util.getLocationString(thisGate.getLocation()));
			}
		}		
	}
	
	public int size() {
		return gates.size();
	}
	
	public Object[] toArray() {
		return gates.values().toArray();
	}
	
	public Object[] namesArray() {
		return gates.keySet().toArray();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<DataGate> iterator() {
		return (Iterator<DataGate>) gates.values();
	}

}
