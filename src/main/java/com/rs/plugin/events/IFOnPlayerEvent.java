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

import java.util.HashMap;
import java.util.Map;

import com.rs.game.model.entity.player.Player;
import com.rs.plugin.handlers.InterfaceOnPlayerHandler;
import com.rs.plugin.handlers.PluginHandler;

public class IFOnPlayerEvent implements PluginEvent {

	private static Map<Object, InterfaceOnPlayerHandler> HANDLERS = new HashMap<>();

	private Player player;
	private Player target;
	private int interfaceId, componentId, slotId, slotId2;
	private boolean atPlayer;

	public IFOnPlayerEvent(Player player, Player target, int interfaceId, int componentId, int slotId, int slotId2, boolean atPlayer) {
		this.player = player;
		this.target = target;
		this.interfaceId = interfaceId;
		this.componentId = componentId;
		this.slotId = slotId;
		this.slotId2 = slotId2;
		this.atPlayer = atPlayer;
	}

	public Player getPlayer() {
		return player;
	}

	public Player getTarget() {
		return target;
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

	public int getSlotId2() {
		return slotId2;
	}

	public boolean isAtPlayer() {
		return atPlayer;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		InterfaceOnPlayerHandler method = HANDLERS.get((interfaceId << 16) + componentId);
		if (method == null)
			method = HANDLERS.get(getInterfaceId());
		if ((method == null) || (!isAtPlayer() && method.isCheckDistance()))
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, (InterfaceOnPlayerHandler) method);
			if (old != null)
				System.err.println("ERROR: Duplicate InterfaceOnPlayer methods for key: " + key);
		}
	}
}
