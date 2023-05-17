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
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.handlers.PluginHandler;

import java.util.HashMap;
import java.util.Map;

public class ButtonClickEvent implements PluginEvent {

	private static Map<Object, PluginHandler<? extends PluginEvent>> HANDLERS = new HashMap<>();

	private Player player;
	private int interfaceId, componentId, slotId, slotId2;
	private ClientPacket packet;

	public ButtonClickEvent(Player player, int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		this.player = player;
		this.interfaceId = interfaceId;
		this.componentId = componentId;
		this.slotId = slotId;
		this.slotId2 = slotId2;
		this.packet = packet;
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

	public int getSlotId2() {
		return slotId2;
	}

	public ClientPacket getPacket() {
		return packet;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		return HANDLERS.get(getInterfaceId());
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, method);
			if (old != null)
				System.err.println("ERROR: Duplicate ButtonClick methods for key: " + key);
		}
	}

}
