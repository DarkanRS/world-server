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
package com.rs.game.content.holidayevents.halloween;

import java.util.List;

import com.rs.game.World;
import com.rs.game.content.holidayevents.halloween.hw07.Halloween2007;
import com.rs.game.content.holidayevents.halloween.hw09.Halloween2009;
import com.rs.game.map.Chunk;
import com.rs.game.map.ChunkManager;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;
import com.rs.utils.Ticks;
import it.unimi.dsi.fastutil.ints.IntSet;

@PluginEventHandler
public class PumpkinSpawning {

	static IntSet regionsToSpawn = IntSet.of(12850, 11828, 12084, 12853, 12597, 12342, 10806, 10547, 13105);
	static int pumpkinCount = 0;
	static int pumpkinsPerChunk = 2;

	@ServerStartupEvent(Priority.POST_PROCESS)
	public static void initSpawning() {
		if (!Halloween2007.ENABLED && !Halloween2009.ENABLED)
			return;
		ChunkManager.permanentlyPreloadRegions(regionsToSpawn);
		WorldTasks.schedule(Ticks.fromSeconds(30), Ticks.fromHours(1), () -> {
			try {
				spawnPumpkins();
				World.sendWorldMessage("<col=EB6123><shad=000000>Pumpkins have spawned in various cities around the world!", false);
			} catch (Throwable e) {
				Logger.handle(PumpkinSpawning.class, "initSpawning", e);
			}
		});
	}

	public static int countPumpkins(int chunkId) {
		pumpkinCount = 0;
		List<GroundItem> itemSpawns = ChunkManager.getChunk(chunkId).getAllGroundItems();
		if (itemSpawns != null && itemSpawns.size() > 0)
			itemSpawns.forEach( spawn -> {
				if (spawn.getId() == 1959)
					pumpkinCount += 1;
			});
		return pumpkinCount;
	}

	public static void spawnPumpkins() {
		for (int id : World.mapRegionIdsToChunks(regionsToSpawn, 0)) {
			Chunk r = ChunkManager.getChunk(id);
			int eggsNeeded = pumpkinsPerChunk-countPumpkins(id);
			for (int i = 0; i < eggsNeeded; i++) {
				int x = r.getBaseX()+Utils.random(8);
				int y = r.getBaseY()+Utils.random(8);
				Tile tile = Tile.of(x, y, 0);
				while (!World.floorAndWallsFree(tile, 1)) {
					x = r.getBaseX()+Utils.random(8);
					y = r.getBaseY()+Utils.random(8);
					tile = Tile.of(x, y, 0);
				}
				World.addGroundItem(new Item(1959), Tile.of(x, y, 0));
			}
		}
	}
}
