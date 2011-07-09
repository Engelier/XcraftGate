package de.xcraft.engelier.XcraftGate;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.*;

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
		
	private XcraftGate plugin;
	private Server server;
	private World world;
	
	public XcraftGateWorld (XcraftGate instance) {
		this.plugin = instance;
		this.server = plugin.getServer();
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
	
	public void load(String name, Environment env) {
		this.name = name;
		this.environment = env;
		this.plugin.log.info(plugin.getNameBrackets() + "loading world " + name + " (" + env.toString() + ")");
		this.world = server.createWorld(name, env);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("name", name);
		values.put("type", environment.toString());
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
		Double max = creatureLimit.doubleValue();
		Integer alive = world.getLivingEntities().size() - world.getPlayers().size();

		if (max <= 0) return;

		if (alive >= max) {
			world.setSpawnFlags(false, false);
		} else if (alive <= max * 0.8) {
			resetSpawnFlags();
		}		
	}	
	
	public void resetFrozenTime() {
		if (!timeFrozen) return;		
		world.setTime(setTime - 100);
	}
		
	public void killAllMonsters() {
		for (LivingEntity entity : world.getLivingEntities()) {
			if (entity instanceof Zombie || entity instanceof Skeleton
					|| entity instanceof PigZombie || entity instanceof Creeper
					|| entity instanceof Ghast || entity instanceof Spider
					|| entity instanceof Giant || entity instanceof Slime)
				entity.remove();
		}
	}

	public void killAllAnimals() {
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
		resetSpawnFlags();
		if (!allow) killAllAnimals();
	}

	public void setAllowMonsters(Boolean allow) {
		this.allowMonsters = (allow != null ? allow : true);
		resetSpawnFlags();
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
		this.world.setPVP(this.allowPvP);
	}
	
	public void setWeather(Weather weather) {
		boolean backup = this.allowWeatherChange;
		this.allowWeatherChange = true;
		this.world.setStorm(weather.getId() == Weather.STORM.id);
		this.setWeather = weather;
		this.allowWeatherChange = backup;
	}

	public void setDayTime(DayTime time) {
		this.world.setTime(time.id);
		this.setTime = time.id;
	}

	public void setDayTime(long time) {
		this.world.setTime(time);
		this.setTime = time;
	}

	public void setTimeFrozen(Boolean frozen) {
		this.timeFrozen = (frozen != null ? frozen : false);
		this.setTime = world.getTime();		
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
	
	public void sendInfo(Player player) {
		player.sendMessage("Worldname: " + name);
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
