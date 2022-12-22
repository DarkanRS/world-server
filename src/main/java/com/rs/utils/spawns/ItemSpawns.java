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
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;

@PluginEventHandler
public final class ItemSpawns {

	private final static String PATH = "data/items/spawns/";
	final static Charset ENCODING = StandardCharsets.UTF_8;

	private static final Object lock = new Object();
	private static final ArrayList<ItemSpawn> ALL_SPAWNS = new ArrayList<>();
	private static final ArrayList<ItemSpawn> ADDED_SPAWNS = new ArrayList<>();
	private static final Map<Integer, List<ItemSpawn>> ITEM_SPAWNS = new HashMap<>();

	@SuppressWarnings("deprecation")
	public static boolean addSpawn(String username, int id, int amount, WorldTile tile) {
		synchronized (lock) {
			File file = new File("data/items/addedSpawns.json");
			ADDED_SPAWNS.add(new ItemSpawn(id, amount, tile, ""+ItemDefinitions.getDefs(id).getName()+" added by " + username));
			World.addGroundItemForever(new Item(id, amount), tile);
			try {
				JsonFileManager.saveJsonFile(ADDED_SPAWNS, file);
			} catch (IOException e) {
				return false;
			}
			return true;
		}
	}

	@ServerStartupEvent(Priority.FILE_IO)
	public static final void init() throws JsonIOException, IOException {
		Logger.info(ItemSpawns.class, "init", "Loading item spawns...");
		File[] spawnFiles = new File(PATH).listFiles();
		for (File f : spawnFiles) {
			ItemSpawn[] spawns = (ItemSpawn[]) JsonFileManager.loadJsonFile(f, ItemSpawn[].class);
			if (spawns != null)
				for(ItemSpawn spawn : spawns)
					if (spawn != null) {
						ALL_SPAWNS.add(spawn);
						List<ItemSpawn> regionSpawns = ITEM_SPAWNS.get(spawn.getTile().getRegionId());
						if (regionSpawns == null)
							regionSpawns = new ArrayList<>();
						regionSpawns.add(spawn);
						ITEM_SPAWNS.put(spawn.getTile().getRegionId(), regionSpawns);
					}
		}
		Logger.info(ItemSpawns.class, "init", "Loaded " + ALL_SPAWNS.size() + " item spawns...");
	}

	public static final void loadItemSpawns(int regionId) {
		List<ItemSpawn> spawns = ITEM_SPAWNS.get(regionId);
		if (spawns != null)
			for (ItemSpawn spawn : spawns)
				spawn.spawn();
	}
}
