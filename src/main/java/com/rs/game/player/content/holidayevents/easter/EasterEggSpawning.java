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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.holidayevents.easter;

import java.util.List;

import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.region.Region;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class EasterEggSpawning {

	static int eggsCount = 0;
	static int eggsPerRegion = 50;
	static int[] regionsToSpawn = { 12850, 11828, 12084, 12853, 12597, 12342, 10806, 10547, 13105 };

	//@ServerStartupEvent
	public static void initSpawning() {
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				for (int id : regionsToSpawn)
					World.getRegion(id, true);
			}
		}, 10);
		CoresManager.schedule(() -> {
			try {
				spawnEggs();
				World.sendWorldMessage("<col=FF0000><shad=000000>Easter Eggs have spawned in various cities around the world!", false);
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}, Ticks.fromSeconds(30), Ticks.fromMinutes(30));
	}


	public static int countEggs(int regionId) {
		eggsCount = 0;
		List<GroundItem> itemSpawns = World.getRegion(regionId).getAllGroundItems();
		if (itemSpawns != null && itemSpawns.size() > 0)
			itemSpawns.forEach( spawn -> {
				if (spawn.getId() == 1961)
					eggsCount += 1;
			});
		return eggsCount;
	}

	public static void spawnEggs() {
		for (int id : regionsToSpawn) {
			Region r = World.getRegion(id);
			int eggsNeeded = eggsPerRegion-countEggs(id);
			for (int i = 0; i < eggsNeeded; i++) {
				int x = r.getBaseX()+Utils.random(64);
				int y = r.getBaseY()+Utils.random(64);
				WorldTile tile = new WorldTile(x, y, 0);
				while (!World.floorAndWallsFree(tile, 1)) {
					x = r.getBaseX()+Utils.random(64);
					y = r.getBaseY()+Utils.random(64);
					tile = new WorldTile(x, y, 0);
				}
				World.addGroundItem(new Item(1961), new WorldTile(x, y, 0));
			}
		}
	}
}
