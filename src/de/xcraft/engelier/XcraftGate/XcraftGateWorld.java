package de.xcraft.engelier.XcraftGate;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.*;
import org.bukkit.generator.ChunkGenerator;

import de.xcraft.engelier.XcraftGate.Generator.Generator;

public class XcraftGateWorld {
	public String name;
	public Environment environment;
	public Boolean allowAnimals = true;
	public Boolean allowMonsters = true;
	public Boolean allowPvP = false;
	public Boolean allowWeatherChange = true;
	public Integer creatureLimit = 0;
	public Integer border = 0;
	public Weather setWeather = Weather.SUN;
	public long setTime = 100;
	public Boolean timeFrozen = false;
	public Boolean suppressHealthRegain = true;
	public Generator generator;
		
	private XcraftGate plugin;
	private Server server;
	private Long lastAction = null;
	public World world;
	
	public XcraftGateWorld (XcraftGate instance) {
		this(instance, null, World.Environment.NORMAL, null);
	}
	
	public XcraftGateWorld (XcraftGate instance, String worldName) {
		this(instance, worldName, World.Environment.NORMAL, null);
	}

	public XcraftGateWorld (XcraftGate instance, String worldName, Environment env) {
		this(instance, worldName, env, null);
	}

	public XcraftGateWorld (XcraftGate instance, String worldName, Environment env, Generator gen) {
		this.plugin = instance;
		this.server = plugin.getServer();
		this.allowPvP = plugin.castBoolean(plugin.serverconfig.getProperty("pvp", "false"));
		
		this.world = server.getWorld(worldName);
		this.name = worldName;
		this.environment = env;
		this.generator = (gen != null) ? gen : Generator.DEFAULT;
		this.lastAction = System.currentTimeMillis();
	}
	
	public enum Weather {
		SUN(0),
		STORM(1);

		private final int id;
		private static final Map<Integer, Weather> lookup = new HashMap<Integer, Weather>();

		private Weather(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static Weather getWeather(int id) {
			return lookup.get(id);
		}

		static {
			for (Weather env : values()) {
				lookup.put(env.getId(), env);
			}
		}
	}

	public enum DayTime {
		SUNRISE(100),
		NOON(6000),
		SUNSET(12100),
		MIDNIGHT(18000);

		private final int id;
		private static final Map<Integer, DayTime> lookup = new HashMap<Integer, DayTime>();

		private DayTime(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static DayTime getDayTime(int id) {
			return lookup.get(id);
		}

		static {
			for (DayTime env : values()) {
				lookup.put(env.getId(), env);
			}
		}
	}
	
	public void load() {
		load(null);
	}
	
	public void load(Long seed) {
		if (world != null) {
			return;
		}
		
		ChunkGenerator thisGen = (generator != Generator.DEFAULT) ? generator.getChunkGenerator() : null;
		
		if (seed == null && thisGen == null) {
			this.world = server.createWorld(name, environment);
		} else if (seed == null && thisGen != null) {
			this.world = server.createWorld(name, World.Environment.NORMAL, thisGen);
		} else {
			this.world = server.createWorld(name, World.Environment.NORMAL, seed, thisGen);
		}
		
		lastAction = System.currentTimeMillis();

		this.plugin.log.info(plugin.getNameBrackets() + "loaded world " + name + " (Environment: " + environment.toString() + ", Seed: " + world.getSeed() + ", Generator: " + generator.toString() + ")");
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("name", name);
		values.put("type", environment.toString());
		values.put("generator", generator.toString());
		values.put("border", border);
		values.put("creatureLimit", creatureLimit);
		values.put("allowAnimals", allowAnimals);
		values.put("allowMonsters", allowMonsters);
		values.put("allowPvP", allowPvP);
		values.put("allowWeatherChange", allowWeatherChange);
		values.put("setWeather", setWeather.toString());
		values.put("setTime", setTime);
		values.put("timeFrozen", timeFrozen);
		values.put("suppressHealthRegain", suppressHealthRegain);
		return values;
	}
	
	public void resetSpawnFlags() {
		world.setSpawnFlags(allowMonsters, allowAnimals);
	}
	
	public void checkCreatureLimit() {
		if (world == null) return;
		
		Double max = creatureLimit.doubleValue();
		Integer alive = world.getLivingEntities().size() - world.getPlayers().size();

		if (max <= 0) return;

		if (alive >= max) {
			world.setSpawnFlags(false, false);
		} else if (alive <= max * 0.8) {
			resetSpawnFlags();
		}		
	}	

	public Boolean checkInactive() {
		if (world == null) return false;
		
		if (world.getPlayers().size() > 0) {
			lastAction = System.currentTimeMillis();
			return false;
		}
		
		if (lastAction + 300 < System.currentTimeMillis()) {
			return true;
		}
		
		return false;
	}
	
	public void resetFrozenTime() {
		if (world == null) return;
		if (!timeFrozen) return;		
		world.setTime(setTime - 100);
	}
		
	public void killAllMonsters() {
		if (world == null) return;
		for (LivingEntity entity : world.getLivingEntities()) {
			if (entity instanceof Zombie || entity instanceof Skeleton
					|| entity instanceof PigZombie || entity instanceof Creeper
					|| entity instanceof Ghast || entity instanceof Spider
					|| entity instanceof Giant || entity instanceof Slime)
				entity.remove();
		}
	}

	public void killAllAnimals() {
		if (world == null) return;
		for (LivingEntity entity : world.getLivingEntities()) {
			if (entity instanceof Pig || entity instanceof Sheep
					|| entity instanceof Wolf || entity instanceof Cow
					|| entity instanceof Squid || entity instanceof Chicken)
				entity.remove();
		}
	}
	
	public void setCreatureLimit(Integer limit) {
		this.creatureLimit = (limit != null ? limit : 0);
		if (this.creatureLimit > 0) {
			killAllMonsters();
			killAllAnimals();
		}
	}
	
	public void setAllowAnimals(Boolean allow) {
		this.allowAnimals = (allow != null ? allow : true);
		setParameters();
		if (!allow) killAllAnimals();
	}

	public void setAllowMonsters(Boolean allow) {
		this.allowMonsters = (allow != null ? allow : true);
		setParameters();
		if (!allow) killAllMonsters();
	}
	
	public void setAllowWeatherChange(Boolean allow) {
		this.allowWeatherChange = (allow != null ? allow : true);
	}
	
	public void setBorder(Integer border) {
		this.border = (border != null ? border : 0);
	}
	
	public void setAllowPvP(Boolean allow) {
		this.allowPvP = (allow != null ? allow : false);
		setParameters();
	}
	
	public void setWeather(Weather weather) {
		boolean backup = this.allowWeatherChange;
		this.allowWeatherChange = true;
		this.setWeather = weather;
		this.allowWeatherChange = backup;
		setParameters();
	}

	public void setDayTime(DayTime time) {
		this.setTime = time.id;
		setParameters(true);
	}

	public void setDayTime(long time) {
		this.setTime = time;
		setParameters(true);
	}

	public void setTimeFrozen(Boolean frozen) {
		this.timeFrozen = (frozen != null ? frozen : false);
		if (world != null) this.setTime = world.getTime();		
	}
	
	public void setSuppressHealthRegain(Boolean suppressed) {
		this.suppressHealthRegain = (suppressed != null ? suppressed : true);
	}
	
	public boolean checkBorder(Location location) {
		return (border > 0 && Math.abs(location.getX()) <= border && Math.abs(location.getZ()) <= border) || border == 0;
	}
	
	public String timeToString(long time) {
		if (time <= 3000) {
			return "SUNRISE";
		} else if (time <= 9000) {
			return "NOON";
		} else if (time <= 15000) {
			return "SUNSET";
		} else {
			return "MIDNIGHT";
		}
	}
	
	public void setParameters() {
		setParameters(false);
	}
	
	public void setParameters(Boolean changeTime) {
		if (world == null) {
			return;
		}
		
		world.setPVP(allowPvP);
		world.setSpawnFlags(allowMonsters, allowAnimals);
		world.setStorm(setWeather.getId() == Weather.STORM.getId());
		if (changeTime) world.setTime(setTime);
		setCreatureLimit(creatureLimit);
	}
	
	public void sendInfo(Player player) {
		player.sendMessage("World: " + name + " (" + environment.toString() + ")");
		player.sendMessage("Player count: "	+ world.getPlayers().size());
		player.sendMessage("Border: " + (border > 0 ? border : "none"));
		player.sendMessage("PvP allowed: " + (allowPvP ? "yes" : "no"));
		player.sendMessage("Animals allowed: " + (allowAnimals ? "yes" : "no"));
		player.sendMessage("Monsters allowed: " + (allowMonsters ? "yes" : "no"));
		player.sendMessage("Creature count/limit: "
				+ (world.getLivingEntities().size() - world.getPlayers().size()) + "/"
				+ (creatureLimit > 0 ? creatureLimit : "unlimited"));
		player.sendMessage("Health regaining suppressed: " + (suppressHealthRegain ? "yes" : "no"));
		player.sendMessage("Weather changes allowed: " + (allowWeatherChange ? "yes" : "no"));
		player.sendMessage("Current Weather: " + setWeather.toString());
		player.sendMessage("Time frozen: " + (timeFrozen ? "yes" : "no"));
		player.sendMessage("Current Time: " + timeToString(world.getTime()));
		player.sendMessage("Seed: " + world.getSeed());
	}
}
