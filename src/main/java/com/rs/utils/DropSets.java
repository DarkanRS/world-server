package com.rs.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.google.gson.JsonIOException;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

@PluginEventHandler
public class DropSets {

	private final static String PATH = "data/npcs/drops/";
	public static HashMap<String, DropSet> DROPS = new HashMap<>();
	public static HashMap<Object, DropSet> NPC_DROPS = new HashMap<>();
	public static final DropSet DEFAULT_DROPSET = new DropSet(new DropTable(0.0, 0.0, false));
	final static Charset ENCODING = StandardCharsets.UTF_8;

	@ServerStartupEvent
	public static final void init() {
		loadPackedNPCDrops();
	}

	public static void reloadDrops() {
		NPC_DROPS.clear();
		DROPS.clear();
		loadPackedNPCDrops();
	}
	
	public static DropSet getDropSet(String string) {
		if (NPC_DROPS.get(string) != null) {
			return NPC_DROPS.get(string);
		}
		return null;
	}

	public static DropSet getDropSet(int npcId) {
		if (NPC_DROPS.get(npcId) != null) {
			return NPC_DROPS.get(npcId);
		} else if (NPC_DROPS.get(NPCDefinitions.getDefs(npcId).getName()) != null) {
			return NPC_DROPS.get(NPCDefinitions.getDefs(npcId).getName());
		}
		return DEFAULT_DROPSET;
	}

	private static void loadPackedNPCDrops() {
		try {
			File[] dropFiles = new File(PATH).listFiles();
			for (File f : dropFiles) {
				loadFile(f);
			}
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}
	
	private static void loadFile(File f) throws JsonIOException, IOException {
		if (f.isDirectory()) {
			for (File dir : f.listFiles())
				loadFile(dir);
			return;
		}
		DropSet table = (DropSet) JsonFileManager.loadJsonFile(f, DropSet.class);
		if (table != null) {
			table.getDropList();
			if (table.isOverflowed()) {
				System.err.println(f.getName() + " is overflowed by " + table.getDropList().getOverflow());
			}
			DROPS.put(f.getName(), table);
			if (table.getIds() != null) {
				for (int id : table.getIds())
					NPC_DROPS.put(id, table);
			}
			if (table.getNames() != null) {
				for (String name : table.getNames())
					NPC_DROPS.put(name, table);
			}
		}
	}

	public HashMap<Object, DropSet> getDropMap() {
		return NPC_DROPS;
	}
}