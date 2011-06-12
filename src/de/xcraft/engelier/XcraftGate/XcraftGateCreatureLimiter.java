package de.xcraft.engelier.XcraftGate;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.*;

public class XcraftGateCreatureLimiter implements Runnable {
	private XcraftGate plugin;

	public XcraftGateCreatureLimiter(XcraftGate instance) {
		plugin = instance;
	}

	public void killAllMonsters(World world) {
		for (LivingEntity entity : world.getLivingEntities()) {
			if (entity instanceof Zombie || entity instanceof Skeleton
					|| entity instanceof PigZombie || entity instanceof Creeper
					|| entity instanceof Ghast || entity instanceof Spider
					|| entity instanceof Giant || entity instanceof Slime)
				entity.remove();
		}
	}

	public void killAllAnimals(World world) {
		for (LivingEntity entity : world.getLivingEntities()) {
			if (entity instanceof Pig || entity instanceof Sheep
					|| entity instanceof Wolf || entity instanceof Cow
					|| entity instanceof Squid || entity instanceof Chicken)
				entity.remove();
		}
	}

	public void denyMonsters(World world) {
		((CraftWorld) world).getHandle().allowMonsters = false;
	}

	public void denyAnimals(World world) {
		((CraftWorld) world).getHandle().allowAnimals = false;
	}

	public void allowMonsters(World world) {
		((CraftWorld) world).getHandle().allowMonsters = true;
	}

	public void allowAnimals(World world) {
		((CraftWorld) world).getHandle().allowAnimals = true;
	}

	public void checkLimit(World world) {
		Double max = plugin.config.getDouble("worlds." + world.getName()
				+ ".creatureLimit", 0.0);
		Integer alive = world.getLivingEntities().size()
				- world.getPlayers().size();

		if (max <= 0)
			return;

		if (alive >= max) {
			denyMonsters(world);
			denyAnimals(world);
		} else if (alive <= max * 0.8) {
			allowMonsters(world);
			allowAnimals(world);
		}
	}

	public void run() {
		for (World thisWorld : plugin.getServer().getWorlds()) {
			checkLimit(thisWorld);
		}
	}
}
