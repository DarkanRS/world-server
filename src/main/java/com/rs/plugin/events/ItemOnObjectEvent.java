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
import com.rs.plugin.handlers.NPCDropHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
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
		Map<Object, Map<Integer, List<ItemOnObjectHandler>>> itemFromNPCHandlers = OBJECT_HANDLERS.get(object.getDefinitions(player).getName());
		if (itemFromNPCHandlers == null)
			itemFromNPCHandlers = OBJECT_HANDLERS.get(object.getId());
		if (itemFromNPCHandlers != null) {
			if (tileMaps == null)
				tileMaps = itemFromNPCHandlers.get(item.getDefinitions().getName());
			if (tileMaps == null)
				tileMaps = itemFromNPCHandlers.get(item.getId());
			if (tileMaps == null)
				tileMaps = itemFromNPCHandlers.get(-1);
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
				Map<Object, Map<Integer, List<ItemOnObjectHandler>>> itemMap = OBJECT_HANDLERS.get(objectKey);
				if (itemMap == null) {
					itemMap = new HashMap<>();
					OBJECT_HANDLERS.put(objectKey, itemMap);
				}
				if (handler.getItemKeys() != null) {
					for (Object itemKey : handler.getItemKeys()) {
						Map<Integer, List<ItemOnObjectHandler>> locMap = itemMap.get(itemKey);
						if (locMap == null) {
							locMap = new HashMap<>();
							itemMap.put(itemKey, locMap);
						}
						mapLocations(handler, locMap);
					}
				} else {
					if (handler.getTiles() == null || handler.getTiles().length <= 0) {
						List<ItemOnObjectHandler> methods = new ArrayList<>();
						Map<Integer, List<ItemOnObjectHandler>> locMap = itemMap.get(-1);
						if (locMap == null) {
							locMap = new HashMap<>();
							itemMap.put(-1, locMap);
						}
						if (methods == null)
							methods = new ArrayList<>();
						methods.add(handler);
						locMap.put(0, methods);
					} else
						for (Tile tile : handler.getTiles()) {
							List<ItemOnObjectHandler> methods = new ArrayList<>();
							Map<Integer, List<ItemOnObjectHandler>> locMap = itemMap.get(tile.getTileHash());
							if (locMap == null) {
								locMap = new HashMap<>();
								itemMap.put(tile.getTileHash(), locMap);
							}
							if (methods == null)
								methods = new ArrayList<>();
							methods.add(handler);
							locMap.put(tile.getTileHash(), methods);
						}
				}
			}
		} else {
			for (Object itemKey : handler.getItemKeys()) {
				Map<Integer, List<ItemOnObjectHandler>> locMap = ITEM_HANDLERS.get(itemKey);
				if (locMap == null) {
					locMap = new HashMap<>();
					ITEM_HANDLERS.put(itemKey, locMap);
				}
				mapLocations(handler, locMap);
			}
		}
	}

	private static void mapLocations(ItemOnObjectHandler handler, Map<Integer, List<ItemOnObjectHandler>> locMap) {
		if (handler.getTiles() == null || handler.getTiles().length <= 0) {
			List<ItemOnObjectHandler> methods = locMap.get(0);
			if (methods == null)
				methods = new ArrayList<>();
			methods.add(handler);
			locMap.put(0, methods);
		} else
			for (Tile tile : handler.getTiles()) {
				List<ItemOnObjectHandler> methods = locMap.get(tile.getTileHash());
				if (methods == null)
					methods = new ArrayList<>();
				methods.add(handler);
				locMap.put(tile.getTileHash(), methods);
			}
	}

}
