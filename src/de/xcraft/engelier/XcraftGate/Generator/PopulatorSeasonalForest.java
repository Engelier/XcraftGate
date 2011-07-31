package de.xcraft.engelier.XcraftGate.Generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;

public class PopulatorSeasonalForest extends PopulatorHelper {
	private static final int LAKE_CHANCE = 2; // out of 1000
	private static final int TREE_CHANCE = 170; // out of 1000
	private static final int GRASS_CHANCE = 70; // out of 1000
	private static final int FLOWER_CHANCE = 7; // out of 1000
	
	@Override
	public void populate(World world, Random random, Chunk chunk) {
		createForest(random, world, chunk, Biome.SEASONAL_FOREST, TREE_CHANCE, (int)Math.floor(TREE_CHANCE / 10), LAKE_CHANCE, FLOWER_CHANCE, GRASS_CHANCE);
	}
}
