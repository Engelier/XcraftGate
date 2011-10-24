package de.xcraft.engelier.XcraftGate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.yaml.snakeyaml.Yaml;

import de.xcraft.engelier.XcraftGate.DataWorld.Weather;
import de.xcraft.engelier.XcraftGate.Generator.Generator;

public class SetWorld implements Iterable<DataWorld> {
	private static XcraftGate plugin;
	private Map<String, DataWorld> worlds = new HashMap<String, DataWorld>();
	
	public SetWorld (XcraftGate plugin) {
		SetWorld.plugin = plugin;
	}

	@SuppressWarnings("unchecked")
	public void load() {
		File configFile = plugin.getConfigFile("worlds.yml");
		int counter = 0;
	
		try {
			Yaml yaml = new Yaml();
			Map<String, Object> worldsYaml = (Map<String, Object>) yaml.load(new FileInputStream(configFile));
			
			DataWorld newWorld;
			
			if (worldsYaml.entrySet() == null) {
				plugin.log.info(plugin.getNameBrackets() + "empty worlds.yml - initializing");
				return;
			}
			
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
				
				newWorld = new DataWorld(plugin, worldName, env, gen);
				
				newWorld.setBorder(Util.castInt(worldData.get("border")));
				newWorld.setAllowPvP(Util.castBoolean(worldData.get("allowPvP")));
				newWorld.setAllowAnimals(Util.castBoolean(worldData.get("allowAnimals")));
				newWorld.setAllowMonsters(Util.castBoolean(worldData.get("allowMonsters")));
				newWorld.setCreatureLimit(Util.castInt(worldData.get("creatureLimit")));
				newWorld.setAllowWeatherChange(Util.castBoolean(worldData.get("allowWeatherChange")));
				newWorld.setTimeFrozen(Util.castBoolean(worldData.get("timeFrozen")));
				newWorld.setDayTime(Util.castInt(worldData.get("setTime")));
				newWorld.setSuppressHealthRegain(Util.castBoolean(worldData.get("suppressHealthRegain")));
				newWorld.setSuppressHunger(Util.castBoolean(worldData.get("suppressHunger")));
				newWorld.setSticky(Util.castBoolean(worldData.get("sticky")));
				newWorld.setAnnouncePlayerDeath(Util.castBoolean(worldData.get("announcePlayerDeath")));
				newWorld.setDifficulty(Util.castInt(worldData.get("difficulty")));
				newWorld.setGameMode(Util.castInt(worldData.get("gamemode")));

				worlds.put(worldName, newWorld);

				String weather = (String) worldData.get("setWeather");
				for(Weather thisWeather: DataWorld.Weather.values()) {
					if (thisWeather.toString().equalsIgnoreCase(weather)) {
						newWorld.setWeather(thisWeather);
					}
				}
				
				counter++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
		
		plugin.log.info(plugin.getNameBrackets() + "loaded " + counter + " world configurations");
	}

	public void save() {
		File configFile = plugin.getConfigFile("worlds.yml");
		
		Map<String, Object> toDump = new HashMap<String, Object>();

		for (DataWorld thisWorld : worlds.values()) {
			toDump.put(thisWorld.getName(), thisWorld.toMap());
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
	
	public void onWorldLoad(World world) {
		if (worlds.get(world.getName()) != null) {
			plugin.log.info(plugin.getNameBrackets() + "World '" + world.getName() + "' detected. Applying config.");
			get(world).setWorld(world);
			get(world).setParameters();
		} else {
			plugin.log.info(plugin.getNameBrackets() + "World '" + world.getName() + "' detected. Adding to config.");
			DataWorld newWorld = new DataWorld(plugin, world.getName(), world.getEnvironment());
			add(newWorld);
			save();
		}
		
		plugin.getGates().onWorldLoad(get(world));
	}

	public void add(DataWorld world) {
		worlds.put(world.getName(), world);
		save();
	}
	
	public void remove(String worldName) {
		worlds.remove(worldName);
		save();
	}
	
	public DataWorld get(World world) {
		return get(world.getName());
	}
	 
	public DataWorld get(String name) {
		return worlds.get(name);
	}
	
	@Override
	public Iterator<DataWorld> iterator() {
		return worlds.values().iterator();
	}

}
