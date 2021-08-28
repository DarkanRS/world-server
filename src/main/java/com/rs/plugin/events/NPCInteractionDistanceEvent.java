package com.rs.plugin.events;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.plugin.handlers.PluginHandler;

public class NPCInteractionDistanceEvent implements PluginEvent {
	
	private static Map<Object, NPCInstanceHandler> HANDLERS = new HashMap<>();

	private Player player;
	private NPC npc;

	public NPCInteractionDistanceEvent(Player player, NPC npc) {
		this.player = player;
		this.npc = npc;
	}
	
	public Player getPlayer() {
		return player;
	}

	public NPC getNpc() {
		return npc;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		NPCInstanceHandler method = HANDLERS.get(npc.getId());
		if (method == null)
			method = HANDLERS.get(npc.getDefinitions().getName(player.getVars()));
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
