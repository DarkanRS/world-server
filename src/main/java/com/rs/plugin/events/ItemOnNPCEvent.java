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
import com.rs.lib.game.Item;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.PluginHandler;

import java.util.HashMap;
import java.util.Map;

public class ItemOnNPCEvent implements PluginEvent {

	private static Map<Object, ItemOnNPCHandler> HANDLERS = new HashMap<>();

	private Player player;
	private NPC npc;
	private Item item;
	private boolean atNPC;

	public ItemOnNPCEvent(Player player, NPC npc, Item item, boolean atNPC) {
		this.player = player;
		this.npc = npc;
		this.item = item;
		this.atNPC = atNPC;
	}

	public Player getPlayer() {
		return player;
	}

	public NPC getNPC() {
		return npc;
	}

	public Item getItem() {
		return item;
	}

	public boolean isAtNPC() {
		return atNPC;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		ItemOnNPCHandler method = HANDLERS.get(getNPC().getId());
		if (method == null)
			method = HANDLERS.get(getNPC().getDefinitions().getName(getPlayer().getVars()));
		if ((method == null) || (!isAtNPC() && method.isCheckDistance()))
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, (ItemOnNPCHandler) method);
			if (old != null)
				System.err.println("ERROR: Duplicate ItemOnNPC methods for key: " + key);
		}
	}
}
