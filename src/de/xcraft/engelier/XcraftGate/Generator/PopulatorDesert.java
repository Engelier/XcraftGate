package de.xcraft.engelier.XcraftGate.Generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class PopulatorDesert extends BlockPopulator {
	private final static int CACTUS_CHANCE = 1; // out of 1000
	
	@Override
	public void populate(World world, Random random, Chunk chunk) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int realX = (chunk.getX() * 16) + x;
				int realZ = (chunk.getZ() * 16) + z;
				int y = world.getHighestBlockYAt(realX, realZ);
				
				Block block = world.getBlockAt(realX, y - 1, realZ);
				if (block.getBiome() != Biome.DESERT || block.getType() == Material.WATER || block.getType() == Material.LAVA || block.getType() == Material.LEAVES) continue;
				
				for (int dy = y - 1; dy > y - 5; dy--) {
					world.getBlockAt(realX, dy, realZ).setType(Material.SAND);
				}

				int rnd = random.nextInt(1000);
				if (rnd > CACTUS_CHANCE - 1) continue;

				boolean fail = false;
				for (int dy = y; dy < y + 3; dy++) {
					for (int dx = realX - 1; dx <= realX + 1; dx++) {
						for (int dz = realZ - 1; dz <= realZ + 1; dz++) {
							if (world.getBlockAt(dx, dy, dz).getType() != Material.AIR) {
								fail = true;
							}
						}
					}
				}

				if (fail) continue;
				
				for (int dy = y; dy < y + 3; dy++) {
					world.getBlockAt(realX, dy, realZ).setType(Material.CACTUS);
				}
			}
		}
		
	}

}
