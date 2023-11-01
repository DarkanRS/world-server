// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.plugin.events;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.PluginHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemOnObjectEvent implements PluginEvent {

	private static Map<Object, Map<Object, Map<Integer, List<ItemOnObjectHandler>>>> OBJECT_HANDLERS = new HashMap<>();
	private static Map<Object, Map<Integer, List<ItemOnObjectHandler>>> ITEM_HANDLERS = new HashMap<>();

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
		objectId = object.getId();
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
		Map<Integer, List<ItemOnObjectHandler>> tileMaps = null;
		Map<Object, Map<Integer, List<ItemOnObjectHandler>>> itemMappings = OBJECT_HANDLERS.get(object.getId());
		if (itemMappings == null)
			itemMappings = OBJECT_HANDLERS.get(object.getDefinitions(player).getName());
		if (itemMappings != null) {
			if (tileMaps == null)
				tileMaps = itemMappings.get(item.getDefinitions().getName());
			if (tileMaps == null)
				tileMaps = itemMappings.get(item.getId());
			if (tileMaps == null)
				tileMaps = itemMappings.get(-1);
		} else {
			if (tileMaps == null)
				tileMaps = ITEM_HANDLERS.get(item.getDefinitions().getName());
			if (tileMaps == null)
				tileMaps = ITEM_HANDLERS.get(item.getId());
		}
		if (tileMaps == null)
			return null;
		List<ItemOnObjectHandler> methods = tileMaps.get(getObject().getTile().getTileHash());
		if (methods == null)
			methods = tileMaps.get(-getObject().getType().id);
		if (methods == null)
			methods = tileMaps.get(0);
		for (ItemOnObjectHandler method : methods) {
			if (!isAtObject() && method.isCheckDistance())
				continue;
			valids.add(method);
		}
		return valids;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		ItemOnObjectHandler handler = (ItemOnObjectHandler) method;
		if (handler.getObjectKeys() != null) {
			for (Object objectKey : handler.getObjectKeys()) {
				Map<Object, Map<Integer, List<ItemOnObjectHandler>>> itemMap = OBJECT_HANDLERS.computeIfAbsent(objectKey, k -> new HashMap<>());
				if (handler.getItemKeys() != null) {
					for (Object itemKey : handler.getItemKeys())
						updateLocationMap(handler, itemMap.computeIfAbsent(itemKey, k -> new HashMap<>()));
				} else
					updateLocationMap(handler, itemMap.computeIfAbsent(-1, k -> new HashMap<>()));
			}
		} else {
			for (Object itemKey : handler.getItemKeys())
				updateLocationMap(handler, ITEM_HANDLERS.computeIfAbsent(itemKey, k -> new HashMap<>()));
		}
	}

	private static void updateLocationMap(ItemOnObjectHandler handler, Map<Integer, List<ItemOnObjectHandler>> locMap) {
		if (handler.getTiles() == null || handler.getTiles().length <= 0) {
			List<ItemOnObjectHandler> methods = locMap.computeIfAbsent(0, k -> new ArrayList<>());
			methods.add(handler);
		} else {
			for (Tile tile : handler.getTiles()) {
				List<ItemOnObjectHandler> methods = locMap.computeIfAbsent(tile.getTileHash(), k -> new ArrayList<>());
				methods.add(handler);
			}
		}
	}

}
