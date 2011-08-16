package de.xcraft.engelier.XcraftGate.Generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public abstract class PopulatorHelper extends BlockPopulator {
	protected XcraftGate plugin;
	
	public PopulatorHelper(XcraftGate plugin) {
		this.plugin = plugin;
	}
	
	public int getDistance(Location from, Location to) {
		return getDistance(from.getX(), from.getY(), from.getZ(), to.getY(), to.getY(), to.getZ());
	}
	
	public int getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
		Vector start = new BlockVector(x1, y1, z1);
		return (int)Math.floor(start.distance(new BlockVector(x2, y2, z2)));
	}
	
	public void createLake(Random random, World world, int x, int y, int z, int radius, Material mat) {
		int lowerY = world.getMaxHeight();
		
		radius++;
		
		// get lowest block in reach
		for (int ax = x - radius; ax <= x + radius; ax++) {
			for (int az = z - radius; az <= z + radius; az++) {
				int check = world.getHighestBlockYAt(ax, az) - 1;
				lowerY = (lowerY > check) ? check : lowerY;
			}
		}
		
		// fill radius with water and make sure 2 blocks above is air
		for (int ax = x - radius; ax <= x + radius; ax++) {
			for (int az = z - radius; az <= z + radius; az++) {
				if (getDistance(x, y, z, ax, y, az) > radius - random.nextInt(2)) continue;
				
				if (world.getBlockAt(ax, lowerY, az).getType() == Material.SAND || world.getBlockAt(ax, lowerY, az).getType() == Material.AIR
						|| world.getBlockAt(ax, lowerY + 1, az).getType() == Material.LOG) {
					continue;
				}
				
				world.getBlockAt(ax, lowerY, az).setType(mat);
				for (int ay = lowerY + 1; ay <= lowerY + 2; ay++) {
					world.getBlockAt(ax, ay, az).setType(Material.AIR);
				}
			}
		}
	}
	
	public void createForest(Random random, World world, Chunk chunk, Biome biome, int treeChance, int bigTreeChance, int lakeChance, int flowerChance, int grassChance) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int realX = (chunk.getX() * 16) + x;
				int realZ = (chunk.getZ() * 16) + z;
				int y = world.getHighestBlockYAt(realX, realZ);

				Block block = world.getBlockAt(realX, y, realZ);
				if (block.getBiome() != biome) continue;
				
				int rnd = random.nextInt(1000);
				
				Block blockBelow = world.getBlockAt(realX, y - 1, realZ);
				
				if (blockBelow.getType() == Material.GRASS && rnd < lakeChance) {
					createLake(random, world, realX, y - 1, realZ, random.nextInt(3) + 2, Material.WATER);
				}
				
				if (blockBelow.getType() == Material.GRASS && rnd < flowerChance) {
					switch (rnd % 2) {
						case 0: block.setType(Material.YELLOW_FLOWER); break;
						case 1: block.setType(Material.RED_ROSE); break;
					}
				}

				if (block.getType() == Material.AIR && blockBelow.getType() == Material.GRASS && rnd < grassChance) {
					if (rnd < grassChance / 4 && (biome == Biome.RAINFOREST || biome == Biome.SEASONAL_FOREST)) {
						block.setTypeIdAndData(Material.LONG_GRASS.getId(), (byte) 2, true);
					} else {
						block.setTypeIdAndData(Material.LONG_GRASS.getId(), (byte) 1, true);						
					}
				}

				
				if (block.getType() == Material.AIR && blockBelow.getType() == Material.GRASS && rnd < treeChance) {
					if (rnd < bigTreeChance) {
						world.generateTree(new Location(world, realX, y, realZ), TreeType.BIG_TREE);
					} else {
						switch (rnd % 3) {
							case 0: world.generateTree(new Location(world, realX, y, realZ), TreeType.TREE); break;
							case 1: world.generateTree(new Location(world, realX, y, realZ), TreeType.BIRCH); break;
							case 2: world.generateTree(new Location(world, realX, y, realZ), TreeType.REDWOOD); break;
						}
					}
				}
			}
		}

	}
}
