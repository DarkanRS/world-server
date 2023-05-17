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
package com.rs.tools;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.utils.spawns.NPCSpawn;
import com.rs.utils.spawns.NPCSpawns;

import java.io.IOException;

public class NPCSpawnChecker {

	public static void main(String[] args) throws IOException {
		//Cache.init();
		NPCSpawns.init();

		for (NPCSpawn spawn : NPCSpawns.getAllSpawns()) {
			NPCDefinitions defs = NPCDefinitions.getDefs(spawn.getNPCId());
			if (defs.getConfigInfoString().contains("transformed into by"))
				System.out.println("NPC: " + defs.getName());
		}
	}

}
