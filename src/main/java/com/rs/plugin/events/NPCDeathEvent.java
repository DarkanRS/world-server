package com.rs.plugin.events;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.plugin.handlers.PluginHandler;

public class NPCDeathEvent implements PluginEvent {
	
	private static Map<Object, PluginHandler<? extends PluginEvent>> HANDLERS = new HashMap<>();

	private NPC npc;
	private Entity killer;

	public NPCDeathEvent(NPC npc, Entity killer) {
		this.npc = npc;
		this.killer = killer;
	}

	public NPC getNPC() {
		return npc;
	}

	public Entity getKiller() {
		return killer;
	}

	public boolean killedByPlayer() {
		if (killer != null && (killer instanceof Player))
			return true;
		return false;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		PluginHandler<? extends PluginEvent> method = HANDLERS.get(npc.getId());
		if (method == null)
			method = HANDLERS.get(npc.getDefinitions().getName());
		if (method == null)
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, method);
			if (old != null) {
				System.err.println("ERROR: Duplicate NPCDeath methods for key: " + key);
			}
		}
	}

}
