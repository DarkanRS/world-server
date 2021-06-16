package com.rs.plugin.events;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.handlers.PluginHandler;

public class ItemEquipEvent implements PluginEvent {
	
	private static Map<Object, PluginHandler<? extends PluginEvent>> HANDLERS = new HashMap<>();

	private Player player;
	private Item item;
	private boolean equipping;

	public ItemEquipEvent(Player player, Item item, boolean equipping) {
		this.player = player;
		this.item = item;
		this.equipping = equipping;
	}

	public Player getPlayer() {
		return player;
	}

	public Item getItem() {
		return item;
	}
	
	public boolean equip() {
		return equipping;
	}
	
	public boolean dequip() {
		return !equipping;
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
}
