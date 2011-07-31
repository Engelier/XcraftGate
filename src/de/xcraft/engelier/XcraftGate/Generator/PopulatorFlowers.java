package de.xcraft.engelier.XcraftGate.Generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class PopulatorFlowers extends BlockPopulator {
	private final static int FLOWER_CHANCE = 15; // out of 1000
	
	private final Material matYellowFlower = Material.YELLOW_FLOWER;
	private final Material matRedRose = Material.RED_ROSE;
	private final Material matLongGrass = Material.LONG_GRASS;
	
	private Material[] mats = { matYellowFlower, matRedRose, matLongGrass };

	@Override
	public void populate(World world, Random random, Chunk chunk) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int rnd = random.nextInt(1000);
				if (rnd > FLOWER_CHANCE - 1) continue;
				
				int realX = (chunk.getX() * 16) + x;
				int realZ = (chunk.getZ() * 16) + z;
				int y = world.getHighestBlockYAt(realX, realZ);
				
				Block block = world.getBlockAt(realX, y - 1, realZ);
				
				if (block.getType() != Material.GRASS) continue;
				
				if (block.getBiome() == Biome.SAVANNA || block.getBiome() == Biome.SHRUBLAND || block.getBiome() == Biome.RAINFOREST) {
					int type = (int) Math.floor(rnd/(FLOWER_CHANCE/3));
					world.getBlockAt(realX, y, realZ).setType(mats[type]);
					if (type == 2) {
						world.getBlockAt(realX, y, realZ).setData((byte)1);
					}
				} else if (block.getBiome() == Biome.FOREST || block.getBiome() == Biome.SEASONAL_FOREST) {
					world.getBlockAt(realX, y, realZ).setType(mats[(int) Math.floor(rnd/(FLOWER_CHANCE/2))]);				
				} else if (block.getBiome() == Biome.PLAINS) {
					world.getBlockAt(realX, y, realZ).setTypeIdAndData(matLongGrass.getId(), (byte) (rnd % 2 + 1), true);					
				}
			}
		}
		
	}
}
