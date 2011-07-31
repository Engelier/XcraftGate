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

public class PopulatorTrees extends BlockPopulator {
	private final static int TREE_CHANCE = 20; // out of 1000

	@Override
	public void populate(World world, Random random, Chunk chunk) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int rnd = random.nextInt(1000);
				 if (rnd > TREE_CHANCE - 1) continue;
				 
				int realX = (chunk.getX() * 16) + x;
				int realZ = (chunk.getZ() * 16) + z;
				int y = world.getHighestBlockYAt(realX, realZ);
				
				Block block = world.getBlockAt(realX, y - 1, realZ);
				
				if (block.getType() != Material.GRASS) continue;
		
				if (block.getBiome() == Biome.FOREST) {
					if (rnd % 5 == 0) {
						world.generateTree(new Location(world, realX, y, realZ), TreeType.BIG_TREE);
					} else {
						world.generateTree(new Location(world, realX, y, realZ), TreeType.TREE);						
					}
				} else if (block.getBiome() == Biome.SEASONAL_FOREST) {
					world.generateTree(new Location(world, realX, y, realZ), TreeType.BIRCH);
				} else if (block.getBiome() == Biome.RAINFOREST) {
					world.generateTree(new Location(world, realX, y, realZ), TreeType.REDWOOD);
				} else if (block.getBiome() == Biome.SAVANNA || block.getBiome() == Biome.SHRUBLAND && rnd <= TREE_CHANCE / 5) {
					world.generateTree(new Location(world, realX, y, realZ), TreeType.TREE);
				} else if (block.getBiome() == Biome.PLAINS || block.getBiome() == Biome.TUNDRA && rnd <= TREE_CHANCE / 10) {
					world.generateTree(new Location(world, realX, y, realZ), TreeType.TREE);
				}
			}
		}
	}
}
