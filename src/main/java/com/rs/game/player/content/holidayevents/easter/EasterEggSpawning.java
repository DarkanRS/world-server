package com.rs.game.player.content.holidayevents.easter;

import java.util.List;

import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.region.Region;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
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
	static int[] regionsToSpawn = new int[] { 12850, 11828, 12084, 12853, 12597, 12342, 10806, 10547, 13105 };
	
	//@ServerStartupEvent
	public static void initSpawning() {
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
					spawnEggs();
					World.sendWorldMessage("<col=FF0000><shad=000000>Easter Eggs have spawned in various cities around the world!", false);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, Ticks.fromSeconds(30), Ticks.fromMinutes(30));
	}
	
	
	public static int countEggs(int regionId) {
		eggsCount = 0;
		List<GroundItem> itemSpawns = World.getRegion(regionId).getAllGroundItems();
		if (itemSpawns != null && itemSpawns.size() > 0) {
			itemSpawns.forEach( spawn -> {
				if (spawn.getId() == 1961)
					eggsCount += 1;
			});
		}
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
