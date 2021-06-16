package com.rs.tools;

import java.io.IOException;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.utils.spawns.NPCSpawn;
import com.rs.utils.spawns.NPCSpawns;

public class NPCSpawnChecker {
	
	public static void main(String[] args) throws IOException {
		//Cache.init();
		NPCSpawns.init();
		
		for (NPCSpawn spawn : NPCSpawns.getAllSpawns()) {
			NPCDefinitions defs = NPCDefinitions.getDefs(spawn.getNPCId());
			if (defs.getConfigInfoString().contains("transformed into by")) {
				System.out.println("NPC: " + defs.getName());
			}
		}
	}

}
