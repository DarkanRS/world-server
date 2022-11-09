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
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;

@PluginEventHandler
public final class ObjectSpawns {

	private final static String PATH = "data/map/objectspawns/";
	final static Charset ENCODING = StandardCharsets.UTF_8;

	private static final ArrayList<ObjectSpawn> ALL_SPAWNS = new ArrayList<>();
	private static final Map<Integer, List<ObjectSpawn>> OBJECT_SPAWNS = new HashMap<>();

	@ServerStartupEvent(Priority.FILE_IO)
	public static final void init() throws JsonIOException, IOException {
		Logger.info(ObjectSpawns.class, "init", "Loading map object spawns...");
		File[] spawnFiles = new File(PATH).listFiles();
		for (File f : spawnFiles) {
			ObjectSpawn[] spawns = (ObjectSpawn[]) JsonFileManager.loadJsonFile(f, ObjectSpawn[].class);
			if (spawns != null)
				for(ObjectSpawn spawn : spawns)
					if (spawn != null)
						add(spawn);
		}
		Logger.info(ObjectSpawns.class, "init", "Loaded " + ALL_SPAWNS.size() + " map object spawns...");
	}

	public static void loadObjectSpawns(int regionId) {
		List<ObjectSpawn> spawns = OBJECT_SPAWNS.get(regionId);
		if (spawns != null)
			for (ObjectSpawn spawn : spawns)
				spawn.spawn();
	}

	public static void add(ObjectSpawn spawn) {
		ALL_SPAWNS.add(spawn);
		List<ObjectSpawn> regionSpawns = OBJECT_SPAWNS.get(spawn.getTile().getRegionId());
		if (regionSpawns == null)
			regionSpawns = new ArrayList<>();
		regionSpawns.add(spawn);
		OBJECT_SPAWNS.put(spawn.getTile().getRegionId(), regionSpawns);
	}
}
