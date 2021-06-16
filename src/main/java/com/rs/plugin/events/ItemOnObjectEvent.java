package com.rs.plugin.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.PluginHandler;

public class ItemOnObjectEvent implements PluginEvent {
	
	private static Map<Object, Map<Integer, List<ItemOnObjectHandler>>> METHODS = new HashMap<>();

	private Player player;
	private Item item;
	private GameObject object;
	private boolean atObject;
	private int objectId;

	public ItemOnObjectEvent(Player player, Item item, GameObject object, boolean atObject) {
		this.player = player;
		this.item = item;
		this.object = object;
		this.atObject = atObject;
		this.objectId = object.getId();
	}

	public Player getPlayer() {
		return player;
	}

	public Item getItem() {
		return item;
	}

	public GameObject getObject() {
		return object;
	}

	public boolean isAtObject() {
		return atObject;
	}
	
	public int getObjectId() {
		return objectId;
	}
	
	@Override
	public List<PluginHandler<? extends PluginEvent>> getMethods() {
		List<PluginHandler<? extends PluginEvent>> valids = new ArrayList<>();
		Map<Integer, List<ItemOnObjectHandler>> methodMapping = METHODS.get(getObjectId());
		if (methodMapping == null)
			methodMapping = METHODS.get(getObject().getDefinitions(getPlayer()).getName());
		if (methodMapping == null)
			return null;
		List<ItemOnObjectHandler> methods = methodMapping.get(getObject().getTileHash());
		if (methods == null)
			methods = methodMapping.get(0);
		if (methods == null)
			return null;
		for (ItemOnObjectHandler method : methods) {
			if (!isAtObject() && method.isCheckDistance())
				continue;
			valids.add(method);
		}
		return valids;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		ItemOnObjectHandler handler = (ItemOnObjectHandler) method;
		for (Object key : handler.keys()) {
			Map<Integer, List<ItemOnObjectHandler>> locMap = METHODS.get(key);
			if (locMap == null) {
				locMap = new HashMap<>();
				METHODS.put(key, locMap);
			}
			if (handler.getTiles() == null || handler.getTiles().length <= 0) {
				List<ItemOnObjectHandler> methods = locMap.get(0);
				if (methods == null)
					methods = new ArrayList<>();
				methods.add(handler);
				locMap.put(0, methods);
			} else {
				for (WorldTile tile : handler.getTiles()) {
					List<ItemOnObjectHandler> methods = locMap.get(tile.getTileHash());
					if (methods == null)
						methods = new ArrayList<>();
					methods.add(handler);
					locMap.put(tile.getTileHash(), methods);
				}
			}
		}
	}

}
