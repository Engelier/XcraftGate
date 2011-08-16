package de.xcraft.engelier.XcraftGate.Generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.config.Configuration;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class PopulatorMiniLakes extends PopulatorHelper {
	private Configuration config;
	
	public PopulatorMiniLakes(XcraftGate plugin) {
		super(plugin);
		config = plugin.config;
	}

	private int getLakeChance(String thisBio) {
		return config.getInt("biomes." + thisBio + ".chanceLakeWater", 0) +
				config.getInt("biomes." + thisBio + ".chanceLakeLava", 0);
	}
	
	@Override
	public void populate(World world, Random random, Chunk chunk) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int realX = (chunk.getX() * 16) + x;
				int realZ = (chunk.getZ() * 16) + z;
				int realY = world.getHighestBlockYAt(realX, realZ);
								
				Block blockAffected = world.getBlockAt(realX, realY, realZ);
				Block blockBelow = world.getBlockAt(realX, realY - 1, realZ);
				
				String bioHere = blockAffected.getBiome().toString().toLowerCase();
				
				// we don't have to populate AIR, BEDROCK, WATER or LAVA
				if (blockBelow.getType() == Material.LAVA || blockBelow.getType() == Material.STATIONARY_LAVA 
						|| blockBelow.getType() == Material.WATER || blockBelow.getType() == Material.STATIONARY_WATER
						|| blockBelow.getType() == Material.AIR || blockBelow.getType() == Material.BEDROCK) {
					continue;
				}

				// we don't want lakes in SAND
				if (blockBelow.getType() == Material.SAND) {
					continue;
				}
				
				// one roll to change it all ...
				int rnd = random.nextInt(1000);
				
				if (rnd < getLakeChance(bioHere)) {
					if (rnd <= config.getInt("biomes." + bioHere + ".chanceLakeWater", 0)) {
						if (bioHere.equals("swampland")) {
							createLake(random, world, realX, realY, realZ, 1 + random.nextInt(2), Material.STATIONARY_WATER);
						} else {
							createLake(random, world, realX, realY, realZ, 2 + random.nextInt(3), Material.STATIONARY_WATER);							
						}
					} else {
						createLake(random, world, realX, realY, realZ, 2 + random.nextInt(3), Material.LAVA);													
					}	
					
					continue;
				}
			}
		}
	}

}
