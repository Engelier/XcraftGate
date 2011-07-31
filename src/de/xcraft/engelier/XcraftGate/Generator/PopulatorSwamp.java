package de.xcraft.engelier.XcraftGate.Generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public class PopulatorSwamp extends PopulatorHelper {
	private final static int LAKE_CHANCE = 10; // out of 1000
	private final static int SUGAR_CHANCE = 100; // out of 1000
	
	@Override
	public void populate(World world, Random random, Chunk chunk) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int realX = (chunk.getX() * 16) + x;
				int realZ = (chunk.getZ() * 16) + z;
				int y = world.getHighestBlockYAt(realX, realZ);

				Block block = world.getBlockAt(realX, y, realZ);
				if (block.getBiome() != Biome.SWAMPLAND) continue;
				
				Block blockBelow = world.getBlockAt(realX, y - 1, realZ);
				int rnd = random.nextInt(1000);
				
				if (blockBelow.getType() == Material.GRASS && rnd < LAKE_CHANCE) {
					createLake(random, world, realX, y - 1, realZ, random.nextInt(2) + 1, Material.WATER);
					continue;
				}
				
				if (blockBelow.getType() == Material.GRASS && rnd < SUGAR_CHANCE) {
					for (int sx = -1; sx <= 1; sx++) {
						for (int sz = -1; sz <= 1; sz++) {
							if (sx == sz) continue;
							if (world.getBlockAt(realX + sx, y - 1, realZ + sz).getType() == Material.WATER) {
								for (int sy = y; sy < y + 3; sy++) {
									world.getBlockAt(realX, sy, realZ).setType(Material.SUGAR_CANE_BLOCK);
								}
							}
						}
					}
				}
			}
		}
	}
}
