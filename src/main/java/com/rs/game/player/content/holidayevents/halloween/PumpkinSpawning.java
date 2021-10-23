package com.rs.game.player.content.holidayevents.halloween;

import java.util.List;

import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.player.content.holidayevents.halloween.hw07.Halloween2007;
import com.rs.game.player.content.holidayevents.halloween.hw09.Halloween2009;
import com.rs.game.region.Region;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.utils.Ticks;

@PluginEventHandler
public class PumpkinSpawning {
	
	static int[] regionsToSpawn = new int[] { 12850, 11828, 12084, 12853, 12597, 12342, 10806, 10547, 13105 };
	static int pumpkinCount = 0;
	static int pumpkinsPerRegion = 50;
	
	@ServerStartupEvent
	public static void initSpawning() {
		if (!Halloween2007.ENABLED && !Halloween2009.ENABLED)
			return;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				for (int id : regionsToSpawn) {
					World.getRegion(id, true);
				}
			}
		}, 10);
		CoresManager.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawnPumpkins();
					World.sendWorldMessage("<col=EB6123><shad=000000>Pumpkins have spawned in various cities around the world!", false);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, Ticks.fromSeconds(30), Ticks.fromHours(1));
	}

	public static int countPumpkins(int regionId) {
		pumpkinCount = 0;
		List<GroundItem> itemSpawns = World.getRegion(regionId).getAllGroundItems();
		if (itemSpawns != null && itemSpawns.size() > 0) {
			itemSpawns.forEach( spawn -> {
				if (spawn.getId() == 1959)
					pumpkinCount += 1;
			});
		}
		return pumpkinCount;
	}
	
	public static void spawnPumpkins() {
		for (int id : regionsToSpawn) {
			Region r = World.getRegion(id);
			int eggsNeeded = pumpkinsPerRegion-countPumpkins(id);
			for (int i = 0; i < eggsNeeded; i++) {
				int x = r.getBaseX()+Utils.random(64);
				int y = r.getBaseY()+Utils.random(64);
				WorldTile tile = new WorldTile(x, y, 0);
				while (!World.floorAndWallsFree(tile, 1)) {
					x = r.getBaseX()+Utils.random(64);
					y = r.getBaseY()+Utils.random(64);
					tile = new WorldTile(x, y, 0);
				}
				World.addGroundItem(new Item(1959), new WorldTile(x, y, 0));
			}
		}
	}
}
