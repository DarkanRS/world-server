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
package com.rs.utils;

import com.google.gson.JsonIOException;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@PluginEventHandler
public class Areas {

	private final static String PATH = "data/areas/";
	private static HashMap<String, Set<Integer>> AREAS = new HashMap<>();

	@SuppressWarnings("unchecked")
	@ServerStartupEvent(Priority.FILE_IO)
	public static final void init() throws JsonIOException, IOException {
		Logger.info(Areas.class, "init", "Loading areas...");
		File[] spawnFiles = new File(PATH).listFiles();
		for (File f : spawnFiles) {
			Set<Object> initial = JsonFileManager.loadJsonFile(f, Set.class);
			Set<Integer> area = initial.stream().map(number -> {
						if (number instanceof Integer)
							return (Integer) number;
						else if (number instanceof Double)
							return ((Double) number).intValue();
						else if (number instanceof Long)
							return ((Long) number).intValue();
						else
							throw new IllegalArgumentException("Unsupported chunk ID type: " + number.getClass().getName());
					}).collect(Collectors.toSet());
			AREAS.put(f.getName().replace(".json", ""), area);
		}
		Logger.info(Areas.class, "init", "Loaded " + AREAS.size() + " areas...");
	}

	@SuppressWarnings("unlikely-arg-type")
	public static boolean withinArea(String name, int chunkId) {
		Set<Integer> area = AREAS.get(name);
		if (area != null)
			return AREAS.get(name).contains(chunkId);
		return false;
	}

//	public static void main(String[] args) throws JsonIOException, IOException {
//		Logger.setupFormat();
//		Logger.setLevel(Level.FINE); //FINER for traces
//		JsonFileManager.setGSON(new GsonBuilder()
//				.setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
//				.registerTypeAdapter(Controller.class, new ControllerAdapter())
//				.registerTypeAdapter(Date.class, new DateAdapter())
//				.registerTypeAdapter(PacketEncoder.class, new PacketEncoderAdapter())
//				.registerTypeAdapter(Packet.class, new PacketAdapter())
//				.registerTypeAdapterFactory(new RecordTypeAdapterFactory())
//				.disableHtmlEscaping()
//				.setPrettyPrinting()
//				.create());
//
//		Settings.loadConfig();
//		if (!Settings.getConfig().isDebug())
//			Logger.setLevel(Level.WARNING);
//		Cache.init(Settings.getConfig().getCachePath());
//		init();
//		Set<Integer> area = AREAS.get("christmasevent");
//		Set<Integer> newMulti = new HashSet<>();
//		for (int d : area) {
//			for (int newId : oldChunkIdToNewChunkIds(d))
//				newMulti.add(newId);
//		}
//		JsonFileManager.saveJsonFile(newMulti, new File("./dumps/christmasevent.json"));
//		Set<Integer> newOnes = JsonFileManager.loadJsonFile(new File("./dumps/christmasevent.json"), Set.class);
//		System.out.println(newOnes);
//	}

	public static int[] oldChunkIdToNewChunkIds(int oldChunkId) {
		int chunkX = oldChunkId >> 14 & 2047;
		int chunkY = oldChunkId >> 3 & 2047;
		return new int[] {
				MapUtils.encode(MapUtils.Structure.CHUNK, chunkX, chunkY, 0),
				MapUtils.encode(MapUtils.Structure.CHUNK, chunkX, chunkY, 1),
				MapUtils.encode(MapUtils.Structure.CHUNK, chunkX, chunkY, 2),
				MapUtils.encode(MapUtils.Structure.CHUNK, chunkX, chunkY, 3)
		};
	}
}
