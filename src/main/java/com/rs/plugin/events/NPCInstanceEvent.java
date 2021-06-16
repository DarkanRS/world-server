package com.rs.plugin.events;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.plugin.handlers.PluginHandler;

public class NPCInstanceEvent implements PluginEvent {
	
	private static Map<Object, NPCInstanceHandler> HANDLERS = new HashMap<>();

	private int npcId;
	private WorldTile tile;
	private boolean spawned;

	public NPCInstanceEvent(int npcId, WorldTile tile, boolean spawned) {
		this.npcId = npcId;
		this.tile = tile;
		this.spawned = spawned;
	}
	
	public int getNpcId() {
		return npcId;
	}

	public WorldTile getTile() {
		return tile;
	}
	
	public boolean isSpawned() {
		return spawned;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		NPCInstanceHandler method = HANDLERS.get(npcId);
		if (method == null)
			method = HANDLERS.get(NPCDefinitions.getDefs(npcId).getName());
		if (method == null)
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, (NPCInstanceHandler) method);
			if (old != null) {
				System.err.println("ERROR: Duplicate ItemOnNPC methods for key: " + key);
			}
		}
	}
}
