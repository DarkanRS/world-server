// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.tools.old;

import com.google.gson.JsonIOException;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.spawns.NPCSpawn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
			converted.add(new NPCSpawn(spawn.npcId, Tile.of(spawn.x, spawn.y, spawn.plane), spawn.npcName));
		}
		JsonFileManager.saveJsonFile(converted, new File("./dumps/shaunySpawns.json"));
	}

}
