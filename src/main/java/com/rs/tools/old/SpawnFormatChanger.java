package com.rs.tools.old;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonIOException;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.spawns.NPCSpawn;

public class SpawnFormatChanger {
	
	class ShaunyNPCSpawn {
		private String npcName;
		private int npcId;
		private int x, y, plane;
	}
	
	public static void main(String[] args) throws JsonIOException, IOException {
		//Cache.init();
		List<NPCSpawn> converted = new ArrayList<>();
		ShaunyNPCSpawn[] spawns = (ShaunyNPCSpawn[]) JsonFileManager.loadJsonFile(new File("./dumps/newSpawns.json"), ShaunyNPCSpawn[].class);
		for (ShaunyNPCSpawn spawn : spawns) {
			if (spawn.npcId > Utils.getNPCDefinitionsSize())
				continue;
			converted.add(new NPCSpawn(spawn.npcId, new WorldTile(spawn.x, spawn.y, spawn.plane), spawn.npcName));
		}
		JsonFileManager.saveJsonFile(converted, new File("./dumps/shaunySpawns.json"));
	}

}
