package com.rs.plugin.events;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.player.Player;
import com.rs.lib.game.GroundItem;
import com.rs.plugin.handlers.PluginHandler;

public class PickupItemEvent implements PluginEvent {
	
	private static Map<Object, PluginHandler<? extends PluginEvent>> HANDLERS = new HashMap<>();

	private Player player;
	private GroundItem item;
	private boolean cancelPickup;

	public PickupItemEvent(Player player, GroundItem item) {
		this.player = player;
		this.item = item;
	}

	public Player getPlayer() {
		return player;
	}

	public GroundItem getItem() {
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
				System.err.println("ERROR: Duplicate ItemEquip methods for key: " + key);
			}
		}
	}

	public boolean isCancelPickup() {
		return cancelPickup;
	}

	public void setCancelPickup(boolean cancelPickup) {
		this.cancelPickup = cancelPickup;
	}
}
