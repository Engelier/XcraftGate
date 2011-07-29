package de.xcraft.engelier.XcraftGate.Generator;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.generator.ChunkGenerator;

public enum Generator {
	DEFAULT(0),
	MOON(1),
	FLATLANDS(2);
	
	private final int id;
	private static final Map<Integer, Generator> lookup = new HashMap<Integer, Generator>();

	private Generator(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static Generator getGenerator(int id) {
		return lookup.get(id);
	}
	
	public ChunkGenerator getChunkGenerator() {
		switch (id) {
			case 0: return (ChunkGenerator)null;
			case 1: return new GeneratorMoon();
			case 2: return new GeneratorFlatlands();
		}
		
		return null;
	}

	static {
		for (Generator env : values()) {
			lookup.put(env.getId(), env);
		}
	}

}
