package de.xcraft.engelier.XcraftGate.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class GeneratorIsland extends GeneratorHelper {
	private final static int SIZE = 128;
	private final static int MAX_HEIGHT = 6;
	private final static int VARIANT = 32;
		
	private final byte matBedrock = (byte)Material.BEDROCK.getId();
	private final byte matCoalOre = (byte)Material.COAL_ORE.getId();
	private final byte matDirt = (byte)Material.DIRT.getId();
	private final byte matDiamondOre = (byte)Material.DIAMOND_ORE.getId();
	private final byte matGrass = (byte)Material.GRASS.getId();
	private final byte matIronOre = (byte)Material.IRON_ORE.getId();
	private final byte matLapisOre = (byte)Material.LAPIS_ORE.getId();
	private final byte matRedstoneOre = (byte)Material.REDSTONE_ORE.getId();
	private final byte matSand = (byte)Material.SAND.getId();
	private final byte matStone = (byte)Material.STONE.getId();
	private final byte matWater = (byte)Material.WATER.getId();
	
	private XcraftGate plugin = null;

    private Integer variantX = null;
    private Integer variantZ = null;
    
    public GeneratorIsland(XcraftGate plugin) {
    	this.plugin = plugin;
    }
     
	@Override
	public byte[] generate(World world, Random random, int chunkX, int chunkY) {
        byte[] result = new byte[32768];
        
        if (variantX == null) variantX = random.nextInt(VARIANT) - VARIANT;
        if (variantZ == null) variantZ = random.nextInt(VARIANT) - VARIANT;
        
        int worldHeight = world.getMaxHeight();
        int halfHeight = (int)(worldHeight / 2);
        int distance = 0;
        int height = 0;
        int realX = 0;
        int realZ = 0;
        int pos = 0;
        int rnd = 0;
        
        for (int x = 0; x < 16; x++) {
       		for (int z = 0; z < 16; z++) {
    			realX = (chunkX * 16) + x + variantX;
    			realZ = (chunkY * 16) + z + variantZ;
    			distance = (int)Math.floor(Math.sqrt(Math.pow(realX, 2) + Math.pow(realZ, 2)));
				height = getHeight(world, chunkX + x * 0.0625, chunkY + z * 0.0625, 1) + (int)Math.floor((SIZE - distance) / MAX_HEIGHT) + (halfHeight - 1);
				
				if (height < halfHeight) height = halfHeight;
    			
               	for (int y = 0; y <= height; y++) {
        			pos = (x * 16 + z) * worldHeight + y;

        			if (y == 0) {
        				result[pos] = matBedrock;
        				continue;
        			}
        			
        			if (distance > SIZE) {
        				result[pos] = matWater;
        			} else {
    					if (height == halfHeight) {
    						result[pos] = matSand;
    					} else {
    						if (y == height) {
        						result[pos] = matGrass;
        					} else {
        						if (y > height - 9) {
        							result[pos] = matDirt;
        						} else {
        							rnd = random.nextInt(1000);
        							if (rnd < 5 && y <= 16) {
        								result[pos] = matDiamondOre;
        							} else if (rnd < 10 && y <= 16) {
        								result[pos] = matLapisOre;
        							} else if (rnd < 15 && y <= 32) {
        								result[pos] = matRedstoneOre;
        							} else if (rnd < 20 && y <= 48) {
        								result[pos] = matIronOre;
        							} else if (rnd < 30) {
        								result[pos] = matCoalOre;
        							} else {
        								result[pos] = matStone;
        							}
        						}
        					}
    					}
        			}
        		}
        	}
        }
        
		return result;
	}

	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return (List<BlockPopulator>) Arrays.asList((BlockPopulator) new PopulatorNormal(plugin));
	}


}
