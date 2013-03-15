package de.xcraft.engelier.XcraftGate.Generator;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class GeneratorFlatlands extends ChunkGenerator {

	@Override
	public byte[] generate(World arg0, Random arg1, int arg2, int arg3) {
        byte[] result = new byte[32768];

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 64; y++) {
                	if (y < 54) {
                		result[(x * 16 + z) * 128 + y] = (byte)Material.STONE.getId();
                	} else if (y < 63) {
                		result[(x * 16 + z) * 128 + y] = (byte)Material.DIRT.getId();                		
                	} else {
                		result[(x * 16 + z) * 128 + y] = (byte)Material.GRASS.getId();
                	}
                }
            }
        }

        return result;
	}
	
	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
        int x = random.nextInt(200) - 100;
        int z = random.nextInt(200) - 100;
        int y = world.getHighestBlockYAt(x, z);
        return new Location(world, x, y, z);
    }
}
