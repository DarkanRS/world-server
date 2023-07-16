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
import com.rs.plugin.handlers.InterfaceOnNPCHandler;
import com.rs.plugin.handlers.PluginHandler;

import java.util.HashMap;
import java.util.Map;

public class IFOnNPCEvent implements PluginEvent {

	private static Map<Object, InterfaceOnNPCHandler> HANDLERS = new HashMap<>();

	private Player player;
	private NPC target;
	private int interfaceId, componentId, slotId, slotId2;
	private boolean atNPC;

	public IFOnNPCEvent(Player player, NPC target, int interfaceId, int componentId, int slotId, int slotId2, boolean atNPC) {
		this.player = player;
		this.target = target;
		this.interfaceId = interfaceId;
		this.componentId = componentId;
		this.slotId = slotId;
		this.slotId2 = slotId2;
		this.atNPC = atNPC;
	}

	public Player getPlayer() {
		return player;
	}

	public NPC getTarget() {
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

	public boolean isAtNPC() {
		return atNPC;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		InterfaceOnNPCHandler method = HANDLERS.get((interfaceId << 16) + componentId);
		if (method == null)
			method = HANDLERS.get(getInterfaceId());
		if ((method == null) || (!isAtNPC() && method.isCheckDistance()))
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			InterfaceOnNPCHandler old = HANDLERS.put(key, (InterfaceOnNPCHandler) method);
			if (old != null)
				System.err.println("ERROR: Duplicate InterfaceOnNPC methods for key: " + key);
		}
	}
}
