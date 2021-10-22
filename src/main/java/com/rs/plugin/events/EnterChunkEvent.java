package com.rs.plugin.events;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.plugin.handlers.PluginHandler;

public class EnterChunkEvent implements PluginEvent {
	
	private static List<PluginHandler<? extends PluginEvent>> HANDLERS = new ArrayList<>();

	private Entity entity;
	private int chunkId;

	public EnterChunkEvent(Entity entity, int chunkId) {
		this.entity = entity;
		this.chunkId = chunkId;
	}
	
	public NPC getNPC() {
		if (entity instanceof NPC n)
			return n;
		return null;
	}
	
	public Player getPlayer() {
		if (entity instanceof Player p)
			return p;
		return null;
	}

	public Entity getEntity() {
		return entity;
	}

	public int getChunkId() {
		return chunkId;
	}

	@Override
	public List<PluginHandler<? extends PluginEvent>> getMethods() {
		return HANDLERS;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		HANDLERS.add(method);
	}
}
