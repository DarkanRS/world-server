package com.rs.game.player.content.world.doors;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonIOException;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.lib.file.JsonFileManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class DoorPair {
	
	private static final String PATH = "./data/map/doorPairs.json";
	
	private static DoorPair[] DOOR_PAIRS;
	private static Map<Integer, Integer> PAIRING_MAP = new HashMap<>();
		
	private int closed;
	private int open;
	
	@ServerStartupEvent
	public static void loadPairs() {
		try {
			DOOR_PAIRS = (DoorPair[]) JsonFileManager.loadJsonFile(new File(PATH), DoorPair[].class);
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
		for (DoorPair pair : DOOR_PAIRS) {
			PAIRING_MAP.put(pair.open, pair.closed);
			PAIRING_MAP.put(pair.closed, pair.open);
		}
	}
	
	public static int getOpposingDoor(Player player, GameObject door) {
		return PAIRING_MAP.get(door.getDefinitions(player).id) != null ? PAIRING_MAP.get(door.getDefinitions(player).id) : 1532;
	}
	
	public DoorPair(int closed, int open) {
		this.closed = closed;
		this.open = open;
	}
	
	public int getClosed() {
		return closed;
	}

	public int getOpen() {
		return open;
	}

}
