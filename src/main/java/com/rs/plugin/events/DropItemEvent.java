package com.rs.plugin.events;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.handlers.PluginHandler;

public class DropItemEvent implements PluginEvent {
	
	private static Map<Object, PluginHandler<? extends PluginEvent>> HANDLERS = new HashMap<>();

	private Player player;
	private Item item;

	public DropItemEvent(Player player, Item item) {
		this.player = player;
		this.item = item;
	}

	public Player getPlayer() {
		return player;
	}

	public Item getItem() {
		return item;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		PluginHandler<? extends PluginEvent> method = HANDLERS.get(item.getId());
		if (method == null)
			method = HANDLERS.get(item.getDefinitions().getName());
		if (method == null)
			return null;
		return method;
	}
	
	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, method);
			if (old != null) {
				System.err.println("ERROR: Duplicate DropItem methods for key: " + key);
			}
		}
	}

}
