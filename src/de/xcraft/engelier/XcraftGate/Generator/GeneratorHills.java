package de.xcraft.engelier.XcraftGate.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class GeneratorHills extends GeneratorHelper {		
	private final byte matBedrock = (byte)Material.BEDROCK.getId();
	private final byte matDirt = (byte)Material.DIRT.getId();
	private final byte matGrass = (byte)Material.GRASS.getId();
	private final byte matStone = (byte)Material.STONE.getId();
	private final byte matWater = (byte)Material.STATIONARY_WATER.getId();
	
	private XcraftGate plugin = null;

    public GeneratorHills(XcraftGate plugin) {
    	this.plugin = plugin;
    }
     
	@Override
	public byte[] generate(World world, Random random, int chunkX, int chunkY) {
        byte[] result = new byte[32768];
                
        int worldHeight = world.getMaxHeight();
        int halfHeight = (int)(worldHeight / 2);
        int realX = 0;
        int realZ = 0;
        int pos;
                
        int waterlevel = plugin.config.getInt("generators.hills.waterlevel", 64);
        
        double scale = plugin.config.getDouble("generators.hills.scale", 64.0);
        int octaves = plugin.config.getInt("generators.hills.octaves", 8);
        int variance = plugin.config.getInt("generators.hills.variance", 16);

        SimplexOctaveGenerator gen = new SimplexOctaveGenerator(new Random(world.getSeed()), octaves);
        gen.setScale(1 / scale);
        
        for (int x = 0; x < 16; x++) {
       		for (int z = 0; z < 16; z++) {
    			realX = (chunkX * 16) + x;
    			realZ = (chunkY * 16) + z;
    			
    			int height = (int)Math.ceil(gen.noise(realX, realZ, 0.5, 0.5) * variance) + halfHeight;
    			
    			for (int y = 0; y < (height > waterlevel ? height : waterlevel); y++) {
    				pos = (x * 16 + z) * worldHeight + y;
    				
    				if (y == 0) {
    					result[pos] = matBedrock;
    				} else if (y < height - (random.nextInt(5) + 3)) {
    					result[pos] = matStone;
    				} else if (y < height - 1) {
    					result[pos] = matDirt;
    				} else if (y == height - 1) {
    					result[pos] = matGrass;
    				} else {
    					result[pos] = matWater;
    				}
    			}
       		}
        }
        
        return result;
	}

	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return Arrays.asList((BlockPopulator) new PopulatorNormal(plugin));
	}
}