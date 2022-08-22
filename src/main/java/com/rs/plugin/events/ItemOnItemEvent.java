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
import com.rs.lib.util.Utils;
import com.rs.plugin.handlers.PluginHandler;

public class ItemOnItemEvent implements PluginEvent {

	private static Map<Object, PluginHandler<? extends PluginEvent>> HANDLERS = new HashMap<>();

	private Player player;
	private Item item1;
	private Item item2;

	public ItemOnItemEvent(Player player, Item item1, Item item2) {
		this.player = player;
		this.item1 = item1;
		this.item2 = item2;
	}

	public boolean usedWith(int item1, int item2) {
		return (this.item1.getId() == item1 && this.item2.getId() == item2) || (this.item1.getId() == item2 && this.item2.getId() == item1);
	}

	public Item getUsedWith(int used) {
		if (item1.getId() == used)
			return item2;
		if (item2.getId() == used)
			return item1;
		return null;
	}

	public Item getUsedWith(int... used) {
		for (int use : used) {
			if (item1.getId() == use)
				return item2;
			if (item2.getId() == use)
				return item1;
		}
		return null;
	}

	public Item getItem2() {
		return item2;
	}

	public Item getItem1() {
		return item1;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		int hash1 = (item1.getId() << 16) + item2.getId();
		int hash2 = (item2.getId() << 16) + item1.getId();
		PluginHandler<? extends PluginEvent> method = HANDLERS.get(hash1);
		if (method == null)
			method = HANDLERS.get(hash2);
		if (method == null)
			method = HANDLERS.get(-item1.getId());
		if (method == null)
			method = HANDLERS.get(-item2.getId());
		if (method == null)
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, method);
			if (old != null) {
				int item1 = Utils.interfaceIdFromHash((int) key);
				int item2 = Utils.componentIdFromHash((int) key);
				System.err.println("ERROR: Duplicate ItemOnItem methods for key: " + key + "(" + item1 + ", " + item2 + ") " + method);
			}
		}
	}
}
