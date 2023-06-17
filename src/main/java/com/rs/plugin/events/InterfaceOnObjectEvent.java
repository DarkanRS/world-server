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
import com.rs.plugin.handlers.InterfaceOnObjectHandler;
import com.rs.plugin.handlers.PluginHandler;

import java.util.HashMap;
import java.util.Map;

public class InterfaceOnObjectEvent implements PluginEvent {

	private static Map<Object, InterfaceOnObjectHandler> HANDLERS = new HashMap<>();

	private Player player;
	private int interfaceId;
	private int componentId;
	private int slotId;
	private GameObject object;
	private boolean atObject;

	public InterfaceOnObjectEvent(Player player, GameObject object, int interfaceId, int componentId, int slotId, boolean atObject) {
		this.player = player;
		this.interfaceId = interfaceId;
		this.componentId = componentId;
		this.slotId = slotId;
		this.object = object;
		this.atObject = atObject;
	}

	public Player getPlayer() {
		return player;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public int getComponentId() {
		return componentId;
	}

	public int getSlotId() {
		return slotId;
	}

	public GameObject getObject() {
		return object;
	}

	public int getObjectId() {
		return object.getId();
	}

	public boolean isAtObject() {
		return atObject;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		InterfaceOnObjectHandler method = HANDLERS.get((interfaceId << 16) + componentId);
		if (method == null)
			method = HANDLERS.get(getInterfaceId());
		if ((method == null) || (!isAtObject() && method.isCheckDistance()))
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, (InterfaceOnObjectHandler) method);
			if (old != null)
				System.err.println("ERROR: Duplicate InterfaceOnObject methods for key: " + key);
		}
	}
}
