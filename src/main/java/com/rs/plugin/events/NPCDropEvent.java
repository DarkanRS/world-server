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
import com.rs.plugin.handlers.NPCDropHandler;
import com.rs.plugin.handlers.PluginHandler;

import java.util.HashMap;
import java.util.Map;

public class NPCDropEvent implements PluginEvent {

	private static Map<Object, Map<Object, NPCDropHandler>> NPC_HANDLERS = new HashMap<>();
	private static Map<Object, NPCDropHandler> ITEM_HANDLERS = new HashMap<>();

	private Player player;
	private NPC npc;
	private Item item;

	public NPCDropEvent(Player player, NPC npc, Item item) {
		this.player = player;
		this.npc = npc;
		this.item = item;
	}

	public Player getPlayer() {
		return player;
	}

	public Item getItem() {
		return item;
	}
	
	public NPC getNPC() {
		return npc;
	}

	public void deleteItem() {
		item.setId(-1);
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		NPCDropHandler method = null;
		Map<Object, NPCDropHandler> itemFromNPCHandlers = NPC_HANDLERS.get(npc.getDefinitions(player).getName());
		if (itemFromNPCHandlers == null)
			itemFromNPCHandlers = NPC_HANDLERS.get(npc.getId());
		if (itemFromNPCHandlers != null) {
			if (method == null)
				method = itemFromNPCHandlers.get(item.getDefinitions().getName());
			if (method == null)
				method = itemFromNPCHandlers.get(item.getId());
			if (method == null)
				method = itemFromNPCHandlers.get(-1);
		} else {
			if (method == null)
				method = ITEM_HANDLERS.get(item.getDefinitions().getName());
			if (method == null)
				method = ITEM_HANDLERS.get(item.getId());
		}
		if (method == null)
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		NPCDropHandler handler = (NPCDropHandler) method;
		if (handler.getNpcKeys() != null) {
			for (Object npcKey : handler.getNpcKeys()) {
				Map<Object, NPCDropHandler> map = NPC_HANDLERS.get(npcKey);
				if (map == null)
					map = new HashMap<>();
				if (handler.getItemKeys() != null) {
					for (Object itemKey : handler.getItemKeys())
						if (map.put(itemKey, handler) != null)
							System.err.println("ERROR: Duplicate NPCDrop methods for npc key: " + npcKey + " and item key: " + itemKey);
				} else
					map.put(-1, handler);
				if (NPC_HANDLERS.put(npcKey, map) != null)
					System.err.println("ERROR: Duplicate NPCDrop methods for npc key: " + npcKey);
				
			}
		} else {
			for (Object key : handler.getItemKeys()) {
				PluginHandler<? extends PluginEvent> old = ITEM_HANDLERS.put(key, handler);
				if (old != null)
					System.err.println("ERROR: Duplicate NPCDrop methods for item key: " + key);
			}
		}
	}
}
