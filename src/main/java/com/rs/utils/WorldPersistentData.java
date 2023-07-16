package com.rs.utils;

import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Item;
import com.rs.lib.util.GenericAttribMap;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

import java.io.File;
import java.io.IOException;

@PluginEventHandler
public class WorldPersistentData {
	
	private static final String DATA_PATH = "./data/world.json";
	
	private static WorldPersistentData ATTRIBUTES;
	
	@ServerStartupEvent
	public static void loadStorage() throws ClassNotFoundException {
		File dataFile = new File(DATA_PATH);
		if (!dataFile.exists())
			ATTRIBUTES = new WorldPersistentData();
		else
			try {
				ATTRIBUTES = (WorldPersistentData) JsonFileManager.loadJsonFile(dataFile, WorldPersistentData.class);
			} catch (Exception e) {
				ATTRIBUTES = new WorldPersistentData();
			}
	}

	public static void save() {
		try {
			File dataFile = new File(DATA_PATH);
			if (ATTRIBUTES == null)
				ATTRIBUTES = new WorldPersistentData();
			JsonFileManager.saveJsonFile(ATTRIBUTES, dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private GenericAttribMap counters;
	private ItemsContainer<Item> partyRoomStorage;
	private ItemsContainer<Item> partyRoomDrop;
	
	public WorldPersistentData() {
		counters = new GenericAttribMap();
	}

	public static WorldPersistentData get() {
		return ATTRIBUTES;
	}

	public GenericAttribMap getAttribs() {
		return counters;
	}

	public ItemsContainer<Item> getPartyRoomStorage() {
		if (partyRoomStorage == null)
			partyRoomStorage = new ItemsContainer<>(216, false);
		return partyRoomStorage;
	}

	public ItemsContainer<Item> getPartyRoomDrop() {
		if (partyRoomDrop == null)
			partyRoomDrop = new ItemsContainer<>(216, false);
		return partyRoomDrop;
	}
}
