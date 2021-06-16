package com.rs.plugin.events;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.PluginHandler;

public class ItemOnNPCEvent implements PluginEvent {
	
	private static Map<Object, ItemOnNPCHandler> HANDLERS = new HashMap<>();

	private Player player;
	private NPC npc;
	private Item item;
	private boolean atNPC;

	public ItemOnNPCEvent(Player player, NPC npc, Item item, boolean atNPC) {
		this.player = player;
		this.npc = npc;
		this.item = item;
		this.atNPC = atNPC;
	}

	public Player getPlayer() {
		return player;
	}

	public NPC getNPC() {
		return npc;
	}

	public Item getItem() {
		return item;
	}

	public boolean isAtNPC() {
		return atNPC;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		ItemOnNPCHandler method = HANDLERS.get(getNPC().getId());
		if (method == null)
			method = HANDLERS.get(getNPC().getDefinitions().getName(getPlayer().getVars()));
		if (method == null)
			return null;
		if (!isAtNPC() && method.isCheckDistance())
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, (ItemOnNPCHandler) method);
			if (old != null) {
				System.err.println("ERROR: Duplicate ItemOnNPC methods for key: " + key);
			}
		}
	}
}
