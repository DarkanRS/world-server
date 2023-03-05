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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Tile;
import com.rs.plugin.handlers.PickupItemHandler;
import com.rs.plugin.handlers.PluginHandler;

public class PickupItemEvent implements PluginEvent {

	private static Map<Object, Map<Integer, List<PickupItemHandler>>> HANDLERS = new HashMap<>();

	private Player player;
	private GroundItem item;
	private boolean cancelPickup;
	private boolean telegrabbed;

	public PickupItemEvent(Player player, GroundItem item, boolean telegrabbed) {
		this.player = player;
		this.item = item;
		this.telegrabbed = telegrabbed;
	}

	public Player getPlayer() {
		return player;
	}

	public GroundItem getItem() {
		return item;
	}

	public boolean isTelegrabbed() {
		return telegrabbed;
	}

	@Override
	public List<PluginHandler<? extends PluginEvent>> getMethods() {
		List<PluginHandler<? extends PluginEvent>> valids = new ArrayList<>();
		Map<Integer, List<PickupItemHandler>> methodMapping = HANDLERS.get(getItem().getId());
		if (methodMapping == null)
			methodMapping = HANDLERS.get(getItem().getName());
		if (methodMapping == null)
			return null;
		List<PickupItemHandler> methods = methodMapping.get(getItem().getTile().getTileHash());
		if (methods == null)
			methods = methodMapping.get(-getItem().getId());
		if (methods == null)
			methods = methodMapping.get(0);
		if (methods == null)
			return null;
		for (PickupItemHandler method : methods)
			valids.add(method);
		return valids;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		PickupItemHandler handler = (PickupItemHandler) method;
		for (Object key : handler.keys()) {
			Map<Integer, List<PickupItemHandler>> locMap = HANDLERS.get(key);
			if (locMap == null) {
				locMap = new HashMap<>();
				HANDLERS.put(key, locMap);
			}
			if (handler.getTiles() == null || handler.getTiles().length <= 0) {
				List<PickupItemHandler> methods = locMap.get(0);
				if (methods == null)
					methods = new ArrayList<>();
				methods.add(handler);
				locMap.put(0, methods);
			} else
				for (Tile tile : handler.getTiles()) {
					List<PickupItemHandler> methods = locMap.get(tile.getTileHash());
					if (methods == null)
						methods = new ArrayList<>();
					methods.add(handler);
					locMap.put(tile.getTileHash(), methods);
				}
		}
	}

	public boolean isCancelPickup() {
		return cancelPickup;
	}

	public void cancelPickup() {
		this.cancelPickup = true;
	}
}
