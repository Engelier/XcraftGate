package de.xcraft.engelier.XcraftGate;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.*;

public class XcraftGateWorld {
	public String name;
	public Boolean allowAnimals;
	public Boolean allowMonsters;
	public Integer creatureLimit;
	public Integer border;
	public Environment environment;
	
	private XcraftGate plugin;
	private Server server;
	private World world;
	
	public XcraftGateWorld (XcraftGate instance) {
		this.plugin = instance;
		this.server = plugin.getServer();
	}
	
	public void load(String name, Environment env) {
		this.name = name;
		this.environment = env;
		this.plugin.log.info(plugin.getNameBrackets() + "loading world " + name + "(" + env.toString() + ")");
		this.world = server.createWorld(name, env);
	}
	
	public void checkCreatureLimit() {
		Double max = creatureLimit.doubleValue();
		Integer alive = world.getLivingEntities().size() - world.getPlayers().size();

		if (max <= 0) return;

		if (alive >= max) {
			((CraftWorld) world).getHandle().allowAnimals = false;
			((CraftWorld) world).getHandle().allowMonsters = false;
		} else if (alive <= max * 0.8) {
			((CraftWorld) world).getHandle().allowAnimals = true;
			((CraftWorld) world).getHandle().allowMonsters = true;
		}
		
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
		this.creatureLimit = limit;
		killAllMonsters();
		killAllAnimals();
	}
	
	public void setAllowAnimals(Boolean allow) {
		this.allowAnimals = allow;
		((CraftWorld) world).getHandle().allowAnimals = allow;
		if (!allow) killAllAnimals();
	}

	public void setAllowMonsters(Boolean allow) {
		this.allowAnimals = allow;
		((CraftWorld) world).getHandle().allowMonsters = allow;
		if (!allow) killAllMonsters();
	}
	
	public void setBorder(Integer border) {
		this.border = border;
	}
	
	public boolean checkBorder(Location location) {
		return (Math.abs(location.getX()) <= border && Math.abs(location.getZ()) <= border);
	}
}
