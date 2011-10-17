package de.xcraft.engelier.XcraftGate.Generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class PopulatorNormal extends PopulatorHelper {
	private YamlConfiguration config = null;
	
	public PopulatorNormal (XcraftGate plugin) {
		super(plugin);
		this.config = plugin.config;
	}
	
	private int getTreeChance(String thisBio) {
		return config.getInt("biomes." + thisBio + ".chanceTreeNormal", 0) +
				config.getInt("biomes." + thisBio + ".chanceTreeBig", 0) +
				config.getInt("biomes." + thisBio + ".chanceTreeRedwood", 0) +
				config.getInt("biomes." + thisBio + ".chanceTreeTallRedwood", 0) +
				config.getInt("biomes." + thisBio + ".chanceTreeBirch", 0);
	}

	private int getFlowerChance(String thisBio) {
		return config.getInt("biomes." + thisBio + ".chanceFlowerYellow", 0) +
				config.getInt("biomes." + thisBio + ".chanceFlowerRedRose", 0);
	}

	private int getGrassChance(String thisBio) {
		return config.getInt("biomes." + thisBio + ".chanceGrassShrub", 0) +
				config.getInt("biomes." + thisBio + ".chanceGrassTall", 0) +
				config.getInt("biomes." + thisBio + ".chanceGrassFern", 0);
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

				// make DESERT look like DESERT
				if (bioHere.equals("desert") && blockBelow.getType() != Material.LEAVES) {
					for (int y = realY - 5; y < realY; y++) {
						world.getBlockAt(realX, y, realZ).setType(Material.SAND);
					}
				}

				// one roll to change it all ...
				int rnd = random.nextInt(1000);

				if (rnd < getTreeChance(bioHere) && (blockBelow.getType() == Material.GRASS || blockBelow.getType() == Material.DIRT)) {
					int tNo = config.getInt("biomes." + bioHere + ".chanceTreeNormal", 0);
					int tBT = config.getInt("biomes." + bioHere + ".chanceTreeBig", 0);
					int tBi = config.getInt("biomes." + bioHere + ".chanceTreeBirch", 0);
					int tRW = config.getInt("biomes." + bioHere + ".chanceTreeRedwood", 0);

					if (rnd <= tNo) {
						world.generateTree(blockAffected.getLocation(), TreeType.TREE);
					} else if (rnd - tNo <= tBT) {
						world.generateTree(blockAffected.getLocation(), TreeType.BIG_TREE);
					} else if (rnd - tNo - tBT <= tBi) {
						world.generateTree(blockAffected.getLocation(), TreeType.BIRCH);						
					} else if (rnd - tNo - tBT - tBi <= tRW) {
						world.generateTree(blockAffected.getLocation(), TreeType.REDWOOD);						
					} else {
						world.generateTree(blockAffected.getLocation(), TreeType.TALL_REDWOOD);						
					}	
					
					continue;
				}

				if (blockBelow.getType() == Material.GRASS || blockBelow.getType() == Material.DIRT) rnd -= getTreeChance(bioHere);
				
				if (rnd < getGrassChance(bioHere) && blockBelow.getType() == Material.GRASS) {
					int gSh = config.getInt("biomes." + bioHere + ".chanceGrassShrub", 0);
					int gFe = config.getInt("biomes." + bioHere + ".chanceGrassFern", 0);
					
					if (rnd < gSh) {
						blockAffected.setTypeIdAndData(Material.LONG_GRASS.getId(), (byte) 0, false);
					} else if (rnd - gSh < gFe) {
						blockAffected.setTypeIdAndData(Material.LONG_GRASS.getId(), (byte) 2, false);
					} else {
						blockAffected.setTypeIdAndData(Material.LONG_GRASS.getId(), (byte) 1, false);
					}
					
					continue;
				}
				
				if (blockBelow.getType() == Material.GRASS) rnd -= getGrassChance(bioHere);

				if (rnd < getFlowerChance(bioHere) && blockBelow.getType() == Material.GRASS) {
					if (rnd < config.getInt("biomes." + bioHere + ".chanceFlowerYellow", 0)) {
						blockAffected.setType(Material.YELLOW_FLOWER);
					} else {
						blockAffected.setType(Material.RED_ROSE);
					}
					
					continue;
				}
				
				if (blockBelow.getType() == Material.GRASS) rnd -= getFlowerChance(bioHere);

				if (rnd < config.getInt("biomes." + bioHere + ".chanceCactus", 0) && blockBelow.getType() == Material.SAND) {
					boolean fail = false;
					for (int dy = realY; dy < realY + 3; dy++) {
						for (int dx = realX - 1; dx <= realX + 1; dx++) {
							for (int dz = realZ - 1; dz <= realZ + 1; dz++) {
								if (world.getBlockAt(dx, dy, dz).getType() != Material.AIR) {
									fail = true;
								}
							}
						}
					}

					if (!fail) {
						for (int dy = realY; dy < realY + 3; dy++) {
							world.getBlockAt(realX, dy, realZ).setType(Material.CACTUS);
						}
					}
					
					continue;
				}
				
				if (blockBelow.getType() == Material.SAND) rnd -= config.getInt("biomes." + bioHere + ".chanceCactus", 0);

				if (rnd < config.getInt("biomes." + bioHere + ".chanceDeadShrub", 0) && blockBelow.getType() == Material.SAND) {
					blockAffected.setType(Material.DEAD_BUSH);
					
					continue;
				}
				
				if (blockBelow.getType() == Material.SAND) rnd -= config.getInt("biomes." + bioHere + ".chanceDeadShrub", 0);

				if (rnd < config.getInt("biomes." + bioHere + ".chanceSugarCane", 0) && blockBelow.getType() == Material.GRASS) {
					for (int sx = -1; sx <= 1; sx++) {
						for (int sz = -1; sz <= 1; sz++) {
							if (sx == sz) continue;
							if (world.getBlockAt(realX + sx, realY - 1, realZ + sz).getType() == Material.WATER) {
								for (int sy = realY; sy < realY + 3; sy++) {
									world.getBlockAt(realX, sy, realZ).setType(Material.SUGAR_CANE_BLOCK);
								}
							}
						}
					}
				}
			}
		}
		
		// cover everything with SNOW and make top WATER to ICE in TAIGA and TUNDRA
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int realX = (chunk.getX() * 16) + x;
				int realZ = (chunk.getZ() * 16) + z;
				int realY = world.getHighestBlockYAt(realX, realZ);
				
				Block blockAffected = world.getBlockAt(realX, realY, realZ);
				
				if (blockAffected.getBiome() != Biome.TAIGA && blockAffected.getBiome() != Biome.TUNDRA) continue;
				
				if (world.getBlockAt(realX, realY - 1, realZ).getType() == Material.WATER) {
					world.getBlockAt(realX, realY - 1, realZ).setType(Material.ICE);
				} else {
					blockAffected.setType(Material.SNOW);					
				}
			}
		}
	}
}
