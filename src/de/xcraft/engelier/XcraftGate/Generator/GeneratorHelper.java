package de.xcraft.engelier.XcraftGate.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

public class GeneratorHelper extends ChunkGenerator {
    private NoiseGenerator generator;

    protected List<BlockPopulator> popList = (List<BlockPopulator>) Arrays.asList(
    		new PopulatorDesert(),
    		new PopulatorForest(),
    		new PopulatorPlains(),
    		new PopulatorRainforest(),
    		new PopulatorSavanna(),
    		new PopulatorSeasonalForest(),
    		new PopulatorShrubland(),
    		new PopulatorSwamp(),
    		new PopulatorTaiga(),
    		new PopulatorTundra()
    		); 
    
    private NoiseGenerator getGenerator(World world) {
        if (generator == null) {
            generator = new SimplexNoiseGenerator(world);
        }

        return generator;
    }

    protected int getHeight(World world, double x, double y, int variance) {
        NoiseGenerator gen = getGenerator(world);

        double result = gen.noise(x, y);
        result *= variance;
        return NoiseGenerator.floor(result);
    }
    
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return popList;
    }
    
	@Override
	public byte[] generate(World arg0, Random arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean canSpawn(World world, int x, int z) {
		return true;
	}
	
	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
        int x = random.nextInt(128) - 64;
        int z = random.nextInt(128) - 64;
        int y = world.getHighestBlockYAt(x, z);
        return new Location(world, x, y, z);
    }
}
