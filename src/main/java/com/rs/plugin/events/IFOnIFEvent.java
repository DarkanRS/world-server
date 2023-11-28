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

import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.handlers.*;

import java.util.HashMap;
import java.util.Map;

public class IFOnIFEvent implements PluginEvent {

	private static Map<Object, Map<Object, InterfaceOnInterfaceHandler>> FROM_HANDLERS = new HashMap<>();
	private static Map<Object, Map<Object, InterfaceOnInterfaceHandler>> TO_HANDLERS = new HashMap<>();

	private Player player;
	private int fromInterfaceId, fromComponentId, fromSlotId, fromSlotId2;
	private int toInterfaceId, toComponentId, toSlotId, toSlotId2;

	public IFOnIFEvent(Player player, int fromInterfaceId, int fromComponentId, int fromSlotId, int fromSlotId2, int toInterfaceId, int toComponentId, int toSlotId, int toSlotId2) {
		this.player = player;
		this.fromInterfaceId = fromInterfaceId;
		this.fromComponentId = fromComponentId;
		this.fromSlotId = fromSlotId;
		this.fromSlotId2 = fromSlotId2;
		this.toInterfaceId = toInterfaceId;
		this.toComponentId = toComponentId;
		this.toSlotId = toSlotId;
		this.toSlotId2 = toSlotId2;
	}

	public Player getPlayer() {
		return player;
	}

	public int getFromInterfaceId() {
		return fromInterfaceId;
	}

	public int getFromComponentId() {
		return fromComponentId;
	}

	public int getFromSlotId() {
		return fromSlotId;
	}

	public int getFromSlotId2() {
		return fromSlotId2;
	}

	public int getToInterfaceId() {
		return toInterfaceId;
	}

	public int getToComponentId() {
		return toComponentId;
	}

	public int getToSlotId() {
		return toSlotId;
	}

	public int getToSlotId2() {
		return toSlotId2;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		boolean to = false;
		Map<Object, InterfaceOnInterfaceHandler> mapping = FROM_HANDLERS.get((fromInterfaceId << 16) + fromComponentId);
		if (mapping == null) {
			mapping = TO_HANDLERS.get((toInterfaceId << 16) + toComponentId);
			to = true;
		}
		if (mapping != null) {
			InterfaceOnInterfaceHandler handler = mapping.get(((to ? fromInterfaceId : toInterfaceId) << 16) + (to ? fromComponentId : toComponentId));
			if (handler == null)
				handler = mapping.get(to ? fromInterfaceId : toInterfaceId);
			if (handler == null)
				handler = mapping.get(-1);
			if (handler != null)
				return handler;
		}
		to = false;
		mapping = FROM_HANDLERS.get(fromInterfaceId);
		if (mapping == null) {
			mapping = TO_HANDLERS.get(toInterfaceId);
			to = true;
		}
		if (mapping != null) {
			InterfaceOnInterfaceHandler handler = mapping.get(((to ? fromInterfaceId : toInterfaceId) << 16) + (to ? fromComponentId : toComponentId));
			if (handler == null)
				handler = mapping.get(to ? fromInterfaceId : toInterfaceId);
			if (handler == null)
				handler = mapping.get(-1);
			if (handler != null)
				return handler;
		}
		return null;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		InterfaceOnInterfaceHandler handler = (InterfaceOnInterfaceHandler) method;
		map(handler, false);
		if (handler.isBidirectional())
			map(handler, true);
	}

	public static void map(InterfaceOnInterfaceHandler handler, boolean reverse) {
		Object[] fromKeys = (reverse ? handler.getToKeys() : handler.getFromKeys());
		Object[] toKeys = (reverse ? handler.getFromKeys() : handler.getToKeys());

		if (fromKeys != null) {
			for (Object fromKey : fromKeys) {
				Map<Object, InterfaceOnInterfaceHandler> toMapping = FROM_HANDLERS.get(fromKey);
				if (toMapping == null)
					toMapping = new HashMap<>();
				if (toKeys == null)
					toMapping.put(-1, handler);
				else
					for (Object toKey : toKeys)
						toMapping.put(toKey, handler);
				FROM_HANDLERS.put(fromKey, toMapping);
			}
		} else if (toKeys != null) {
			for (Object toKey : toKeys) {
				Map<Object, InterfaceOnInterfaceHandler> toMapping = TO_HANDLERS.get(toKey);
				if (toMapping == null)
					toMapping = new HashMap<>();
				if (fromKeys == null)
					toMapping.put(-1, handler);
				else
					for (Object fromKey : fromKeys)
						toMapping.put(fromKey, handler);
				TO_HANDLERS.put(toKey, toMapping);
			}
		}
	}
}
