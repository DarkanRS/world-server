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
import com.rs.lib.game.Item;
import com.rs.plugin.handlers.ItemOnPlayerHandler;
import com.rs.plugin.handlers.PluginHandler;

public class ItemOnPlayerEvent implements PluginEvent {

	private static Map<Object, ItemOnPlayerHandler> HANDLERS = new HashMap<>();

	private Player player;
	private Player otherPlayer;
	private Item item;
	private boolean atPlayer;

	public ItemOnPlayerEvent(Player player, Player otherPlayer, Item item, boolean atPlayer) {
		this.player = player;
		this.otherPlayer = otherPlayer;
		this.item = item;
		this.atPlayer = atPlayer;
	}

	public Player getPlayer() {
		return player;
	}

	public Player getTarget() {
		return otherPlayer;
	}

	public Item getItem() {
		return item;
	}

	public boolean isAtPlayer() {
		return atPlayer;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		ItemOnPlayerHandler method = HANDLERS.get(item.getId());
		if (method == null)
			method = HANDLERS.get(item.getDefinitions().getName());
		if ((method == null) || (!isAtPlayer() && method.isCheckDistance()))
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			ItemOnPlayerHandler old = HANDLERS.put(key, (ItemOnPlayerHandler) method);
			if (old != null)
				System.err.println("ERROR: Duplicate ItemOnPlayer methods for key: " + key);
		}
	}
}
