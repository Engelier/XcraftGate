package de.xcraft.engelier.XcraftGate.Generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class PopulatorPlains extends BlockPopulator {
	private static final int FLOWER_CHANCE = 15; //out of 1000
	private static final int GRASS_CHANCE = 150; //out of 1000
	
	@Override
	public void populate(World world, Random random, Chunk chunk) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int realX = (chunk.getX() * 16) + x;
				int realZ = (chunk.getZ() * 16) + z;
				int y = world.getHighestBlockYAt(realX, realZ);

				Block block = world.getBlockAt(realX, y, realZ);
				if (block.getBiome() != Biome.PLAINS) continue;
				
				Block blockBelow = world.getBlockAt(realX, y - 1, realZ);
				int rnd = random.nextInt(1000);
				
				if (blockBelow.getType() == Material.GRASS && rnd < GRASS_CHANCE) {
					block.setTypeIdAndData(Material.LONG_GRASS.getId(), (byte) 1, true);
				}

				if (blockBelow.getType() == Material.GRASS && rnd < FLOWER_CHANCE) {
					switch (rnd % 2) {
					case 0: block.setType(Material.YELLOW_FLOWER); break;
					case 1: block.setType(Material.RED_ROSE); break;
					}
				}

			}
		}
	}
}
