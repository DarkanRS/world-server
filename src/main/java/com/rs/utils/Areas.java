package com.rs.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;

import com.google.gson.JsonIOException;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class Areas {
	
	private final static String PATH = "data/areas/";
	final static Charset ENCODING = StandardCharsets.UTF_8;
	
	private static HashMap<String, Set<Integer>> AREAS = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	@ServerStartupEvent
	public static final void init() throws JsonIOException, IOException {
		Logger.log("Areas", "Loading areas...");
		File[] spawnFiles = new File(PATH).listFiles();
		for (File f : spawnFiles) {
			Set<Integer> area = (Set<Integer>) JsonFileManager.loadJsonFile(f, Set.class);
			AREAS.put(f.getName().replace(".json", ""), area);
		}
		Logger.log("Areas", "Loaded " + AREAS.size() + " areas...");
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public static boolean withinArea(String name, double chunkId) {
		Set<Integer> area = AREAS.get(name);
		if (area != null)
			return AREAS.get(name).contains(chunkId);
		return false;
	}
	
//	public static void main(String[] args) throws JsonIOException, IOException {
//		init();
//		Set<Double> multi = (Set<Double>) JsonFileManager.loadJsonFile(new File("./data/areas/multi.json"), Set.class);
//		Iterator<Double> i = multi.iterator();
//		List<Double> toRemove = new ArrayList<>();
//		while(i.hasNext()) {
//			Double d = i.next();
//			if (MapUtils.chunkToRegionId(d.intValue()) == 14231) {
//				toRemove.add(d);
//			}
//		}
//		for (Double d : toRemove)
//			multi.remove(d);
//		JsonFileManager.saveJsonFile(multi, new File("./dumps/multi.json"));
//	}

}
