package com.rs.utils.spawns;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonIOException;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public final class NPCSpawns {

	private final static String PATH = "data/npcs/spawns/";
	final static Charset ENCODING = StandardCharsets.UTF_8;
	
	private static final Object lock = new Object();
	private static final ArrayList<NPCSpawn> ALL_SPAWNS = new ArrayList<>();
	private static final ArrayList<NPCSpawn> ADDED_SPAWNS = new ArrayList<>();
	private static final Map<Integer, List<NPCSpawn>> NPC_SPAWNS = new HashMap<>();

	public static boolean addSpawn(String username, int id, WorldTile tile) {
		synchronized (lock) {
			File file = new File("./data/npcs/addedSpawns.json");
			ADDED_SPAWNS.add(new NPCSpawn(id, tile, ""+NPCDefinitions.getDefs(id).getName()+" added by " + username));
			World.spawnNPC(id, tile, -1, true);
			try {
				JsonFileManager.saveJsonFile(ADDED_SPAWNS, file);
			} catch (IOException e) {
				return false;
			}
			return true;
		}
	}

	@ServerStartupEvent
	public static final void init() throws JsonIOException, IOException {
		Logger.log("NPCSpawns", "Loading NPC spawns...");
		File[] spawnFiles = new File(PATH).listFiles();
		for (File f : spawnFiles) {
			if (f.getName().startsWith("_"))
				continue;
			NPCSpawn[] spawns = (NPCSpawn[]) JsonFileManager.loadJsonFile(f, NPCSpawn[].class);
			if (spawns != null) {
				for(NPCSpawn spawn : spawns) {
					if (spawn != null) {
						add(spawn);
					}
				}
			}
		}
		Logger.log("NPCSpawns", "Loaded " + ALL_SPAWNS.size() + " NPC spawns...");
	}
	
	public static void add(NPCSpawn spawn) {
		if (spawn != null) {
			ALL_SPAWNS.add(spawn);
			List<NPCSpawn> regionSpawns = NPC_SPAWNS.get(spawn.getTile().getRegionId());
			if (regionSpawns == null)
				regionSpawns = new ArrayList<>();
			regionSpawns.add(spawn);
			NPC_SPAWNS.put(spawn.getTile().getRegionId(), regionSpawns);
		}
	}

	public static List<NPCSpawn> getAllSpawns() {
		return ALL_SPAWNS;
	}
	
	public static void loadNPCSpawns(int regionId) {
		List<NPCSpawn> spawns = NPC_SPAWNS.get(regionId);
		if (spawns != null) {
			for (NPCSpawn spawn : spawns) {
				spawn.spawn();
			}
		}
	}
}
