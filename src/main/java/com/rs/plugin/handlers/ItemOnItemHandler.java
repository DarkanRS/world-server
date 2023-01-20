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
package com.rs.plugin.handlers;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.rs.plugin.events.ItemOnItemEvent;

public class ItemOnItemHandler extends PluginHandler<ItemOnItemEvent> {

	public ItemOnItemHandler(int[] itemsUsed, int[] usedWiths, Consumer<ItemOnItemEvent> handler) {
		super(null, handler);
		Set<Object> list = new HashSet<>();
		if (usedWiths.length > 0)
			for (Integer usedWith : usedWiths)
				for (int used : itemsUsed) {
					list.add((usedWith << 16) + used);
					list.add((used << 16) + usedWith);
				}
		else
			for (int used : itemsUsed)
				list.add(-used);
		keys = list.toArray();
	}

	public ItemOnItemHandler(int itemUsed, int[] usedWith, Consumer<ItemOnItemEvent> handler) {
		this(new int[] { itemUsed }, usedWith, handler);
	}

	public ItemOnItemHandler(int[] itemUsed, int usedWith, Consumer<ItemOnItemEvent> handler) {
		this(itemUsed, new int[] { usedWith }, handler);
	}

	public ItemOnItemHandler(int used, int usedWith, Consumer<ItemOnItemEvent> handler) {
		this(new int[] { used },  new int[] { usedWith }, handler);
	}

	public ItemOnItemHandler(int[] used, Consumer<ItemOnItemEvent> handler) {
		this(used,  new int[] { }, handler);
	}

	public ItemOnItemHandler(int used, Consumer<ItemOnItemEvent> handler) {
		this(new int[] { used },  new int[] { }, handler);
	}

	public ItemOnItemHandler(boolean directKeySet, Object[] keys, Consumer<ItemOnItemEvent> handler) {
		super(keys, handler);
	}

}
