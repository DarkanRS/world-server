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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.plugin.handlers;

import java.util.HashSet;
import java.util.Set;

import com.rs.plugin.events.ItemOnItemEvent;

public abstract class ItemOnItemHandler extends PluginHandler<ItemOnItemEvent> {
			
	public ItemOnItemHandler(int[] itemsUsed, int[] usedWiths) {
		super(null);
		Set<Object> list = new HashSet<>();
		if (usedWiths.length > 0) {
			for (Integer usedWith : usedWiths) {
				for (int used : itemsUsed) {
					list.add((usedWith << 16) + used);
					list.add((used << 16) + usedWith);
				}
			}
		} else {
			for (int used : itemsUsed)
				list.add(-used);
		}
		this.keys = list.toArray();
	}
	
	public ItemOnItemHandler(int itemUsed, int[] usedWith) {
		this(new int[] { itemUsed }, usedWith);
	}
	
	public ItemOnItemHandler(int[] itemUsed, int usedWith) {
		this(itemUsed, new int[] { usedWith });
	}
	
	public ItemOnItemHandler(int used, int usedWith) {
		this(new int[] { used },  new int[] { usedWith });
	}
	
	public ItemOnItemHandler(int[] used) {
		this(used,  new int[] { });
	}
	
	public ItemOnItemHandler(int used) {
		this(new int[] { used },  new int[] { });
	}

	public ItemOnItemHandler(boolean directKeySet, Object[] keys) {
		super(keys);
	}

}
