package de.xcraft.engelier.XcraftGate.Generator;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

public abstract class GeneratorHelper extends ChunkGenerator {
    private NoiseGenerator generator;
    
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
    public abstract List<BlockPopulator> getDefaultPopulators(World world);
    
	@Override
	public abstract byte[] generate(World arg0, Random arg1, int arg2, int arg3);
	
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
